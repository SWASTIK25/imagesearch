package com.assignment.domain.interfaces

import com.model.StatusData

interface ApiIntractor {
    /** abstract impl for handle failure for request
     */
     fun handleViewModelUpdatesOnFailure(status: StatusData?, throwable: Throwable?)
    /** abstract impl for handle success for request
     */
    fun handleViewModelUpdatesOnSuccess(status: StatusData?)
}