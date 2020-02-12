package com.assignment.exceptions

class ApiException : Exception {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super("$MESSAGE : $message", cause)

    companion object {

        private val MESSAGE = "Unknown Exception"
    }
}

