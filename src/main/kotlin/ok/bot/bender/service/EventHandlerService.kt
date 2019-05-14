package ok.bot.bender.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EventHandlerService {
    @Value("\${bender.slack.verificationToken}")
    var verificationToken: String? = null

    fun verifyRequest(token: String?): Boolean {
        return when (token) {
            verificationToken -> true
            else -> false
        }
    }
}