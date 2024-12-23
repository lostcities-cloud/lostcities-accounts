package io.dereknelson.lostcities.accounts.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.auditing.AbstractAuditingEntity
import io.dereknelson.lostcities.common.model.Role
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "users_login_index", columnList = "login", unique = true),
        Index(name = "users_email_index", columnList = "email", unique = true),
    ],
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class UserEntity : AbstractAuditingEntity(), Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", initialValue = 3)
    val id: Long? = null

    @Column(length = 50, unique = true, nullable = false)
    var login:
        @NotNull
        @Pattern(regexp = Constants.LOGIN_REGEX)
        @Size(min = 1, max = 50)
        String? =
        null
        set(username) { field = StringUtils.lowerCase(username, Locale.ENGLISH) }

    @Column(length = 254, unique = true)
    var email:
        @Email
        @Size(min = 5, max = 254)
        String? = null

    @JsonIgnore
    @Column(name = "password_hash", unique = false, length = 60, nullable = false)
    var password:
        @NotNull
        @Size(min = 60, max = 60)
        String? = null

    @Column(name = "first_name", length = 50)
    var firstName:
        @Size(max = 50)
        String? = null

    @Column(name = "last_name", length = 50)
    var lastName:
        @Size(max = 50)
        String? = null

    @Column(nullable = false)
    var activated = false

    @Column(name = "lang_key", length = 10)
    var langKey:
        @Size(min = 2, max = 10)
        String? = null

    @Column(name = "image_url", length = 256)
    var imageUrl:
        @Size(max = 256)
        String? = null

    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    var activationKey:
        @Size(max = 20)
        String? = null

    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    var resetKey:
        @Size(max = 20)
        String? = null

    @Column(name = "reset_date")
    var resetDate: Instant? = null

    @JsonIgnore
    @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "users_authority",
        joinColumns = [JoinColumn(name = "users_id", referencedColumnName = "id")],
        foreignKey = ForeignKey(name = "USER_AUTHORITY_FOREIGN_KEY"),
    )
    @Enumerated(EnumType.STRING)
    var authorities: Set<Role> = HashSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is UserEntity) {
            false
        } else id != null && id == other.id
    }

    override fun hashCode(): Int {
        return 31
    }

    // prettier-ignore
    override fun toString(): String {
        return "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
