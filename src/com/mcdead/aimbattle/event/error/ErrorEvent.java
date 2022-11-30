package com.mcdead.aimbattle.event.error;

import com.mcdead.aimbattle.event.Event;

public abstract class ErrorEvent implements Event {
    private String m_cause;
    private boolean m_isCritical;

    public ErrorEvent(final String cause, final boolean isCritical) {
        m_cause = cause;
        m_isCritical = isCritical;
    }

    public String getCause() {
        return m_cause;
    }

    public boolean isCritical() {
        return m_isCritical;
    }

    public abstract ErrorEventType getType();
}
