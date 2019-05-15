package ok.bot.bender.repository

import ok.bot.bender.entity.TransactionHistory
import org.springframework.data.repository.CrudRepository

interface BenderRepository : CrudRepository<TransactionHistory, Long> {

}
