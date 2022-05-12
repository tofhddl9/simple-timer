package com.lgtm.default_Android_Project_Template.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lgtm.default_Android_Project_Template.data.FooData

@Database(entities = [FooData::class], version = 1)
abstract class FooDatabase : RoomDatabase() {

    abstract fun fooDao(): FooDao

}