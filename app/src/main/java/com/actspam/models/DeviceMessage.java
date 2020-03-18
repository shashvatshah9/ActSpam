package com.actspam.models;

/**
 * Stores info about the message on the device
 * A message can belong to a conversation thread or a single message
 */
public class DeviceMessage {

    private Message message;

    private Long threadId;

    private Long id;

    private boolean hasRead;

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
