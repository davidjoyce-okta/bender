package ok.bot.bender.model

data class Message(
        val api_app_id: String?,
        val authed_users: List<String?>?,
        val event: Event?,
        val event_id: String?,
        val event_time: Int?,
        val team_id: String?,
        val token: String?,
        val type: String?,
        val challenge: String?
)