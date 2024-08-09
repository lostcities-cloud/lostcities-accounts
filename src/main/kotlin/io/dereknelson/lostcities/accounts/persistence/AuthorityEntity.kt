package io.dereknelson.lostcities.accounts.persistence

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.util.*
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class AuthorityEntity(
    @Id
    @Column(length = 50)
    var name: @NotNull @Size(max = 50) String
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is AuthorityEntity) {
            false
        } else name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    // prettier-ignore
    override fun toString(): String {
        return "Authority{" +
            "name='" + name + '\'' +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
