package io.dereknelson.lostcities.accounts.service

import io.dereknelson.lostcities.accounts.persistence.UserEntity
import io.dereknelson.lostcities.accounts.persistence.UserRepository
import io.dereknelson.lostcities.accounts.service.exceptions.UserAlreadyExistsException
import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.auth.entity.UserRef
import io.dereknelson.lostcities.common.model.User
import org.modelmapper.ModelMapper
import org.springframework.cache.CacheManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private var modelMapper: ModelMapper,
    private var userRepository: UserRepository,
    private var passwordEncoder: PasswordEncoder,
    private var cacheManager: CacheManager,
) {

    fun findRefByLogin(login: String): Optional<UserRef> {
        return userRepository.findOneByLogin(login)
            .map { UserRef(it.id, it.login, it.email) }
    }

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
            .map {
                User(
                    id = it.id,
                    login = it.login!!,
                    email = it.email!!,
                    langKey = it.langKey ?: Constants.DEFAULT_LANGUAGE,
                )
            }
    }

    fun findAllById(id: Iterable<Long>): Collection<User> {
        return userRepository.findAllById(id).map { modelMapper.map(it, User::class.java) }
    }

    fun delete(user: User) {
        userRepository.findById(user.id!!).ifPresent { userRepository.delete(it) }
    }

    fun register(registration: Registration): User {
        registration.password = passwordEncoder.encode(registration.password)

        userRepository.findOneByLogin(registration.login).ifPresent {
            throw UserAlreadyExistsException("")
        }

        userRepository.findOneByEmailIgnoreCase(registration.email).ifPresent {
            throw UserAlreadyExistsException("")
        }

        var userEntity = modelMapper.map(
            registration,
            UserEntity::class.java,
        )
        userEntity.createdBy = Constants.SYSTEM_ACCOUNT
        userEntity.activated = true
        userEntity = userRepository.save(userEntity)

        return User(
            id = userEntity.id,
            login = userEntity.login!!,
            email = userEntity.email!!,
        )
    }

    fun activateRegistration(key: String?): Optional<User> {
        return userRepository
            .findOneByActivationKey(key)
            .map { user ->
                user.activated = true
                user.activationKey = null
                clearUserCaches(user)
                User(id = user.id, login = user.login!!, user.email!!, user.langKey!!)
            }
    }

    private fun clearUserCaches(user: UserEntity) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE))!!
            .evict(user.login!!)

        if (user.email != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE))!!
                .evict(user.email!!)
        }
    }
}
