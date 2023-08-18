package com.example.ethwallet.mainscreen

import com.example.ethwallet.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
                            walletAddress = generateEthAddressFromMnemonic(generateRandomMnemonic()).first,
                            privayeKey = generateEthAddressFromMnemonic(generateRandomMnemonic()).second
                        )
                    }
                }

                else -> throw IllegalArgumentException("unknown action :$action")
            }
        }
    }

}

fun generateEthAddressFromMnemonic(mnemonic: String): Pair<String, String> {
    val seedBytes = MnemonicUtils.generateSeed(mnemonic, "")
    val masterKeyPair = HDKeyDerivation.createMasterPrivateKey(seedBytes)
    val derivedKeyPair = HDKeyDerivation.deriveChildKey(masterKeyPair, ChildNumber.ZERO_HARDENED)

    val privateKey = derivedKeyPair.privKeyBytes
    val publicKey = ECKey.fromPrivate(privateKey).publicKeyAsHex

    val address = Keys.getAddress(publicKey)
    return Pair("0x$address", Numeric.toHexStringNoPrefix(privateKey))
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