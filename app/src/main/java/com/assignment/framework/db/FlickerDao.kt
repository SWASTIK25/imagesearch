package com.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FlickerDao {

    @Insert(onConflict = REPLACE)
    fun addImages(flicker: FlickerEntity)

    @Query("SELECT * FROM flicker")
    fun getImagePaths(): List<FlickerEntity>

    @Query("SELECT * FROM flicker WHERE `query` = :flickerQuery AND web_path =:webPath")
    fun getImagePath(flickerQuery: String, webPath: String): FlickerEntity

    @Query("DELETE FROM flicker where `query`= :flickerQuery")
    fun removeImage(flickerQuery: String)

}