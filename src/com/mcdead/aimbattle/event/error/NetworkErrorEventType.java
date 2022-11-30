package com.mcdead.aimbattle.event.error;

public enum NetworkErrorEventType implements ErrorEventType {
    SOCKET_ERROR(), TASK_PROCESSING_FAIL(), INCORRECT_ROLE(), EXIT_TIMEOUT(),
    CONNECTION_FAILED();
}
