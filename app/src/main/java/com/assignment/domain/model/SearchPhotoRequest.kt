package com.assignment.domain.model

import javax.inject.Inject
import javax.inject.Named

class SearchPhotoRequest @Inject
constructor(@param:Named("flickerApiKey") var apiKey: String?, @param:Named("flickerSearchPhotoMethod") var method: String?) {
    var text: String? = null
    var pageSize: Int = 0
    var page: Int = 0
    var format: String? = null
    var noJsonCallback: Int = 0


    init {
        format = "json"
        noJsonCallback = 1
    }
}
