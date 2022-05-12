package com.lgtm.simple_timer.data.source.local

import com.lgtm.simple_timer.data.source.FooDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FooLocalDataSource(
    private val fooDao: FooDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FooDataSource {
}