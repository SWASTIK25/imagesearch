package com.model

data class Photos(var perpage: Int = 0,var total: String? = null,
             var pages: Int = 0,var photo: List<PhotoItem>? = null,
             var page: Int = 0)