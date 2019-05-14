package ok.bot.bender.controller

import ok.bot.bender.model.Message
import ok.bot.bender.model.RewardType
import ok.bot.bender.model.TransactionHistory
import ok.bot.bender.repository.BenderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import khttp.post

@RestController
@RequestMapping("/v1/slack")
class EventsAPIController(@Autowired val repo: BenderRepository) {

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

        return "HEY"
    }

}
