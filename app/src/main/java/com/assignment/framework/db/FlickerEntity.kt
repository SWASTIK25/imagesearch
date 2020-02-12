package com.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flicker")
data class FlickerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "query") val documentUri: String,
    @ColumnInfo(name = "web_path") val page: String,
    @ColumnInfo(name = "data") val data:String
)