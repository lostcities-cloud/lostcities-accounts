package io.dereknelson.lostcities.accounts.auth.crypto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.security.KeyPair
import java.util.Base64

@Entity
@Table(name = "key_pairs")
class KeyPairEntity(
    @Column(length = 2056, unique = true)
    val privateKey: String,
    @Column(length = 2056, unique = true)
    val publicKey: String,
) {
    companion object {
        fun fromKeyPair(keyPair: KeyPair): KeyPairEntity {
            return KeyPairEntity(
                Base64.getEncoder().encodeToString(keyPair.public.encoded),
                Base64.getEncoder().encodeToString(keyPair.private.encoded)
            )
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_generator")
    @SequenceGenerator(name = "user_sequence_generator", initialValue = 3)
    val id : Long? = null
}
