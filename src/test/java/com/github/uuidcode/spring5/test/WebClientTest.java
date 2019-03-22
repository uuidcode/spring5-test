package com.github.uuidcode.spring5.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.uuidcode.util.CoreUtil;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import static org.slf4j.LoggerFactory.getLogger;

public class WebClientTest {
    protected static Logger logger = getLogger(WebClientTest.class);

    public TcpClient getTcpClient() {
        return TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .doOnConnected(connection ->
                connection.addHandlerLast(new ReadTimeoutHandler(10))
                    .addHandlerLast(new WriteTimeoutHandler(10)));
    }

    public ClientHttpConnector getClientHttpConnector() {
        HttpClient httpClient = HttpClient.from(this.getTcpClient());
        return new ReactorClientHttpConnector(httpClient);
    }

    @Test
    public void block() {
        String content = getResponseSpec().block();

        if (logger.isDebugEnabled()) {
            logger.debug(">>> test content: {}", CoreUtil.toJson(content));
        }
    }

    private Mono<String> getResponseSpec() {
        return WebClient.builder()
            .clientConnector(this.getClientHttpConnector())
            .baseUrl("https://www.google.com")
            .build()
            .get()
            .uri("/")
            .retrieve()
            .bodyToMono(String.class);

    }

    @Test
    public void subscribe() {
        this.getResponseSpec().subscribe(this::print);
        CoreUtil.sleepSecond(5);
    }

    public void print(String content) {
        if (logger.isDebugEnabled()) {
            logger.debug(">>> print content: {}", CoreUtil.toJson(content));
        }
    }
}
