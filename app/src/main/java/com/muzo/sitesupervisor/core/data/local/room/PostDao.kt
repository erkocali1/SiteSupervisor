package com.muzo.sitesupervisor.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.muzo.sitesupervisor.core.data.model.DataModel

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePost(post: DataModel):Long

}