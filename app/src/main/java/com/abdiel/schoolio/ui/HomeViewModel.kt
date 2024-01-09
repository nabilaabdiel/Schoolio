package com.abdiel.schoolio.ui

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.abdiel.schoolio.data.mapel.Mapel
import com.abdiel.schoolio.data.session.Session
import com.abdiel.schoolio.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel() {

    private val _listSubject = MutableSharedFlow<List<Mapel>>()
    val listSubject = _listSubject.asSharedFlow()

    fun listSubject() = viewModelScope.launch {
        ApiObserver(
            { apiService.indexSubject() }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Mapel>(gson)
                    _listSubject.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            })
    }

    fun logout() = viewModelScope.launch {
        ApiObserver(
            { apiService.logout() }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    session.clearUser()
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            })
    }

    fun getProfile(result: (Boolean) -> Unit) = viewModelScope.launch {
        ApiObserver(
            { apiService.getProfile() }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject("data").toObject<User>(gson)
                    session.saveUser(data)
                    result(true)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    result(false)
                }
            })
    }

}