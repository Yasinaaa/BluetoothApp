package ru.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import org.junit.Test;

import java.util.UUID;

import ru.android.bluetooth.bluetooth.BluetoothMessage;
import ru.android.bluetooth.bluetooth.ConnectedThread;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBTAdapter;
    private BluetoothMessage mBluetoothMessage;
    private Handler mHandler;
    private BluetoothSocket mBTSocket = null;
    private ConnectedThread mConnectedThread;

    @Test
    public void test(){
        //System.out.println(crc(4));
        String newNumBinary = Integer.toBinaryString(4);
        System.out.println(newNumBinary);
        //assertNotNull(crc(4));
    }
    private byte crc(int num){
        String newNumBinary = Integer.toBinaryString(num);
        System.out.println(newNumBinary);
        byte answer = Byte.valueOf(newNumBinary);
        byte one = 1;
        byte t = (byte) (answer + one);

        return t;
    }
   /* @Test
    public void testBluetoothMessage() throws Exception {
        init();
        mBluetoothMessage.writeMessage(BluetoothCommands.DEBUG);

    }

    private void init(){

        this.mBluetoothMessage = BluetoothMessage.createBluetoothMessage();
        this.mHandler = mBluetoothMessage.getHandler();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        connectDevice();

        mBluetoothMessage.setBluetoothMessageListener(new BluetoothMessage.BluetoothMessageListener() {
            @Override
            public void onResponse(String answer) {
                assertFalse("is=0,920,494".equals(answer));
            }
        });
    }

    public void connectDevice(){

        final String address = "20:16:06:12:88:68";
        final String name = "yasina";

        new Thread()
        {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;

                }
                try {
                    mBTSocket.connect();
                } catch (IOException e) {

                    mHandler.obtainMessage(BluetoothCommands.CONNECTING_STATUS, -1, -1)
                            .sendToTarget();
                }
                if(fail == false) {
                    mConnectedThread = ConnectedThread.createConnectedThread(mBTSocket, mHandler);
                    mConnectedThread.start();
                    mBluetoothMessage.setConnectedThread(mConnectedThread);

                    mHandler.obtainMessage(BluetoothCommands.CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();

                }
            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }*/
}