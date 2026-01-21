package io.dereknelson.lostcities.accounts.auth.crypto

import io.jsonwebtoken.Jwts

import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec

@Service
class KeyPairService(
    val keyPairRepository : KeyPairRepository
) {

    companion object {
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
    }

    private val keyBytes: Int = 2048

    fun loadKeyPair(): Pair<PublicKey, PrivateKey> {
        var keyPair = keyPairRepository.findLatest()

        if (keyPair == null) {
            keyPair = generateKeyPair()
        }

        return Pair(
            keyFactory.generatePublic(X509EncodedKeySpec(keyPair.publicKey.encodeToByteArray())),
            keyFactory.generatePrivate(X509EncodedKeySpec(keyPair.privateKey.encodeToByteArray()))
        )
    }


    private fun saveKeyPair(keyPairEntity: KeyPairEntity) {
        keyPairRepository.save(keyPairEntity)
    }

    private fun generateKeyPair(): KeyPairEntity {
        val randomSeed: ByteArray = SecureRandom.getInstance("SHA1PRNG").generateSeed(keyBytes)
        val secureRandom = SecureRandom(randomSeed)
        val keyPair = Jwts.SIG.RS256.keyPair().random(secureRandom).build()

        val entity = KeyPairEntity.fromKeyPair(keyPair)
        saveKeyPair(entity)

        return entity
    }
}
