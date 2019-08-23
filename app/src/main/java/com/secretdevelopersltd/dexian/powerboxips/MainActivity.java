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
import android.view.View;
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

    private int xVID = 1659; //NANO is the main circuit board
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

    public void btnClicked(View view) {
        double p = 0.0;
        String temp = "";
        switch (view.getId()){

            case R.id.btn_Done:
                sendArduino("EXIT");
                display("EXIT");
                break;

            case R.id.btn_charAMP:
                p = Double.parseDouble(ET_charAMP.getText().toString());
                sendArduino("AMPADJ="+p+"\n"); //confusion
                display("Setting AMPADJ="+p);

                break;

            case R.id.btn_acADJ:
                p = Double.parseDouble(ET_acADJ.getText().toString());
                sendArduino("ACADJ="+p+"\n");
                display("Setting ACADJ="+p);
                break;

            case R.id.btn_watt:
                temp = sp_watt.getSelectedItem().toString();
                sendArduino("WATT="+temp+"\n");
                display("Setting WATT="+temp);
                break;

            case R.id.btn_fullLoadSet:
                sendArduino("FULLLOAD"+"\n");
                display("Setting FULLLOAD");
                break;

            case R.id.btn_noLoadSet:
                sendArduino("NOLOAD"+"\n");
                display("Setting NOLOAD");
                break;

            case R.id.btn_pwmADJ:
                p = Double.parseDouble(ET_pwmADJ.getText().toString());
                sendArduino("PWMADJ="+p+"\n");
                display("Setting PWMADJ="+p);
                break;

            case R.id.btn_reconnect:
                p = Double.parseDouble(ET_reconnect.getText().toString());
                sendArduino("RECONNECT="+p+""+"\n");
                display("Setting RECONNECT="+p);
                break;

            case R.id.btn_connectionIndicator:

                break;

            case R.id.btn_fullChar:
                p = Double.parseDouble(ET_fullChar.getText().toString());
                sendArduino("FULLCHAR="+p+""+"\n");
                display("Setting FULLCHAR="+p);
                break;

            case R.id.btn_lowBatt:
                 p = Double.parseDouble(ET_lowBatt.getText().toString());
                sendArduino("LOWBATT="+p+""+"\n");
                display("Setting LOWBATT="+p);
                break;

            case R.id.btn_battADJ:
                p = Double.parseDouble(ET_battADJ.getText().toString());
                sendArduino("BATTADJ="+p+""+"\n");
                display("Setting BATTADJ="+p);
                break;

            case R.id.btn_battery:
                temp = sp_battery.getSelectedItem().toString();
                sendArduino(""+temp+"\n");
                display("Setting "+temp);
                break;

            case R.id.btn_model:
                temp = sp_model.getSelectedItem().toString();
                for(int i=temp.length();i<15;i++){
                    temp += " ";
                }
                temp += ".";

                sendArduino("VERSION=MODEL:"+temp+"\n");
                display("Setting MODEL:"+temp);
                break;

            case R.id.btn_product:
                temp = ET_product.getText().toString();

                if(temp.length() == 16){
                    sendArduino("BRAND="+temp+"\n");
                }else{

                    display("16 Character Please");
                }

                break;

            case R.id.btn_company:
                temp = ET_company.getText().toString();

                if(temp.length() == 16){
                    sendArduino("COMPANY="+temp+"\n");
                }else{

                    display("16 Character Please");
                }

                break;

            case R.id.IB_sync:
                sendArduino("PARAMS");
                display("DATA SYNC!");
                break;

            default:
                break;

        }
    }

    private void initialzeAllUI(){


        sp_model = findViewById(R.id.sp_model);
        sp_battery = findViewById(R.id.sp_battery);
        sp_watt = findViewById(R.id.sp_watt);

        IB_sync = findViewById(R.id.IB_sync);

        btn_Done = findViewById(R.id.btn_Done);
        btn_charAMP = findViewById(R.id.btn_charAMP);
        btn_acADJ = findViewById(R.id.btn_acADJ);
        btn_watt = findViewById(R.id.btn_watt);
        btn_fullLoadSet = findViewById(R.id.btn_fullLoadSet);
        btn_noLoadSet = findViewById(R.id.btn_noLoadSet);
        btn_pwmADJ = findViewById(R.id.btn_pwmADJ);
        btn_reconnect = findViewById(R.id.btn_reconnect);
        btn_connectionIndicator = findViewById(R.id.btn_connectionIndicator);
        btn_fullChar = findViewById(R.id.btn_fullChar);
        btn_lowBatt = findViewById(R.id.btn_lowBatt);
        btn_battery = findViewById(R.id.btn_battery);
        btn_battADJ = findViewById(R.id.btn_battADJ);
        btn_model = findViewById(R.id.btn_model);
        btn_product = findViewById(R.id.btn_product);
        btn_company = findViewById(R.id.btn_company);

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

        ET_battADJ.setText(""+sb_battADJ.getProgress());
        ET_lowBatt.setText(""+sb_lowBatt.getProgress());
        ET_fullChar.setText(""+sb_fullChar.getProgress());
        ET_reconnect.setText(""+sb_reconnect.getProgress());
        ET_pwmADJ.setText(""+sb_pwmADJ.getProgress());
        ET_acADJ.setText(""+sb_acADJ.getProgress());
        ET_charAMP.setText(""+sb_charAMP.getProgress());



    }

    private void allListeners(){
        sb_battADJ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double tp = (double) progress / 10;

                ET_battADJ.setText(""+tp);
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
                double tp = (double) progress / 10;
                ET_lowBatt.setText(""+tp);
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
                double tp = (double) progress / 10;
                ET_fullChar.setText(""+tp);
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
                double tp = (double) progress / 10;
                ET_reconnect.setText(""+tp);
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
                double tp = (double) progress;
                ET_pwmADJ.setText(""+tp);
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
                double tp = (double) progress;
                ET_acADJ.setText(""+tp);
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
                double tp = (double) progress;
                ET_charAMP.setText(""+tp);
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
                                //display("Connection Opened!\n");
                                display("Device Connected\n");


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

                    if(data.length() != 0){

                        displayWithNewThread("RECEIVED : "+data);

                        if(data.equals("AT")){
                            sendArduino("OK");
                        }else if(data.substring(0,2).equals("COM")){
                            //"COMPANY=" + company
                        }else if(data.substring(0,2).equals("BRA")){
                            //"BRAND=" + brand
                        }else if(data.substring(0,2).equals("VER")){

                            //"VERSION=" + version
                        }else if(data.substring(0,4).equals("BATTM")){
                            //"BATTMODE=" + battMode

                        }else if(data.substring(0,4).equals("BATTA")){
                            //"BATTADJ=" + battFactor

                        }else if(data.substring(0,2).equals("LOW")){
                            //"LOWBATT=" + lowBatt

                        }else if(data.substring(0,3).equals("PWM=")){
                            //"PWM=" + pwmAdc

                        }else if(data.substring(0,3).equals("PWMA")){
                            //"PWMADJ=" + pwmFactor

                        }else if(data.substring(0,2).equals("WAT")){
                            //"WATT=" + maxWatt

                        }else if(data.substring(0,2).equals("ACA")){
                            //"ACADJ=" + acFactor

                        }else if(data.substring(0,2).equals("AMP")){
                            //"AMPADJ=" + ampFactor

                        }else if(data.substring(0,2).equals("FUL")){
                            //"FULLCHARGE=" + fullCharge

                        }else if(data.substring(0,2).equals("REC")){
                            //"RECONNECT=" + reConnect

                        }

                    }

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

    private void displayWithNewThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
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
