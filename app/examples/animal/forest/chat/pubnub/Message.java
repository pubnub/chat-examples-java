package animal.forest.chat.pubnub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;

import animal.forest.chat.model.Users;
import animal.forest.chat.prefs.Prefs;
import animal.forest.chat.util.ChatItem;
import animal.forest.chat.util.Helper;

// tag::BIND-3[]
public class Message extends ChatItem {

    private static final long TIMESTAMP_DIVIDER = 10_000L;

    private String senderId, text;

    /**
     * Formatted timestamp
     */
    private transient String timestamp;

    private transient long timetoken;

    private transient boolean ownMessage;

    /**
     * On of the six possible view types
     */
    private transient int type;

    /**
     * Key for grouping messages by their timetoken.
     */
    private transient Long key;

    /**
     * Message owner.
     */
    private transient Users.User user;

    // tag::ignore[]
/*
// end::ignore[]
}
// tag::ignore[]
*/
    // end::ignore[]
    // end::BIND-3[]

    private transient boolean sent = true;

    /**
     * Disable instance creation via constructor.
     * Use the {@code newBuilder} method instead.
     */
    private Message() {

    }

    /**
     * Adopts the builder pattern and it's used to create instances.
     */
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

        public JsonObject build() {
            return new Message(this).generate();
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private Message(Builder builder) {
        senderId = Prefs.get().uuid();
        text = builder.text;
        timetoken = builder.timetoken;
        initializeCustomProperties();
    }

    private void initializeCustomProperties() {
        ownMessage = Prefs.get().uuid().equals(senderId);
        timestamp = Helper.parseTime(timetoken / TIMESTAMP_DIVIDER);
        user = Users.getUserById(senderId);
        key = Helper.trimTime(timetoken / TIMESTAMP_DIVIDER);
        type = ownMessage ? TYPE_OWN_HEADER : TYPE_REC_HEADER;
    }

    @Override
    public int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    static Message serialize(PNHistoryItemResult pnHistoryItemResult) {
        Message message = new Gson().fromJson(pnHistoryItemResult.getEntry(), Message.class);
        message.timetoken = pnHistoryItemResult.getTimetoken();
        message.initializeCustomProperties();
        return message;
    }

    // tag::MSG-2[]
    public static Message serialize(PNMessageResult pnMessageResult) {
        Message message = new Gson().fromJson(pnMessageResult.getMessage(), Message.class);
        message.timetoken = pnMessageResult.getTimetoken();
        message.initializeCustomProperties();
        return message;
    }
    // end::MSG-2[]

    public static Message createUnsentMessage(JsonObject jsonObject) {
        Message message = new Gson().fromJson(jsonObject, Message.class);
        message.timetoken = System.currentTimeMillis() * TIMESTAMP_DIVIDER;
        message.initializeCustomProperties();
        message.setSent(false);
        return message;
    }

    private JsonObject generate() {
        String json = new Gson().toJson(this);
        JsonObject payload = new JsonParser().parse(json).getAsJsonObject();
        if (timetoken != 0L) {
            // if editing an existing message, pass it's timetoken within the payload
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
        return ownMessage;
    }

    public Users.User getUser() {
        return user;
    }

    public Long getKey() {
        return key;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
