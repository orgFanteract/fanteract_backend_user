package org.fanteract_user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class FanteractUserApplication

fun main(args: Array<String>) {
	runApplication<FanteractUserApplication>(*args)
}
