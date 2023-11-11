package com.abdiel.schoolio.ui.login

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.data.session.Session
import com.abdiel.schoolio.data.user.User
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    fun login(email_or_phone: String, password: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.login( email_or_phone, password) }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    val token = response.getJSONObject(ApiCode.DATA).getJSONObject("token").getString("access_token")
                    session.saveUser(data)

                    session.setValue(Const.USER.PASSWORD, password)
                    session.setValue(Const.USER.PROFILE, "Login")
                    session.setValue(Const.TOKEN.PREF_TOKEN, token)

                    _apiResponse.emit(ApiResponse().responseSuccess("Logging in"))
                }
            })
    }
}