package com.muzo.sitesupervisor.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muzo.sitesupervisor.core.data.model.DataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePost(post: DataModel):Long

    @Query("SELECT * FROM post WHERE id = :postId")
    fun getPost(postId: Long): Flow<DataModel>


}