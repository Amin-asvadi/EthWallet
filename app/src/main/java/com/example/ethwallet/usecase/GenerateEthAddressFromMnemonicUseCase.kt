package com.example.ethwallet.usecase

import com.example.ethwallet.utils.IoDispatcher
import com.example.ethwallet.utils.ResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import org.bitcoinj.core.ECKey
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric
import javax.inject.Inject

class GenerateEthAddressFromMnemonicUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher
) : ResultUseCase<String, Pair<String, String>>(dispatcher) {
    override suspend fun doWork(params: String): Pair<String, String> {
        val seedBytes = MnemonicUtils.generateSeed(params, "")
        val masterKeyPair = HDKeyDerivation.createMasterPrivateKey(seedBytes)
        val derivedKeyPair = HDKeyDerivation.deriveChildKey(masterKeyPair, ChildNumber.ZERO_HARDENED)

        val privateKey = derivedKeyPair.privKeyBytes
        val publicKey = ECKey.fromPrivate(privateKey).publicKeyAsHex

        val address = Keys.getAddress(publicKey)
        return Pair("0x$address", Numeric.toHexStringNoPrefix(privateKey))
    }
}