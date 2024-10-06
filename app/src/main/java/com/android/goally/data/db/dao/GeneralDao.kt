package com.android.goally.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.goally.data.db.entities.token.Authentication
import com.android.goally.data.model.api.response.copilet.Routines


@Dao
interface GeneralDao {
    @Query("Select * from authentication")
    fun getAuthenticationLive(): LiveData<Authentication?>
    @Query("Select * from authentication")
    suspend fun getAuthentication(): Authentication?


    @Query("SELECT * FROM routines")
    suspend fun getAllCopilots(): List<Routines>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCopilots(copilots: List<Routines>)

    @Query("SELECT * FROM routines WHERE _id = :id")
    suspend fun getCopilotById(id: String): Routines?


}