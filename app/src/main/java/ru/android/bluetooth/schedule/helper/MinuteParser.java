package ru.android.bluetooth.schedule.helper;

/**
 * Created by yasina on 22.08.17.
 */

public class MinuteParser {

    private OneDayModel oneDayModel;

    public MinuteParser(OneDayModel oneDayModel) {
        this.oneDayModel = oneDayModel;
    }

    private String getTime(String time){
        int timeMin = Integer.parseInt(time);
        return String.valueOf(timeMin / 60) + ":" + String.valueOf(timeMin % 60);
    }

    public String[] parseToStr(){
        String[] answer = new String[2];
        answer[0] = getTime(oneDayModel.getOnMinutes());
        answer[1] = getTime(oneDayModel.getOffMinutes());
        return answer;
    }
}
