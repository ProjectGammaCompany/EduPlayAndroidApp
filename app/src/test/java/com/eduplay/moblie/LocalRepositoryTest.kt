package com.eduplay.moblie

import android.content.Context
import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventStatus
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.localrepository.Database
import com.eduplay.moblie.repository.localrepository.LocalRepository
import com.eduplay.moblie.repository.localrepository.dao.AnswerDao
import com.eduplay.moblie.repository.localrepository.dao.BlockDao
import com.eduplay.moblie.repository.localrepository.dao.ConditionDao
import com.eduplay.moblie.repository.localrepository.dao.CorrectAnswerDao
import com.eduplay.moblie.repository.localrepository.dao.EventDao
import com.eduplay.moblie.repository.localrepository.dao.FileDao
import com.eduplay.moblie.repository.localrepository.dao.GroupDao
import com.eduplay.moblie.repository.localrepository.dao.OptionDao
import com.eduplay.moblie.repository.localrepository.dao.TaskDao
import com.eduplay.moblie.repository.localrepository.dao.UserDao
import com.eduplay.moblie.repository.localrepository.dao.UserEventStatusDao
import com.eduplay.moblie.repository.localrepository.entity.AnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity
import com.eduplay.moblie.repository.localrepository.entity.ConditionEntity
import com.eduplay.moblie.repository.localrepository.entity.CorrectAnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEventStatusEntity
import com.eduplay.moblie.repository.localrepository.entity.UserWithGroups
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.ShortTask
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.TokenManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class LocalRepositoryTest {
    lateinit var repository: LocalRepository

    @MockK
    lateinit var database: Database

    @MockK
    lateinit var tokenManager: TokenManager

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var offlineModeManager: OfflineModeManager

    @MockK
    lateinit var userDao: UserDao

    @MockK
    lateinit var answerDao: AnswerDao

    @MockK
    lateinit var blockDao: BlockDao

    @MockK
    lateinit var conditionDao: ConditionDao

    @MockK
    lateinit var eventDao: EventDao

    @MockK
    lateinit var groupDao: GroupDao

    @MockK
    lateinit var optionDao: OptionDao

    @MockK
    lateinit var taskDao: TaskDao

    @MockK
    lateinit var userEventStatusDao: UserEventStatusDao

    @MockK
    lateinit var correctAnswerDao: CorrectAnswerDao

    @MockK
    lateinit var fileDao: FileDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = LocalRepository(database, tokenManager, offlineModeManager, context)
        every { database.userDao() }.returns(userDao)
        every { database.answerDao() }.returns(answerDao)
        every { database.blockDao() }.returns(blockDao)
        every { database.conditionDao() }.returns(conditionDao)
        every { database.eventDao() }.returns(eventDao)
        every { database.optionDao() }.returns(optionDao)
        every { database.taskDao() }.returns(taskDao)
        every { database.groupDao() }.returns(groupDao)
        every { database.userEventStatus() }.returns(userEventStatusDao)
        every { database.correctAnswerDao() }.returns(correctAnswerDao)
        every { database.fileDao() }.returns(fileDao)
    }

    @Test
    fun `getRole when no event is found returns role Participant`() {
        coEvery { eventDao.getEventById(any()) }.returns(null)

        val result = runBlocking {
            repository.getRole("")
        }

        assertEquals(EventRole.PARTICIPANT, result)
    }

    @Test
    fun `getRole when event's authors contain user returns role Author`() {
        val event = EventEntity(
            id = "1",
            title = "",
            description = "",
            tags = "",
            cover = "",
            startDate = "",
            endDate = "",
            lastEditionDate = "",
            groupEvent = false,
            authorId = "['author 1', 'author 2', 'author 3']"
        )
        val user = "author 3"
        coEvery { eventDao.getEventById(any()) }.returns(event)
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))

        val result = runBlocking {
            repository.getRole("")
        }

        assertEquals(EventRole.AUTHOR, result)
    }

    @Test
    fun `getRole when event's authors dont contain user returns role Participant`() {
        val event = EventEntity(
            id = "1",
            title = "",
            description = "",
            tags = "",
            cover = "",
            startDate = "",
            endDate = "",
            lastEditionDate = "",
            groupEvent = false,
            authorId = "['author 1', 'author 2']"
        )
        val user = "author 3"
        coEvery { eventDao.getEventById(any()) }.returns(event)
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))

        val result = runBlocking {
            repository.getRole("")
        }

        assertEquals(EventRole.PARTICIPANT, result)
    }

    @Test
    fun `getPlayerEventInfo when event is null throws IllegalAccessException`() {
        coEvery { eventDao.getEventById(any()) }.returns(null)
        var exception: Exception? = null

        runBlocking {
            try {
                repository.getPlayerEventInfo("1")
            } catch (e: Exception) {
                exception = e
            }
        }

        assertEquals(true, exception is IllegalAccessException)
        assertEquals("event not downloaded 1", exception?.message)
    }

    @Test
    fun `getPlayerEventInfo when event without groups is converted correctly and with null status`() {
        val startTime = LocalDateTime.now().minusDays(3)
        val endTime = LocalDateTime.now().plusDays(3)
        val lastEditionDate = LocalDateTime.now()
        val event = EventEntity(
            id = "1",
            title = "title",
            description = "description",
            tags = "['tag1', 'tag2', 'tag3']",
            cover = "cover",
            startDate = startTime.toString(),
            endDate = endTime.toString(),
            lastEditionDate = lastEditionDate.toString(),
            groupEvent = false,
            authorId = "['author 1', 'author 2']"
        )
        coEvery { eventDao.getEventById(any()) }.returns(event)
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf("1"))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(any(), any()) }
            .returns(null)

        val expected = EventPlayerInfo(
            title = event.title,
            description = event.description,
            rate = 0.0f,
            favorite = false,
            startDate = event.startDate,
            endDate = event.endDate,
            tags = listOf(
                EventTag("0", "tag1"),
                EventTag("1", "tag2"),
                EventTag("2", "tag3"),
            ),
            cover = event.cover,
            status = EventStatus.NOT_STARTED.status,
            lastEditionDate = event.lastEditionDate,
            authors = listOf(),
            needGroup = false,
            canBeDownloaded = false,
            rated = true,
        )
        val result = runBlocking {
            repository.getPlayerEventInfo("1")
        }
        assertEquals(expected, result)
    }

    @Test
    fun `getPlayerEventInfo check correct status conversion`() {
        val startTime = LocalDateTime.now().minusDays(3)
        val endTime = LocalDateTime.now().plusDays(3)
        val lastEditionDate = LocalDateTime.now()
        val event = EventEntity(
            id = "1",
            title = "title",
            description = "description",
            tags = "['tag1', 'tag2', 'tag3']",
            cover = "cover",
            startDate = startTime.toString(),
            endDate = endTime.toString(),
            lastEditionDate = lastEditionDate.toString(),
            groupEvent = false,
            authorId = "['author 1', 'author 2']"
        )
        val statuses = listOf(
            Pair<UserEventStatusEntity?, String>(null, EventStatus.NOT_STARTED.status),
            Pair<UserEventStatusEntity?, String>(
                UserEventStatusEntity(
                    userId = "",
                    eventId = "1",
                    blockId = "1",
                    taskId = "1",
                    isFinished = false,
                    choseTaskInBlock = false,
                    id = 1
                ),
                EventStatus.STARTED.status
            ),
            Pair<UserEventStatusEntity?, String>(
                UserEventStatusEntity(
                    userId = "",
                    eventId = "1",
                    blockId = "1",
                    taskId = "1",
                    isFinished = true,
                    choseTaskInBlock = false,
                    id = 1
                ),
                EventStatus.ENDED.status
            )
        )
        coEvery { eventDao.getEventById(any()) }.returns(event)
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf("1"))
        for (status in statuses) {
            coEvery { userEventStatusDao.getStatusByUserAndEvent(any(), any()) }
                .returns(status.first)

            val result = runBlocking {
                repository.getPlayerEventInfo("1")
            }

            assertEquals(status.second, result.status)
        }
    }

    @Test
    fun `getPlayerEventInfo check needGroup conversion for group event`() {
        val startTime = LocalDateTime.now().minusDays(3)
        val endTime = LocalDateTime.now().plusDays(3)
        val lastEditionDate = LocalDateTime.now()
        val event = EventEntity(
            id = "1",
            title = "title",
            description = "description",
            tags = "['tag1', 'tag2', 'tag3']",
            cover = "cover",
            startDate = startTime.toString(),
            endDate = endTime.toString(),
            lastEditionDate = lastEditionDate.toString(),
            groupEvent = true,
            authorId = "['author 1', 'author 2']"
        )
        val groupsWithEvent = listOf(GroupEntity("group1", "1", "login", "password"))
        val groupsWithoutEvent = listOf(GroupEntity("group1", "2", "login", "password"))
        val groupStatuses = listOf<Pair<UserWithGroups, Boolean>>(
            Pair(
                UserWithGroups(
                    user = UserEntity("1"),
                    groups = groupsWithoutEvent
                ), true
            ),
            Pair(
                UserWithGroups(
                    user = UserEntity("1"),
                    groups = groupsWithEvent
                ), false
            )
        )
        coEvery { eventDao.getEventById(any()) }.returns(event)
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf("1"))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(any(), any()) }.returns(null)
        for (status in groupStatuses) {
            coEvery { userDao.getUserWithGroupsById(any()) }
                .returns(status.first)

            val result = runBlocking {
                repository.getPlayerEventInfo("1")
            }

            assertEquals(status.second, result.needGroup)
        }
    }

    @Test
    fun `getOwnerEventInfo throws IllegalAccess exception when event form database is null`() {
        coEvery { eventDao.getEventById(any()) }.returns(null)
        var exception: Exception? = null

        runBlocking {
            try {
                repository.getOwnerEventInfo("1")
            } catch (e: Exception) {
                exception = e
            }
        }

        assertEquals(true, exception is IllegalAccessException)
        assertEquals("event not downloaded 1", exception?.message)
    }

    @Test
    fun `getOwnerEventInfo event is parces correctly when repository returns event`() {
        val startTime = LocalDateTime.now().minusDays(3)
        val endTime = LocalDateTime.now().plusDays(3)
        val lastEditionDate = LocalDateTime.now()
        val event = EventEntity(
            id = "1",
            title = "title",
            description = "description",
            tags = "['tag1', 'tag2', 'tag3']",
            cover = "cover",
            startDate = startTime.toString(),
            endDate = endTime.toString(),
            lastEditionDate = lastEditionDate.toString(),
            groupEvent = true,
            authorId = "['author 1', 'author 2']"
        )
        coEvery { eventDao.getEventById(any()) }.returns(event)

        val result = runBlocking { repository.getOwnerEventInfo("") }

        val expected = EventOwnerInfo(
            title = event.title,
            description = event.description,
            tags = listOf("tag1", "tag2", "tag3"),
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

        assertEquals(expected, result)
    }

    @Test
    fun `getProfile returns empty profile`() {
        val expected = ProfileInfo(
            username = "",
            avatar = "",
            email = ""
        )

        val result = runBlocking {
            repository.getProfile()
        }

        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when event status is not null and is finished return finished status`() {
        val user = "1"
        val eventId = "1"
        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = eventId,
            taskId = eventId,
            isFinished = true,
            choseTaskInBlock = true,
            id = 1
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)

        val result = runBlocking {
            repository.getNextStage(eventId)
        }

        val expected = EventStage(
            type = StageType.END.stageName,
            task = null,
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when event status is null and first block is parallel returns parallel block status`() {
        val user = "1"
        val eventId = "1"
        val currentStatus = null
        val blockId = "block1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            ),
            TaskEntity(
                id = "task2",
                blockId = "block1",
                name = "task2",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 2
            ),
            TaskEntity(
                id = "task3",
                blockId = "block1",
                name = "task3",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 3
            )
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)

        val result = runBlocking {
            repository.getNextStage(eventId)
        }

        val expectedTasks = listOf(
            ShortTask(
                id = "task1",
                name = "task1",
                time = 0,
                isCompleted = false
            ),
            ShortTask(
                id = "task2",
                name = "task2",
                time = 0,
                isCompleted = false
            ),
            ShortTask(
                id = "task3",
                name = "task3",
                time = 0,
                isCompleted = false
            )
        )
        val expected = EventStage(
            type = StageType.BLOCK.stageName,
            task = null,
            block = Block(
                id = blockId,
                name = "block",
                tasks = expectedTasks
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when event status is null and first block is not parallel returns status for first task`() {
        val user = "1"
        val eventId = "1"
        val currentStatus = null
        val blockId = "block1"
        val taskId = "task1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "description",
                type = TaskType.INFO,
                time = 10,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            ),
            TaskEntity(
                id = "task2",
                blockId = "block1",
                name = "task2",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 2
            ),
            TaskEntity(
                id = "task3",
                blockId = "block1",
                name = "task3",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 3
            )
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { taskDao.getTaskById(taskId) }.returns(tasks.first())
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(listOf())
        coEvery { fileDao.getFilesByTaskId(taskId) }.returns(listOf())


        val result = runBlocking {
            repository.getNextStage(eventId)
        }


        val expectedTask = Task(
            id = "task1",
            blockId = blockId,
            name = "task1",
            description = "description",
            type = TaskType.INFO.optionNumber,
            options = listOf(),
            files = listOf(),
            time = 10,
            timeStamp = null
        )
        val expected = EventStage(
            type = StageType.TASK.stageName,
            task = expectedTask,
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when all tasks have final answers and there are no conditions displays next block`() {
        val user = "1"
        val eventId = "1"

        val blockId = "block1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            ),
            TaskEntity(
                id = "task2",
                blockId = "block1",
                name = "task2",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 2
            )
        )
        val nextBlockId = "block2"
        val nextBlock = BlockEntity(
            id = nextBlockId,
            name = "block2",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val nextTasks = listOf(
            TaskEntity(
                id = "task3",
                blockId = "block2",
                name = "task3",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 3
            )
        )

        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = blockId,
            taskId = "task2",
            isFinished = false,
            choseTaskInBlock = false,
            id = 1
        )
        val answers = listOf(
            AnswerEntity(
                taskId = "task1",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            ),
            AnswerEntity(
                taskId = "task2",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            ),
            AnswerEntity(
                taskId = "task3",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = false,
                isSynchronized = false
            )
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 2) }.returns(nextBlock)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { blockDao.getBlockById(nextBlockId) }.returns(nextBlock)
        coEvery { blockDao.getPointsInBlockById(blockId, user) }.returns(0)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { taskDao.getAllTasksInBlock(nextBlockId) }.returns(nextTasks)
        coEvery { answerDao.getAnswerByTaskAndUserId("task1", user) }.returns(answers[0])
        coEvery { answerDao.getAnswerByTaskAndUserId("task2", user) }.returns(answers[1])
        coEvery { answerDao.getAnswerByTaskAndUserId("task3", user) }.returns(answers[2])
        coEvery { conditionDao.getConditionsByBlockId(any()) }.returns(listOf())
        coEvery { userDao.getUserWithGroupsById(user) }.returns(null)

        val result = runBlocking {
            repository.getNextStage(eventId)
        }

        val expectedTasks = listOf(
            ShortTask(
                id = "task3",
                name = "task3",
                time = 0,
                isCompleted = false
            )
        )
        val expected = EventStage(
            type = StageType.BLOCK.stageName,
            task = null,
            block = Block(
                id = nextBlockId,
                name = "block2",
                tasks = expectedTasks
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when all tasks have final answers and there are no conditions and no next block returns end status`() {
        val user = "1"
        val eventId = "1"

        val blockId = "block1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            ),
            TaskEntity(
                id = "task2",
                blockId = "block1",
                name = "task2",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 2
            )
        )

        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = blockId,
            taskId = "task2",
            isFinished = false,
            choseTaskInBlock = false,
            id = 1
        )
        val answers = listOf(
            AnswerEntity(
                taskId = "task1",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            ),
            AnswerEntity(
                taskId = "task2",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            )
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 2) }.returns(null)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { blockDao.getPointsInBlockById(blockId, user) }.returns(0)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { answerDao.getAnswerByTaskAndUserId("task1", user) }.returns(answers[0])
        coEvery { answerDao.getAnswerByTaskAndUserId("task2", user) }.returns(answers[1])
        coEvery { conditionDao.getConditionsByBlockId(any()) }.returns(listOf())
        coEvery { userDao.getUserWithGroupsById(user) }.returns(null)

        val result = runBlocking {
            repository.getNextStage(eventId)
        }


        val expected = EventStage(
            type = StageType.END.stageName,
            task = null,
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when condition applies returns stage with condition next block`() {
        val user = "1"
        val userGroup = "group"
        val eventId = "1"
        val blocks = mutableListOf<BlockEntity>()
        val blockTasks = mutableListOf<List<TaskEntity>>()
        for (i in 1..48) {
            blocks.add(
                BlockEntity(
                    id = "block$i",
                    name = "block$i",
                    blockOrder = i,
                    isParallel = true,
                    showPoints = false,
                    showAnswers = false,
                    partialPoints = false,
                    eventId = eventId
                )
            )
            blockTasks.add(
                listOf<TaskEntity>(
                    TaskEntity(
                        id = "block$i",
                        blockId = "block$i",
                        name = "block$i",
                        description = "",
                        type = TaskType.INFO,
                        time = 0,
                        points = 0,
                        partialPoints = false,
                        taskOrder = 1
                    )
                )
            )

            coEvery { blockDao.getBlockById("block$i") }.returns(blocks[i - 1])
            coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, i) }.returns(blocks[i - 1])
            coEvery { taskDao.getAllTasksInBlock("block$i") }.returns(blockTasks[i - 1])
            coEvery {
                taskDao.getTaskByBlockIdAndOrder(
                    "block$i",
                    1
                )
            }.returns(blockTasks[i - 1].first())

        }
        val answers =
            AnswerEntity(
                taskId = "block1",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            )
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)
        coEvery { answerDao.getAnswerByTaskAndUserId("block1", user) }.returns(answers)
        val startBlock = "block1"
        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = startBlock,
            taskId = "task2",
            isFinished = false,
            choseTaskInBlock = false,
            id = 1
        )

        val conditions = mutableListOf<ConditionEntity>()
        val minValues = listOf<Int?>(null, 10, 20)
        val maxValues = listOf<Int?>(null, 20, 30)
        val groupValues = listOf(null, userGroup, "fake group")
        var conditionCount = 1
        for (group in groupValues) {
            for (minVal in minValues) {
                for (maxVal in maxValues) {
                    conditionCount++
                    conditions.add(
                        ConditionEntity(
                            conditionId = "condition$conditionCount",
                            prevBlockId = startBlock,
                            nextBlockId = "block$conditionCount",
                            groupName = group,
                            min = minVal,
                            max = maxVal
                        )
                    )
                }
            }
        }
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)

        for (condition in conditions) {
            val userPoints = listOf(0, 10, 20, 30, 40)
            val userGroup = listOf(
                UserWithGroups(
                    user = UserEntity(user),
                    groups = listOf(
                        GroupEntity(
                            groupId = "1",
                            eventId = "1",
                            login = "",
                            password = ""
                        ),
                    )
                ),
                UserWithGroups(
                    user = UserEntity(user),
                    groups = listOf<GroupEntity>()
                )
            )
            for (group in userGroup) {
                for (points in userPoints) {
                    coEvery { blockDao.getPointsInBlockById(startBlock, user) }.returns(points)

                    coEvery { conditionDao.getConditionsByBlockId(any()) }.returns(listOf(condition))
                    coEvery { userDao.getUserWithGroupsById(user) }.returns(group)


                    val result = runBlocking {
                        repository.getNextStage(eventId)
                    }

                    var toConditionBlock = false
                    if (condition.min != null) {
                        toConditionBlock = condition.min <= points
                    }
                    if (condition.max != null) {
                        toConditionBlock = condition.max > points
                    }

                    if (condition.groupName != null) {
                        if (group.groups.isEmpty()) {
                            toConditionBlock = false
                        } else {
                            toConditionBlock = (group.groups.first().login) == condition.groupName
                        }
                    }

                    val expectedTasks = if (toConditionBlock) {
                        listOf(
                            ShortTask(
                                id = condition.nextBlockId,
                                name = condition.nextBlockId,
                                time = 0,
                                isCompleted = false
                            )
                        )
                    } else {
                        listOf(
                            ShortTask(
                                id = "block2",
                                name = "block2",
                                time = 0,
                                isCompleted = false
                            )
                        )
                    }
                    val expected =
                        EventStage(
                            type = StageType.BLOCK.stageName,
                            task = null,
                            block = Block(
                                id = if (toConditionBlock) condition.nextBlockId else "block2",
                                name = if (toConditionBlock) condition.nextBlockId else "block2",
                                tasks = expectedTasks
                            )
                        )
                    assertEquals(expected, result)
                }
            }
        }
    }

    @Test
    fun `getNextStage when chose task in parallel block returns task in block`() {
        val user = "1"
        val eventId = "1"

        val blockId = "block1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task 1",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            )
        )

        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = blockId,
            taskId = "task1",
            isFinished = false,
            choseTaskInBlock = true,
            id = 1
        )
        val answers = listOf<AnswerEntity>()
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 2) }.returns(null)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { blockDao.getPointsInBlockById(blockId, user) }.returns(0)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getTaskById("task1") }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { conditionDao.getConditionsByBlockId(any()) }.returns(listOf())
        coEvery { userDao.getUserWithGroupsById(user) }.returns(null)
        coEvery { optionDao.getOptionsByTaskId(any()) }.returns(listOf())
        coEvery { fileDao.getFilesByTaskId(any()) }.returns(listOf())
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)


        val result = runBlocking {
            repository.getNextStage(eventId)
        }


        val expected = EventStage(
            type = StageType.TASK.stageName,
            task = Task(
                id = "task1",
                blockId = "block1",
                name = "task 1",
                description = "",
                type = TaskType.INFO.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = null
            ),
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage gets all options for tasks`() {
        val user = "1"
        val eventId = "1"
        val currentStatus = null
        val blockId = "block1"
        val taskId = "task1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "description",
                type = TaskType.INFO,
                time = 10,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            )
        )
        val options = listOf(
            OptionEntity(
                id = "option1",
                taskId = "task1",
                value = "option1"
            ),
            OptionEntity(
                id = "option2",
                taskId = "task1",
                value = "option2"
            ),
        )
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { taskDao.getTaskById(taskId) }.returns(tasks.first())
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { fileDao.getFilesByTaskId(taskId) }.returns(listOf())


        val result = runBlocking {
            repository.getNextStage(eventId)
        }


        val expectedOptions = options.map { AnswerOption(it.id, it.value) }
        val expectedTask = Task(
            id = "task1",
            blockId = blockId,
            name = "task1",
            description = "description",
            type = TaskType.INFO.optionNumber,
            options = expectedOptions,
            files = listOf(),
            time = 10,
            timeStamp = null
        )
        val expected = EventStage(
            type = StageType.TASK.stageName,
            task = expectedTask,
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage gets all files for tasks`() {
        val user = "1"
        val eventId = "1"
        val currentStatus = null
        val blockId = "block1"
        val taskId = "task1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "description",
                type = TaskType.INFO,
                time = 10,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            )
        )
        val files = listOf("file1", "file2")
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { taskDao.getTaskById(taskId) }.returns(tasks.first())
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), user) }.returns(null)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(listOf())
        coEvery { fileDao.getFilesByTaskId(taskId) }.returns(files)


        val result = runBlocking {
            repository.getNextStage(eventId)
        }

        val expectedFiles = files.map { Task.TaskFile(it, it) }
        val expectedTask = Task(
            id = "task1",
            blockId = blockId,
            name = "task1",
            description = "description",
            type = TaskType.INFO.optionNumber,
            options = listOf(),
            files = expectedFiles,
            time = 10,
            timeStamp = null
        )
        val expected = EventStage(
            type = StageType.TASK.stageName,
            task = expectedTask,
            block = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `getNextStage when enters completed block deletes all answers in it`() {
        val user = "1"
        val eventId = "1"

        val blockId = "block1"
        val block = BlockEntity(
            id = blockId,
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val tasks = listOf<TaskEntity>(
            TaskEntity(
                id = "task1",
                blockId = "block1",
                name = "task1",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 1
            ),
            TaskEntity(
                id = "task2",
                blockId = "block1",
                name = "task2",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 2
            )
        )
        val nextBlockId = "block2"
        val nextBlock = BlockEntity(
            id = nextBlockId,
            name = "block2",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = false,
            partialPoints = false,
            eventId = eventId
        )
        val nextTasks = listOf(
            TaskEntity(
                id = "task3",
                blockId = "block2",
                name = "task3",
                description = "",
                type = TaskType.INFO,
                time = 0,
                points = 0,
                partialPoints = false,
                taskOrder = 3
            )
        )

        val currentStatus = UserEventStatusEntity(
            userId = user,
            eventId = eventId,
            blockId = blockId,
            taskId = "task2",
            isFinished = false,
            choseTaskInBlock = false,
            id = 1
        )
        val answers = listOf(
            AnswerEntity(
                taskId = "task1",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            ),
            AnswerEntity(
                taskId = "task2",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            ),
            AnswerEntity(
                taskId = "task3",
                options = "[]",
                userId = user,
                startTime = "",
                endTime = "",
                points = 0,
                isFinal = true,
                isSynchronized = false
            )
        )
        var deletedAnswers = false
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { userEventStatusDao.getStatusByUserAndEvent(user, eventId) }.returns(currentStatus)
        coEvery { userEventStatusDao.insertStatus(any()) }.returns(Unit)
        coEvery { userEventStatusDao.updateStatus(any()) }.returns(Unit)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 1) }.returns(block)
        coEvery { blockDao.getBlockByEventIdAndBlockOrder(eventId, 2) }.returns(nextBlock)
        coEvery { blockDao.getBlockById(blockId) }.returns(block)
        coEvery { blockDao.getBlockById(nextBlockId) }.returns(nextBlock)
        coEvery { blockDao.getPointsInBlockById(blockId, user) }.returns(0)
        coEvery { taskDao.getTaskByBlockIdAndOrder(blockId, 1) }.returns(tasks.first())
        coEvery { taskDao.getAllTasksInBlock(blockId) }.returns(tasks)
        coEvery { taskDao.getAllTasksInBlock(nextBlockId) }.returns(nextTasks)
        coEvery { answerDao.getAnswerByTaskAndUserId("task1", user) }.returns(answers[0])
        coEvery { answerDao.getAnswerByTaskAndUserId("task2", user) }.returns(answers[1])
        coEvery { answerDao.getAnswerByTaskAndUserId("task3", user) }.answers {
            if (deletedAnswers) null else answers[2]
        }
        coEvery { answerDao.deleteAllAnswersInBlock("block2", user) }.answers {
            deletedAnswers = true
            1
        }
        coEvery { conditionDao.getConditionsByBlockId(any()) }.returns(listOf())
        coEvery { userDao.getUserWithGroupsById(user) }.returns(null)

        val result = runBlocking {
            repository.getNextStage(eventId)
        }

        val expectedTasks = listOf(
            ShortTask(
                id = "task3",
                name = "task3",
                time = 0,
                isCompleted = false
            )
        )
        val expected = EventStage(
            type = StageType.BLOCK.stageName,
            task = null,
            block = Block(
                id = nextBlockId,
                name = "block2",
                tasks = expectedTasks
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `postTaskAnswer when task is not found throws IllegalAccess exception`() {
        coEvery { taskDao.getTaskById(any()) }.returns(null)
        var exception: Exception? = null

        runBlocking {
            try {
                repository.postTaskAnswer(
                    "1",
                    "block1",
                    "task1",
                    listOf()
                )
            } catch (e: Exception) {
                exception = e
            }
        }


        assertEquals(true, exception is IllegalAccessException)
        assertEquals("no such task in database task1", exception?.message)
    }

    @Test
    fun `postTaskAnswer when no answer is stored throws IllegalAccessException`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val user = "user"
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById(any()) }.returns(null)
        coEvery { answerDao.getAnswerByTaskAndUserId(any(), any()) }.returns(null)
        var exception: Exception? = null

        runBlocking {
            try {
                repository.postTaskAnswer(
                    "1",
                    "block1",
                    "task1",
                    listOf()
                )
            } catch (e: Exception) {
                exception = e
            }
        }


        assertEquals(true, exception is IllegalAccessException)
        assertEquals("no time preloaded to answer", exception?.message)
    }

    @Test
    fun `postTaskAnswer points is not null when show points is true`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = true,
            showAnswers = false,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()
        for (i in 0..5) {
            options.add(
                OptionEntity(
                    id = i.toString(),
                    taskId = taskId,
                    value = i.toString()
                )
            )
        }
        val correctOptions = mutableListOf<CorrectAnswerEntity>()
        for (i in 0..5) {
            correctOptions.add(
                CorrectAnswerEntity(
                    taskId = taskId,
                    value = "",
                    id = 1
                )
            )
        }
        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.returns(Unit)

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                listOf()
            )
        }


        assertNotEquals(null, result.points)
    }

    @Test
    fun `postTaskAnswer points is null when show points is false`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = false,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()
        for (i in 0..5) {
            options.add(
                OptionEntity(
                    id = i.toString(),
                    taskId = taskId,
                    value = i.toString()
                )
            )
        }
        val correctOptions = mutableListOf<CorrectAnswerEntity>()
        for (i in 0..5) {
            correctOptions.add(
                CorrectAnswerEntity(
                    taskId = taskId,
                    value = "",
                    id = 1
                )
            )
        }
        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.returns(Unit)

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                listOf()
            )
        }


        assertEquals(null, result.points)
    }

    @Test
    fun `postTaskAnswer answers is not null when show answers is true`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = true,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()
        for (i in 0..5) {
            options.add(
                OptionEntity(
                    id = i.toString(),
                    taskId = taskId,
                    value = i.toString()
                )
            )
        }
        val correctOptions = mutableListOf<CorrectAnswerEntity>()
        for (i in 0..5) {
            correctOptions.add(
                CorrectAnswerEntity(
                    taskId = taskId,
                    value = "",
                    id = 1
                )
            )
        }
        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.returns(Unit)

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                listOf()
            )
        }


        assertNotEquals(null, result.rightAnswer)
    }

    @Test
    fun `postTaskAnswer answers is null when show answers is false`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = false,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()
        for (i in 0..5) {
            options.add(
                OptionEntity(
                    id = i.toString(),
                    taskId = taskId,
                    value = i.toString()
                )
            )
        }
        val correctOptions = mutableListOf<CorrectAnswerEntity>()
        for (i in 0..5) {
            correctOptions.add(
                CorrectAnswerEntity(
                    taskId = taskId,
                    value = "",
                    id = 1
                )
            )
        }
        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.returns(Unit)


        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                listOf()
            )
        }


        assertEquals(null, result.rightAnswer)
    }

    @Test
    fun `postTaskAnswer when partial answer on task is true and not all answers are correct answer returns status partial`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = true,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = true,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()

        options.add(
            OptionEntity(
                id = "1",
                taskId = taskId,
                value = "1"
            )
        )

        val correctOptions = mutableListOf<CorrectAnswerEntity>()

        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9",
                id = 1
            )
        )
        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
                id = 2
            )
        )

        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        var savedAnswer: AnswerEntity? = null
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.answers {
            savedAnswer = arg(0)
        }

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                options.map { it.value }
            )
        }


        assertEquals(TaskAnswerStatus.PARTIALLY, result.isCorrect)
        assertEquals(5, savedAnswer?.points)
    }

    @Test
    fun `postTaskAnswer when partial answer on task is false and not all answers are correct answer returns status incorrect`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = false,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = true,
            showAnswers = true,
            partialPoints = false,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()

        options.add(
            OptionEntity(
                id = "1",
                taskId = taskId,
                value = "1"
            )
        )

        val correctOptions = mutableListOf<CorrectAnswerEntity>()

        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9",
                id = 1
            )
        )
        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
                id = 2
            )
        )

        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        var savedAnswer: AnswerEntity? = null
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.answers {
            savedAnswer = arg(0)
        }

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                options.map { it.value }
            )
        }


        assertEquals(TaskAnswerStatus.INCORRECT, result.isCorrect)
        assertEquals(0, savedAnswer?.points)
    }

    @Test
    fun `postTaskAnswer when all answers are correct answer returns status correct`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = false,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = false,
            showPoints = false,
            showAnswers = true,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()

        options.add(
            OptionEntity(
                id = "1",
                taskId = taskId,
                value = "1"
            )
        )

        val correctOptions = mutableListOf<CorrectAnswerEntity>()

        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
                id = 1
            )
        )

        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        var savedAnswer: AnswerEntity? = null
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.answers {
            savedAnswer = arg(0)
        }

        val result = runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                options.map { it.value }
            )
        }


        assertEquals(TaskAnswerStatus.CORRECT, result.isCorrect)
        assertEquals(10, savedAnswer?.points)
    }

    @Test
    fun `postTaskAnswer when block is parallel check status updates to chose task in block false`() {
        val taskId = "task1"
        val task = TaskEntity(
            id = taskId,
            blockId = "block1",
            name = "task",
            description = "",
            type = TaskType.MULTIPLE_CHOICE,
            time = 0,
            points = 10,
            partialPoints = false,
            taskOrder = 1
        )
        val block = BlockEntity(
            id = "block1",
            name = "block",
            blockOrder = 1,
            isParallel = true,
            showPoints = false,
            showAnswers = true,
            partialPoints = true,
            eventId = "event"
        )
        val options = mutableListOf<OptionEntity>()

        options.add(
            OptionEntity(
                id = "1",
                taskId = taskId,
                value = "1"
            )
        )

        val correctOptions = mutableListOf<CorrectAnswerEntity>()

        correctOptions.add(
            CorrectAnswerEntity(
                taskId = taskId,
                value = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
                id = 1
            )
        )

        val user = "user"
        val startTime = LocalDateTime.now().minusMinutes(1)
        val answer = AnswerEntity(
            taskId = taskId,
            options = listOf(),
            userId = user,
            startTime = startTime.toString(),
            endTime = "",
            points = 0,
            isFinal = false,
            isSynchronized = false
        )

        val status = UserEventStatusEntity(
            userId = user,
            eventId = "event",
            blockId = "block1",
            taskId = "task1",
            isFinished = false,
            choseTaskInBlock = true,
            id = 1
        )
        var savedStatus: UserEventStatusEntity? = null
        coEvery { offlineModeManager.getCurrentUserId() }.returns(flowOf(user))
        coEvery { taskDao.getTaskById(taskId) }.returns(task)
        coEvery { blockDao.getBlockById("block1") }.returns(block)
        coEvery { optionDao.getOptionsByTaskId(taskId) }.returns(options)
        coEvery { correctAnswerDao.getAnswersByTask(taskId) }.returns(correctOptions)
        coEvery { answerDao.getAnswerByTaskAndUserId(taskId, user) }.returns(answer)
        coEvery { answerDao.updateAnswer(any()) }.returns(Unit)
        coEvery { userEventStatusDao.getStatusByUserAndEvent(any(), any()) }.returns(status)
        coEvery { userEventStatusDao.updateStatus(any()) }.answers {
            savedStatus = arg(0)
        }

        runBlocking {
            repository.postTaskAnswer(
                "1",
                "block1",
                "task1",
                options.map { it.value }
            )
        }


        assertEquals(false, savedStatus?.choseTaskInBlock)
    }

}