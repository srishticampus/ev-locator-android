package com.project.evlocator

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {


    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "userId"
        private const val KEY_PHONE_NUMBER = "phone"
        private const val KEY_STATION_ID = "station_id"
        private const val CHOSEN_DATE = "chosen_date"
        private const val CLICKED_STATUS = "clicked_status"
        private const val CHARGER_NAME = "charger_name"
        private const val TIME_SLOT = "time_slot"
        private const val SELECTED_BOOKING_ID = "booking_id"
        private const val LOGIN_STATUS = "loginStatus"
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun savePhoneNumber(phone: String) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phone).apply()
    }

    fun saveStationId(stationId: String) {
        sharedPreferences.edit().putString(KEY_STATION_ID, stationId).apply()
    }

    fun saveChosenDate(chosenDate: String) {
        sharedPreferences.edit().putString(CHOSEN_DATE, chosenDate).apply()
    }

    fun saveChoosenCharger(chargerName: String) {
        sharedPreferences.edit().putString(CHARGER_NAME, chargerName).apply()
    }

    fun saveChoosenTime(timeSlot: String) {
        sharedPreferences.edit().putString(TIME_SLOT, timeSlot).apply()
    }

    fun saveBookingId(bookingId: String) {
        sharedPreferences.edit().putString(SELECTED_BOOKING_ID, bookingId).apply()
    }

    fun saveLoginStatus(loginStatus: String) {
        sharedPreferences.edit().putString(LOGIN_STATUS, loginStatus).apply()
    }

    fun getLoginStatus(): String {
        return sharedPreferences.getString(LOGIN_STATUS, "") ?: ""
    }

    fun getBookingId(): String {
        return sharedPreferences.getString(SELECTED_BOOKING_ID, "") ?: ""
    }

    fun getChoosenTime(): String {
        return sharedPreferences.getString(TIME_SLOT, "") ?: ""
    }

    fun getChoosenCharegr(): String {
        return sharedPreferences.getString(CHARGER_NAME, "") ?: ""
    }

    fun getChosenDate(): String {
        return sharedPreferences.getString(CHOSEN_DATE, "") ?: ""
    }

    fun getStationId(): String {
        return sharedPreferences.getString(KEY_STATION_ID, "") ?: ""
    }

    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }

    fun getPhoneNumber(): String {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, "") ?: ""
    }

}