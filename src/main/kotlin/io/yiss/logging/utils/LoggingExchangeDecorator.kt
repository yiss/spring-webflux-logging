package io.yiss.logging.utils

import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator

class LoggingExchangeDecorator : ServerWebExchangeDecorator {

    private val responseDecorator: ServerHttpResponseDecorator

    constructor(delegate: ServerWebExchange) : super(delegate) {
        this.responseDecorator = LoggingResponseDecorator(delegate.response)
    }

    override fun getResponse(): ServerHttpResponse {
        return this.responseDecorator
    }
}