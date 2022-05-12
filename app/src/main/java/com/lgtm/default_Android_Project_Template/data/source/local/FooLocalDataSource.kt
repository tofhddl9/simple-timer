package com.lgtm.default_Android_Project_Template.data.source.local

import com.lgtm.default_Android_Project_Template.data.source.FooDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FooLocalDataSource(
    private val fooDao: FooDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FooDataSource {
}