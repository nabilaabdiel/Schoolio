package com.abdiel.schoolio.ui.main

import androidx.lifecycle.viewModelScope
import com.abdiel.schoolio.base.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): BaseViewModel() {
    fun splash(done: (Boolean) -> Unit) = viewModelScope.launch {
        delay(1000)
        done (true)
    }
}