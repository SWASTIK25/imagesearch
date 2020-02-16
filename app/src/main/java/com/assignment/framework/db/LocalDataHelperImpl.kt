package com.assignment.framework.db
import android.content.Context
import com.db.FlickerDatabase
import com.db.FlickerEntity
import javax.inject.Inject

class LocalDataHelperImpl @Inject constructor(context: Context) : LocalDataHelper {
    var db: FlickerDatabase? = null

    init {
        db = FlickerDatabase.getDatabase(context)
    }

    override fun insertData(query: String, webPath: String, filePath: String) {
        db?.flickerDao()?.addImages(FlickerEntity(documentUri = query, page = webPath,
                data = filePath))
    }

    override fun getAllImagePaths(query: String): List<String> {
        val imagePaths: ArrayList<String> by lazy {
            ArrayList<String>()
        }
        val flickerEntities = db?.flickerDao()?.getImagePaths()
        flickerEntities?.forEach {
            imagePaths.add(it.data)
        }
        return imagePaths
    }

    override fun getLocalImagePath(query: String, webUrl: String): String? {
        var imagePath: String?
        val flickerEntity = db?.flickerDao()?.getImagePath(flickerQuery = query, webPath = webUrl)
        imagePath = flickerEntity?.data
        return imagePath
    }

    override fun deleteAllFilesRelatedToQuery(query: String) {
            db?.flickerDao()?.removeImage(query)

    }
}