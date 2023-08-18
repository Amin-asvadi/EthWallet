package com.example.ethwallet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EthWalletApplication:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}