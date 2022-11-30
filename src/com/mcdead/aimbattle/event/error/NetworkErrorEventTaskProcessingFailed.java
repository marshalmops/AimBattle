package com.mcdead.aimbattle.event.error;

public class NetworkErrorEventTaskProcessingFailed extends NetworkErrorEvent {
    public NetworkErrorEventTaskProcessingFailed() {
        super("Network task processing has been failed!", true);
    }

    @Override
    public NetworkErrorEventType getType() {
        return NetworkErrorEventType.TASK_PROCESSING_FAIL;
    }
}
