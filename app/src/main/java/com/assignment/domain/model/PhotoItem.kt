package com.model

data class PhotoItem(var owner: String? = null,var server: String? = null,
                var ispublic: Int = 0,var isfriend: Int = 0,
                var farm: Int = 0,var id: String? = null,var secret: String? = null,
                var title: String? = null,var isfamily: Int = 0,var photoUrl: String? = null)