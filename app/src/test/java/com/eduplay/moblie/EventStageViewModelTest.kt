package com.eduplay.moblie

import android.util.Log
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.ui.viewmodel.EventStageViewmodel
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.downloadUsecases.FileDownloadStatus
import com.eduplay.moblie.useCases.managers.OfflineModeManager
import com.eduplay.moblie.useCases.downloadUsecases.TaskDownloadUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.ConnectException
import java.time.LocalDateTime

class EventStageViewModelTest {
    @MockK
    lateinit var repository: EduRepository

    @MockK
    lateinit var taskDownloader: TaskDownloadUseCase

    @MockK
    lateinit var offlineModeManager: OfflineModeManager
    val testContextProvider = TestContextProvider()

    lateinit var viewModel: EventStageViewmodel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel =
            EventStageViewmodel(repository, taskDownloader, offlineModeManager, testContextProvider)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun chooseTask_currentStageType_is_None_when_no_exceptions_are_caught_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        viewModel.currentStageType.value = StageType.BLOCK
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        coEvery { repository.postTaskChoice(eventId, blockId, taskId) }.returns(true)

        viewModel.chooseTask(eventId, taskId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.NONE, viewModel.currentStageType.value)
        assertEquals(false, viewModel.noInternet.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun chooseTask_currentStageType_is_Block_and_noInternet_is_true_when_ConnectException_is_caught_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        viewModel.currentStageType.value = StageType.BLOCK
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        coEvery { repository.postTaskChoice(eventId, blockId, taskId) }.throws(ConnectException())

        viewModel.chooseTask(eventId, taskId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.BLOCK, viewModel.currentStageType.value)
        assertEquals(true, viewModel.noInternet.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun chooseTask_currentStageType_is_Block_and_unauthorised_is_true_when_NotAuthorisedException_is_caught_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        viewModel.currentStageType.value = StageType.BLOCK
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        coEvery { repository.postTaskChoice(eventId, blockId, taskId) }.throws(
            NotAuthorisedException()
        )

        viewModel.chooseTask(eventId, taskId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.BLOCK, viewModel.currentStageType.value)
        assertEquals(false, viewModel.noInternet.value)
        assertEquals(true, viewModel.unauthorised.value)
    }

    @Test
    fun sendAnswer_currentStageType_is_Task_and_noInternet_is_true_when_ConnectException_is_caught_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        coEvery {
            repository.postAnswer(
                eventId,
                blockId,
                taskId,
                answers
            )
        }.throws(ConnectException())

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(true, viewModel.noInternet.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun sendAnswer_currentStageType_is_Task_and_unauthorised_is_true_when_NotAuthorisedException_is_caught_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        coEvery {
            repository.postAnswer(
                eventId,
                blockId,
                taskId,
                answers
            )
        }.throws(NotAuthorisedException())

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(false, viewModel.noInternet.value)
        assertEquals(true, viewModel.unauthorised.value)
    }

    @Test
    fun sendAnswer_currentStageType_is_None_and_showAnswers_is_false_when_fields_in_AnswerResult_are_all_null_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val answerResult = AnswerResult(null, null, null)
        coEvery { repository.postAnswer(eventId, blockId, taskId, answers) }.returns(answerResult)

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.NONE, viewModel.currentStageType.value)
        assertEquals(false, viewModel.showResults.value)
    }

    @Test
    fun sendAnswer_currentStageType_is_Task_and_showAnswers_is_true_and_isAnswerCorrect_is_not_null_when_isCorrect_in_AnswerResult_is_not_null_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val answerResult = AnswerResult(null, null, TaskAnswerStatus.INCORRECT)
        coEvery { repository.postAnswer(eventId, blockId, taskId, answers) }.returns(answerResult)

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(true, viewModel.showResults.value)
        assertNotEquals(null, viewModel.isAnswerCorrect)
        assertEquals(TaskAnswerStatus.INCORRECT, viewModel.isAnswerCorrect)
    }

    @Test
    fun sendAnswer_currentStageType_is_Task_and_showAnswers_is_true_and_points_is_not_null_when_points_in_AnswerResult_is_not_null_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        val points = 10
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val answerResult = AnswerResult(null, points, null)
        coEvery { repository.postAnswer(eventId, blockId, taskId, answers) }.returns(answerResult)

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(true, viewModel.showResults.value)
        assertNotEquals(null, viewModel.points)
        assertEquals(points, viewModel.points)
    }

    @Test
    fun sendAnswer_currentStageType_is_Task_and_showAnswers_is_true_and_correctAnswers_is_not_null_when_rightAnswer_in_AnswerResult_is_not_null_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        val correctAnswers = listOf("answer")
        viewModel.currentStageType.value = StageType.TASK
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val answerResult = AnswerResult(correctAnswers, null, null)
        coEvery { repository.postAnswer(eventId, blockId, taskId, answers) }.returns(answerResult)

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(true, viewModel.showResults.value)
        assertNotEquals(null, viewModel.correctAnswer)
        assertEquals(correctAnswers, viewModel.correctAnswer)
    }

    @Test
    fun sendAnswer_in_text_task_only_last_answer_is_sent_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>("answer 1", "answer 2")
        viewModel.currentStageType.value = StageType.TASK
        viewModel.answers.clear()
        viewModel.answers.addAll(answers)
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val receivedAnswers = mutableListOf<String>()
        coEvery { repository.postAnswer(eventId, blockId, taskId, any<List<String>>()) }
            .answers {
                val list = arg<List<String>>(3)
                receivedAnswers.addAll(list)
                AnswerResult(
                    rightAnswer = null,
                    points = null,
                    isCorrect = null
                )
            }

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        val expectedAnswer = listOf("answer 2")
        assertEquals(expectedAnswer, receivedAnswers)
    }

    @Test
    fun sendAnswer_in_single_choice_task_only_last_answer_is_sent_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>("answer 1", "answer 2")
        viewModel.currentStageType.value = StageType.TASK
        viewModel.answers.clear()
        viewModel.answers.addAll(answers)
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.SINGLE_CHOICE.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val receivedAnswers = mutableListOf<String>()
        coEvery { repository.postAnswer(eventId, blockId, taskId, any<List<String>>()) }
            .answers {
                val list = arg<List<String>>(3)
                receivedAnswers.addAll(list)
                AnswerResult(
                    rightAnswer = null,
                    points = null,
                    isCorrect = null
                )
            }

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        val expectedAnswer = listOf("answer 2")
        assertEquals(expectedAnswer, receivedAnswers)
    }

    @Test
    fun sendAnswer_in_QR_task_only_last_answer_is_sent_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>("answer 1", "answer 2")
        viewModel.currentStageType.value = StageType.TASK
        viewModel.answers.clear()
        viewModel.answers.addAll(answers)
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.QR.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val receivedAnswers = mutableListOf<String>()
        coEvery { repository.postAnswer(eventId, blockId, taskId, any<List<String>>()) }
            .answers {
                val list = arg<List<String>>(3)
                receivedAnswers.addAll(list)
                AnswerResult(
                    rightAnswer = null,
                    points = null,
                    isCorrect = null
                )
            }

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        val expectedAnswer = listOf("answer 2")
        assertEquals(expectedAnswer, receivedAnswers)
    }

    @Test
    fun sendAnswer_in_Multiple_choice_task_all_answers_are_sent_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>("answer 1", "answer 2")
        viewModel.currentStageType.value = StageType.TASK
        viewModel.answers.clear()
        viewModel.answers.addAll(answers)
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.MULTIPLE_CHOICE.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val receivedAnswers = mutableListOf<String>()
        coEvery { repository.postAnswer(eventId, blockId, taskId, any<List<String>>()) }
            .answers {
                val list = arg<List<String>>(3)
                receivedAnswers.addAll(list)
                AnswerResult(
                    rightAnswer = null,
                    points = null,
                    isCorrect = null
                )
            }

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(answers, receivedAnswers)
    }

    @Test
    fun sendAnswer_answers_is_empty_empty_list_is_sent_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val answers = listOf<String>()
        viewModel.currentStageType.value = StageType.TASK
        viewModel.answers.clear()
        viewModel.answers.addAll(answers)
        viewModel.currentTask.value = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.QR.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = null
        )
        viewModel.currentBlock.value = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val receivedAnswers = mutableListOf<String>()
        coEvery { repository.postAnswer(eventId, blockId, taskId, any<List<String>>()) }
            .answers {
                val list = arg<List<String>>(3)
                receivedAnswers.addAll(list)
                AnswerResult(
                    rightAnswer = null,
                    points = null,
                    isCorrect = null
                )
            }

        viewModel.sendAnswer(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(answers, receivedAnswers)
    }

    @Test
    fun getNextStage_noInternet_is_true_when_ConnectException_is_caught_test() {
        val eventId = "event"

        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.throws(ConnectException())

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.NONE, viewModel.currentStageType.value)
        assertEquals(true, viewModel.noInternet.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun getNextStage_unauthorised_is_true_when_NotAuthorisedException_is_caught_test() {
        val eventId = "event"

        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.throws(NotAuthorisedException())

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.NONE, viewModel.currentStageType.value)
        assertEquals(false, viewModel.noInternet.value)
        assertEquals(true, viewModel.unauthorised.value)
    }

    @Test
    fun getNextStage_task_is_copied_when_no_exceptions_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val task = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(),
            time = 0,
            timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
        )
        val stage = EventStage(
            type = StageType.TASK.stageName,
            task = task,
            block = null
        )
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }.returns(true)
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.ONLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.TASK, viewModel.currentStageType.value)
        assertEquals(task, viewModel.currentTask.value)
        assertEquals(null, viewModel.currentBlock.value)
        assertEquals(false, viewModel.unauthorised.value)
        assertEquals(false, viewModel.noInternet.value)
    }

    @Test
    fun getNextStage_block_is_copied_when_no_exceptions_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val block = Block(
            id = blockId,
            name = blockId,
            tasks = listOf()
        )
        val stage = EventStage(
            type = StageType.BLOCK.stageName,
            task = null,
            block = block
        )
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }.returns(true)
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.ONLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StageType.BLOCK, viewModel.currentStageType.value)
        assertEquals(null, viewModel.currentTask.value)
        assertEquals(block, viewModel.currentBlock.value)
        assertEquals(false, viewModel.unauthorised.value)
        assertEquals(false, viewModel.noInternet.value)
    }

    @Test
    fun getNextStage_online_mode_files_dont_have_status_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val task = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = listOf(Task.TaskFile("file 1", "file 1"), Task.TaskFile("file 2", "file 2")),
            time = 0,
            timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
        )
        val stage = EventStage(
            type = StageType.TASK.stageName,
            task = task,
            block = null
        )
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }.returns(true)
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.ONLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.fileStatusFlows.isEmpty())
    }

    @Test
    fun getNextStage_offline_mode_files_have_success_download_status_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val files = listOf(Task.TaskFile("file 1", "file 1"), Task.TaskFile("file 2", "file 2"))
        val task = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = files,
            time = 0,
            timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
        )
        val stage = EventStage(
            type = StageType.TASK.stageName,
            task = task,
            block = null
        )
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }.returns(true)
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.OFFLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        runBlocking {
            assertEquals(files.size, viewModel.fileStatusFlows.size)
            for (file in files) {
                assertEquals(
                    FileDownloadStatus.SUCCESS,
                    viewModel.fileStatusFlows[file.url]?.first()
                )
            }
        }
    }

    @Test
    fun getNextStage_start_time_is_sent_when_timestamp_is_null_and_stage_type_is_task_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val files = listOf(Task.TaskFile("file 1", "file 1"), Task.TaskFile("file 2", "file 2"))
        val task = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = files,
            time = 0,
            timeStamp = null
        )
        val stage = EventStage(
            type = StageType.TASK.stageName,
            task = task,
            block = null
        )
        val now = LocalDateTime.now()
        var gotTime: LocalDateTime? = null
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }
            .answers {
                gotTime = arg<LocalDateTime>(3)
                true
            }
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.ONLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertTrue(now.isBefore(gotTime))
    }

    @Test
    fun getNextStage_start_time_is_not_sent_when_timestamp_is_not_null_and_stage_type_is_task_test() {
        val eventId = "event"
        val taskId = "task"
        val blockId = "block"
        val files = listOf(Task.TaskFile("file 1", "file 1"), Task.TaskFile("file 2", "file 2"))
        val task = Task(
            id = taskId,
            blockId = blockId,
            name = taskId,
            description = taskId,
            type = TaskType.TEXT.optionNumber,
            options = null,
            files = files,
            time = 0,
            timeStamp = LocalDateTime.now().toString()
        )
        val stage = EventStage(
            type = StageType.TASK.stageName,
            task = task,
            block = null
        )
        var gotTime: LocalDateTime? = null
        viewModel.currentStageType.value = StageType.NONE
        coEvery { repository.getNextStage(eventId) }.returns(stage)
        coEvery { repository.postTaskStartTime(eventId, blockId, taskId, any()) }
            .answers {
                gotTime = arg<LocalDateTime>(3)
                true
            }
        every { offlineModeManager.getAppMode() }.returns(flowOf(OfflineModeManager.AppModes.ONLINE))

        viewModel.getNextStage(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, gotTime)
    }


}