package com.lgtm.simple_timer.di

import android.content.Context
import androidx.room.Room
import com.lgtm.simple_timer.data.source.FooDataSource
import com.lgtm.simple_timer.data.source.FooRepository
import com.lgtm.simple_timer.data.source.FooRepositoryImpl
import com.lgtm.simple_timer.data.source.local.FooDatabase
import com.lgtm.simple_timer.data.source.local.FooLocalDataSource
import com.lgtm.simple_timer.page.timer.Timer
import com.lgtm.simple_timer.page.timer.dialtimer.CircleDialProgressCalculator
import com.lgtm.simple_timer.page.timer.dialtimer.DefaultProgressBarConfig
import com.lgtm.simple_timer.page.timer.dialtimer.ProgressBarConfig
import com.lgtm.simple_timer.page.timer.dialtimer.TouchDirectionCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
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

@Module
@InstallIn(ViewModelComponent::class)
object TimerViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideTimer(): Timer {
        return Timer(0L, 1_000L)
    }

    @ViewModelScoped
    @Provides
    fun provideProgressTimerConfig(): ProgressBarConfig {
        return DefaultProgressBarConfig()
    }

    @ViewModelScoped
    @Provides
    fun provideCircleDialProgressCalculator(): CircleDialProgressCalculator {
        return CircleDialProgressCalculator(DefaultProgressBarConfig().dialTickInfo, TouchDirectionCalculator())
    }
}