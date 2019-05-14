package ok.bot.bender.controller

import ok.bot.bender.model.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/slack")
class EventsAPIController {

    @Value("\${bender.slack.verificationToken}")
    var verificationToken: String? = null

    @PostMapping("/event")
    fun postEvent(@RequestBody message: Message) : String? {
        if (message.type == "url_verification") {
            return when(message.token) {
                verificationToken -> message.challenge
                else -> ""
            }
        }

        println(message.token)
        println(message.challenge)
        println(message.type)
        println(message.toString())
        return "HEY"
    }

}