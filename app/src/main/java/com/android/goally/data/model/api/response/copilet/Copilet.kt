package com.android.goally.data.model.api.response.copilet

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.getgoally.learnerapp.data.db.DateConverter

data class CopilotResponse(
    val routines: List<Routines>
)
@Entity
data class Routines(
    @PrimaryKey(autoGenerate = false)
    val _id: String,
    val name: String,
    val type: String,
    val imgURL: String,
    val scheduleV2: ScheduleV2?,
    val activities: List<Activities> = emptyList(),
    val folder: String
)

data class ScheduleV2(
    val type: String?,
    val yearlyRepeatDateValue: String?,
    val dailyRepeatValues: Map<String, List<String>>?
)

data class Activities(
    val name: String?,
    val audioUrl: String?,
    val imgUrl: String?
)
