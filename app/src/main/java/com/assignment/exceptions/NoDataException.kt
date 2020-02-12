package com.assignment.exceptions

class NoDataException : BaseException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}
