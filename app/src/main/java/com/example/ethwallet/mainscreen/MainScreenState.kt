package com.example.ethwallet.mainscreen

import org.bitcoinj.crypto.MnemonicCode

data class MainScreenState(
    val walletAddress:String ="",
    val mnemonicCode: String = ""
)
