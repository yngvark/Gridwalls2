package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.netcom.connection.BrokerConnecterHolder;
import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;
import com.yngvark.gridwalls.netcom.connection.connect_status.Connected;
import com.yngvark.gridwalls.netcom.connection.connect_status.Disconnected;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;
import com.yngvark.gridwalls.netcom.publish.NetcomSucceeded;
import com.yngvark.gridwalls.netcom.publish.Publisher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class NetcomPublishTest {
    @Test
    public void should_publish_message_when_connected() throws Exception {
        // Given
        BrokerConnecterHolder brokerConnecterHolder = mock(BrokerConnecterHolder.class);
        ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
        when(brokerConnecterHolder.connectIfNotConnected()).thenReturn(new Connected(connectionWrapper));

        Publisher publisher = mock(Publisher.class);
        when(publisher.publish(eq(connectionWrapper), eq("weather"), eq("it's raining"))).thenReturn(new NetcomSucceeded());

        Netcom netcom = new Netcom(brokerConnecterHolder, null, publisher, null);

        // When
        NetcomResult publishResult = netcom.publish("weather", "it's raining");

        // Then
        verify(publisher).publish(eq(connectionWrapper), eq("weather"), eq("it's raining"));
        assertTrue(publishResult.succeeded());
        assertTrue(publishResult.getFailedInfo().length() > 0);
    }

    @Test
    public void should_return_error_when_unable_to_connect_when_publishing_message() throws Exception {
        // Given
        BrokerConnecterHolder brokerConnecterHolder = mock(BrokerConnecterHolder.class);
        when(brokerConnecterHolder.connectIfNotConnected()).thenReturn(new Disconnected("Could not contact host."));

        Publisher publisher = mock(Publisher.class);

        Netcom netcom = new Netcom(brokerConnecterHolder, null, publisher, null);

        // When
        NetcomResult publishResult = netcom.publish("weather", "it's raining");

        // Then
        verify(publisher, times(0)).publish(any(ConnectionWrapper.class), any(String.class), any(String.class));

        assertFalse(publishResult.succeeded());
        assertEquals("Could not publish. Details: Could not contact host.", publishResult.getFailedInfo());
    }
}
