package com.example.ethwallet.mainscreen

import com.example.ethwallet.utils.AsyncResult
import com.example.ethwallet.utils.Uninitialized

data class MainScreenState(
    val walletAddress:String ="",
    val mnemonicCode: String = "",
    val privayeKey :String = "",
    val generateAddressResponse: AsyncResult<Pair<String,String>> = Uninitialized,
)
