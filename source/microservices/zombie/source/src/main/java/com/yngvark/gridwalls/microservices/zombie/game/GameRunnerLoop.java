package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GameRunnerLoop {
    private final GameLoopFactory gameLoopFactory;
    private final BlockingQueue blockingQueue;

    private boolean runLoopStarted = false;
    private boolean shouldStartRunloop = true;
    private GameLoop gameLoop;

    public GameRunnerLoop(GameLoopFactory gameLoopFactory, BlockingQueue blockingQueue) {
        this.gameLoopFactory = gameLoopFactory;
        this.blockingQueue = blockingQueue;
    }

    public void run(GameConfig gameConfig) {
        if (!shouldStartRunloop) {
            System.out.println("Not running game loop.");
            return;
        }

        System.out.println("Starting game loop.");
        runLoopStarted = true;

        gameLoop = gameLoopFactory.create(gameConfig);
        gameLoop.loop();

        signalDone();
        System.out.println("GameLoop stopped.");
    }

    private void signalDone() {
        try {
            System.out.println("GameLoop: Signaling stop to continue.");
            blockingQueue.put("loop has exited");
        } catch (InterruptedException e) {
            System.out.println("WARN: Interrupted while putting signal to blocking queue. Details: "+ e.getMessage());
        }
    }

    public void stopLoopAndWaitUntilItCompletes() {
        System.out.println("Stopping game loop");
        shouldStartRunloop = false;

        if (gameLoop != null)
            gameLoop.stopLoop();

        if (runLoopStarted)
            waitForDoneSignal();
    }

    private void waitForDoneSignal() {
        try {
            System.out.println("Waiting for done signal");
            Object nullIfTimeout = blockingQueue.poll(2, TimeUnit.SECONDS);
            System.out.println("Waiting for done signal - done");

            if (nullIfTimeout == null)
                System.out.println("WARN: Never received game-loop-done signal. Timed out.");
            else
                System.out.println("Received signal: " + nullIfTimeout);
        } catch (InterruptedException e) {
            System.out.println("WARN: Interrupted while waiting for game loop to stop. Details: " + e.getMessage());
        }
    }
}
