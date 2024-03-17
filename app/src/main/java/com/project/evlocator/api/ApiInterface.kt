package com.project.evlocator.api

import com.project.evlocator.Utils.SECRET_KEY
import com.project.evlocator.models.Charging
import com.project.evlocator.models.CustomerModel
import com.project.evlocator.models.DetailedHistory
import com.project.evlocator.models.Faq
import com.project.evlocator.models.OrderHistory
import com.project.evlocator.models.PaymentDetails
import com.project.evlocator.models.PaymentIntentModel
import com.project.evlocator.models.Rating
import com.project.evlocator.models.Root
import com.project.evlocator.models.RootX
import com.project.evlocator.models.RootXX
import com.project.evlocator.models.TimeSlot
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query


interface ApiInterface {

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer(): Response<CustomerModel>

    @Headers(
        "Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2023-10-16"
    )
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ): Response<CustomerModel>

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: String,
        @Query("currency") currency: String = "inr",
        @Query("automatic_payment_methods[enabled]") automaticPayment: Boolean = true

    ): Response<PaymentIntentModel>

    @GET("user_registration.php")
    suspend fun userReg(
        @Query("name") name: String,
        @Query("phone") phone: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<Root>

    @GET("login.php")
    suspend fun userLogin(
        @Query("phone") phone: String,
        @Query("password") password: String,
        @Query("device_token") deviceToken: String
    ): Response<Root>

    @Multipart
    @POST("edit_user_profile.php")
    suspend fun updateUserProfile(
        @PartMap params: HashMap<String?, RequestBody?>,
        @Part image: MultipartBody.Part,
    ): Response<Root>

    @GET("user_profile.php")
    suspend fun viewUserProfile(
        @Query("id") userId: String
    ): Response<Root>

    @GET("view_station_with_latandlong.php")
    suspend fun viewStations(
        @Query("lattitude") latitude: String,
        @Query("longitude") longitude: String
    ): Response<Root>

    @GET("view_station_by_stationid.php")
    suspend fun viewStationDetails(
        @Query("station_id") stationId: String
    ): Response<RootX>

    @GET("list_charger.php")
    suspend fun availableChargers(
        @Query("station_id") stationId: String,
        @Query("date") date: String
    ): Response<RootXX>

    @GET("timeslot.php")
    suspend fun availableTimeSlot(
        @Query("charger_name") chargerName: String,
        @Query("station_id") stationId: String,
        @Query("date") date: String,
    ): Response<TimeSlot>

    @GET("booking.php")
    suspend fun bookingApi(
        @Query("userid") userId: String,
        @Query("station_id") stationId: String,
        @Query("date") date: String,
        @Query("charger") charger: String,
        @Query("slot") slotTiming: String,
    ): Response<TimeSlot>

    @GET("view_history.php")
    suspend fun orderHistory(
        @Query("userid") userId: String
    ): Response<OrderHistory>

    @GET("order_details.php")
    suspend fun detailedOrderHistory(
        @Query("userid") userId: String,
        @Query("booking_id") bookingId: String
    ): Response<DetailedHistory>

    @GET("status_update.php")
    suspend fun startCharging(
        @Query("booking_id") bookingId: String,
        @Query("start_time") startTime: String
    ): Response<Charging>

    @GET("charging_complete.php")
    suspend fun stopCharging(
        @Query("booking_id") bookingId: String,
        @Query("stop_time") startTime: String
    ): Response<Charging>

    @GET("search_station.php")
    suspend fun searchStation(
        @Query("station_name") search: String
    ): Response<Root>

    @GET("payment_details.php")
    suspend fun paymentDetails(
        @Query("booking_id") bookingId: String
    ): Response<PaymentDetails>

    @GET("payment_completed.php")
    suspend fun balancePay(
        @Query("booking_id") bookingId: String
    ): Response<Root>

    @GET("view_faq.php")
    suspend fun faqApi():Response<Faq>

    @POST("rating.php")
    @FormUrlEncoded
    suspend fun addRating(@FieldMap params: HashMap<String?, String?>):Response<Rating>

}