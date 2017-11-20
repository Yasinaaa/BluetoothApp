package ru.android.autorele.main.helper;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;

import ru.android.autorele.R;

/**
 * Created by yasina on 22.08.17.
 */

public class ResponseView {

    private static final String TAG = "ResponseView";
    public static final int RESET = R.string.controller_is_reset;
    public static final int SET_PASSWORD = R.string.password_is_changed;

    public static void showSnackbar(RelativeLayout view, int message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        final View snackBarView = snackbar.getView();
        snackbar.show();
    }
}
