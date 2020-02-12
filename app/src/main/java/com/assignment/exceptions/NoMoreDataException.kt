package com.assignment.exceptions


class NoMoreDataException : BaseException {

    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}
