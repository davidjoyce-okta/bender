package ok.bot.bender.entity

import javax.persistence.Entity

@Entity
class TransactionHistory(
    val owedFrom: String,
    val owedTo: String,
    val quantity : Int,
    val rewardType : RewardType
) : AbstractJpaPersistable<Long>()
