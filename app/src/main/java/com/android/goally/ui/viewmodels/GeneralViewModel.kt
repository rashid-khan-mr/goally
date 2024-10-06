package com.android.goally.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.goally.app.GoallyApp
import com.android.goally.constants.FilterOption
import com.android.goally.data.model.api.response.copilet.Activities
import com.android.goally.data.model.api.response.copilet.Routines
import com.android.goally.data.model.api.response.copilet.ScheduleV2
import com.android.goally.data.repo.GeneralRepo
import com.android.goally.util.AppUtil
import com.android.goally.util.LogUtil
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneralViewModel @Inject constructor(
    private val generalRepo: GeneralRepo
) : ViewModel() {

    private val _copilotList = MutableLiveData<List<Routines>>()
    val copilotList: LiveData<List<Routines>> = _copilotList


    fun getCopiletDetails(
        id:String,
        success:(copilet:Routines) -> Unit
        ){
        viewModelScope.launch {
            val response = generalRepo.getCopiletById(id)
            response?.let{
                success(response)
            }
        }
    }


    fun fetchCopilot(
        token: String,
        onLoading: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {

        onLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!AppUtil.isInternetAvailable(GoallyApp.applicationContext())){
                    when(val res =generalRepo.getCopilets(token)) {
                        is NetworkResponse.Success -> {
                           // Log.v("response copilet ", "" + res.body.toString())

                            generalRepo.insertCopilets(res.body.routines)
                            withContext(Dispatchers.Main){
                                _copilotList.value =(res.body.routines)
                                onLoading(false)
                            }

                        }

                        is NetworkResponse.ServerError -> {
                            LogUtil.e(res.code.toString())
                            LogUtil.e(res.body?.message)
                            getCopiletFromDb().let {
                                _copilotList.postValue(it)
                            }
                            withContext(Dispatchers.Main) {
                               // onLoading(false)
                                onError("Internet Error")
                            }
                        }

                        is NetworkResponse.NetworkError -> {
                            res.error.printStackTrace()
                            getCopiletFromDb().let {
                                _copilotList.postValue(it)
                            }
                            withContext(Dispatchers.Main) {
                              //  onLoading(false)
                                getCopiletFromDb()
                                onError("Server Error")
                            }

                        }

                        is NetworkResponse.UnknownError -> {
                            res.error.printStackTrace()
                            getCopiletFromDb().let {
                                _copilotList.postValue(it)
                            }
                            withContext(Dispatchers.Main) {
                             //   onLoading(false)
                                onError("Internet Error")
                        }
                        }
                    }
                }else{
                    getCopiletFromDb().let {
                        _copilotList.postValue(it)
                    }
                    withContext(Dispatchers.Main) {
                        onLoading(false)
                        onError("Internet Error")
                    }
                }
            } catch (e: Exception) {
                getCopiletFromDb().let {
                    _copilotList.postValue(it)
                }
                withContext(Dispatchers.Main) {
                    onLoading(false)
                    onError("Internet Error")
                }
            }
        }
    }
    fun filterFolder(
        filterOption: FilterOption,
        folder: String,
        filteredCopilet:(coiletList: List<Routines>)-> Unit,
        visibilityFilterTitle:(shouldVisible:Boolean)-> Unit

    ) {
        when(filterOption){
            FilterOption.SCHEDULE -> {
                if (folder.equals("All", ignoreCase = true)){
                    copilotList.value?.let{
                        filteredCopilet(it)
                    }
                    visibilityFilterTitle(false)
                }
                else{
                    copilotList.value?.filter {
                        it.scheduleV2?.type.equals(folder, ignoreCase = true)
                    }?.let { list ->
                        filteredCopilet(list)
                    }
                    visibilityFilterTitle(true)

                }
            }
            FilterOption.FOLDER ->{
                if (folder.equals("All Folders", ignoreCase = true)){
                    copilotList.value?.let{
                        filteredCopilet(it)
                    }
                    visibilityFilterTitle(false)
                }
                else{
                    copilotList.value?.filter {
                        it.folder.equals(folder, ignoreCase = true)
                    }?.let { list->
                        filteredCopilet(list)
                    }
                    visibilityFilterTitle(true)

                }
            }


        }



    }


    fun checkServerHealth(
        onLoading: (Boolean) -> Unit,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit) {
        onLoading(true)
        viewModelScope.launch {
            when (val res = generalRepo.checkHealth()) {
                is NetworkResponse.Success -> {
                    LogUtil.i(res.body.toString())
                    if(res.body?.status.equals("ok", true)) {
                        onSuccess("Server is up")
                    } else {
                        onError("Server is down")
                    }
                    onLoading(false)
                }

                is NetworkResponse.ServerError -> {
                    LogUtil.e(res.code.toString())
                    LogUtil.e(res.body?.message)
                    onError(res.body?.message ?: "Server error")
                    onLoading(false)
                }

                is NetworkResponse.NetworkError -> {
                    res.error.printStackTrace()
                    onError(res.error.message ?: "Network error")
                    onLoading(false)
                }

                is NetworkResponse.UnknownError -> {
                    res.error.printStackTrace()
                    onError(res.error.message ?: "Unknown error")
                    onLoading(false)
                }
            }
        }
    }

    fun getTokenFor(userEmail:String,
        onLoading: (Boolean) -> Unit,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit) {
        onLoading(true)
        viewModelScope.launch {
            when (val res = generalRepo.getToken(userEmail)) {
                is NetworkResponse.Success -> {
                    LogUtil.i(res.body.toString())
                    res.body?.let {
                        if(!it.token.isNullOrEmpty() && !it.name.isNullOrEmpty()){
                            //save token here which will be used for further api calls
                            onSuccess(it.token.toString())
                        }
                    }?:run {
                        onError("Something went wrong")
                    }
                    onLoading(false)
                }

                is NetworkResponse.ServerError -> {
                    LogUtil.e(res.code.toString())
                    LogUtil.e(res.body?.message)
                    onError(res.body?.message ?: "Server error")
                    onLoading(false)
                }

                is NetworkResponse.NetworkError -> {
                    res.error.printStackTrace()
                    onError(res.error.message ?: "Network error")
                    onLoading(false)
                }

                is NetworkResponse.UnknownError -> {
                    res.error.printStackTrace()
                    onError(res.error.message ?: "Unknown error")
                    onLoading(false)
                }
            }
        }
    }

    suspend fun getCopiletFromDb() = generalRepo.getAllCopilets()



    fun getAuthenticationLive() = generalRepo.getAuthenticationLive()
    suspend fun getAuthentication() = generalRepo.getAuthentication()
}