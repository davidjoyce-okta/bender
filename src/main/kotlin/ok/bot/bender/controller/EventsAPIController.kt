package ok.bot.bender.controller

import ok.bot.bender.model.ChallengeVerification
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
    fun postEvent(@RequestBody challengeVerification: ChallengeVerification) : String {
        if (challengeVerification.token != verificationToken) {
            return "NO SOUP FOR YOU"
        }
        return "YAY"
    }

}