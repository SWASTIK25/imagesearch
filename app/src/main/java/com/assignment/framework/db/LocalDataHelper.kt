package com.assignment.framework.db

interface LocalDataHelper {
    fun insertData(query: String, webPath: String, filePath: String)
    fun getAllImagePaths(query: String): List<String>
    fun getLocalImagePath(query: String, webUrl: String):String?
    fun deleteAllFilesRelatedToQuery(query: String)
}