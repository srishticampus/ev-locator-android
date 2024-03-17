package com.project.evlocator.models

data class DataAvailableX(
    var charger_id: String?,
    var charger_name: String?,
    var timeslot: List<TimeslotX>?
)