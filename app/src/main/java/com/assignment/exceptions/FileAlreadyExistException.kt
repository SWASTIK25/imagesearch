package com.assignment.exceptions

class FileAlreadyExistException : BaseException {
    constructor() {

    }

    constructor(message: String) : super("$MESSAGE : $message") {}

    constructor(message: String, cause: Throwable) : super("$MESSAGE : $message", cause) {}

    companion object {

        private val MESSAGE = "File already exits."
    }
}
