package ru.android.bluetooth.main.helper;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import ru.android.bluetooth.bluetooth.BluetoothCommands;

/**
 * Created by yasina on 22.08.17.
 */

public class ResponseView {

    private static final String TAG = "ResponseView";
    public static final String RESET = "Контроллер сброшен";
    public static final String STATUS = "Информация о статусе обновлена";
    public static final String VERSION = "Информация о версии обновлена";
    public static final String SET_TIME = "Время обновлено";
    public static final String SET_DATE = "Дата обновлена";
    public static final String GET_TIME = "Информация о времени обновлена";
    private static final String SET_NAME = "Имя изменено";
    private static final String SET_PASSWORD = "Пароль изменен";
    public static final String SET_DATA = "Рассписание изменено";
    public static final String DEBUG = "";
    public static final String ON = "Реле включено";
    public static final String OFF = "Реле выключено";
    public static final String MANUAL_ON = "Переход на ручной режим";
    public static final String MANUAL_OFF = "Переход на автоматический режим";

    public static void showSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        final View snackBarView = snackbar.getView();
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 80);

        snackBarView.setLayoutParams(params);
        snackbar.show();
    }
}
