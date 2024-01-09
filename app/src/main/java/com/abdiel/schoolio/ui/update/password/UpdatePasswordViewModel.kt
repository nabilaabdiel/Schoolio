package com.abdiel.schoolio.ui.update.password

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    fun changePassword(new_password: String, password_confirmation: String) =
        viewModelScope.launch {
            _apiResponse.emit(ApiResponse().responseLoading())
            ApiObserver({ apiService.changePassword(new_password, password_confirmation) },
                false,
                object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val message = response.getString(ApiCode.MESSAGE)
                        _apiResponse.emit(ApiResponse().responseSuccess(message))
                    }
                })
        }
}