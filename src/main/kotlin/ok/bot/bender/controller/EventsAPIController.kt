package ok.bot.bender.controller

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ok.bot.bender.model.Message
import ok.bot.bender.repository.BenderRepository
import org.springframework.beans.factory.annotation.Autowired
import ok.bot.bender.service.EventHandlerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/slack")
class EventsAPIController {

    @Autowired
    lateinit var eventHandlerService: EventHandlerService
    private var unauthorized: String = "Unauthorized"

    @PostMapping("/event")
    fun postEvent(@RequestBody message: Message): ResponseEntity<*> {
        val verified: Boolean = eventHandlerService.verifyRequest(message.token)
        val channelTypes: List<String> = listOf("channel", "group", "im")

        if (message.type == "url_verification") {
            return when (verified) {
                true -> ResponseEntity.ok().body(message.challenge)
                else -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(unauthorized)
            }
        } else if (!verified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(unauthorized)
        }

        println(message.toString())

        when {
            message.event?.type == "message" &&
            message.event.subtype != "message_deleted" &&
            message.event.channel_type in channelTypes
                -> GlobalScope.launch { eventHandlerService.processChannelMessage(message) }
            else -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("")
        }

        return ResponseEntity.status(HttpStatus.OK).body("")
    }
}
