package com.abdiel.schoolio.ui.detailAssightment

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.api.ApiService
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import com.abdiel.schoolio.data.assignment.ByIdMapel
import com.abdiel.schoolio.data.mapel.Assignment
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailAssignmentViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
) : BaseViewModel() {

    private val _byIdSubject = MutableSharedFlow<Assignment>()
    val byIdSubject = _byIdSubject.asSharedFlow()


    fun byIdAssignment(id : String) = viewModelScope.launch {
        ApiObserver(
            { apiService.byIdAssignment(id) }, false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
//                    val data = response.getJSONObject(ApiCode.DATA).getJSONArray("assignment").toList<Assignment>(gson)
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Assignment>(gson)
                    _byIdSubject.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            })
    }


}