package resourcecenterdemo.pubnub;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import java.util.ArrayList;
import java.util.List;

public class History {

    public abstract static class CallbackSkeleton {

        public abstract void handleResponse(List<Message> messages);
    }

    public static void getAllMessages(PubNub pubNub, final String channel, Long start, final int count,
                                      final CallbackSkeleton callback) {
        pubNub.history()
                .channel(channel) // where to fetch history from
                .count(count) // how many items to fetch
                .start(start) // where to start
                .includeTimetoken(true)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        new Thread(() -> {
                            if (!status.isError() && !result.getMessages().isEmpty()) {
                                List<Message> messages = new ArrayList<>();
                                for (PNHistoryItemResult message : result.getMessages()) {
                                    JsonObject entry = message.getEntry().getAsJsonObject();

                                    Message msg = Message.newBuilder()
                                            .text(entry.get("text").getAsString())
                                            .senderId(entry.get("senderId").getAsString())
                                            .timetoken(message.getTimetoken())
                                            .build();

                                    messages.add(msg);
                                }
                                callback.handleResponse(messages);
                            } else {
                                callback.handleResponse(new ArrayList<>());
                            }
                        }).start();

                    }
                });
    }
}
