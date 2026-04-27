package com.eduplay.moblie.repository.localrepository

import android.content.Context
import android.util.Log
import androidx.room.RoomRawQuery
import androidx.sqlite.SQLiteStatement
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventStatus
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.localrepository.entity.AnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity
import com.eduplay.moblie.repository.localrepository.entity.ConditionEntity
import com.eduplay.moblie.repository.localrepository.entity.CorrectAnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.FileEntity
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEventStatusEntity
import com.eduplay.moblie.repository.localrepository.entity.UserGroupEntity
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.responseTypes.ShortTask
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.requestTypes.AnswerBatch
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.repository.webrepository.responseTypes.UserEventStatus
import com.eduplay.moblie.services.JwtDecoder
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Base64

class LocalRepository @Inject constructor(
    private val eventDatabase: Database,
    private val tokenManager: TokenManager,
    private val offlineModeManager: OfflineModeManager,
    @ApplicationContext context: Context
) : Repository {
    private val context: Context

    init {
        this.context = context
    }

    private var choseTaskInParallelBlock = false

    suspend fun getEvents(
        tags: List<String>?,
        active: Boolean,
        title: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<EventEntity> {
        val stringBuilder = StringBuilder("SELECT * FROM events ")
        val binders = mutableListOf<(SQLiteStatement, Int) -> Unit>()
        if (!tags.isNullOrEmpty() || active || title.isNotBlank()) {
            stringBuilder.append("WHERE 1=1 ") // вот это условие тут только для более красивого добавление условий через AND
        }
        val addStringBinder = { str: String ->
            binders.add { it: SQLiteStatement, idx: Int ->
                it.bindText(
                    idx,
                    str.trim()
                )
            }
        }
        val addIntBinder = { number: Int ->
            binders.add { it: SQLiteStatement, idx: Int ->
                it.bindInt(
                    idx,
                    number
                )
            }
        }
        if (!tags.isNullOrEmpty()) {
            for (tag in tags) {
                stringBuilder.append("AND tags,?) ")
                addStringBinder(tag)
            }
        }
        if (title.isNotBlank()) {
            stringBuilder.append("AND instr(lower(title),  lower(?)) ")
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
            onBindStatement = { statement ->
                binders.forEachIndexed { idx, binder ->
                    binder(
                        statement,
                        idx + 1
                    )
                }
            }
        )
        return eventDatabase.eventDao().getEventsByArguments(query)
    }

    override suspend fun getRole(eventId: String): EventRole {
        val authorsJson = eventDatabase.eventDao().getEventById(eventId)?.authorId
        if (authorsJson == null) return EventRole.PARTICIPANT

        val authors = Gson().fromJson<List<String>>(authorsJson, List::class.java)
        val userId = getCurrentUser()
        return if (authors.contains(userId)) EventRole.AUTHOR else EventRole.PARTICIPANT
    }

    override suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo {
        val event = eventDatabase.eventDao().getEventById(eventId)
        if (event == null) {
            throw IllegalAccessException("event not downloaded $eventId")
        }
        val tags = Gson()
            .fromJson<List<String>>(event.tags, List::class.java)
            .mapIndexed { idx, it -> EventTag(idx.toString(), it) }

        val status = eventDatabase.userEventStatus()
            .getStatusByUserAndEvent(getCurrentUser(), eventId)
        val textStatus = if (status == null) {
            EventStatus.NOT_STARTED.status
        } else if (status.isFinished) {
            EventStatus.ENDED.status
        } else {
            EventStatus.STARTED.status
        }

        var needGroup = event.groupEvent
        if (needGroup) {
            val groups =
                eventDatabase.userDao().getUserWithGroupsById(getCurrentUser())?.groups ?: listOf()
            needGroup = groups.none { it.eventId == eventId }
        }

        return EventPlayerInfo(
            title = event.title,
            description = event.description,
            rate = 0.0f,
            favorite = false,
            startDate = event.startDate,
            endDate = event.endDate,
            tags = tags,
            cover = event.cover,
            status = textStatus,
            lastEditionDate = event.lastEditionDate,
            authors = listOf(),
            needGroup = needGroup,
            canBeDownloaded = false,
            rated = true,
        )
    }

    override suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo { // теоритически такое может случиться
        val event = eventDatabase.eventDao().getEventById(eventId)
        if (event == null) {
            throw IllegalAccessException("event not downloaded $eventId")
        }
        val tags = Gson()
            .fromJson<List<String>>(event.tags, List::class.java)

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
            groups = listOf(),
            eventRating = 0.0f,
            collaborators = listOf(),
            allowDownloading = true
        )
    }

    suspend fun getFavouriteEvents(
        limit: Int,
        offset: Int
    ): List<EventEntity> {
        val stringBuilder = StringBuilder("SELECT * FROM events ")
        val binders = mutableListOf<(SQLiteStatement, Int) -> Unit>()

        val addIntBinder = { number: Int ->
            binders.add { it: SQLiteStatement, idx: Int ->
                it.bindInt(
                    idx,
                    number
                )
            }
        }

        stringBuilder.append("ORDER BY startDate ")
        stringBuilder.append("LIMIT ? OFFSET ?")
        addIntBinder(limit)
        addIntBinder(offset)
        val query = RoomRawQuery(
            sql = stringBuilder.toString(),
            onBindStatement = { statement ->
                binders.forEachIndexed { idx, binder ->
                    binder(
                        statement,
                        idx + 1
                    )
                }
            }
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
        return ProfileInfo(
            username = "",
            avatar = "",
            email = ""
        )
    }

    override suspend fun getNextStage(eventId: String): EventStage {
        val user = getCurrentUser()
        var status = eventDatabase.userEventStatus().getStatusByUserAndEvent(user, eventId)
        if (status != null && status.isFinished) {
            return eventEnded()
        }

        if (status == null) status = fillStarterStatus(eventId)
        val currentBlock =
            if (status.blockId != null)
                eventDatabase.blockDao().getBlockById(status.blockId)
            else
                eventDatabase.blockDao().getBlockByEventIdAndBlockOrder(status.eventId, 1)

        if (currentBlock == null) {
            Log.e("NEXT_STAGE", "did not find block ${status.blockId}")
            return eventEnded()
        }

        var allTasksCompleted = true
        val tasks = eventDatabase.taskDao().getAllTasksInBlock(currentBlock.id)
            .map { task ->
                val answer = eventDatabase.answerDao().getAnswerByTaskAndUserId(task.id, user)
                val isCompleted = answer != null && answer.isFinal // дан ответ на задание
                allTasksCompleted = allTasksCompleted && isCompleted
                ShortTask(
                    id = task.id,
                    name = task.name,
                    time = task.time,
                    isCompleted = isCompleted
                )
            }

        return if (allTasksCompleted) {
            proceedToNextBlock(currentBlock, status)
        } else {
            displayCurrentBlock(currentBlock, tasks, status)
        }
    }

    private suspend fun fillStarterStatus(eventId: String): UserEventStatusEntity {
        val block = eventDatabase.blockDao().getBlockByEventIdAndBlockOrder(eventId, 1)
        val task = eventDatabase.taskDao().getTaskByBlockIdAndOrder(block?.id ?: "", 1)
        val user = getCurrentUser()
        val status = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = block?.id
                ?: throw IllegalAccessException("no block downloaded for event $eventId"),
            taskId = task?.id
                ?: throw IllegalAccessException("no block downloaded for event $eventId"),
            isFinished = false,
            choseTaskInBlock = false
        )
        eventDatabase.userEventStatus().insertStatus(status)
        return status
    }

    private suspend fun displayCurrentBlock(
        currentBlock: BlockEntity,
        tasks: List<ShortTask>,
        status: UserEventStatusEntity
    ): EventStage {
        val user = getCurrentUser()
        if (currentBlock.isParallel) {
            if (status.choseTaskInBlock && status.taskId != null) {
                return displayTask(status.taskId, user, status)
            } else {
                return displayParallelBlock(currentBlock, tasks)
            }
        } else {
            val taskIdx = tasks.indexOfFirst { it.id == status.taskId }

            val taskId = if (taskIdx != -1 && tasks[taskIdx].isCompleted) {
                tasks[taskIdx + 1].id
            } else if (taskIdx != -1 && !tasks[taskIdx].isCompleted) {
                tasks[taskIdx].id
            } else {
                tasks.first().id
            }
            return displayTask(taskId, user, status)
        }
    }

    private suspend fun displayTask(
        taskId: String,
        userId: String,
        status: UserEventStatusEntity
    ): EventStage {
        val task = eventDatabase.taskDao().getTaskById(taskId)
        if (task == null) throw IllegalAccessException("didnt download task $taskId")

        eventDatabase.userEventStatus().updateStatus(
            UserEventStatusEntity(
                userId = status.userId,
                eventId = status.eventId,
                blockId = task.blockId,
                taskId = taskId,
                isFinished = status.isFinished,
                choseTaskInBlock = false,
                id = status.id
            )
        )

        val options = eventDatabase.optionDao().getOptionsByTaskId(taskId)
        val startTime = eventDatabase.answerDao().getAnswerByTaskAndUserId(taskId, userId)
        val files = eventDatabase.fileDao().getFilesByTaskId(taskId)
        return EventStage(
            type = StageType.TASK.stageName,
            task = Task(task, options, startTime?.startTime, files),
            block = null
        )
    }

    private fun displayParallelBlock(block: BlockEntity, tasks: List<ShortTask>): EventStage {
        return EventStage(
            type = StageType.BLOCK.stageName,
            task = null,
            block = Block(
                id = block.id,
                name = block.name,
                tasks = tasks
            )
        )
    }

    private suspend fun proceedToNextBlock(
        currentBlock: BlockEntity,
        status: UserEventStatusEntity
    ): EventStage {
        val user = getCurrentUser()
        val conditions = eventDatabase.conditionDao().getConditionsByBlockId(currentBlock.id)
        val points = eventDatabase.blockDao().getPointsInBlockById(currentBlock.id)
        val groups: List<GroupEntity> =
            eventDatabase.userDao().getUserWithGroupsById(user)?.groups ?: listOf()

        for (condition in conditions) {
            var gotConditions = true
            if (condition.min != null) {
                gotConditions = points >= condition.min
            }
            if (condition.max != null) {
                gotConditions = points < condition.max
            }

            if (condition.groupName != null) {
                gotConditions =
                    groups.any { group -> group.eventId == currentBlock.eventId && group.login == condition.groupName }
            }

            if (gotConditions) {
                val newBlock = eventDatabase.blockDao().getBlockById(condition.nextBlockId)
                    ?: throw IllegalAccessException("no block found for condition ${condition.conditionId}")
                return changeBlock(newBlock, user, status)
            }
        }
        val nextBlockByOrder = eventDatabase.blockDao()
            .getBlockByEventIdAndBlockOrder(currentBlock.eventId, currentBlock.blockOrder + 1)
        if (nextBlockByOrder == null) {
            return eventEnded(status)
        }
        return changeBlock(nextBlockByOrder, user, status)

    }

    private suspend fun changeBlock(
        newBlock: BlockEntity,
        user: String,
        status: UserEventStatusEntity
    ): EventStage {
        var allTasksCompleted = true
        var tasks = eventDatabase.taskDao().getAllTasksInBlock(newBlock.id)
            .map { task ->
                val answer = eventDatabase.answerDao().getAnswerByTaskAndUserId(task.id, user)
                val isCompleted = answer != null && answer.isFinal // дан ответ на задание
                allTasksCompleted = allTasksCompleted && isCompleted
                ShortTask(
                    id = task.id,
                    name = task.name,
                    time = task.time,
                    isCompleted = isCompleted
                )
            }
        if (allTasksCompleted) { // если заходим второй раз в блок затираем все ответы
            eventDatabase.answerDao().deleteAllAnswersInBlock(newBlock.id, user)
            tasks = tasks.map { task ->
                ShortTask(
                    id = task.id,
                    name = task.name,
                    time = task.time,
                    isCompleted = false
                )
            }
        }
        val newStatus = UserEventStatusEntity(
            userId = status.userId,
            eventId = status.eventId,
            blockId = newBlock.id,
            taskId = tasks.first().id,
            isFinished = false,
            choseTaskInBlock = false,
            id = status.id
        )
        eventDatabase.userEventStatus().updateStatus(newStatus)
        return displayCurrentBlock(newBlock, tasks, newStatus)
    }

    private fun eventEnded(): EventStage {
        return EventStage(
            type = StageType.END.stageName,
            task = null,
            block = null
        )
    }

    private suspend fun eventEnded(status: UserEventStatusEntity): EventStage { // also updates player status to finished
        eventDatabase.userEventStatus().updateStatus(
            UserEventStatusEntity(
                userId = status.userId,
                eventId = status.eventId,
                blockId = status.blockId,
                taskId = status.taskId,
                isFinished = true,
                choseTaskInBlock = false,
                id = status.id
            )
        )
        return eventEnded()
    }

    override suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean {
        val task = eventDatabase.taskDao().getTaskById(taskId)
        if (task == null) return false
        val userId = getCurrentUser()
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf<String>(),
            userId = userId,
            startTime = DateConverter.convertToServerFormat(startTime),
            endTime = DateConverter.convertToServerFormat(LocalDateTime.MIN),
            points = -1,
            isFinal = false,
            isSynchronized = false
        )
        eventDatabase.answerDao().insertAnswer(answer)
        return true
    }

    override suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        val endTime = LocalDateTime.now()

        val task = eventDatabase.taskDao().getTaskById(taskId)
        if (task == null) {
            throw IllegalAccessException("no such task in database $taskId")
        }

        val userId = getCurrentUser()
        var answer = eventDatabase.answerDao().getAnswerByTaskAndUserId(taskId, userId)

        if (answer == null) {
            throw IllegalAccessException("no time preloaded to answer")
        }

        var points: Int? = 0
        var rightAnswer: List<String>? = null
        var isCorrect: TaskAnswerStatus? = null
        val block = eventDatabase.blockDao().getBlockById(blockId)

        if (block == null) throw IllegalAccessException("no block $blockId")
        val hashedAnswers = answers.map { hashString(it) }

        // проверяем ответ
        if (TaskType.valueOf(task.type) != TaskType.INFO) {
            val correctAnswers =
                eventDatabase.correctAnswerDao().getAnswersByTask(taskId).map { it.value }.toSet()
            if (block.showAnswers) {
                rightAnswer = findCorrectAnswers(correctAnswers, taskId)

            }
            val intersection = correctAnswers.intersect(hashedAnswers)
            if (intersection.isEmpty()) {
                isCorrect = TaskAnswerStatus.INCORRECT
                points = 0
            } else if (intersection.size == correctAnswers.size) {
                isCorrect = TaskAnswerStatus.CORRECT
                points = task.points
            } else if (block.partialPoints || task.partialPoints) {
                isCorrect = TaskAnswerStatus.PARTIALLY
                points = (intersection.size / correctAnswers.size) * task.points
            }
        }

        answer = AnswerEntity(
            taskId = answer.taskId,
            options = Gson().toJson(answers),
            userId = answer.userId,
            startTime = answer.startTime,
            endTime = DateConverter.convertToServerFormat(endTime),
            points = points!!,
            isFinal = true,
            answerId = answer.answerId,
            isSynchronized = false
        )
        // сохраняем ответ
        eventDatabase.answerDao().updateAnswer(
            answer
        )

        if (block.isParallel) { // отмечаем что надо перейти обратно в блок
            val status = eventDatabase.userEventStatus().getStatusByUserAndEvent(userId, eventId)
            if (status != null) {
                eventDatabase.userEventStatus().updateStatus(
                    UserEventStatusEntity(
                        userId = status.userId,
                        eventId = status.eventId,
                        blockId = status.blockId,
                        taskId = status.taskId,
                        isFinished = status.isFinished,
                        choseTaskInBlock = false,
                        id = status.id
                    )
                )
            }
        }

        if (!block.showPoints) points = null
        if (!block.showAnswers) isCorrect = null
        return AnswerResult(
            rightAnswer = rightAnswer,
            points = points,
            isCorrect = isCorrect
        )
    }

    private suspend fun findCorrectAnswers(
        correctAnswers: Set<String>,
        taskId: String
    ): List<String> {
        val answers = mutableListOf<String>()
        val allOptions = eventDatabase.optionDao().getOptionsByTaskId(taskId)
        allOptions.forEach { optionEntity ->
            if (correctAnswers.contains(hashString(optionEntity.value))) {
                answers.add(optionEntity.value)
            }
        }
        return answers
    }

    override suspend fun postTaskChoice(
        eventId: String,
        blockId: String,
        taskId: String
    ): Boolean {
        val userId = getCurrentUser()
        val currentStatus = eventDatabase.userEventStatus().getStatusByUserAndEvent(userId, eventId)
        if (currentStatus == null) return false
        if (currentStatus.blockId != blockId) return false
        val block = eventDatabase.blockDao().getBlockById(currentStatus.blockId)
        if (!(block?.isParallel ?: false)) return false

        val task = eventDatabase.taskDao().getTaskById(taskId)
        if (task == null || task.blockId != blockId) return false

        val answer = eventDatabase.answerDao().getAnswerByTaskAndUserId(taskId, userId)

        if (answer != null && answer.points != -1) return false

        val updatedStatus = UserEventStatusEntity(
            userId = currentStatus.userId,
            eventId = currentStatus.eventId,
            blockId = currentStatus.blockId,
            taskId = taskId,
            isFinished = currentStatus.isFinished,
            choseTaskInBlock = true,
            id = currentStatus.id
        )

        eventDatabase.userEventStatus().updateStatus(updatedStatus)
        choseTaskInParallelBlock = true
        return true
    }

    override suspend fun getResults(eventId: String): PlayerStats {
        val userId = getCurrentUser()
        return PlayerStats(
            fullStats = false,
            groupEvent = false,
            users = listOf(
                PlayerStats.StatUser(
                    id = userId,
                    username = getProfile().username,
                    avatar = null,
                    points = 0,
                )
            ),
            groups = null
        )
    }

    override suspend fun addToFavourite(
        eventId: String,
        isFavorite: Boolean
    ): Boolean {
        return false
    }


    override suspend fun getTags(): EventTagList {
        return EventTagList(listOf())
    }

    override suspend fun enterGroupEvent(
        eventId: String,
        groupName: String,
        groupPassword: String
    ) {
        val userId = getCurrentUser()
        val userGroup = eventDatabase.userDao()
            .getUserWithGroupsById(userId)
            ?.groups?.firstOrNull { it.eventId == eventId }
        val group = eventDatabase.groupDao().getGroupByEventIdAndLogin(eventId, groupName)
        if (userGroup != null) {
            return
        }
        if (group == null) throw IllegalAccessException("wrong group or password")
        if (hashString(groupPassword) != group.password) throw IllegalAccessException("wrong group or password")
        eventDatabase.groupDao().insertUserGroup(UserGroupEntity(userId, group.groupId))
    }

    override suspend fun getNotifications(
        page: Int,
        maxOnPage: Int
    ): List<NotificationData> {
        return listOf()
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
            eventDatabase.userDao().insertUser(UserEntity(userId))
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

    private fun hashString(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = s.toByteArray()
        val bytes = md.digest(input)
        return Base64.getEncoder().encodeToString(bytes)
    }

    suspend fun addEvent(eventEntity: EventEntity) {
        val event = eventDatabase.eventDao().getEventById(eventEntity.id)
        if (event == null) {
            eventDatabase.eventDao().insertEvent(eventEntity)
        } else {
            eventDatabase.eventDao().updateEvent(eventEntity)
        }
    }

    suspend fun addBlock(block: BlockEntity) {
        val event = eventDatabase.blockDao().getBlockById(block.id)
        if (event == null) {
            eventDatabase.blockDao().insertBlock(block)
        } else {
            eventDatabase.blockDao().updateBlock(block)
        }
    }

    suspend fun addCondition(conditionEntity: ConditionEntity) {
        val event = eventDatabase.conditionDao().getConditionById(conditionEntity.conditionId)
        if (event == null) {
            eventDatabase.conditionDao().insertCondition(conditionEntity)
        } else {
            eventDatabase.conditionDao().updateCondition(conditionEntity)
        }
    }

    suspend fun addGroup(groupEntity: GroupEntity) {
        val event = eventDatabase.groupDao().getGroupById(groupEntity.groupId)
        if (event == null) {
            eventDatabase.groupDao().insertGroup(groupEntity)
        } else {
            eventDatabase.groupDao().updateGroup(groupEntity)
        }
    }

    suspend fun addTask(taskEntity: TaskEntity, files: List<String>, eventId: String) {
        val event = eventDatabase.taskDao().getTaskById(taskEntity.id)
        if (event == null) {
            eventDatabase.taskDao().insertTask(taskEntity)
        } else {
            eventDatabase.taskDao().updateTask(taskEntity)
        }
        for (file in files) {
            eventDatabase.fileDao().insertFile(
                FileEntity(
                    eventId = eventId,
                    taskId = taskEntity.id,
                    fileName = file
                )
            )
        }

    }

    suspend fun addOption(optionEntity: OptionEntity) {
        val event = eventDatabase.optionDao().getOptionById(optionEntity.id)
        if (event == null) {
            eventDatabase.optionDao().insertOption(optionEntity)
        } else {
            eventDatabase.optionDao().updateOption(optionEntity)
        }
    }

    suspend fun addAnswer(correctAnswerEntity: CorrectAnswerEntity) {
        val event = eventDatabase.correctAnswerDao().getAnswerByValue(correctAnswerEntity.value)
        if (event == null) {
            eventDatabase.correctAnswerDao().insertAnswer(correctAnswerEntity)
        } else {
            eventDatabase.correctAnswerDao().updateAnswer(correctAnswerEntity)
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        val event = eventDatabase.eventDao().getEventById(eventId)
        val files = eventDatabase.fileDao().getFilesByEventId(eventId)
        if (event != null) {
            for (file in files) {
                File(this.context.filesDir, file).delete()
            }
            eventDatabase.eventDao().deleteEvent(event)
            return true
        }
        eventDatabase.userEventStatus().deleteStatusesByEvent(eventId)
        return false
    }

    suspend fun getCurrentPlayerStatus(eventId: String): AnswerBatch? {
        val user = getCurrentUser()
        val status = eventDatabase.userEventStatus().getStatusByUserAndEvent(user, eventId)
        if (status == null) return null
        val points = eventDatabase.answerDao().getTotalPointsForEvent(eventId, user)
        val answers = eventDatabase
            .answerDao().getUnsynchronisedAnswersByEventAndUserId(eventId, user)
            .map {
                AnswerBatch.Answer(
                    taskId = it.taskId,
                    options = Gson().fromJson<List<String>>(it.options, List::class.java)
                )
            }
        return AnswerBatch(
            userId = user,
            answers = answers,
            totalPoints = points,
            currentBlock = status.blockId ?: "",
            currentTask = status.taskId ?: "",
            isDone = status.isFinished
        )
    }

    suspend fun markAnswersAsSynchronised(answers: List<AnswerBatch.Answer>) {
        val user = getCurrentUser()
        for (answer in answers) {
            val entity = eventDatabase.answerDao().getAnswerByTaskAndUserId(answer.taskId, user)
            if (entity == null) continue
            val answer =  AnswerEntity(
                taskId = entity.taskId,
                options = entity.options,
                userId = entity.userId,
                startTime = entity.startTime,
                endTime = entity.endTime,
                points = entity.points,
                isFinal = entity.isFinal,
                isSynchronized = true,
                answerId = entity.answerId
            )
            eventDatabase.answerDao().updateAnswer(
                answer
            )
        }
    }

    suspend fun getLastUpdateDate(eventId: String): String? {
        val event = eventDatabase.eventDao().getEventById(eventId)
        if (event == null) return null
        return event.lastEditionDate
    }

    suspend fun updateUserStatuses(okEvents: List<UserEventStatus>) {
        val user = getCurrentUser()
        for (event in okEvents) {
            val status =
                eventDatabase.userEventStatus().getStatusByUserAndEvent(user, event.eventId)
            val isFinished = EventStatus.statusOf(event.status) == EventStatus.ENDED

            // заполняем статусы
            if (status != null) {
                val newStatus = UserEventStatusEntity(
                    userId = status.userId,
                    eventId = status.eventId,
                    blockId = if (event.blockId?.isNotBlank() ?: false) event.blockId else null,
                    taskId = if (event.taskId?.isNotBlank() ?: false) event.taskId else null,
                    isFinished = isFinished,
                    choseTaskInBlock = false,
                    id = status.id
                )
                eventDatabase.userEventStatus().updateStatus(newStatus)
            } else {
                val newStatus = UserEventStatusEntity(
                    userId = user,
                    eventId = event.eventId,
                    blockId = if (event.blockId?.isNotBlank() ?: false) event.blockId else null,
                    taskId = if (event.taskId?.isNotBlank() ?: false) event.taskId else null,
                    isFinished = isFinished,
                    choseTaskInBlock = false,
                )
                eventDatabase.userEventStatus().insertStatus(newStatus)
            }

            // обновили группу
            if (event.groupId != null) {
                val userGroup =
                    eventDatabase.userDao().getUserWithGroupsById(user)?.groups?.firstOrNull {
                        it.eventId == event.eventId
                    }
                if (userGroup == null) {
                    eventDatabase.groupDao().insertUserGroup(UserGroupEntity(user, event.groupId))
                }
            }

            // залили ответы
            if (event.blockId == null || isFinished) return

            eventDatabase.answerDao().deleteAllAnswersInBlock(event.blockId, user)

            val date = DateConverter.convertToServerFormat(LocalDateTime.MIN)
            val fakeAnswers = event.completedTasksInBlock?.mapIndexed { idx, task ->
                if (idx == 0 && event.pointsInBlock != 0) {
                    AnswerEntity(
                        taskId = task,
                        options = listOf(),
                        userId = user,
                        startTime = date,
                        endTime = date,
                        points = event.pointsInBlock,
                        isFinal = true,
                        isSynchronized = true
                    )
                } else {
                    AnswerEntity(
                        taskId = task,
                        options = listOf(),
                        userId = user,
                        startTime = date,
                        endTime = date,
                        points = 0,
                        isFinal = true,
                        isSynchronized = true
                    )
                }
            } ?: listOf()
            for (answer in fakeAnswers) {
                eventDatabase.answerDao().insertAnswer(answer)
            }

            if (event.timeStamp != null && event.timeStamp.isNotBlank()) {
                postTaskStartTime(
                    event.eventId,
                    event.blockId,
                    event.taskId!!,
                    DateConverter.convertFromServerFormat(event.timeStamp)
                )
            }
        }
    }

    suspend fun containsUnsentAnswers(): Boolean {
        return eventDatabase.answerDao().containsUnsynchronisedAnswers() != 0
    }


}