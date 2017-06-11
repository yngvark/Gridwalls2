package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MapInfoReceiver implements Producer, NetGameMsgListener {
    private final Serializer serializer;
    private final ZombieMoverFactory zombieMoverFactory;

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    private ProducerContext producerContext;

    public MapInfoReceiver(Serializer serializer,
            ZombieMoverFactory zombieMoverFactory) {
        this.serializer = serializer;
        this.zombieMoverFactory = zombieMoverFactory;
        produce("/subscribeTo MapInfo");
    }

    private void produce(String msg) {
        try {
            messages.put(msg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String nextMsg(ProducerContext producerContext) {
        try {
            this.producerContext = producerContext;
            return messages.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void messageReceived(NetwMsgReceiverContext netwMsgReceiverContext, String msg) {
        MapInfo mapInfo = serializer.deserialize(msg, MapInfo.class);
        netwMsgReceiverContext.setCurrentListener(new NoOpReceiver());

        producerContext.setCurrentProducer(zombieMoverFactory.create(mapInfo));
        produce(producerContext.nextMsg());
    }

}
