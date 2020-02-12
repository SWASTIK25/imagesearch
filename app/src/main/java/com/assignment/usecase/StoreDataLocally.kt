package com.assignment.usecase

import com.assignment.data.PhotoSearchRepository
import com.model.SearchPhotoDataModel

class StoreDataLocally(private val photoSearchRepository: PhotoSearchRepository) {
    operator fun invoke(query:String, searchPhotoDataModel: SearchPhotoDataModel)
            = photoSearchRepository.getFileInDirectoryAndStoreData(query, searchPhotoDataModel.photos?.photo)
}