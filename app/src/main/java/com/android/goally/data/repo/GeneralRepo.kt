package com.android.goally.data.repo

import com.android.goally.app.GoallyApp
import com.android.goally.data.db.dao.GeneralDao
import com.android.goally.data.model.api.response.copilet.Routines
import com.android.goally.data.network.rest.api.GeneralApi
import com.android.goally.util.AppUtil


class GeneralRepo(
    private val generalApi: GeneralApi,
    private val generalDao: GeneralDao,
) {

    suspend fun checkHealth() = generalApi.checkHealth()
    suspend fun getToken(userEmail:String) = generalApi.getToken(userEmail)
    suspend fun getCopilets(token:String) = generalApi.getCopilot(token)


    fun getAuthenticationLive() = generalDao.getAuthenticationLive()
    suspend fun getAuthentication() = generalDao.getAuthentication()

    suspend fun insertCopilets(copilets: List<Routines>) = generalDao.insertAllCopilots(copilets)
    suspend fun getAllCopilets() = generalDao.getAllCopilots()
    suspend fun getCopiletById(id:String) = generalDao.getCopilotById(id)

}