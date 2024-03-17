package com.project.evlocator.models

data class RootX(
    var amenitiesDetails: List<AmenitiesDetail?>?,
    var message: String?,
    var stationDetails: StationDetails?,
    var status: Boolean?
)