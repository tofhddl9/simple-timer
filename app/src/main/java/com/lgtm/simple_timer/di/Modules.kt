package com.lgtm.simple_timer.di

import android.content.Context
import com.lgtm.simple_timer.data.source.TimerDataSource
import com.lgtm.simple_timer.data.source.TimerRepository
import com.lgtm.simple_timer.data.source.TimerRepositoryImpl
import com.lgtm.simple_timer.data.source.local.TimerLocalDataSource
import com.lgtm.simple_timer.page.timer.Timer
import com.lgtm.simple_timer.page.timer.TimerSharedPreference
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTimerLocalDataSource(
        timerSharedPreference: TimerSharedPreference,
    ): TimerDataSource {
        return TimerLocalDataSource(timerSharedPreference)
    }

    @Singleton
    @Provides
    fun provideTimerRepository(
        localTimerDataSource: TimerDataSource,
    ): TimerRepository {
        return TimerRepositoryImpl(localTimerDataSource)
    }

    @Singleton
    @Provides
    fun provideTimerSharedPreference(@ApplicationContext context: Context): TimerSharedPreference {
        return TimerSharedPreference(context)
    }

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