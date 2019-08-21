package com.secretdevelopersltd.dexian.powerboxips;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String TAG = "XIAN";

    //USB VARIABLES
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    private int xVID = 9025; //NANO is the main circuit board
    //Arduino Uno 0x2341 , 9025 P 67
    //Nano 6790 P 7523
    //pro mini 1659


    //ALL UI OBJECTS
    private EditText ET_company, ET_product, ET_battADJ, ET_lowBatt, ET_fullChar, ET_reconnect, ET_pwmADJ, ET_acADJ, ET_charAMP;
    private Button btn_Done, btn_charAMP, btn_acADJ, btn_watt, btn_fullLoadSet, btn_noLoadSet, btn_pwmADJ, btn_reconnect, btn_connectionIndicator,
                    btn_fullChar, btn_lowBatt, btn_battADJ, btn_battery, btn_model, btn_product, btn_company;
    private Spinner sp_model, sp_battery, sp_watt;
    private SeekBar sb_battADJ, sb_lowBatt, sb_fullChar, sb_reconnect, sb_pwmADJ, sb_acADJ, sb_charAMP;
    private ImageButton IB_sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCreateArduinoReceiver();
        initialzeAllUI();
        allListeners();


    }

    private void initialzeAllUI(){

        sb_battADJ = findViewById(R.id.sb_battADJ);
        sb_lowBatt = findViewById(R.id.sb_lowBatt);
        sb_fullChar = findViewById(R.id.sb_fullChar);
        sb_reconnect = findViewById(R.id.sb_reconnect);
        sb_pwmADJ = findViewById(R.id.sb_pwmADJ);
        sb_acADJ = findViewById(R.id.sb_acADJ);
        sb_charAMP = findViewById(R.id.sb_charAMP);

        ET_company = findViewById(R.id.ET_company);
        ET_product = findViewById(R.id.ET_product);
        ET_battADJ = findViewById(R.id.ET_battADJ);
        ET_lowBatt = findViewById(R.id.ET_lowBatt);
        ET_fullChar = findViewById(R.id.ET_fullChar);
        ET_reconnect = findViewById(R.id.ET_reconnect);
        ET_pwmADJ = findViewById(R.id.ET_pwmADJ);
        ET_acADJ = findViewById(R.id.ET_acADJ);
        ET_charAMP = findViewById(R.id.ET_charAMP);

    }

    private void allListeners(){
        sb_battADJ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_battADJ.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_lowBatt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_lowBatt.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_fullChar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_fullChar.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_reconnect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_reconnect.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_pwmADJ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_pwmADJ.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_acADJ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_acADJ.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_charAMP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ET_charAMP.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }


    //Arduino
    UsbSerialInterface.UsbReadCallback mCallback;
    private BroadcastReceiver broadcastReceiver ;

    public boolean grantAutomaticPermission(UsbDevice usbDevice) {
        try
        {
            Context context= getApplicationContext();
            PackageManager pkgManager = context.getPackageManager();
            ApplicationInfo appInfo=pkgManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            Class serviceManagerClass=Class.forName("android.os.ServiceManager");
            Method getServiceMethod=serviceManagerClass.getDeclaredMethod("getService",String.class);
            getServiceMethod.setAccessible(true);
            android.os.IBinder binder=(android.os.IBinder)getServiceMethod.invoke(null, Context.USB_SERVICE);

            Class iUsbManagerClass=Class.forName("android.hardware.usb.IUsbManager");
            Class stubClass=Class.forName("android.hardware.usb.IUsbManager$Stub");
            Method asInterfaceMethod=stubClass.getDeclaredMethod("asInterface", android.os.IBinder.class);
            asInterfaceMethod.setAccessible(true);
            Object iUsbManager=asInterfaceMethod.invoke(null, binder);


            System.out.println("UID : " + appInfo.uid + " " + appInfo.processName + " " + appInfo.permission);
            final Method grantDevicePermissionMethod = iUsbManagerClass.getDeclaredMethod("grantDevicePermission", UsbDevice.class,int.class);
            grantDevicePermissionMethod.setAccessible(true);
            grantDevicePermissionMethod.invoke(iUsbManager, usbDevice,appInfo.uid);


            System.out.println("Method OK : " + binder + "  " + iUsbManager);
            return true;
        }
        catch(Exception e)
        {
            System.err.println("Error trying to assing automatic usb permission : ");
            e.printStackTrace();
            return false;
        }
    }

    public void startArduino(){

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == xVID)//Arduino Vendor ID
                {
                    //device.getProductId()
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi); //26 May 2019
                    /*if(usbManager.hasPermission(device)){
                        Toast.makeText(getApplicationContext(),"HASH PERMISSION", Toast.LENGTH_LONG).show();
                    }else{
                        usbManager.requestPermission(device, pi); //26 May 2019
                        grantAutomaticPermission(device);
                    }*/
                    //Toast.makeText(getApplicationContext(),"PID : "+device.getProductId()+" VID : "+xVID+" DID : "+device.getDeviceId()+" NAME : "+device.getDeviceName(), Toast.LENGTH_LONG).show();

                    keep = false;




                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }

    }
    public void sendArduino(String string) {

        try{
            serialPort.write(string.getBytes());
            Log.i(TAG,"ARDUINO SEND "+string);
        }catch (Exception e){
            Log.i(TAG,""+e);
            Log.i(TAG,"ARDUINO FAILED "+string);
        }
        //tvAppend(textView, "\nData Sent : " + string + "\n");

    }
    public void stopArduino() {


        serialPort.close();
        display("Connection Closed!");

    }

    public void onCreateArduinoReceiver(){

        //Ardiono
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);

        broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) {
                                //Set Serial Connection Parameters.
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);
                                //tvAppend(textView,"Serial Connection Opened!\n"+xix);
                                display("Connection Opened!\n");



                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                                display("PORT NOT OPEN");

                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                            display("PORT IS NULL");

                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                        display("PERM NOT GRANTED");

                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    startArduino();
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    try{
                        if(!serialPort.equals(null) && serialPort.open()){
                            stopArduino();
                        }

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"ERROR CODE#03: WRONG CIRCUIT BOARD!",Toast.LENGTH_LONG).show();
                    }

                }
            }

            ;
        };

        mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
            @Override
            public void onReceivedData(byte[] arg0) {
                String data = null;

                try {
                    data = new String(arg0, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }


            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        startArduino();

    }

    //Arduino Ends

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
