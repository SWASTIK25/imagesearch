package com.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FlickerDao {

    @Insert(onConflict = REPLACE)
    suspend fun addImages(flicker: FlickerEntity)

    @Query("SELECT * FROM flicker")
    suspend fun getImagePaths(): List<FlickerEntity>

    @Query("SELECT * FROM flicker WHERE `query` = :flickerQuery AND web_path =:webPath")
    suspend fun getImagePath(flickerQuery: String, webPath: String): FlickerEntity

    @Query("DELETE FROM flicker where `query`= :flickerQuery")
    suspend fun removeImage(flickerQuery: String)

}