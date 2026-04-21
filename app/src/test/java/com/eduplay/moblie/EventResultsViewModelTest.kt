package com.eduplay.moblie

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.ui.viewmodel.EventResultsViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.ConnectException

class EventResultsViewModelTest {
    @get:Rule
    val instantTestExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var repository: EduRepository

    lateinit var viewModel: EventResultsViewModel

    val testContextProvider = TestContextProvider()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = EventResultsViewModel(repository, testContextProvider)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun test_fetchResults_unauthorizedException_unauthorised_is_true() {
        val eventId = "id"
        coEvery { repository.getEventResults(eventId) }.throws(NotAuthorisedException())


        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(true, viewModel.unauthorised.value)
    }

    @Test
    fun test_fetchResults_connectException_noInternetConnection_is_true() {
        val eventId = "id"
        coEvery { repository.getEventResults(eventId) }.throws(ConnectException())

        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun test_fetchResults_stats_users_are_copied_to_users() {
        val eventId = "id"
        val users = listOf(
            PlayerStats.StatUser(
                id = "user 1",
                username = "user 1",
                avatar = "avatar",
                points = 10
            )
        )
        val statsWithUsers = PlayerStats(
            fullStats = true,
            groupEvent = false,
            users = users,
            groups = null
        )
        coEvery { repository.getEventResults(eventId) }.returns(statsWithUsers)


        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(users, viewModel.users.toList())
        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun test_fetchResults_users_empty_when_stats_users_equal_null() {
        val eventId = "id"
        val statsWithUsers = PlayerStats(
            fullStats = true,
            groupEvent = false,
            users = null,
            groups = null
        )
        coEvery { repository.getEventResults(eventId) }.returns(statsWithUsers)


        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, viewModel.users.isEmpty())
        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun test_fetchResults_stats_groups_are_copied_to_groups() {
        val eventId = "id"
        val users = listOf(
            PlayerStats.StatUser(
                id = "user 1",
                username = "user 1",
                avatar = "avatar",
                points = 10
            )
        )
        val groups = listOf(
            PlayerStats.StatGroup(
                id = "user 1",
                name = "group 1",
                users = users,
            )
        )
        val statsWithUsers = PlayerStats(
            fullStats = true,
            groupEvent = true,
            users = null,
            groups = groups
        )
        coEvery { repository.getEventResults(eventId) }.returns(statsWithUsers)

        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(groups, viewModel.groups.toList())
        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
    }

    @Test
    fun test_fetchResults_groups_empty_when_stats_groups_equal_null() {
        val eventId = "id"
        val statsWithUsers = PlayerStats(
            fullStats = true,
            groupEvent = false,
            users = null,
            groups = null
        )
        coEvery { repository.getEventResults(eventId) }.returns(statsWithUsers)


        viewModel.fetchResults(eventId)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, viewModel.groups.isEmpty())
        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
    }


}