package com.android.goally.data.db

import androidx.room.TypeConverter
import com.android.goally.data.model.api.response.copilet.Activities
import com.android.goally.data.model.api.response.copilet.ScheduleV2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun activitiesListToString(activities: List<Activities>?): String? {
        return activities?.let {
            Gson().toJson(it)
        }
    }

    @TypeConverter
    fun stringToActivitiesList(json: String?): List<Activities>? {
        return json?.let {
            Gson().fromJson(it, object : TypeToken<List<Activities>>() {}.type)
        }
    }

    @TypeConverter
    fun fromScheduleV2ToJson(scheduleV2: ScheduleV2?): String? {
        return Gson().toJson(scheduleV2)
    }

    @TypeConverter
    fun fromJsonToScheduleV2(json: String?): ScheduleV2? {
        return Gson().fromJson(json, ScheduleV2::class.java)
    }


    @TypeConverter
    fun fromDailyRepeatValuesToJson(dailyRepeatValues: Map<String, List<String>>?): String? {
        return Gson().toJson(dailyRepeatValues)
    }

    @TypeConverter
    fun fromJsonToDailyRepeatValues(json: String?): Map<String, List<String>>? {
        return Gson().fromJson(json, object : TypeToken<Map<String, List<String>>>() {}.type)
    }

}