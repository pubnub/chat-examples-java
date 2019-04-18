package resourcecenterdemo.pubnub;

import resourcecenterdemo.model.Users;
import resourcecenterdemo.prefs.Prefs;
import resourcecenterdemo.util.Helper;

public class Message {

    String senderId, text;
    transient String timestamp;
    transient long timetoken;
    transient boolean isOwnMessage;

    transient Users.User user;

    private Message(Builder builder) {
        senderId = builder.senderId;
        text = builder.text;
        timetoken = builder.timetoken;
        isOwnMessage = senderId.equals(Prefs.get().uuid());
        timestamp = Helper.parseTime(timetoken / 10_000L);
        user = Users.getUserById(senderId);
    }

    public static final class Builder {

        private String senderId;
        private String text;
        private long timetoken;

        private Builder() {
        }

        public Builder senderId(String senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder timetoken(long timetoken) {
            this.timetoken = timetoken;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getTimetoken() {
        return timetoken;
    }

    public boolean isOwnMessage() {
        return isOwnMessage;
    }

    public Users.User getUser() {
        return user;
    }
}
