package com.eduplay.moblie

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
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

class EventListViewModelTest {

    @get:Rule
    val instantTestExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var repository: EduRepository

    lateinit var viewModel: EventListViewModel

    val testContextProvider = TestContextProvider()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = EventListViewModel(repository, testContextProvider)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun test_changeFavourite() {
        val eventId = "id"
        val isFavorite = false
        coEvery { repository.addToFavourites(eventId, isFavorite) }.returns(true)

        viewModel.changeFavourite(eventId, isFavorite)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
        assertEquals(false, viewModel.unknownError.value)
    }

    @Test
    fun test_changeFavourite_unauthorizedException_unauthorised_is_true() {
        val eventId = "id"
        val isFavorite = false
        coEvery { repository.addToFavourites(eventId, isFavorite) }.throws(NotAuthorisedException())


        viewModel.changeFavourite(eventId, isFavorite)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(true, viewModel.unauthorised.value)
        assertEquals(false, viewModel.unknownError.value)
    }

    @Test
    fun test_changeFavourite_connectException_noInternetConnection_is_true() {
        val eventId = "id"
        val isFavorite = false
        coEvery { repository.addToFavourites(eventId, isFavorite) }.throws(ConnectException())

        viewModel.changeFavourite(eventId, isFavorite)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
        assertEquals(false, viewModel.unknownError.value)
    }

    @Test
    fun test_changeFavourite_Exception_unknownError_is_true() {
        val eventId = "id"
        val isFavorite = false
        coEvery { repository.addToFavourites(eventId, isFavorite) }.throws(RuntimeException())

        viewModel.changeFavourite(eventId, isFavorite)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel.noInternetConnection.value)
        assertEquals(false, viewModel.unauthorised.value)
        assertEquals(true, viewModel.unknownError.value)
    }
}