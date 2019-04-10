package chatresourcecenter;

import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.Arrays;
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
}
