package io.dereknelson.lostcities.accounts.user

import io.dereknelson.lostcities.common.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface AuthoritiesRepository: JpaRepository<Authorities, String> {
}
