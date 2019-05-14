package ok.bot.bender.controller

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ok.bot.bender.model.Message
import ok.bot.bender.model.RewardType
import ok.bot.bender.model.TransactionHistory
import ok.bot.bender.repository.BenderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import ok.bot.bender.service.EventHandlerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import khttp.post


@RestController
@RequestMapping("/v1/slack")
class EventsAPIController(@Autowired val repo: BenderRepository) {

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

        println(message.token)
        println(message.challenge)
        println(message.type)
        println(message.toString())

        when (message.event?.type?.equals("app_mention")) {
            true -> {
                val event = message.event
                val user = event.user
                khttp.post(
                        url = "https://hooks.slack.com/services/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",
                        json = mapOf("text" to "Hi ${user}, thanks for the mention!"))

            }
        }

        when (message.event?.text?.contains("<@")) {
            true -> {
                val event = message.event;
                val t = TransactionHistory(
                        event.user!!,
                        event.text.substringAfter("<@").substringBefore(">"),
                        1,
                        RewardType.juice)
                repo.save(t)
            }
        }

        when {
            message.event?.type == "message" && message.event.channel_type == "channel"
                -> GlobalScope.launch { eventHandlerService.incrementBooze(message) }
            else -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("")
        }

        println("YO DAWG")
        return ResponseEntity.status(HttpStatus.OK).body("")
    }
}
