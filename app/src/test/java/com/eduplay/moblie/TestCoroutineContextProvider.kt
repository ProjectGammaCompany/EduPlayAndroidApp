package com.eduplay.moblie

import com.eduplay.moblie.utils.CoroutineContextProvider
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.coroutines.CoroutineContext

class TestContextProvider: CoroutineContextProvider() {
    val testCoroutineDispatcher = StandardTestDispatcher()
    override val Main: CoroutineContext = testCoroutineDispatcher
    override val IO: CoroutineContext = testCoroutineDispatcher
}