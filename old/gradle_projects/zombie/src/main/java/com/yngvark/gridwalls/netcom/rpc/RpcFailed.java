package com.yngvark.gridwalls.netcom.rpc;

public class RpcFailed implements RpcResult {
    private final String failReason;

    public RpcFailed(String failReason) {
        this.failReason = failReason;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public String getFailedInfo() {
        return failReason;
    }

    @Override
    public String getRpcResponse() {
        return "No RPC response for a failed RPC call";
    }
}
