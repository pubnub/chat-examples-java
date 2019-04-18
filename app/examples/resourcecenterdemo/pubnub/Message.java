package resourcecenterdemo.pubnub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import resourcecenterdemo.model.Users;
import resourcecenterdemo.prefs.Prefs;
import resourcecenterdemo.util.Helper;

public class Message {

    String senderId, text;

    transient String timestamp;
    transient long timetoken;
    transient boolean isOwnMessage;

    transient Users.User user;

    private Message() {

    }

    private Message(Builder builder) {
        senderId = Prefs.get().uuid();
        text = builder.text;
        timetoken = builder.timetoken;
        initializeCustomProperties();
    }

    private void initializeCustomProperties() {
        isOwnMessage = Prefs.get().uuid().equals(senderId);
        timestamp = Helper.parseTime(timetoken / 10_000L);
        user = Users.getUserById(senderId);
    }

    public static final class Builder {

        private String text;
        private long timetoken;

        private Builder() {
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

    public static Message serialize(PNHistoryItemResult pnHistoryItemResult) {
        Message message = new Gson().fromJson(pnHistoryItemResult.getEntry(), Message.class);
        message.timetoken = pnHistoryItemResult.getTimetoken();
        message.initializeCustomProperties();
        return message;
    }

    public static Message serialize(PNMessageResult pnMessageResult) {
        Message message = new Gson().fromJson(pnMessageResult.getMessage(), Message.class);
        message.timetoken = pnMessageResult.getTimetoken();
        message.initializeCustomProperties();
        return message;
    }

    public JsonObject generate() {
        String json = new Gson().toJson(this);
        JsonObject payload = new JsonParser().parse(json).getAsJsonObject();
        if (timetoken != 0L) {
            payload.addProperty("timetoken", timetoken);
        }
        return payload;
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
