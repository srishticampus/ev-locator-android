package com.project.evlocator.models

data class PaymentDetail(
    var amount_paid: String?,
    var booking_id: String?,
    var final_amount: String?,
    var id: String?,
    var pending_amount: String?
)