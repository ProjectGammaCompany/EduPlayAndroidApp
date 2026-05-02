package com.eduplay.moblie

import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.localrepository.LocalRepository
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.ShortTask
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.webrepository.WebRepository
import com.eduplay.moblie.repository.webrepository.requestTypes.AnswerBatch
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.UserEditorStat
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.repository.webrepository.responseTypes.UserEventStatus
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.OfflineModeManager.AppModes
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.spyk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class EduRepositoryTest {

    lateinit var repository: EduRepository

    @MockK
    lateinit var localRepository: LocalRepository

    @MockK
    lateinit var webRepository: WebRepository

    @MockK
    lateinit var offlineModeManager: OfflineModeManager

    @SpyK
    var playerInfo: EventPlayerInfo = spyk(
        EventPlayerInfo(
            title = "",
            description = "",
            rate = 0f,
            favorite = false,
            startDate = "",
            endDate = "",
            tags = listOf(),
            cover = "",
            status = "",
            lastEditionDate = "",
            authors = listOf(),
            needGroup = false,
            canBeDownloaded = false,
            rated = false,
            isDownloaded = false,
            needsUpdate = false
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = EduRepository(webRepository, localRepository, offlineModeManager)
    }

    @Test
    fun `login_ user is saved to local repo when AuthResponse from web repo is Success test`() {
        var userSaved = false
        coEvery { webRepository.login(any<Auth>()) }.returns(AuthResult.SUCCESSES)
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }
        val authData = Auth("", "")


        runBlocking {
            repository.login(authData)
        }


        assertEquals(true, userSaved)
    }

    @Test
    fun `login_ user is not saved to local repo when AuthResponse from web repo is not Success test`() {
        var userSaved = false

        val notSuccessResponses = AuthResult.entries.filter { it != AuthResult.SUCCESSES }.toList()
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }
        for (response in notSuccessResponses) {
            coEvery { webRepository.login(any<Auth>()) }.returns(response)
            val authData = Auth("", "")


            runBlocking {
                repository.login(authData)
            }


            assertEquals(false, userSaved)
        }
    }

    @Test
    fun `register_ user is saved to local repo when AuthResponse from web repo is Success test`() {
        var userSaved = false
        coEvery { webRepository.register(any<RegistrationData>()) }.returns(AuthResult.SUCCESSES)
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }
        val authData = RegistrationData("", "", "")


        runBlocking {
            repository.register(authData)
        }


        assertEquals(true, userSaved)
    }

    @Test
    fun `register_ user is not saved to local repo when AuthResponse from web repo is not Success test`() {
        var userSaved = false
        val notSuccessResponses = AuthResult.entries.filter { it != AuthResult.SUCCESSES }.toList()
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }
        val authData = RegistrationData("", "", "")
        for (response in notSuccessResponses) {
            coEvery { webRepository.register(any<RegistrationData>()) }.returns(response)


            runBlocking {
                repository.register(authData)
            }


            assertEquals(false, userSaved)
        }
    }

    @Test
    fun `updatePassword_ user is saved to local repo when AuthResponse from web repo is Success test`() {
        var userSaved = false
        coEvery {
            webRepository.updatePassword(
                any<String>(),
                any<String>(),
                any<String>()
            )
        }.returns(AuthResult.SUCCESSES)
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }


        runBlocking {
            repository.updatePassword("", "", "")
        }


        assertEquals(true, userSaved)
    }

    @Test
    fun `updatePassword_ user is not saved to local repo when AuthResponse from web repo is not Success test`() {
        var userSaved = false
        val notSuccessResponses = AuthResult.entries.filter { it != AuthResult.SUCCESSES }.toList()
        coEvery { localRepository.saveUser() }.answers {
            userSaved = true
        }
        val authData = RegistrationData("", "", "")
        for (response in notSuccessResponses) {
            coEvery {
                webRepository.updatePassword(
                    any<String>(),
                    any<String>(),
                    any<String>()
                )
            }.returns(response)


            runBlocking {
                repository.updatePassword("", "", "")
            }


            assertEquals(false, userSaved)
        }
    }

    @Test
    fun `logout_ user is removed from local repo when web repo returns true on logout test`() {
        var userRemoved = false
        coEvery { webRepository.logout() }.returns(true)
        coEvery { localRepository.removeCurrentUser() }.answers {
            userRemoved = true
        }


        runBlocking {
            repository.logout()
        }


        assertEquals(true, userRemoved)
    }

    @Test
    fun `logout_ user is not removed from local repo when  web repo returns false on logout test`() {
        var userRemoved = false
        coEvery { webRepository.logout() }.returns(false)
        coEvery { localRepository.removeCurrentUser() }.answers {
            userRemoved = true
        }


        runBlocking {
            repository.logout()
        }


        assertEquals(false, userRemoved)
    }

    @Test
    fun `getEventInfoPlayer_ there is no check for updates when event is not downloaded and app is in online mode test`() {
        var checked = false
        coEvery { webRepository.getPlayerEventInfo(any()) }.returns(playerInfo)
        coEvery { localRepository.isEventDownloaded(any()) }.returns(false)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { localRepository.getLastUpdateDate(any()) }
            .answers {
                checked = true
                ""
            }

        runBlocking {
            repository.getEventInfoPlayer("")
        }

        assertEquals(false, playerInfo.needsUpdate)
        assertEquals(false, checked)
    }

    @Test
    fun `getEventInfoPlayer_ there is no check for updates when app is in offline mode test`() {
        var checked = false
        coEvery { localRepository.getPlayerEventInfo(any()) }.returns(playerInfo)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.OFFLINE))
        coEvery { localRepository.getLastUpdateDate(any()) }.answers {
            checked = true
            ""
        }
        val downloaded = listOf(true, false)

        for (status in downloaded) {
            coEvery { localRepository.isEventDownloaded(any()) }.returns(status)

            runBlocking {
                repository.getEventInfoPlayer("")
            }

            assertEquals(false, playerInfo.needsUpdate)
            assertEquals(false, checked)
        }
    }

    @Test
    fun `getEventInfoPlayer_ playerInfo needsUpdate is false when event is downloaded, app mode is online and event last edition date is equal to the date in local repo test`() {
        var checked = false
        val time = LocalDateTime.now().toString()
        coEvery { webRepository.getPlayerEventInfo(any()) }.returns(playerInfo)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { localRepository.isEventDownloaded(any()) }.returns(true)
        coEvery { localRepository.getLastUpdateDate(any()) }.answers {
            checked = true
            time
        }
        every { playerInfo.lastEditionDate } returns time

        runBlocking {
            repository.getEventInfoPlayer("")
        }

        assertEquals(false, playerInfo.needsUpdate)
        assertEquals(true, checked)
    }

    @Test
    fun `getEventInfoPlayer_ playerInfo needsUpdate is true when event is downloaded, app mode is online and event last edition date is not equal to the date in local repo test`() {
        var checked = false
        coEvery { webRepository.getPlayerEventInfo(any()) }.returns(playerInfo)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { localRepository.isEventDownloaded(any()) }.returns(true)
        coEvery { localRepository.getLastUpdateDate(any()) }.answers {
            checked = true
            LocalDateTime.now().toString()
        }

        runBlocking {
            repository.getEventInfoPlayer("")
        }

        assertEquals(true, playerInfo.needsUpdate)
        assertEquals(true, checked)
    }

    @Test
    fun `getNextStage_ options in task are shuffled when task is not null test`() {
        val options = mutableListOf<AnswerOption>()
        for (i in 0..100) {
            options.add(AnswerOption(i.toString(), i.toString()))
        }
        val optionSet = options.toSet()
        val task = Task(
            id = "",
            blockId = "",
            name = "",
            description = "",
            type = TaskType.MULTIPLE_CHOICE.optionNumber,
            options = options,
            files = listOf(),
            time = 0,
            timeStamp = ""
        )
        val stage = EventStage(StageType.TASK.stageName, task, null)
        coEvery { webRepository.getNextStage(any()) }.returns(stage)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))

        val resultStage = runBlocking {
            repository.getNextStage("")
        }

        assertNotEquals(options, resultStage.task?.options)
        assertEquals(true, optionSet.subtract(resultStage.task?.options ?: listOf()).isEmpty())
        assertEquals(true, resultStage.task?.options?.toSet()?.subtract(optionSet)?.isEmpty())
    }

    @Test
    fun `getNextStage_ tasks in block are shuffled when block is not null test`() {
        val tasks = mutableListOf<ShortTask>()
        for (i in 0..100) {
            tasks.add(ShortTask(i.toString(), i.toString(), 0, false))
        }
        val optionSet = tasks.toSet()
        val block = Block(
            id = "",
            name = "",
            tasks = tasks
        )
        val stage = EventStage(StageType.BLOCK.stageName, null, block)
        coEvery { webRepository.getNextStage(any()) }.returns(stage)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))

        val resultStage = runBlocking {
            repository.getNextStage("")
        }

        assertNotEquals(tasks, resultStage.block?.tasks)
        assertEquals(true, optionSet.subtract(resultStage.block?.tasks ?: listOf()).isEmpty())
        assertEquals(true, resultStage.block?.tasks?.toSet()?.subtract(optionSet)?.isEmpty())
    }

    @Test
    fun `postEventRating_ rating is not sent when app mode is offline`() {
        var ratingSent = false
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.OFFLINE))
        coEvery { webRepository.postRating(any(), any()) }.answers {
            ratingSent = true
        }

        val result = runBlocking {
            repository.postEventRating("", 0)
        }

        assertEquals(false, result)
        assertEquals(false, ratingSent)
    }

    @Test
    fun `postEventRating_ rating is sent when app mode is online`() {
        var ratingSent = false
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { webRepository.postRating(any(), any()) }.answers {
            ratingSent = true
        }

        val result = runBlocking {
            repository.postEventRating("", 0)
        }

        assertEquals(true, result)
        assertEquals(true, ratingSent)
    }

    @Test
    fun `postAnswerBatch_ answers are not sent when app mode is offline`() {
        var answersSent = false
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.OFFLINE))
        coEvery { webRepository.postAnswerBatch(any(), any()) }.answers {
            answersSent = true
            true
        }

        val result = runBlocking {
            repository.postAnswerBatch("")
        }

        assertEquals(true, result)
        assertEquals(false, answersSent)
    }

    @Test
    fun `postAnswerBatch_ answers are not sent when localRepository returns null`() {
        var answersSent = false
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { webRepository.postAnswerBatch(any(), any()) }.answers {
            answersSent = true
            true
        }
        coEvery { localRepository.getCurrentPlayerAnswers(any()) }.returns(null)

        val result = runBlocking {
            repository.postAnswerBatch("")
        }

        assertEquals(true, result)
        assertEquals(false, answersSent)
    }

    @Test
    fun `postAnswerBatch_ when answers are not successfully sent they are not marked in local repo`() {
        var answersSent = false
        var answersMarkedAsSent = false
        val answerBatch = AnswerBatch(
            userId = "",
            answers = listOf(),
            totalPoints = 0,
            currentBlock = "",
            currentTask = "",
            isDone = false
        )
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { webRepository.postAnswerBatch(any(), any()) }.answers {
            answersSent = true
            false
        }
        coEvery { localRepository.getCurrentPlayerAnswers(any()) }.returns(answerBatch)
        coEvery { localRepository.markAnswersAsSynchronised(answerBatch.answers) }.answers {
            answersMarkedAsSent = true
            true
        }

        val result = runBlocking {
            repository.postAnswerBatch("")
        }

        assertEquals(false, result)
        assertEquals(true, answersSent)
        assertEquals(false, answersMarkedAsSent)
    }

    @Test
    fun `postAnswerBatch_ when answers are successfully sent they are marked in local repo`() {
        var answersSent = false
        var answersMarkedAsSent = false
        val answerBatch = AnswerBatch(
            userId = "",
            answers = listOf(),
            totalPoints = 0,
            currentBlock = "",
            currentTask = "",
            isDone = false
        )
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { webRepository.postAnswerBatch(any(), any()) }.answers {
            answersSent = true
            true
        }
        coEvery { localRepository.getCurrentPlayerAnswers(any()) }.returns(answerBatch)
        coEvery { localRepository.markAnswersAsSynchronised(answerBatch.answers) }.answers {
            answersMarkedAsSent = true
            true
        }

        val result = runBlocking {
            repository.postAnswerBatch("")
        }

        assertEquals(true, result)
        assertEquals(true, answersSent)
        assertEquals(true, answersMarkedAsSent)
    }

    @Test
    fun `updateDownloadedEventsStatuses_ when appMode is offline null is returned`() {
        var gotEventsFromLocalRepo = false
        coEvery { localRepository.getEvents(any(), any(), any()) }.answers {
            gotEventsFromLocalRepo = true
            listOf()
        }
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.OFFLINE))

        val result = runBlocking {
            repository.updateDownloadedEventsStatuses()
        }

        assertEquals(null, result)
        assertEquals(false, gotEventsFromLocalRepo)
    }

    @Test
    fun `updateDownloadedEventsStatuses_ when no events are downloaded an empty list is returned`() {
        var gotEventsFromLocalRepo = false
        coEvery { localRepository.getEvents(any(), any(), any(), any(), any()) }.answers {
            gotEventsFromLocalRepo = true
            listOf()
        }
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))

        val result = runBlocking {
            repository.updateDownloadedEventsStatuses()
        }

        assertEquals(true, result?.isEmpty())
        assertEquals(true, gotEventsFromLocalRepo)
    }

    @Test
    fun `updateDownloadedEventsStatuses_ when there are downloaded events and web repo failed to get updates null is returned`() {
        val events = listOf<EventEntity>(
            EventEntity(
                id = "",
                title = "",
                description = "",
                tags = "",
                cover = "",
                startDate = "",
                endDate = "",
                lastEditionDate = "",
                groupEvent = false,
                authorId = ""
            )
        )
        coEvery { localRepository.getEvents(any(), any(), any(), any(), any()) }.returns(events)
        coEvery { webRepository.getDownloadedEventsStatus(any()) }.returns(null)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))

        val result = runBlocking {
            repository.updateDownloadedEventsStatuses()
        }

        assertEquals(null, result)
    }

    @Test
    fun `updateDownloadedEventsStatuses_ when downloaded events are up to date empty list is returned`() {
        val date = LocalDateTime.now().toString()
        val events = listOf<EventEntity>(
            EventEntity(
                id = "1",
                title = "1",
                description = "1",
                tags = "",
                cover = "",
                startDate = "",
                endDate = "",
                lastEditionDate = "",
                groupEvent = false,
                authorId = ""
            )
        )
        val status = UserEventStatus(
            eventId = "",
            status = "",
            type = "",
            taskId = "",
            blockId = "",
            timeStamp = "",
            groupId = "",
            lastEditionDate = date,
            pointsInBlock = 0,
            completedTasksInBlock = listOf()
        )
        var savedStatus = false
        coEvery { localRepository.getEvents(any(), any(), any(), any(), any()) }.returns(events)
        coEvery { webRepository.getDownloadedEventsStatus(any()) }.returns(listOf(status))
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { localRepository.updateUserStatuses(any()) }.answers {
            savedStatus = true
        }

        val result = runBlocking {
            repository.updateDownloadedEventsStatuses()
        }

        assertEquals(true, result?.isEmpty())
        assertEquals(true, savedStatus)
    }

    @Test
    fun `updateDownloadedEventsStatuses_ when downloaded events are not up to date list with events is returned`() {
        val date = LocalDateTime.now().toString()
        val events = listOf<EventEntity>(
            EventEntity(
                id = "1",
                title = "1",
                description = "1",
                tags = "[]",
                cover = "",
                startDate = "",
                endDate = "",
                lastEditionDate = "",
                groupEvent = false,
                authorId = ""
            )
        )
        val status = UserEventStatus(
            eventId = "1",
            status = "",
            type = "",
            taskId = "",
            blockId = "",
            timeStamp = "",
            groupId = "",
            lastEditionDate = LocalDateTime.now().toString(),
            pointsInBlock = 0,
            completedTasksInBlock = listOf()
        )
        coEvery { localRepository.getEvents(any(), any(), any(), any(), any()) }.returns(events)
        coEvery { webRepository.getDownloadedEventsStatus(any()) }.returns(listOf(status))
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { localRepository.updateUserStatuses(any()) }.returns(Unit)

        val result = runBlocking {
            repository.updateDownloadedEventsStatuses()
        }

        assertEquals(false, result?.isEmpty())
    }

    @Test
    fun `getEventEditorStats_ when app mode is offline returns empty stats`() {
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.OFFLINE))
        val expectedResult = ResultStats(false, null, null)

        val result = runBlocking {
            repository.getEventEditorStats("")
        }

        assertEquals(expectedResult, result)
    }

    @Test
    fun `getEventEditorStats_ when app mode is online returns expectedStats from web repo`() {
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        val users = mutableListOf<UserEditorStat>()
        for (i in 0..10) {
            users.add(
                UserEditorStat(
                    id = i.toString(),
                    username = i.toString(),
                    answers = UserEditorStat.Answer(i, 10),
                    points = i,
                    avatar = "TODO()"
                )
            )
        }
        val expectedResult = ResultStats(false, users, null)
        coEvery { webRepository.getEventEditorStats(any()) }.returns(expectedResult)

        val result = runBlocking {
            repository.getEventEditorStats("")
        }

        assertEquals(expectedResult, result)
    }

    @Test
    fun `postAllAnswers_ when failed to send answer batch sending is stopped`() {
        val events = mutableListOf<EventEntity>()
        for (i in 0..10) {
            events.add(
                EventEntity(
                    id = i.toString(),
                    title = "",
                    description = "",
                    tags = "",
                    cover = "",
                    startDate = "",
                    endDate = "",
                    lastEditionDate = "",
                    groupEvent = false,
                    authorId = ""
                )
            )
        }
        val answerBatch = AnswerBatch(
            userId = "",
            answers = listOf(),
            totalPoints = 0,
            currentBlock = "",
            currentTask = "",
            isDone = false
        )
        var eventCounter = 0
        val maxEvents = 7
        coEvery { localRepository.getEvents(any(), any(), any(), any(), any()) }
            .returns(events)
        coEvery { offlineModeManager.getAppMode() }.returns(flowOf(AppModes.ONLINE))
        coEvery { webRepository.postAnswerBatch(any(), any()) }.answers {
            eventCounter < maxEvents
        }
        coEvery { localRepository.getCurrentPlayerAnswers(any()) }.returns(answerBatch)
        coEvery { localRepository.markAnswersAsSynchronised(answerBatch.answers) }.answers {
            eventCounter += 1
        }

        runBlocking {
            repository.postAllAnswers()
        }

        assertEquals(maxEvents, eventCounter)

    }
}