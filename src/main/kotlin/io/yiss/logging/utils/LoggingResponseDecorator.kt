package io.yiss.logging.utils

import lombok.extern.slf4j.Slf4j
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerResponse
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels


@Slf4j
class LoggingResponseDecorator : ServerHttpResponseDecorator {

    constructor(delegate: ServerHttpResponse) : super(delegate) {
        delegate.beforeCommit {
            logResponse(delegate)
            Mono.empty()
        }
    }

    private var readable = false
    private var bodyByte = ByteArrayOutputStream()


    override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> {
        return if (readable) {
            readable = false
            super.writeAndFlushWith(Flux.from(body).map {
                Flux.from(it).map { dataBuffer ->
                    try {
                        Channels.newChannel(bodyByte).write(dataBuffer.asByteBuffer())
                    } catch (e: IOException) {

                    }
                    dataBuffer
                }
            })
        } else {
            super.writeAndFlushWith(body)
        }
    }

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return super.writeWith(Flux.from(body).map {
            try {
                Channels.newChannel(bodyByte).write(it.asByteBuffer())
            } catch (e: IOException) {

            }
            it
        })
    }

    private fun logResponse(response: ServerHttpResponse) {
        val statusCode = response.statusCode.toString()
        val headers = response.headers.toSingleValueMap().toString()
        val body = String(bodyByte.toByteArray())
        println("[$statusCode] [$headers] [$body]")
    }

}