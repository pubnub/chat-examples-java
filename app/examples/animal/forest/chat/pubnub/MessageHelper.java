package animal.forest.chat.pubnub;

import java.util.concurrent.TimeUnit;

import static animal.forest.chat.util.ChatItem.TYPE_OWN_END;
import static animal.forest.chat.util.ChatItem.TYPE_OWN_HEADER_SERIES;
import static animal.forest.chat.util.ChatItem.TYPE_OWN_HEADER_FULL;
import static animal.forest.chat.util.ChatItem.TYPE_OWN_MIDDLE;
import static animal.forest.chat.util.ChatItem.TYPE_REC_END;
import static animal.forest.chat.util.ChatItem.TYPE_REC_HEADER_SERIES;
import static animal.forest.chat.util.ChatItem.TYPE_REC_HEADER_FULL;
import static animal.forest.chat.util.ChatItem.TYPE_REC_MIDDLE;

class MessageHelper {

    private static final int HEADER_FULL = 10;
    private static final int HEADER = 20;
    private static final int MIDDLE = 30;
    private static final int END = 40;

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

                if (isTypeOf(previousMsg, HEADER_FULL)) {
                    previousMsg.setType(assignType(previousMsg, HEADER));
                } else if (isTypeOf(previousMsg, END)) {
                    previousMsg.setType(assignType(previousMsg, MIDDLE));
                }
            } else {
                currentMsg.setType(assignType(currentMsg, HEADER_FULL));
                if (!isTypeOf(previousMsg, HEADER_FULL)) {
                    previousMsg.setType(assignType(previousMsg, END));
                }
            }
        } else {
            currentMsg.setType(assignType(currentMsg, HEADER_FULL));
        }

    }

    private static boolean isTypeOf(Message instance, int type) {
        if (type == HEADER_FULL) {
            return instance.getType() == TYPE_OWN_HEADER_FULL || instance.getType() == TYPE_REC_HEADER_FULL;
        }
        if (type == HEADER) {
            return instance.getType() == TYPE_OWN_HEADER_SERIES || instance.getType() == TYPE_REC_HEADER_SERIES;
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
        if (type == HEADER_FULL) {
            return instance.isOwnMessage() ? TYPE_OWN_HEADER_FULL : TYPE_REC_HEADER_FULL;
        }
        if (type == HEADER) {
            return instance.isOwnMessage() ? TYPE_OWN_HEADER_SERIES : TYPE_REC_HEADER_SERIES;
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
