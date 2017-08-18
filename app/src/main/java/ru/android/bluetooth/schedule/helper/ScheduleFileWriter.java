package ru.android.bluetooth.schedule.helper;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yasina on 17.08.17.
 */

public class ScheduleFileWriter {

    private List<String> mOnTimeList;
    private List<String> mOnMinutesTimeList;
    private List<String> mOffTimeList;
    private List<String> mOffMinutesTimeList;

    private JSONArray jsonArray = new JSONArray();

    public ScheduleFileWriter(List<String> mOnTimeList, List<String> mOnMinutesTimeList, List<String> mOffTimeList, List<String> mOffMinutesTimeList) {
        this.mOnTimeList = mOnTimeList;
        this.mOnMinutesTimeList = mOnMinutesTimeList;
        this.mOffTimeList = mOffTimeList;
        this.mOffMinutesTimeList = mOffMinutesTimeList;
    }

    private boolean isAllListsEqual(){
        if(mOnTimeList.size() == mOnMinutesTimeList.size()){
            if(mOffTimeList.size() == mOffMinutesTimeList.size()) {
                return true;
            }
        }
        return false;
    }

    private void makeJsonFromData(){
        if(isAllListsEqual()){
            JSONObject jsonObject;
            for(int i=0; i<mOnTimeList.size();i++){
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("on_time", mOnTimeList.get(i));
                    jsonObject.put("on_minutes", mOnMinutesTimeList.get(i));
                    jsonObject.put("off_time", mOffTimeList.get(i));
                    jsonObject.put("off_minutes", mOffMinutesTimeList.get(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }
    }

    public String createJsonTitle(Calendar calendar){
        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy");
        String onDate = format1.format(calendar.getTime());
        calendar.add(Calendar.YEAR,1);
        String offDate = format1.format(calendar.getTime());
        return String.format("%s_%s.json", calendar);
    }
    public void writeJSONtoFile(Calendar calendar, String path){
        try {
            Writer output = null;
            File file = new File(path + createJsonTitle(calendar));
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonArray.toString());
            output.close();
            //Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
