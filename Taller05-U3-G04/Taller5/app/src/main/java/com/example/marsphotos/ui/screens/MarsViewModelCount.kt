/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.network.MarsApiCount
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface MarsUiStateCount {
    data class SuccessCount(val photos: String) : MarsUiStateCount
    object ErrorCount : MarsUiStateCount
    object LoadingCount : MarsUiStateCount
}

class MarsViewModelCount : ViewModel() {
    var marsUiStateCount: MarsUiStateCount by mutableStateOf(MarsUiStateCount.LoadingCount)
        private set

    init {
        getMarsPhotosCount()
    }

    fun getMarsPhotosCount() {
        viewModelScope.launch {
            marsUiStateCount = MarsUiStateCount.LoadingCount
            marsUiStateCount = try {
                val listResult = MarsApiCount.retrofitServiceCount.getPhotosCount()
                MarsUiStateCount.SuccessCount(
                    "Éxito: se recuperaron ${listResult.size} fotos de Marte"
                )
            } catch (e: IOException) {
                MarsUiStateCount.ErrorCount
            } catch (e: HttpException) {
                MarsUiStateCount.ErrorCount
            }
        }
    }
}
