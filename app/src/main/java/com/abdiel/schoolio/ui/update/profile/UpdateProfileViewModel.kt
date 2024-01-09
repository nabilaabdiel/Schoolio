package com.abdiel.schoolio.ui.update.profile

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.abdiel.schoolio.data.session.Session
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session
) : BaseViewModel() {

    fun updateProfile(name: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.editProfile(name) }, false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("profile updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

    fun updateProfileImg(name: String, photo: File) = viewModelScope.launch {
        val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)

        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.editProfileImg(name, filePart) }, false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("profile updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }
}