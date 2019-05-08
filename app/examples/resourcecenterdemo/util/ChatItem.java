package resourcecenterdemo.util;

public abstract class ChatItem {

    public static final int TYPE_OWN_HEADER = 1;
    public static final int TYPE_OWN_MIDDLE = 2;
    public static final int TYPE_OWN_END = 3;

    public static final int TYPE_REC_HEADER = 4;
    public static final int TYPE_REC_MIDDLE = 5;
    public static final int TYPE_REC_END = 6;

    public static final int TYPE_DATE = 7;

    abstract public int getType();

}
