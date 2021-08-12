package io.dereknelson.lostcities.accounts

import io.dereknelson.lostcities.accounts.api.RegistrationDto
import io.dereknelson.lostcities.accounts.service.Registration
import io.dereknelson.lostcities.common.model.User
import io.dereknelson.lostcities.common.model.UserRef
import io.dereknelson.lostcities.accounts.persistence.AuthorityEntity
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
class ModelMapperConfiguration {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()

        modelMapper.addConverter({ context ->
            if(context.source == null) {
                null
            } else {
                val src: UserRef = context.source as UserRef

                User(
                    id = src.id,
                    login = src.login!!,
                    email = src.email!!,
                )
            }
        }, UserRef::class.java, User::class.java)

        modelMapper.addConverter({ context ->
            if(context.source == null) {
                null
            } else {
                val src: Timestamp = context.source as Timestamp

                val dest = src.toLocalDateTime()
                dest.atOffset(ZoneOffset.UTC)
                dest
            }
        }, Timestamp::class.java, LocalDateTime::class.java)

        modelMapper.addConverter({ context ->
            val src = context.source as RegistrationDto

            Registration(
                login=src.login,
                email=src.email,
                password=src.password,
                firstName=src.firstName,
                lastName=src.lastName,
                langKey=src.langKey,
                authorities=src.authorities.map { AuthorityEntity(name=it) }.toSet(),
            )
        }, RegistrationDto::class.java, Registration::class.java)

        return modelMapper
    }
}