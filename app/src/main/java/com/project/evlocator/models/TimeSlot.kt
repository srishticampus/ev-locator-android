package com.project.evlocator.models

data class TimeSlot(
    var dataAvailable: List<DataAvailableX>?,
    var message: String?,
    var status: Boolean?
)