package chatresourcecenter;

import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PnUtils {

    private PnUtils() {
    }

    public static boolean isSubscribed(PNStatus status, String channel) {
        return status.getOperation() == PNOperationType.PNSubscribeOperation
                && status.getAffectedChannels().contains(channel);
    }

    public static boolean isSubscribedGroup(PNStatus status, String group) {
        return status.getOperation() == PNOperationType.PNSubscribeOperation
                && status.getAffectedChannelGroups().contains(group);
    }

    public static boolean isUnsubscribed(PNStatus status, String channel, String uuid) {
        return status.getOperation() == PNOperationType.PNUnsubscribeOperation
                && status.getAffectedChannels().contains(channel)
                && status.getUuid().equals(uuid);
    }

    public static boolean isUnsubscribedGroup(PNStatus status, String group, String uuid) {
        return status.getOperation() == PNOperationType.PNUnsubscribeOperation
                && status.getAffectedChannelGroups().contains(group)
                && status.getUuid().equals(uuid);
    }

    public static boolean checkPresence(PNPresenceEventResult presence, String uuid, String event, String channel) {
        return presence.getEvent().equals(event)
                && presence.getUuid().equals(uuid)
                && presence.getChannel().equals(channel);
    }

    public static void printStatus(PNStatus status) {
        StringBuilder logBuilder = new StringBuilder("");
        logBuilder.append("Operation: ").append(status.getOperation()).append("\n");
        logBuilder.append("Category: ").append(status.getCategory()).append("\n");
        logBuilder.append("UUID: ").append(status.getUuid()).append("\n");
        logBuilder.append("Channels: ").append(Arrays.toString(new List[]{status.getAffectedChannels()})).append("\n");
        logBuilder.append("Groups: ")
                .append(Arrays.toString(new List[]{status.getAffectedChannelGroups()}))
                .append("\n");
        System.out.println(logBuilder.toString());

    }

    public static void printMessageMeta(PNMessageResult message) {
        StringBuilder logBuilder = new StringBuilder("");
        logBuilder.append("Channel: ").append(message.getChannel()).append("\n");
        logBuilder.append("Subscription: ").append(message.getSubscription()).append("\n");
        System.out.println(logBuilder.toString());

    }

    public static void printPresence(PNPresenceEventResult presence) {
        StringBuilder logBuilder = new StringBuilder("");
        logBuilder.append("UUID: ").append(presence.getUuid()).append("\n");
        logBuilder.append("Event: ").append(presence.getEvent()).append("\n");
        logBuilder.append("Channel: ").append(presence.getChannel()).append("\n");
        System.out.println(logBuilder.toString());
    }

    public static String parseTimetoken(long timetoken) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM YYYY");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetoken / 10_000L);
        return sdf.format(calendar.getTime());
    }
}
