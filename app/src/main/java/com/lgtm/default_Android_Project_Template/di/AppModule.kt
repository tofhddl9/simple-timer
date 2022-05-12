package com.lgtm.default_Android_Project_Template.di

import android.content.Context
import androidx.room.Room
import com.lgtm.default_Android_Project_Template.data.source.FooDataSource
import com.lgtm.default_Android_Project_Template.data.source.FooRepository
import com.lgtm.default_Android_Project_Template.data.source.FooRepositoryImpl
import com.lgtm.default_Android_Project_Template.data.source.local.FooDatabase
import com.lgtm.default_Android_Project_Template.data.source.local.FooLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFooLocalDataSource(
        database: FooDatabase,
        ioDispatcher: CoroutineDispatcher
    ): FooDataSource {
        return FooLocalDataSource(database.fooDao(), ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideFooRepository(
        localTasksDataSource: FooDataSource,
        ioDispatcher: CoroutineDispatcher
    ): FooRepository {
        return FooRepositoryImpl(localTasksDataSource, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): FooDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FooDatabase::class.java,
            "Foo.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

}