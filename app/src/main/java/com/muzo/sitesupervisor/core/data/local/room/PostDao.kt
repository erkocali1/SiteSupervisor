package com.muzo.sitesupervisor.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePost(post: DataModel):Long

    @Query("SELECT * FROM post WHERE id = :postId")
    fun getPost(postId: Long): Flow<DataModel>

    @Update
    suspend fun updatePost(post: DataModel)

    @Query("UPDATE post SET photoUrl = :newPhotoUrl WHERE id = :postId")
    suspend fun updatePhotoUrl(postId: Long, newPhotoUrl: List<String>)

    //----------------Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTask(taskModel: TaskModel):Long







}