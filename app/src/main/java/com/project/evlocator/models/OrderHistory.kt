package com.project.evlocator.models

data class OrderHistory(
    var message: String?,
    var stationDetails: List<StationDetailX>?,
    var status: Boolean?
)