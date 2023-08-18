package com.example.ethwallet.usecase

import com.example.ethwallet.utils.IoDispatcher
import com.example.ethwallet.utils.ResultUseCase
import kotlinx.coroutines.CoroutineDispatcher
import org.bitcoinj.core.ECKey
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.MnemonicException
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import org.web3j.utils.Numeric
import java.security.SecureRandom
import javax.inject.Inject

class MnemonicCodeGeneratorUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher
) : ResultUseCase<Unit,String>(dispatcher) {
    override suspend fun doWork(params:Unit):String {
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
}