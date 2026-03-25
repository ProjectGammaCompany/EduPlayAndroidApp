package com.eduplay.moblie.repository.localrepository

//import android.database.sqlite.SQLiteStatement
import androidx.sqlite.SQLiteStatement
import android.util.Log
import androidx.room.RoomRawQuery
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEntity
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.services.JwtDecoder
import com.eduplay.moblie.services.OfflineModeManager
import com.eduplay.moblie.useCases.TokenManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
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
            addStringBinder(title)        }

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
        TODO("Not yet implemented")
    }

    override suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getFavouriteEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getCreatedEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(): ProfileInfo {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        TODO("Not yet implemented")
    }

    override suspend fun register(auth: RegistrationData): AuthResult {
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

        if (getCurrentUser().first() != userId) {
            offlineModeManager.saveCurrentUserId(userId)
            Log.d("SAVE_USER", "saved $userId")
        }
    }

    fun getCurrentUser(): Flow<String> {
        return offlineModeManager.getCurrentUserId()
    }

    suspend fun removeCurrentUser() {
        offlineModeManager.removeCurrentUserId()
    }
}