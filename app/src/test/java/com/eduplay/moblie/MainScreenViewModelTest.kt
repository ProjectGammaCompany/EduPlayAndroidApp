package com.eduplay.moblie

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.ui.viewmodel.MainScreenViewModel
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

class MainScreenViewModelTest {
    @get:Rule
    val instantTestExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var repository: EduRepository

    var viewModel: MainScreenViewModel? = null

    val testContextProvider = TestContextProvider()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun test_init_view_model_unauthorizedException_unauthorised_is_true() {
        coEvery { repository.getEvents() }.throws(NotAuthorisedException())


        viewModel = MainScreenViewModel(repository, testContextProvider)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel?.noInternetConnection?.value)
        assertEquals(true, viewModel?.unauthorised?.value)
    }

    @Test
    fun test_init_view_model_connectException_noInternetConnection_is_true() {
        coEvery { repository.getEvents() }.throws(ConnectException())

        viewModel = MainScreenViewModel(repository, testContextProvider)
        testContextProvider.testCoroutineDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, viewModel?.noInternetConnection?.value)
        assertEquals(false, viewModel?.unauthorised?.value)
    }
}

class TestListCallback : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}

class TestDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: T & Any,
        newItem: T & Any
    ): Boolean {
        return oldItem == newItem
    }
}