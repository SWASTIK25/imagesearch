package com.assignment.usecase

import com.assignment.data.PhotoSearchRepository
import com.assignment.domain.model.SearchPhotoRequest

class DeletePhotos(private val photoSearchRepository: PhotoSearchRepository) {
    operator fun invoke(searchPhotoRequest: SearchPhotoRequest)
            =photoSearchRepository.searchPhotos(searchPhotoRequest)
}