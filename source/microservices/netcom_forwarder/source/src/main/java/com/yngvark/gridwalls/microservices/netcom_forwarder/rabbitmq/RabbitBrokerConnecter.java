package com.yngvark.gridwalls.microservices.netcom_forwarder.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitBrokerConnecter {
    public Connection connect(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setConnectionTimeout(0);
        factory.setHost(hostname);
        factory.setRequestedHeartbeat(20);
        factory.setAutomaticRecoveryEnabled(true);

        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Could not connect to host: " + hostname);
        }

    }
}
