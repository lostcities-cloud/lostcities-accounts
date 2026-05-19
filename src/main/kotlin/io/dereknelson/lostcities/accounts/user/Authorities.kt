package io.dereknelson.lostcities.accounts.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "authorities")
class Authorities() {
    @Id
    @Column(name = "authority")
    val authorities: String = ""
}
