package ok.bot.bender.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class TransactionHistory(
        @Column(length = 9) val sender: String,
        @Column(length = 9) val recipient: String,
        @Column(length = 256) val notes: String,
        @Column(columnDefinition = "tinyint(1)") val quantity : Int,
        @Enumerated(EnumType.STRING) @Column(length = 5) val rewardType : RewardType
) : AbstractJpaPersistable<Long>()
