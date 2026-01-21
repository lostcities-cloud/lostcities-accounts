package io.dereknelson.lostcities.accounts.user

import io.dereknelson.lostcities.accounts.registration.Registration
import io.dereknelson.lostcities.accounts.registration.UserAlreadyExistsException
import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.auth.entity.UserRef
import io.dereknelson.lostcities.common.model.User
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(
    private var modelMapper: ModelMapper,
    private var userRepository: UserRepository,
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

    fun findAll(query: Pageable): Page<User> {
        return userRepository.findAll(query)
            .map { modelMapper.map(it, User::class.java) }
    }


}
