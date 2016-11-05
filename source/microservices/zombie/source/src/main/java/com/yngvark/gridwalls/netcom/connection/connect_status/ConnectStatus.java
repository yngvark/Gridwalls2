package com.yngvark.gridwalls.netcom.connection.connect_status;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public interface ConnectStatus<T extends ConnectionWrapper> {
    boolean connected();
    boolean disconnected();
    String getConnectFailedDetails();
    T getConnectionWrapper();
}
