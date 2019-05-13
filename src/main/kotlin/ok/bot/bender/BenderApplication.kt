package ok.bot.bender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BenderApplication

fun main(args: Array<String>) {
	runApplication<BenderApplication>(*args)
}
