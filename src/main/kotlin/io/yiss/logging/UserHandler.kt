package io.yiss.logging

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger


@Component
class UserHandler {

    val users = mutableMapOf<Int, User>()
    val idGen = AtomicInteger(1)
    init {
        initUsers()
    }
    fun getUser(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toInt()
        val user = users[id]
        return if (user != null) {
            ServerResponse.ok().body(Mono.justOrEmpty(user))
        } else {
            notFound().build()
        }
    }

    fun addUser(request: ServerRequest): Mono<ServerResponse> {
        val body = request.bodyToMono(User::class.java)
        val res = body.flatMap {
            users[idGen.getAndIncrement()] = it
            Mono.just(it)
        }
        return ServerResponse.ok().body(res)
    }

    private fun initUsers() {
        users[idGen.getAndIncrement()] = User("Nadine", "Lafti", "nlaft@email.com")
        users[idGen.getAndIncrement()] = User("Reda", "Jean Bar", "rjeanbar@email.com")
        users[idGen.getAndIncrement()] = User("Semone", "Liam", "sliam@email.com")
    }
}