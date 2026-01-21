package io.dereknelson.lostcities.accounts.auth.crypto

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface KeyPairRepository : JpaRepository<KeyPairEntity, Long> {

    @Query("select keyPair from KeyPairEntity keyPair order by keyPair.id desc limit 1")
    fun findLatest(): KeyPairEntity?
}
