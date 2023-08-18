package com.example.ethwallet.mainscreen

import com.example.ethwallet.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.MnemonicException
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : BaseViewModel<MainScreenState, MainScreenAction>(
    MainScreenState()
) {

    init {
        onEachAction { action ->
            when (action) {
                is MainScreenAction.GenerateAddress -> {
                    setState {
                        copy(
                            mnemonicCode = generateRandomMnemonic(),
                            walletAddress = generateEthAddressFromMnemonic(generateRandomMnemonic())
                        )
                    }
                }

                else -> throw IllegalArgumentException("unknown action :$action")
            }
        }
    }

}

fun generateEthAddressFromMnemonic(mnemonic: String): String {
    val derivationPath = "m/44'/60'/0'/0" // Derivation path
    val index = 0 // Address index

    val seedBytes = MnemonicUtils.generateSeed(mnemonic, "")
    val masterKeyPair = HDKeyDerivation.createMasterPrivateKey(seedBytes)
    val derivedKeyPair = HDKeyDerivation.deriveChildKey(masterKeyPair, ChildNumber.ZERO_HARDENED)

    val privateKey = derivedKeyPair.privKey
    val publicKey = ECKeyPair.create(privateKey).publicKey

    val address = Keys.getAddress(publicKey)
    return "0x$address"
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