package resourcecenterdemo.pubnub;

import java.util.concurrent.TimeUnit;

import static resourcecenterdemo.util.ChatItem.TYPE_OWN_END;
import static resourcecenterdemo.util.ChatItem.TYPE_OWN_HEADER;
import static resourcecenterdemo.util.ChatItem.TYPE_OWN_MIDDLE;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_END;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_HEADER;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_MIDDLE;

public class MessageHelper {

    private static final int HEADER = 10;
    private static final int MIDDLE = 20;
    private static final int END = 30;

    private MessageHelper() {
    }

    static void chain(Message currentMsg, Message previousMsg) {

        long diffToPrev = (currentMsg.getTimetoken() - previousMsg.getTimetoken()) / 10_000L;

        long offset = TimeUnit.MINUTES.toMillis(1);

        boolean ownMessage = previousMsg.getUser().getUuid().equals(currentMsg.getUser().getUuid());
        boolean chainable = false;

        if (ownMessage)
            chainable = (diffToPrev <= offset);

        if (ownMessage) {
            if (chainable) {
                currentMsg.setType(assignType(currentMsg, END));
                if (!isTypeOf(previousMsg, HEADER)) {
                    previousMsg.setType(assignType(previousMsg, MIDDLE));
                }
            } else {
                currentMsg.setType(assignType(currentMsg, HEADER));
                if (!isTypeOf(previousMsg, HEADER)) {
                    previousMsg.setType(assignType(previousMsg, END));
                }
            }
        } else {
            currentMsg.setType(assignType(currentMsg, HEADER));
        }

    }

    private static boolean isTypeOf(Message instance, int type) {
        if (type == HEADER) {
            return instance.getType() == TYPE_OWN_HEADER || instance.getType() == TYPE_REC_HEADER;
        }
        if (type == MIDDLE) {
            return instance.getType() == TYPE_OWN_MIDDLE || instance.getType() == TYPE_REC_MIDDLE;
        }
        if (type == END) {
            return instance.getType() == TYPE_OWN_END || instance.getType() == TYPE_REC_END;
        }
        return false;
    }

    private static int assignType(Message instance, int type) {
        if (type == HEADER) {
            return instance.isOwnMessage() ? TYPE_OWN_HEADER : TYPE_REC_HEADER;
        }
        if (type == MIDDLE) {
            return instance.isOwnMessage() ? TYPE_OWN_MIDDLE : TYPE_REC_MIDDLE;
        }
        if (type == END) {
            return instance.isOwnMessage() ? TYPE_OWN_END : TYPE_REC_END;
        }
        return -1;
    }

}
