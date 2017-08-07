package ru.android.bluetooth.bluetooth;

/**
 * Created by yasina on 05.08.17.
 */

public class Api {
    public static final String RESET = "Reset\r\n";
    public static final String STATUS = "Read Status\r\n";
    public static final String VERSION = "Get Version\r\n";
    private static final String SET_TIME = "Set Time=%s:%s:%s\r\n";
    private static final String SET_DATE = "Set Date=%s-%s-%s\r\n";
    public static final String GET_TIME = "Get Time\r\n";
    private static final String SET_NAME = "Set Name=%s\r\n";
    private static final String SET_PASSWORD = "Set Password=%s\r\n";
    public static final String SET_DATA = "Set Data\r\n";
    public static final String DEBUG = "Get AOnOff\r\n";
    public static final String ON = "Rele On\r\n";
    public static final String OFF = "Rele Off\r\n";
    public static final String MANUAL_ON = "Manual On\r\n";
    public static final String MANUAL_OFF = "Manual Off\r\n";
}
