package com.muzo.sitesupervisor.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.muzo.sitesupervisor.core.data.model.DataModel


@Database(
    entities = [DataModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PostDataBase : RoomDatabase() {
    abstract fun getPostDAo(): PostDao

}