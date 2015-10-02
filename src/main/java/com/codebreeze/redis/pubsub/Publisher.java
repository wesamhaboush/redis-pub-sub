package com.codebreeze.redis.pubsub;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.apache.commons.io.IOUtils.readLines;

public class Publisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    private final Jedis publisherJedis;
    private final String channel;

    public Publisher(final Jedis publisherJedis, final String channel) {
        this.publisherJedis = publisherJedis;
        this.channel = channel;
    }

    public void start() {
        LOGGER.info("Starting publishing:");
        try {
            readLines(this.getClass().getResourceAsStream("/messages.txt"), StandardCharsets.UTF_8)
                    .stream()
                    .forEach(publish());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private Consumer<String> publish() {
        return m -> {
            LOGGER.info("publishing message [{}], channel [{}]", m, channel);
            publisherJedis.publish(channel, m);
        };
    }
}
