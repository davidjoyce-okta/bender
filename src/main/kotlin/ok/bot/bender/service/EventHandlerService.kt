package ok.bot.bender.service

import ok.bot.bender.entity.RewardType
import ok.bot.bender.entity.TransactionHistory
import ok.bot.bender.model.Message
import ok.bot.bender.entity.OwedStats
import ok.bot.bender.repository.BenderRepository
import ok.bot.bender.repository.OwedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service


@Service
class EventHandlerService: ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    val benderRepository: BenderRepository? = null

    @Autowired
    val owedRepository: OwedRepository? = null

    @Value("\${bender.slack.verificationToken}")
    var verificationToken: String? = null

    @Value("\${bender.slack.chatPostMessageURL}")
    var chatPostMessageURL: String? = null

    @Value("\${bender.slack.botOAuthAccessToken}")
    var botOAuthAccessToken: String? = null

    @Value("\${bender.slack.increment.regex}")
    var regexIncrementPattern: String? = null
    var incrementRegex: Regex? = null

    @Value("\${bender.slack.owing.regex}")
    var regexOwingPattern: String? = null
    var owingRegex: Regex? = null

    @Value("\${bender.slack.owed.regex}")
    var regexOwedPattern: String? = null
    var owedRegex: Regex? = null

    @Value("#{'\${bender.slack.beer.emojis}'.split(',')}")
    var beerEmojis: List<String>? = null

    @Value("#{'\${bender.slack.wine.emojis}'.split(',')}")
    var wineEmojis: List<String>? = null

    @Value("#{'\${bender.slack.joint.emojis}'.split(',')}")
    var jointEmojis: List<String>? = null

    @Value("#{'\${bender.slack.juice.emojis}'.split(',')}")
    var juiceEmojis: List<String>? = null

    override fun onApplicationEvent(contextRefreshedEvent: ApplicationReadyEvent) {
        incrementRegex = regexIncrementPattern?.toRegex()
        owedRegex = regexOwedPattern?.toRegex()
        owingRegex = regexOwingPattern?.toRegex()
    }

    fun verifyRequest(token: String?): Boolean {
        return when (token) {
            verificationToken -> true
            else -> false
        }
    }

    suspend fun processChannelMessage(message: Message) {
        val incrementMatches = incrementRegex?.matchEntire(message.event?.text.toString())
        val owedMatches = owedRegex?.matchEntire(message.event?.text.toString())
        val owingMatches = owingRegex?.matchEntire(message.event?.text.toString())

        when {
            incrementMatches?.groupValues?.size == 4 ->
                incrementQuantity(
                    message.event?.channel.toString(),
                    incrementMatches.groupValues[1],
                    message.event?.user.toString(),
                    incrementMatches.groupValues[2],
                    incrementMatches.groupValues[3])
            owingMatches?.groupValues?.size == 3 ->
                statsOwing(
                        message.event?.channel.toString(),
                        owingMatches.groupValues[2],
                        message.event?.user.toString())
            owedMatches?.groupValues?.size == 2 ->
                statsOwed(
                        message.event?.channel.toString(),
                        message.event?.user.toString()
                )
        }

        return
    }

    fun statsOwing(channel: String, recipient: String, sender: String) {

    }

    fun statsOwed(channel: String, recipient: String) {
        val statsOwed: List<OwedStats>? = owedRepository?.getOwedStats(recipient)
        var owedMessage: String

        when (statsOwed) {
            null -> owedMessage = "No one currently owes you anything"
            else -> {
                owedMessage = "Currently you are owed the following:\n"

                for (item in statsOwed) {
                    owedMessage += "\n ${item.quantity} "

                    when (item.rewardType?.name) {
                        "BEER" -> owedMessage += " beer(s) from "
                        "JOINT" -> owedMessage += " hit(s) of Cannabis from "
                        "JUICE" -> owedMessage += " glass(es) of juice from "
                        "WINE" -> owedMessage += " glass(es) of wine from "
                    }

                    owedMessage += "<@${item.sender}"
                }
            }
        }

        khttp.post(
            url = chatPostMessageURL.toString(),
            headers = mapOf(
                    "Authorization" to "Bearer $botOAuthAccessToken",
                    "Content-Type" to "application/json"),
            json = mapOf(
                    "channel" to channel,
                    "text" to owedMessage))
    }

    fun incrementQuantity(channel: String, recipient: String, sender: String,
                          rewardType: String, notes: String) {
        val rewardName: String
        val rewardEnum: RewardType

        if (recipient == sender) {
            buyYourOwn(channel, sender, rewardType)
            return
        }

        when (rewardType) {
            in beerEmojis.toString() -> {
                rewardName = "beer"
                rewardEnum = RewardType.BEER
            }
            in wineEmojis.toString() -> {
                rewardName = "glass of wine"
                rewardEnum = RewardType.WINE
            }
            in jointEmojis.toString() -> {
                rewardName = "hit of Cannabis"
                rewardEnum = RewardType.JOINT
            }
            in juiceEmojis.toString() -> {
                rewardName = "juice"
                rewardEnum = RewardType.JUICE
            }
            else -> {
                thatIsNotAThing(channel, sender)
                return
            }
        }

        khttp.post(
                url = chatPostMessageURL.toString(),
                headers = mapOf(
                        "Authorization" to "Bearer $botOAuthAccessToken",
                        "Content-Type" to "application/json"),
                json = mapOf(
                        "channel" to channel,
                        "text" to "<@$sender> you now owe <@$recipient> 1 $rewardName."))


        val transaction = TransactionHistory(
                sender = sender,
                recipient = recipient,
                notes = notes,
                quantity = 1,
                rewardType = rewardEnum
        )

        benderRepository?.save(transaction)
    }

    fun buyYourOwn(channel: String, sender: String, rewardType: String) {
        val rewardName: String = when (rewardType) {
            in beerEmojis.toString() -> "Beer"
            in wineEmojis.toString() -> "Wine"
            in jointEmojis.toString() -> "hit of Cannabis"
            in juiceEmojis.toString() -> "Juice"
            else -> "crap"
        }

        khttp.post(
            url = chatPostMessageURL.toString(),
            headers = mapOf(
                    "Authorization" to "Bearer $botOAuthAccessToken",
                    "Content-Type" to "application/json"),
            json = mapOf(
                    "channel" to channel,
                    "text" to "Kill all humans, kill all humans, must kill all humans... Wh-uh? I was having the most wonderful dream. I think you were in it <@$sender>. Way to owe yourself your own $rewardName meatbag."))
    }

    fun thatIsNotAThing(channel: String, sender: String) {
        khttp.post(
            url = chatPostMessageURL.toString(),
            headers = mapOf(
                    "Authorization" to "Bearer $botOAuthAccessToken",
                    "Content-Type" to "application/json"),
            json = mapOf(
                    "channel" to channel,
                    "text" to "<@$sender>, you meatbag, you can only owe others Beer, Wine, Juice or Cannabis (only if you're Canadian and not a chump)."))
    }
}
