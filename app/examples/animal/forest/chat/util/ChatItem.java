package animal.forest.chat.util;

public abstract class ChatItem {

    public static final int TYPE_OWN_HEADER_FULL = 1;
    public static final int TYPE_OWN_HEADER_SERIES = 2;
    public static final int TYPE_OWN_MIDDLE = 3;
    public static final int TYPE_OWN_END = 4;

    public static final int TYPE_REC_HEADER_FULL = 5;
    public static final int TYPE_REC_HEADER_SERIES = 6;
    public static final int TYPE_REC_MIDDLE = 7;
    public static final int TYPE_REC_END = 8;

    public static final int TYPE_DATE = 9;

    abstract public int getType();

}
