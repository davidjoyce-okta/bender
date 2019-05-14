package ok.bot.bender.controller

import ok.bot.bender.model.Message
import ok.bot.bender.service.EventHandlerService
import org.springframework.beans.factory.annotation.Autowired
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

        if (message.type == "url_verification") {
            return when (verified) {
                true -> ResponseEntity.ok().body(message.challenge)
                else -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(unauthorized)
            }
        } else if (!verified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(unauthorized)
        }

        when {
            message.event?.type == "message" && message.event.channel_type == "channel"
                -> eventHandlerService.incrementBooze(message)
            else -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("")
        }

        return ResponseEntity.status(HttpStatus.OK).body("")
    }
}