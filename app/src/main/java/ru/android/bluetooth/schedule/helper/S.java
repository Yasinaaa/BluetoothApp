package ru.android.bluetooth.schedule.helper;

import android.util.Log;

/**
 * Created by yasina on 10.08.17.
 */

public class S {

    private char makeData(){
       // StringBuilder stringBuilder = new StringBuilder();
        char num = 0;
        for (int i=1; i<=732; i++){

            num = (char)(num + numtostr2revers(i));
        }
        Log.d("ff", num + "");
        return num;

        //return stringBuilder.toString();
    }

    public S() {
    }

    public void setData(){
        //String data = makeData();
        //String result = "Set Data" + "\n" + strToPackCRC(data);
        //Log.d("S", result);
        makeData();
        strToPackCRC(makeData());
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder1 = new StringBuilder();
       for(int i=0;i<366;i++){
           stringBuilder.append("22,");
           stringBuilder1.append("23,");
       }
        String sw ="Set Data\n1465";
       String s =
               "22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22";
       Log.d("str1", stringBuilder.toString());
        Log.d("str2", stringBuilder1.toString());

    }

    private String strToPackCRC(int s){
        int lenght = 5;
        int so = 0;
        int crc = 0;
        if(lenght > 0) {
            so = (char) lenght + s;
            Log.d("So", so + "");
            int[] so2 = new int[]{9,0,2,8,8};

            for (int i = 0; i < 5; i++) {
                crc = crc + so2[i];
            }
            if (crc > 255)
                crc = crc - 255;
        }
        Log.d("sss", so + (char)crc + "");
        return null;
       // return so + (char)crc;
    }
    private char numtostr2revers(int i){
        char symbol = (char) (i % 256);
        i = i/256;
        return (char)(symbol + (i % 256));
    }
/*
* отправка данных в контроллер, реализованная в таймерной части VFP

IF thisform.step=0
 && тут формирование строковой переменной "P" увеличивающимися данными (для отладки)
 thisform.p=""
 FOR _i=1 TO 366*2
  thisform.p=thisform.p+numtostr2revers(_i)
 ENDFOR

 && формирование команды "Set Data" с первым пакетом,указывающим количество отправляемых данных

 _s="Set Data"+CHR(13)+StrToPackCRC(numtostr2(LEN(thisform.p)))

 thisform.addMessage("Начало отправки пакетов!"+CHR(13))
 thisform.com.output=_s
 thisform._seconds=SECONDS()
 thisform.step=1
 thisform.nump=0
 thisform.btnSaveScen.Enabled=.f.
 RETURN
ENDIF

IF thisform.step=1	&& ждать ответа
 IF SECONDS()<thisform._seconds+1
  IF CHR(13)$thisform._strcom
   _s=LEFT(thisform._strcom,AT(CHR(13),thisform._strcom))
   thisform._strcom=SUBSTR(thisform._strcom,AT(CHR(13),thisform._strcom)+1,0xFFFF)
   IF UPPER(_s)="OK"
    thisform.addMessage("Пакет "+TRANSFORM(thisform.nump)+"="+ _s)
    thisform.nump=thisform.nump+1
    thisform.step=2
    RETURN
   ENDIF
  ENDIF
 ELSE
  thisform.addMessage("Ошибка на шаге 1")
  this.Enabled=.f.
 ENDIF
ENDIF

IF thisform.step=2	&& ждать ответа
 IF LEN(thisform.p)>0
  _so=LEFT(thisform.p,128)

  thisform.p=SUBSTR(thisform.p,LEN(_so)+1,0xFFFF)
  *DO WHILE LEN(_so)<128
  * _so=_so+CHR(255)
  *ENDDO
  thisform.com.output=StrToPackCRC(_so)
  thisform._seconds=SECONDS()
  thisform.step=1
 ELSE
  thisform.step=3
  thisform.addMessage("Все пакеты отправлены!"+CHR(13))
  thisform.btnSaveScen.Enabled=.t.
  this.Enabled=.f.
 ENDIF
ENDIF

*******************************************
*StrToPackCRC(_s)
LPARAMETERS _s
LOCAL _so,i,crc
*WAIT WINDOW TRANSFORM(LEN(_s))
IF LEN(_s)>0
 _so=chr(LEN(_s))+_s
 crc=0
 FOR i=1 TO LEN(_so)
  crc=crc+ASC(SUBSTR(_so,i,1))
  IF crc>255
   crc=crc-255
  ENDIF
 ENDFOR
ELSE
 _so=''
 crc=0
ENDIF
RETURN _so+CHR(crc)

***************************************
*NumToStr2revers.prg
LPARAMETERS _n
LOCAL _s
_s=CHR(MOD(_n,256))
_n=int(_n/256)
_s=_s+CHR(MOD(_n,256))
RETURN _s
*************************************************
С помощью команды "Get AOnOff\r\n" можно прочитать массив данных из контроллера
Команда возвращает время включения и отключения одного дня.
При каждом следующем выполнении команды день увеличивается
 */

}
