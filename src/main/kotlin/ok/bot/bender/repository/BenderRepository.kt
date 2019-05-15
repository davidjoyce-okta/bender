package ok.bot.bender.repository

import ok.bot.bender.entity.TransactionHistory
import ok.bot.bender.entity.OwedStats
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface BenderRepository : CrudRepository<TransactionHistory, Int> {

    @Query(value = "SELECT SUM(quantity) FROM transaction_history " +
                   "WHERE recipient = :recipient AND sender = :sender AND reward_type = :reward_type",
           nativeQuery = true)
    fun getOwingStats(@Param("recipient") recipient: String,
                      @Param("sender") sender: String,
                      @Param("reward_type") rewardType: String): Int

    @Query(value = "SELECT SUM(quantity), reward_type, sender " +
                   "FROM transaction_history WHERE recipient = :recipient GROUP BY reward_type, sender",
           nativeQuery = true)
    fun getOwedStats(@Param("recipient") recipient: String): List<OwedStats>
}
