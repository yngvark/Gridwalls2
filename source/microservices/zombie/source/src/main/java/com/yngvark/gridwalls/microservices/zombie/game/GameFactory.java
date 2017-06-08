package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.app.GameEventProducer;
import com.yngvark.gridwalls.microservices.zombie.app.NetworkMessageListener;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;

import java.util.Random;

public class GameFactory {
    private final MapInfoReceiver mapInfoReceiver;

    public static GameFactory create() {
        Serializer serializer = new JsonSerializer();

        MapInfoReceiver mapInfoReceiver = new MapInfoReceiver(
                serializer,
                new ZombieMoverFactory(
                        serializer,
                        new ThreadSleeper(),
                        new Random()
                )
        );

        return new GameFactory(mapInfoReceiver);
    }

    public GameFactory(MapInfoReceiver mapInfoReceiver) {
        this.mapInfoReceiver = mapInfoReceiver;
    }

    public NetworkMessageListener createNetworkMessageListener() {
        return new NetwMsgReceiverContext(mapInfoReceiver);
    }

    public GameEventProducer createEventProducer(OutputFileWriter outputFileWriter) {
        return new BlockingGameEventProducer(
                outputFileWriter,
                new GameLogicContext(mapInfoReceiver)
        );
    }

}
