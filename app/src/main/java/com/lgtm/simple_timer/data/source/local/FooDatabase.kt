package com.lgtm.simple_timer.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lgtm.simple_timer.data.FooData

@Database(entities = [FooData::class], version = 1)
abstract class FooDatabase : RoomDatabase() {

    abstract fun fooDao(): FooDao

}