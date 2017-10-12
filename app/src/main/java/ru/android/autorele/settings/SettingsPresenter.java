package ru.android.autorele.settings;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by yasina on 18.09.17.
 */

public class SettingsPresenter {


    private Context mContext;

    public SettingsPresenter(Context mContext) {
        this.mContext = mContext;
    }


    public void setCheckBoxLocationClickListener(final CheckBox checkButton, final TextInputLayout latitude, final TextInputLayout longitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setCheckBoxLocation(checkButton, latitude, longitude);
            }
        });
    }

    public void setCheckBoxLocation(final CheckBox checkButton, final TextInputLayout latitude, final TextInputLayout longitude){
        if(checkButton.isChecked()){
            latitude.setEnabled(true);
            longitude.setEnabled(true);
        }else {
            latitude.setEnabled(false);
            longitude.setEnabled(false);
        }
    }

    public void setCheckBoxTimezoneClickListener(final CheckBox checkButton, final TextInputLayout latitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckBoxTimezone(checkButton, latitude);
            }
        });
    }

    public void setCheckBoxTimezone(final CheckBox checkButton, final TextInputLayout latitude){
        if(checkButton.isChecked()){
            latitude.setEnabled(true);
        }else {
            latitude.setEnabled(false);
        }
    }
}
