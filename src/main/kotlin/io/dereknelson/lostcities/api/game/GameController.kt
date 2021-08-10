package io.dereknelson.lostcities.api.matches


import io.dereknelson.lostcities.api.game.CommandDto
import io.dereknelson.lostcities.common.model.game.Command
import io.dereknelson.lostcities.config.KafkaConfiguration
import io.dereknelson.lostcities.common.model.game.GameState
import io.dereknelson.lostcities.common.events.MatchCommandEvent
import io.dereknelson.lostcities.domains.matches.MatchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/game")
class GameController(
    private var matchService: MatchService,
    private var modelMapper: ModelMapper,
    private var kafkaConfiguration: KafkaConfiguration

) {
    lateinit var kafkaTemplate: KafkaTemplate<String, MatchCommandEvent>

    @Operation(description = "Retrieve a game state.")
    @ApiResponses(value = [
        ApiResponse(responseCode="200", description= "Game retrieved."),
        ApiResponse(responseCode="404", description= "Game not found.")
    ])
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(
        @PathVariable id: Long,
        userDetails: UserDetails
    ): GameState {
        throw NotImplementedError()
    }

    @Operation(description = "Play a command in a game.")
    @ApiResponses(value = [
        ApiResponse(responseCode="201", description= "Command executed."),
        ApiResponse(responseCode="404", description= "Game not found."),
        ApiResponse(responseCode="406", description= "Invalid command.")
    ])
    @PatchMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun playCommand(@PathVariable id: Long, @RequestBody commandDto: CommandDto) {
        val match = matchService.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val command = modelMapper.map(commandDto, Command::class.java)

        val matchAggregate = MatchCommandEvent(id, match, command)

        kafkaTemplate.send(kafkaConfiguration.commandJobTopic, matchAggregate)
    }
}
