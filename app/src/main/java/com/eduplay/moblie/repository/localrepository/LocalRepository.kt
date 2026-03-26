package com.eduplay.moblie.repository.localrepository

import androidx.sqlite.SQLiteStatement
import android.util.Log
import androidx.room.RoomRawQuery
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventStatus
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEntity
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.services.JwtDecoder
import com.eduplay.moblie.services.OfflineModeManager
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.TokenManager
import com.google.gson.Gson
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class LocalRepository @Inject constructor(
    private val eventDatabase: Database,
    private val tokenManager: TokenManager,
    private val offlineModeManager: OfflineModeManager

) : Repository {
    suspend fun getEvents(
        tags: List<String>?,
        active: Boolean,
        title: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<EventEntity> {
        val stringBuilder = StringBuilder("SELECT * FROM events ")
        val binders = mutableListOf<(SQLiteStatement, Int)->Unit>()
        if (!tags.isNullOrEmpty() || active || title.isNotBlank()) {
            stringBuilder.append("WHERE 1=1 ") // вот это условие тут только для более красивого добавление условий через AND
        }
        val addStringBinder = { str:String -> binders.add { it: SQLiteStatement, idx: Int -> it.bindText(binders.size + 1, str.trim()) } }
        val addIntBinder = {number: Int -> binders.add { it: SQLiteStatement, idx: Int -> it.bindInt(idx, number) }}
        if (!tags.isNullOrEmpty()) {
            for (tag in tags) {
                stringBuilder.append("AND instr(tags, ?) ")
                addStringBinder(tag)
            }
        }
        if (title.isNotBlank()) {
            stringBuilder.append("AND instr(title, ?) ")
            addStringBinder(title)
        }

        if (active) {
            stringBuilder.append("AND datetime(?) BETWEEN datetime(startDate) AND datetime(endDate)")
            addStringBinder(DateConverter.convertToServerFormat(LocalDateTime.now()))

        }

        stringBuilder.append("ORDER BY startDate ")
        stringBuilder.append("LIMIT ? OFFSET ?")
        addIntBinder(limit)
        addIntBinder(offset)
        val query = RoomRawQuery(
            sql = stringBuilder.toString(),
            onBindStatement = { statement -> binders.forEachIndexed { idx, binder->  binder(statement, idx+1) } }
        )
        return eventDatabase.eventDao().getEventsByArguments(query)
    }

    override suspend fun getRole(eventId: String): EventRole {
        val authorsJson = eventDatabase.eventDao().getEventById(eventId)?.authorId
        if (authorsJson == null) return EventRole.PARTICIPANT

        val authors = Gson().fromJson<List<String>>(authorsJson, String::class.java)
        val userId = getCurrentUser()
        return if (authors.contains(userId)) EventRole.AUTHOR else EventRole.PARTICIPANT
    }

    override suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo {
        val event = eventDatabase.eventDao().getEventById(eventId)
        if (event == null) {
            throw IllegalAccessException("event not downloaded $eventId")
        }
        val tags = Gson()
            .fromJson<List<String>>(event.tags, String::class.java)
            .mapIndexed {idx, it-> EventTag(idx.toString(), it) }

        val status = eventDatabase.userEventStatus()
            .getStatusByUserAndEvent(getCurrentUser(), eventId)
        val textStatus = if (status == null) {
            EventStatus.NOT_STARTED.status
        } else if (status.isFinished) {
            EventStatus.ENDED.status
        } else {
            EventStatus.STARTED.status
        }

        return EventPlayerInfo(
            title = event.title,
            description = event.description,
            rate = 0.0f,
            favorite = false,
            startDate = event.startDate,
            endDate = event.endDate,
            tags = tags,
            cover = event.tags,
            status = textStatus,
            lastEditionDate = event.lastEditionDate,
            authors = listOf()
        )
    }

    override suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo { // теоритически такое может случиться
        val event = eventDatabase.eventDao().getEventById(eventId)
        if (event == null) {
            throw IllegalAccessException("event not downloaded $eventId")
        }
        val tags = Gson()
            .fromJson<List<String>>(event.tags, String::class.java)
            .mapIndexed {idx, it-> EventTag(idx.toString(), it) }

        return EventOwnerInfo(
            title = event.title,
            description = event.description,
            tags = tags,
            cover = event.cover,
            startDate = event.startDate,
            endDate = event.endDate,
            private = false,
            password = "",
            lastEditionDate = event.lastEditionDate,
            groupEvent = false,
            groupNames = listOf(),
            groups = listOf(),
            eventRating = 0.0f,
            collaboratos = listOf(),
            allowDownloading = true
        )
    }

    suspend fun getFavouriteEvents(
        limit: Int,
        offset: Int
    ): List<EventEntity> {
        val stringBuilder = StringBuilder("SELECT * FROM events ")
        val binders = mutableListOf<(SQLiteStatement, Int)->Unit>()

        val addIntBinder = {number: Int -> binders.add { it: SQLiteStatement, idx: Int -> it.bindInt(idx, number) }}

        stringBuilder.append("ORDER BY startDate ")
        stringBuilder.append("LIMIT ? OFFSET ?")
        addIntBinder(limit)
        addIntBinder(offset)
        val query = RoomRawQuery(
            sql = stringBuilder.toString(),
            onBindStatement = { statement -> binders.forEachIndexed { idx, binder->  binder(statement, idx+1) } }
        )
        return eventDatabase.eventDao().getEventsByArguments(query)
    }

    suspend fun getCreatedEvents(
        offset: Int,
        limit: Int
    ): List<QuestShortInfo> {
        val userId = getCurrentUser()
        return eventDatabase.eventDao().getEventByAuthor(userId, limit, offset)
            .map { QuestShortInfo(it) }
    }

    suspend fun getCompletedEvents(
        limit: Int,
        offset: Int
    ): List<QuestShortInfo> {
        val userId = getCurrentUser()
        return eventDatabase.eventDao().getCompletedEventByUserId(userId, limit, offset)
            .map { QuestShortInfo(it) }
    }

    override suspend fun getProfile(): ProfileInfo {
        val userId = offlineModeManager.getCurrentUserId().first()
        val user = eventDatabase.userDao().getUserById(userId)
        return ProfileInfo(
            username = user?.email ?: "",
            avatar = user?.avatar ?: ""
        )
    }

    override suspend fun getNextStage(eventId: String): EventStage {
        TODO("Not yet implemented")
    }

    override suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean {
        TODO("добавить соответствующую штуку к ответам в базу")
    }

    suspend fun postTaskEndTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean {
        TODO("добавить соответствующую штуку к ответам в базу")
    }

    override suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        TODO("Not yet implemented")
    }

    override suspend fun postTaskChoice(
        eventId: String,
        blockId: String,
        taskId: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getResults(eventId: String): PlayerStats {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavourite(
        eventId: String,
        isFavorite: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }


    override suspend fun getTags(): EventTagList {
        TODO("Not yet implemented")
    }

    suspend fun saveUser() {
        val token = tokenManager.getAccessToken().first()
        val userId = JwtDecoder.getUserId(token)
        if (userId == null) {
            Log.i("PARSE_ACCESS_TOKEN", "failed to get user id from access token")
            return
        }

        val user = eventDatabase.userDao().getUserById(userId)
        if (user == null) {
            val email = JwtDecoder.getUserEmail(token)
            if (email == null) {
                Log.i("PARSE_ACCESS_TOKEN", "failed to get user email from access token")
                return
            }
            eventDatabase.userDao().insertUser(UserEntity(email, "", userId))
        }

        if (getCurrentUser() != userId) {
            offlineModeManager.saveCurrentUserId(userId)
            Log.d("SAVE_USER", "saved $userId")
        }
    }

    private suspend fun getCurrentUser(): String {
        return offlineModeManager.getCurrentUserId().first()
    }

    suspend fun removeCurrentUser() {
        offlineModeManager.removeCurrentUserId()
    }

    suspend fun isEventDownloaded(eventId: String): Boolean {
        return eventDatabase.eventDao().getEventById(eventId) != null
    }
}