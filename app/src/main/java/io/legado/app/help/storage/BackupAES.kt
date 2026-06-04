package io.legado.app.help.storage

import cn.hutool.crypto.symmetric.AES
import io.legado.app.help.config.LocalConfig
import io.legado.app.utils.MD5Utils
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec

class BackupAES : AES(
    MD5Utils.md5Encode(LocalConfig.password ?: "").encodeToByteArray(0, 16)
) {

    private val secretKey = SecretKeySpec(
        MD5Utils.md5Encode(LocalConfig.password ?: "").encodeToByteArray(0, 16),
        "AES"
    )

    private fun createCipher(mode: Int): Cipher {
        return Cipher.getInstance("AES/ECB/PKCS5Padding").apply {
            init(mode, secretKey)
        }
    }

    fun encryptStream(inputStream: InputStream, outputStream: OutputStream) {
        CipherOutputStream(outputStream, createCipher(Cipher.ENCRYPT_MODE)).use { cipherOut ->
            inputStream.copyTo(cipherOut)
        }
    }

    fun decryptStream(inputStream: InputStream, outputStream: OutputStream) {
        CipherInputStream(inputStream, createCipher(Cipher.DECRYPT_MODE)).use { cipherIn ->
            cipherIn.copyTo(outputStream)
        }
    }
}
