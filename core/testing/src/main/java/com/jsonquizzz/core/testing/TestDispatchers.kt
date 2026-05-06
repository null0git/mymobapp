package com.jsonquizzz.core.testing

import com.jsonquizzz.core.common.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestAppDispatchers : AppDispatchers {
    private val testDispatcher = StandardTestDispatcher()

    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}
