package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import utils.ConvertUtil;

public class PrintMobile extends Activity {

    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private final Charset UTF32 = Charset.forName("UTF32");
    String namePrinter = "B50 Printer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_mobile);

        // we are going to have three buttons for specific functions
        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
// text label and input box
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);

        // open bluetooth connection
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // send data typed by the user to be printed
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // close bluetooth connection
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Đã tắt Bluetooth.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {

            // the text typed by the user
            //String msg = myTextbox.getText().toString();

            //  String msg = "Cộng hòa xã hội viet nam";
            // msg += "\n";

            // mmOutputStream.write(msg.getBytes("UNICODE"));

            byte[] cc = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
            byte[] bb1 = new byte[]{0x1B, 0x21, 0x4};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
            //  print_bar_code("12345678");
            String msg = ConvertUtil.InsertBlank("CÔNG TY CP CẤP NƯỚC HẢI PHÒNG", 32, 0);
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.InsertBlank("BIÊN NHẬN", 32, 0);
            msg += "\n";
            msg += ConvertUtil.InsertBlank("THANH TOÁN TIỀN NƯỚC", 32, 0);
            mmOutputStream.write(bb3);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.InsertBlank("(Tháng: 03/2017)", 32, 0);
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));
           // msg = ConvertUtil.InsertBlank("Từ: 01/03/2017" , 16, 1) + ConvertUtil.InsertBlank("Đến: 31/03/2017", 16, 2);
           // msg += "\n";
           // mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.ConvertToStringAndNewLine("Tên KH: ĐỖ TIẾN DƯƠNG");
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));



            msg = ConvertUtil.ConvertToStringAndNewLine("Địa chỉ: 723-Hải Thành - Dương Kinh - Hải Phòng");
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = "DBĐD: " + "6123457" + "\n";
            Date tungay = new Date();
            Date denngay = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            msg = "Từ : " + ConvertUtil.InsertBlank(sdf1.format(tungay), 24, 1) + "\n";
            msg += "Đến: " + ConvertUtil.InsertBlank(sdf1.format(denngay), 24, 1) + "\n";
            msg += "\n";

            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32)) ;

            msg = "  " + ConvertUtil.InsertBlank("CS Cũ", 9, 0) + ConvertUtil.InsertBlank("CS Mới", 9, 0) + ConvertUtil.InsertBlank("KLTT", 9, 0);
            msg += "\n";
            msg += "  " + ConvertUtil.InsertBlank("132", 9, 0) + ConvertUtil.InsertBlank("156", 9, 0) + ConvertUtil.InsertBlank("24" + "(m3)", 9, 0);
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.getSpaces(32, "-");
            msg += "Tiền nước:" ;

            msg += "\n";
            msg += ConvertUtil.InsertBlank("KL", 5, 0) + ConvertUtil.InsertBlank("Giá", 7, 0) + ConvertUtil.InsertBlank("T.Tiền", 10, 0) + ConvertUtil.InsertBlank("VAT", 10, 0);
            msg += "\n";
            if (10 > 0) {
                msg +=  ConvertUtil.InsertBlank("10", 5, 0) + ConvertUtil.InsertBlank("10000", 7, 0) + ConvertUtil.InsertBlank("100000", 10, 0) + ConvertUtil.InsertBlank("2160", 10, 0);;
                msg += "\n";
            }
            if (12 > 0) {
                msg +=  ConvertUtil.InsertBlank("12", 5, 0) + ConvertUtil.InsertBlank("15000", 7, 0) + ConvertUtil.InsertBlank("18000", 10, 0) + ConvertUtil.InsertBlank("2160", 10, 0);;
                msg += "\n";
            }
            msg+=" DVTN:" ;
            msg+="\n";
            msg+= ConvertUtil.InsertBlank("10", 5, 0) + ConvertUtil.InsertBlank("10000", 7, 0) + ConvertUtil.InsertBlank("100000", 10, 0) + ConvertUtil.InsertBlank("2160", 10, 0);;
            msg+="\n";
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg =  ConvertUtil.getSpaces(32, "-");
            msg += "Tổng tiền:" + ConvertUtil.InsertBlank("280000", 15, 1);
            msg += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));
            msg = "Hai trăm tám mươi nghìn đồng";
            msg += "\n";
            msg += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            mmOutputStream.write(cc);
            msg = "Ngày TB  :" + ConvertUtil.InsertBlank(sdf.format(date), 24, 1);
            msg += "\n";
            msg +="Nhân viên:" + ConvertUtil.InsertBlank( "Phạm Nhật Vượng", 27, 1);
            msg += "\n";
            msg += ConvertUtil.getSpaces(32, "-");
            msg += "\n";

            //Date temp=sdf1.parse("01/"+kyhd);
            /*Calendar cal = Calendar.getInstance();
            cal.setTime(temp);
            cal.add(Calendar.MONTH, 1);
            Date thutungay=cal.getTime();
            cal.setTime(thutungay);
            cal.add(Calendar.DAY_OF_MONTH, 9);

            Date date1 = new Date();
            SimpleDateFormat sdfhethan = new SimpleDateFormat("dd/MM/yyyy");

            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date thudenngay=cal.getTime();*/
            msg += ConvertUtil.ConvertToStringAndNewLine("* Khách hàng vui lòng truy cập website: www.hddt.capnuochaiphong.com.vn để lấy hóa đơn điện tử.");
            msg += "\n";
            msg += "\n";
            msg += "\n";
            msg += "\n";
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));
            mmOutputStream.flush();
            // tell the user data were sent
            myLabel.setText("Đã in.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("Máy không có bluetooth");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    Log.d("Name Printer",device.getName());

                    if (device.getName().equals(namePrinter)) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Đã bật bluetooth trên thiết bị.");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void openBT() throws IOException {
        try {

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();

            myLabel.setText("Sẵn sàng in.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        //final String data = new String(encodedBytes, "US-ASCII");
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
