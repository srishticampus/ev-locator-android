package com.project.evlocator.models

data class Root(
    val message: String,
    val status: Boolean,
    val userData: List<UserData>,
    var stationDetails: List<StationDetail?>?,
)