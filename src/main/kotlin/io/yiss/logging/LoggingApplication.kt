package io.yiss.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class LoggingApplication

fun main(args: Array<String>) {
    runApplication<LoggingApplication>(*args)
}

@Configuration
class RouteConf {
    @Bean
    fun apiRouter(handler: UserHandler) = router {
        ("/api" and accept(MediaType.APPLICATION_JSON_UTF8)).nest {
            GET("/user/{id}", handler::getUser)
            POST("/user", handler::addUser)
        }
    }
}