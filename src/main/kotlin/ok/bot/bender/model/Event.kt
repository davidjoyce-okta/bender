package ok.bot.bender.model

data class Event(
        val channel: String?,
        val channel_type: String?,
        val client_msg_id: String?,
        val event_ts: String?,
        val text: String?,
        val ts: String?,
        val type: String?,
        val user: String?
)