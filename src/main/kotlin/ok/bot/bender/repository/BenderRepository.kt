package ok.bot.bender.repository

import ok.bot.bender.model.TransactionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BenderRepository : JpaRepository<TransactionHistory, Long>{
}
