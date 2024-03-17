package com.project.evlocator.models

data class PaymentDetails(
    var message: String?,
    var paymentDetails: List<PaymentDetail?>?,
    var status: Boolean?
)