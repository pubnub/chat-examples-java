package chatresourcecenter.mock;

public class Log {

    private Log() {
    }

    public static void d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ": " + msg);
    }

    public static void i(String tag, String msg) {
        System.out.println("INFO: " + tag + ": " + msg);
    }

}