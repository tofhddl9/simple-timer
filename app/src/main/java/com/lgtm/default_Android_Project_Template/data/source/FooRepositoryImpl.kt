package com.lgtm.default_Android_Project_Template.data.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FooRepositoryImpl(
    private val fooLocalDataSource: FooDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FooRepository {

}