package ru.android.bluetooth.schedule;

/**
 * Created by dinar on 10.08.17.
 */

public class S {

   /* public S() {
        if (strstrf(buffRxManual0,"set data\r")){
            tmp1=SetData(); // переход на функцию приниема данных
            if(tmp1){
                putUInt(tmp1,1);
                putStr("\r\n");
            }else{
                putStrf((flash char *)prnOk);
            }
        }
    }

//Запись данных во Flash память (блокирующая)
    unsigned char SetData(void){
        unsigned char ret;
        unsigned long int LenData,i;
        ret=GetPacket(0xFF);
//читаем длину передаваемых данных
        if(ret) return ret;
        if(Packet.lenData!=2) return 4; //если длина пакета данных не равна 2, выходим с ошибкой
//длины пакета
        LenData=Packet.data[0];
        LenData=(LenData<<8)+Packet.data[1];
        if(LenData>1464) return 5; //если длина пекета данных превосходит объём микросхемы-
//резерв, выходим с ошибкой
        else if(LenData==0) return 7; //величина передаваемых данных, указанных в пакете, равна
//0 (нулю)
        Wait.Beep=15;
        putStr("Ok\r"); //отправляем сообщение
        i=0;
//
        while(1){
            ret=GetPacket(0xFF);
//читаем пакет данных
            if(ret){
//вернулась ошибка?
                if(ret==3){
//ошибка КС?
                    putStr("Er 3\r"); //отправляем сообщение об ошибке КС
                }else return ret; //иначе выходим с ошибкой
            }
            if(ret!=3){
//нет ощибки контрольной суммы?
                if((i+Packet.lenData)>LenData) return 8; //Общее, пришедшее количество данных
//превысило указанное в пакете _SetData
                ret=write_flash(Packet.data,128,StartATable+i);
                if(ret==0) return 9; //данные записались с ошибкой!
                i+=Packet.lenData;
// наращиваем адрес на длину принятых данных
                Wait.Beep=15;
                putStr("Ok\r");
// посылаем ответ об успешном получении пакета
                if(i>=LenData) return 0; //если данные закончились, выходим из процедуры
            }
        }
    }//Получение пакета с UART0
    unsigned char GetPacket(unsigned char ClrPacket){
        unsigned int crc;
        unsigned char i;
        Wait.DataUart=128;
        Packet.Er=0;
        while(Wait.DataUart){
//принимаем байт длины
            if (rx_counter0){
                Packet.lenData=getchar();
                Wait.DataUart=128;
                if(Packet.lenData>MaxLenDataInPacket) return 2; //ошибка в длине пекета
                crc=Packet.lenData;
                break;
            }
        }
        i=0;
        while(Wait.DataUart){
//принимаем данные
            if (rx_counter0){
                Packet.data[i]=getchar();
                Wait.DataUart=128;
                crc+=Packet.data[i];
                if(crc>255) crc-=255;
                if(++i>=Packet.lenData) break; //если закончились данные, выходим из цикла
            }
        }
        while(Wait.DataUart){
//принимаем контрольную сумму
            if (rx_counter0){
                Packet.CRC=getchar();
                if(Packet.CRC==crc) break; else return 3;
            }
        }
        if(Wait.DataUart==0){
            return 1;
//ошибка таймаута приёма данных
        }else{
            for(i=Packet.lenData;i<MaxLenDataInPacket;i++) Packet.data[i]=ClrPacket; //дозаполняем пекет
            return 0;
//данные приняты успешно
        }
}
    */
}
