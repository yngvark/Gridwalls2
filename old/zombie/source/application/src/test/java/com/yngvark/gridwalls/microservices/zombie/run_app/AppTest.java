package com.yngvark.gridwalls.microservices.zombie.run_app;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.run_game.GameEventProducer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class AppTest {
    @Test
    public void should_get_exception_when_consumer_throws_exception() throws IOException, InterruptedException {
        // Given
        InputFileReader inputFileReader = mock(InputFileReader.class);
        doAnswer((invocation -> {
            throw new MyTestException();
        })).when(inputFileReader).consume(any());

        GameEventProducer gameEventProducer = mock(GameEventProducer.class);
        BlockingQueue<String> gameProduceLock = new LinkedBlockingQueue();
        doAnswer((invocation) -> {
            gameProduceLock.take();
            return Void.TYPE;
        }).when(gameEventProducer).produce();

        ExecutorService executorService = Executors.newCachedThreadPool();
        App app = new App(
                new ExecutorCompletionService(executorService),
                inputFileReader,
                mock(OutputFileWriter.class),
                mock(NetworkMessageReceiver.class),
                gameEventProducer
                );

        // When + Then
        assertTimeoutPreemptively(Duration.ofMillis(300), () -> {
            ExecutionException e = assertThrows(ExecutionException.class, () -> app.run());
            assertTrue(
                    e.getCause().getClass().equals(MyTestException.class),
                    "Unexpected exception: " + e.getClass());
        });

        // Finally
        gameProduceLock.put("");
    }

    class MyTestException extends RuntimeException {}

    @Test
    public void should_get_exception_when_producer_throws_exception() throws IOException, InterruptedException {
        // Given
        InputFileReader inputFileReader = mock(InputFileReader.class);
        BlockingQueue<String> gameProduceLock = new LinkedBlockingQueue();
        doAnswer((invocation -> {
            gameProduceLock.take();
            return Void.TYPE;
        })).when(inputFileReader).consume(any());

        GameEventProducer gameEventProducer = mock(GameEventProducer.class);
        doAnswer((invocation) -> {
            throw new MyTestException();
        }).when(gameEventProducer).produce();

        ExecutorService executorService = Executors.newCachedThreadPool();
        App app = new App(
                new ExecutorCompletionService(executorService),
                inputFileReader,
                mock(OutputFileWriter.class),
                mock(NetworkMessageReceiver.class),
                gameEventProducer
        );

        // When + Then
        assertTimeoutPreemptively(Duration.ofMillis(300), () -> {
            ExecutionException e = assertThrows(ExecutionException.class, () -> app.run());
            assertTrue(
                    e.getCause().getClass().equals(MyTestException.class),
                    "Unexpected exception: " + e.getClass());
        });

        // Finally
        gameProduceLock.put("");
    }

}