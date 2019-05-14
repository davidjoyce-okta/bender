package ok.bot.bender.service

import kotlinx.coroutines.delay
import ok.bot.bender.model.Message
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

    suspend fun incrementBooze(message: Message)  {
        delay(10000L)
        println(message.toString())
        return
    }
}