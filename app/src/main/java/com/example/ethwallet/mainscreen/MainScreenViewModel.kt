package com.example.ethwallet.mainscreen

import androidx.lifecycle.viewModelScope
import com.example.ethwallet.usecase.GenerateEthAddressFromMnemonicUseCase
import com.example.ethwallet.usecase.MnemonicCodeGeneratorUseCase
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
    private val generateEthAddressFromMnemonicUseCase: GenerateEthAddressFromMnemonicUseCase,
    private val mnemonicCodeGeneratorUseCase: MnemonicCodeGeneratorUseCase
) : BaseViewModel<MainScreenState, MainScreenAction>(
    MainScreenState()
) {

    init {
        onEachAction { action ->
            when (action) {
                is MainScreenAction.GenerateAddress -> generateRandomMnemonic()

                else -> throw IllegalArgumentException("unknown action :$action")
            }
        }
        onAsyncResult(
            MainScreenState::mnemonicCodeGeneratorResponse,
            onSuccess = {
                generateEthAddressFromMnemonic(it)
                setState { copy(mnemonicCode = it) }
            },
            onFail = {
                val response = it.message
                println(response)
            }
        )
        onAsyncResult(
            MainScreenState::generateAddressResponse,
            onSuccess = {
                setState {
                    copy(
                        walletAddress = it.first,
                        privayeKey = it.second,
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

    private fun generateRandomMnemonic() {
        viewModelScope.launch {
            suspend {
                mnemonicCodeGeneratorUseCase(Unit)
            }.execute {
                copy(mnemonicCodeGeneratorResponse = it)
            }
        }
    }
}

