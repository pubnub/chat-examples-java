package animal.forest.chat.pubnub;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class History {

    public static final int TOP_ITEM_OFFSET = 3;
    private static final int CHUNK_SIZE = 20;
    private static final AtomicBoolean LOADING = new AtomicBoolean(false);

    private History() {
    }

    public abstract static class CallbackSkeleton {

        public abstract void handleResponse(List<Message> messages);
    }

    // tag::HIS-2[]
    public static void getAllMessages(PubNub pubNub, final String channel, Long start,
                                      final CallbackSkeleton callback) {
        pubNub.history()
                .channel(channel) // where to fetch history from
                .count(CHUNK_SIZE) // how many items to fetch
                .start(start) // where to start
                .includeTimetoken(true)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        new Thread(() -> {
                            if (!status.isError() && !result.getMessages().isEmpty()) {
                                List<Message> messages = new ArrayList<>();
                                for (PNHistoryItemResult message : result.getMessages()) {
                                    Message msg = Message.serialize(message);
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
    // end::HIS-2[]

    // tag::HIS-3[]
    public static void chainMessages(List<Message> list, int count) {

        int limit = count;
        if (limit > list.size()) {
            limit = list.size();
        }

        for (int i = 0; i < limit; i++) {
            Message message = list.get(i);
            if (i > 0) {
                MessageHelper.chain(message, list.get(i - 1));
            }
        }
    }
    // end::HIS-3[]

    public static boolean isLoading() {
        return LOADING.get();
    }

    public static void setLoading(boolean loading) {
        LOADING.set(loading);
    }

    /*public static HashMap<Integer, ViewType> groupMessages(List<Message> list) {
        HashMap<Long, List<Message>> hashMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Message message = list.get(i);
            Long key = message.getKey();
            if (hashMap.containsKey(key)) {
                hashMap.get(key).add(message);
            } else {
                ArrayList<Message> messages = new ArrayList<>();
                messages.add(message);
                hashMap.put(key, messages);
            }
        }

        HashMap<Integer, ViewType> flatMap = new HashMap<>();

        int total = 0;
        for (Map.Entry<Long, List<Message>> entry : hashMap.entrySet()) {
            Long key = entry.getKey();
            ViewType keyViewType = new ViewType(flatMap.size(), ChatAdapter.TYPE_DATE);
            flatMap.put(flatMap.size(), keyViewType);
            for (int i = 0; i < entry.getValue().size(); i++) {
                total++;
                int index = total;
                int type = -1;

                if (entry.getValue().get(i).isOwnMessage()) {
                    switch (entry.getValue().get(i).getSuccessivenessType()) {
                        case Message.TYPE_HEADER:
                            type = TYPE_OWN_HEADER_SERIES;
                            break;
                        case Message.TYPE_MIDDLE:
                            type = TYPE_OWN_MIDDLE;
                            break;
                        case Message.TYPE_END:
                            type = TYPE_OWN_END;
                            break;
                    }
                } else {
                    switch (entry.getValue().get(i).getSuccessivenessType()) {
                        case Message.TYPE_HEADER:
                            type = TYPE_REC_HEADER_SERIES;
                            break;
                        case Message.TYPE_MIDDLE:
                            type = TYPE_REC_MIDDLE;
                            break;
                        case Message.TYPE_END:
                            type = TYPE_REC_END;
                            break;
                    }
                }

                ViewType messageViewType = new ViewType(index, type);
                flatMap.put(flatMap.size(), messageViewType);
            }
        }

        return flatMap;
    }*/

}
