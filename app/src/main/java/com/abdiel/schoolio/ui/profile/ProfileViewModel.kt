package com.abdiel.schoolio.ui.profile

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.abdiel.schoolio.data.session.Session
import com.abdiel.schoolio.data.user.User
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    fun getProfile(result: (Boolean)-> Unit) = viewModelScope.launch {
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
}