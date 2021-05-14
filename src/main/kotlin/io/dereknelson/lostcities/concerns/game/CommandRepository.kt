package io.dereknelson.lostcities.concerns.game

import io.dereknelson.lostcities.concerns.game.entities.CommandEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal interface CommandRepository : JpaRepository<CommandEntity, Long> {

    companion object {
        const val COMMANDS_BY_MATCH_CACHE: String = "commandsByMatchId"
    }

    @Cacheable(cacheNames = [COMMANDS_BY_MATCH_CACHE])
    fun findByMatchId(matchId: Long): List<CommandEntity>
}