package com.actspam.models;

import java.util.List;

public class DeviceMessageThread {

    private List<DeviceMessage> threadMessages;

    private Long threadId;

    private Long unreadMessges;

    public DeviceMessageThread(){
        if(threadMessages!=null){
            unreadMessges = countUnreadMessages();
        }
    }

    public List<DeviceMessage> getThreadMessages() {
        return threadMessages;
    }

    public void setThreadMessages(List<DeviceMessage> threadMessages) {
        this.threadMessages = threadMessages;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getUnreadMessges() {
        return unreadMessges;
    }

    public void setUnreadMessges(Long unreadMessges) {
        this.unreadMessges = unreadMessges;
    }

    public long countUnreadMessages(){
        long count=0;
        for(DeviceMessage msg : threadMessages){
            if(msg.isHasRead()){
                count++;
            }
        }
        return count;
    }
}
