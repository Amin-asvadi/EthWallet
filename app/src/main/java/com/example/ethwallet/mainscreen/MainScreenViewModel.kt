package com.example.ethwallet.mainscreen

import androidx.lifecycle.viewModelScope
import com.example.ethwallet.usecase.GenerateEthAddressFromMnemonicUseCase
import com.example.ethwallet.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.bitcoinj.core.ECKey
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.MnemonicException
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val generateEthAddressFromMnemonicUseCase: GenerateEthAddressFromMnemonicUseCase
) : BaseViewModel<MainScreenState, MainScreenAction>(
    MainScreenState()
) {

    init {
        onEachAction { action ->
            when (action) {
                is MainScreenAction.GenerateAddress -> generateEthAddressFromMnemonic(
                    generateRandomMnemonic()
                )

                else -> throw IllegalArgumentException("unknown action :$action")
            }
        }
        onAsyncResult(
            MainScreenState::generateAddressResponse,
            onSuccess = {
                setState {
                    copy(
                        walletAddress = it.first,
                        privayeKey = it.second,
                        mnemonicCode = generateRandomMnemonic()
                    )
                }
            },
            onFail = {
                val response = it.message
                println(response)
            }
        )
    }

    private fun generateEthAddressFromMnemonic(mnemonic: String) {
        viewModelScope.launch {
            suspend {

                generateEthAddressFromMnemonicUseCase(mnemonic)
            }.execute {
                copy(generateAddressResponse = it)
            }
        }
    }
}
fun generateRandomMnemonic(): String {
    val secureRandom = SecureRandom()
    val entropy = ByteArray(16) // 128 bits entropy for 12-word mnemonic

    secureRandom.nextBytes(entropy)

    try {
        val mnemonicCode = MnemonicCode.INSTANCE
        val mnemonic = mnemonicCode.toMnemonic(entropy)
        return mnemonic.joinToString(" ")
    } catch (e: MnemonicException.MnemonicLengthException) {
        throw RuntimeException("Error generating mnemonic", e)
    }
}