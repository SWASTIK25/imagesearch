package com.assignment.data.database

import android.content.Context
import com.db.FlickerDatabase
import com.db.FlickerEntity
import kotlinx.coroutines.*
import javax.inject.Inject

class LocalDataHelper @Inject constructor(context: Context) {
    var db: FlickerDatabase? = null

    init {
        db = FlickerDatabase.getDatabase(context)
    }

    fun insertData(query: String, webPath: String, filePath: String) {
        GlobalScope.launch {
            db?.flickerDao()?.addImages(FlickerEntity(documentUri = query, page = webPath,
                    data = filePath))
        }
    }

    fun getAllImagePaths(query: String): List<String> {
        val imagePaths: ArrayList<String> by lazy {
            ArrayList<String>()
        }
        GlobalScope.launch {
            val flickerEntities = db?.flickerDao()?.getImagePaths()
            val imagePaths = ArrayList<String>()
            flickerEntities?.forEach {
                imagePaths.add(it.data)
            }
        }
        return imagePaths
    }

     fun getLocalImagePath(query: String, webUrl: String): String? {
         val args = arrayOf(query, webUrl)
        var imagePath: String? = ""
        runBlocking {
            imagePath = withContext(Dispatchers.Default) {
                val flickerEntity = db?.flickerDao()?.getImagePath(flickerQuery = query,webPath=webUrl)
                flickerEntity?.data
            }
        }
        return imagePath
    }

    fun deleteAllFilesRelatedToQuery(query: String) {
        GlobalScope.launch {
            db?.flickerDao()?.removeImage(query)
        }

    }

}