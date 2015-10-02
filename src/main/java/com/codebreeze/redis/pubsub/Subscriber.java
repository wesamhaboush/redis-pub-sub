package com.codebreeze.redis.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.text.MessageFormat;

public class Subscriber extends JedisPubSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
        LOGGER.info("Message received. Channel: {}, Msg: {}", channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOGGER.info(MessageFormat.format("onMessage: pattern[{0}], channel[{1}, message[{2}]]", pattern, channel, message));
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info(MessageFormat.format("onSubscribe: channel[{0}, subscribedChannels[{1}]]", channel, subscribedChannels));
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info(MessageFormat.format("onUsubscribe: channel[{0}, subscribedChannels[{1}]]", channel, subscribedChannels));
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        LOGGER.info(MessageFormat.format("onPUnsubscribe: pattern[{0}, subscribedChannels[{1}]]", pattern, subscribedChannels));
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.info(MessageFormat.format("onPSubscribe: pattern[{0}, subscribedChannels[{1}]]", pattern, subscribedChannels));
    }
}
