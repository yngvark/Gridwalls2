package self_test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;
import com.yngvark.gridwalls.netcom.GameRpcServer;
import com.yngvark.gridwalls.netcom.ThreadedRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameRpcServerTest {
    String rpcQueueName = "rpcqueue";

    @Test
    public void game_rpc_server_should_return_game_info() throws IOException, TimeoutException, InterruptedException {
        // Given
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection = factory.newConnection();

        // Listen for GameInfo RPC-calls from client.
        String serverResponse = "Hey this is server";
        GameRpcServer gameInfoRequestHandler = new GameRpcServer(connection, rpcQueueName, (String request) -> serverResponse);
        ThreadedRunner threadedGameInfoRequestHandler = new ThreadedRunner(gameInfoRequestHandler);
        threadedGameInfoRequestHandler.runInNewThread();

        // When
        String actualResponse = doRpcFromClient();

        // Then
        connection.close();

        assertEquals(serverResponse, actualResponse);
    }

    private String doRpcFromClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchange = "";

        RpcClient rpcClient = new RpcClient(channel, exchange, rpcQueueName);
        String response = rpcClient.stringCall("Message from client.");
        connection.close();

        return response;
    }

}
