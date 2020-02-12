package com.model

import com.interfaces.IAction

data class StatusData(var status: Status? = null,
                      var action: IAction? = null,
                      var throwable: Throwable? = null,
                      var message: String? = null) {


    enum class Status {
        SUCCESS, FAIL
    }
}
