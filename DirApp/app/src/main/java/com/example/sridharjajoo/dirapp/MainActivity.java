package com.example.sridharjajoo.dirapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        findAdapter();
//        try {
//            openBluetooth();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        sendMessage();
    }

    private void findAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(this, "No bluetooth adapter available!", Toast.LENGTH_SHORT).show();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> st = bluetoothAdapter.getBondedDevices();
        Log.i("MainActivity.class", "findAdapter: " +st.size());
        if (st.size() > 0)
        {
            for(BluetoothDevice device : st)
            {
                Log.i("Names", "findAdapter: " + device.getName() + "\n");
                if(device.getName().equals("HC-05"))
                {
                    Toast.makeText(this, "avaiable", Toast.LENGTH_SHORT).show();
                    bluetoothDevice = device;
                    sendMessage();
                    break;
                }
            }
        }
    }

    private void openBluetooth() throws IOException{
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        bluetoothSocket.connect();
        outputStream = bluetoothSocket.getOutputStream();
        inputStream = bluetoothSocket.getInputStream();
        Log.i("MainActivity.class", "openBluetooth: " + outputStream.toString() + "\n" + inputStream.toString());
        beginListenForData();
//        sendMessage();
 }

    private void sendMessage() {
        Uri mUri = Uri.parse("smsto:+9660360554");
        String messageToSend = "I am in Danger\n: Lat : 25.425208\n Lon:81.887764 ";
        String number = "9660360554";
        SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);
        Toast.makeText(this, "Message Sent to your contacts!", Toast.LENGTH_SHORT).show();

        Uri mUri1 = Uri.parse("smsto:+9459858491");
        String messageToSend1 = "I am in Danger\n: Lat : 25.425208\n Lon:81.887764 ";
        String number1 = "9459858491";
        SmsManager.getDefault().sendTextMessage(number1, null, messageToSend1, null,null);
        Toast.makeText(this, "Message Sent to your contacts!", Toast.LENGTH_SHORT).show();
    }

    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                Log.i("HEllo1", "run: ");

                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    Log.i("HEllo2", "run: ");

                    try
                    {
                        int bytesAvailable = inputStream.available();
                        Log.i("HEllo3", "run: " + inputStream.available());
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    Log.i("MainActivity.class", "run: " + data);
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
//                                            myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
}
