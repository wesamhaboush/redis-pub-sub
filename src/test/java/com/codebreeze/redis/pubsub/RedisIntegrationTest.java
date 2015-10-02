package com.codebreeze.redis.pubsub;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class RedisIntegrationTest {

    private static final String CHANNEL_NAME = "commonChannel";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisIntegrationTest.class);

    @Test
    public void testSubscribtionAndPublishingWorks() throws IOException, URISyntaxException, InterruptedException {
        RedisServer redisServer = startAServer();
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
        final Jedis subscriberJedis = jedisPool.getResource();
        final Jedis publisherJedis = jedisPool.getResource();
        final Subscriber subscriber = new Subscriber();
        new Thread(subscription(subscriberJedis, subscriber)).start();
        new Publisher(publisherJedis, CHANNEL_NAME).start();
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        unsubscribe(jedisPool, subscriberJedis, subscriber, publisherJedis);
        stopServer(redisServer);
    }

    private void stopServer(RedisServer redisServer) throws InterruptedException {
        redisServer.stop();
    }

    private RedisServer startAServer() throws IOException, URISyntaxException {
        RedisServer redisServer = new RedisServer(6379);
        redisServer.start();
        return redisServer;
    }

    private static void unsubscribe(JedisPool jedisPool, Jedis subscriberJedis, Subscriber subscriber, Jedis publisherJedis) {
        try {
            subscriber.unsubscribe();
        } finally {
            closeQuietly(subscriberJedis);
            closeQuietly(publisherJedis);
            closeQuietly(jedisPool);
        }
    }

    private static Runnable subscription(Jedis subscriberJedis, Subscriber subscriber) {
        return () -> {
            try {
                LOGGER.info("Subscribing to \"commonChannel\". This thread will be blocked.");
                subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
                LOGGER.info("Subscription ended.");
            } catch (Exception e) {
                LOGGER.error("Subscribing failed.", e);
            }
        };
    }

}
