package com.example.ethwallet.utils

import kotlinx.coroutines.flow.SharedFlow

data class NetworkErrorData(
    val text: Int,
    val isError: Boolean,
    val isWarning: Boolean = false,
    val message: String? = null
)

interface NetworkErrorHandler {
    val event: SharedFlow<NetworkErrorData>
    fun emitEvent(networkErrorData: NetworkErrorData)
}