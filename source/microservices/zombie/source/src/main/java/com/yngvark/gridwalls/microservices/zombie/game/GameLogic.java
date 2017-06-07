package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.Serializer;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

class GameLogic {
    private final Logger logger = getLogger(getClass());

    private final Sleeper sleeper;
    private final Serializer serializer;
    private final Random random;

    private MapInfo mapInfo;
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private boolean mapInfoReceived = false;

    private MapInfoReceivedGameLogic mapInfoReceivedGameLogic;

    public GameLogic(Sleeper sleeper, Serializer serializer, Random random) {
        this.sleeper = sleeper;
        this.serializer = serializer;
        this.random = random;
    }

    public String nextMsg() {
        if (!mapInfoReceived) {
            try {
                blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Getting next message.");
        sleeper.sleep(100 + random.nextInt(901));
        Move move = getNextMove();
        return serializer.serialize(move, Move.class);
    }

    private Move getNextMove() {
        int toX = random.nextInt(mapInfo.width) + 1;
        int toY = random.nextInt(mapInfo.height) + 1;
        return new Move(toX, toY);
    }

    public void messageReceived(String msg) {
        mapInfo = serializer.deserialize(msg, MapInfo.class);
        mapInfoReceived = true;
        try {
            blockingQueue.put("We have received mapInfo, proceed.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
