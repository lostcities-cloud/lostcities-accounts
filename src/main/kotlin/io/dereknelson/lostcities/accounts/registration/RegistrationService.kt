package io.dereknelson.lostcities.accounts.registration

import io.dereknelson.lostcities.accounts.user.UserEntity
import io.dereknelson.lostcities.accounts.user.UserRepository
import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.model.User
import org.modelmapper.ModelMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class RegistrationService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val modelMapper: ModelMapper
) {

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
                User(id = user.id, login = user.login!!, user.email!!, user.langKey!!)
            }
    }
}
