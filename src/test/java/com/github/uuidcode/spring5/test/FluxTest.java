package com.github.uuidcode.spring5.test;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import reactor.core.publisher.Flux;

import static org.slf4j.LoggerFactory.getLogger;

public class FluxTest {
    protected static Logger logger = getLogger(FluxTest.class);

    @Test
    public void test() {
        Flux.just("red", "white", "blue")
            .log()
            .map(String::toUpperCase)
            .subscribe();
    }

    @Test
    public void subscribe() {
        Flux.just("red", "white", "blue")
            .log()
            .map(String::toUpperCase)
            .subscribe(new Subscriber<String>() {
                private long count = 0;
                private Subscription subscription;

                @Override
                public void onSubscribe(Subscription subscription) {
                    this.subscription = subscription;
                    subscription.request(2);
                }

                @Override
                public void onNext(String t) {
                    count++;

                    if (count >= 2) {
                        count = 0;
                        subscription.request(2);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (logger.isErrorEnabled()) {
                        logger.error(">>> FluxTest onError error", throwable);
                    }
                }

                @Override
                public void onComplete() {
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>> onComplete");
                    }
                }
            });
    }
}
