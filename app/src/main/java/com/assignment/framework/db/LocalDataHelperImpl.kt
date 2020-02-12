package com.assignment.framework.db

import android.content.Context
import com.db.FlickerDatabase
import com.db.FlickerEntity
import kotlinx.coroutines.*
import javax.inject.Inject

class LocalDataHelperImpl @Inject constructor(context: Context): LocalDataHelper{
    var db: FlickerDatabase? = null

    init {
        db = FlickerDatabase.getDatabase(context)
    }

    override fun insertData(query: String, webPath: String, filePath: String) {
        GlobalScope.launch {
            db?.flickerDao()?.addImages(FlickerEntity(documentUri = query, page = webPath,
                    data = filePath))
        }
    }

    override fun getAllImagePaths(query: String): List<String> {
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

     override fun getLocalImagePath(query: String, webUrl: String): String? {
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

    override fun deleteAllFilesRelatedToQuery(query: String) {
        GlobalScope.launch {
            db?.flickerDao()?.removeImage(query)
        }

    }

}