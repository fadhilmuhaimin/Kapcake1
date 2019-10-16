package com.exomatik.kapcake.Activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.kapcake.Adapter.ListBluetooth;
import com.exomatik.kapcake.Featured.ItemClickSupport;
import com.exomatik.kapcake.Featured.PrinterCommands;
import com.exomatik.kapcake.Featured.Utils;
import com.exomatik.kapcake.Model.Menu;
import com.exomatik.kapcake.Model.ModelPesanan;
import com.exomatik.kapcake.Model.Pesanan;
import com.exomatik.kapcake.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GetKoneksi extends AppCompatActivity {
    public int sizeKertas = 32;
    private CheckBox cbEnable, cbVisible;
    private ImageButton btnSearch;
    private RecyclerView rcBluetooth;
    private TextView textNama;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> listDevice = new ArrayList<BluetoothDevice>();
    private ListBluetooth adapterBluetooth;
    public static ModelPesanan listPesanan;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    private static OutputStream outputStream;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private BroadcastReceiver receiverDevice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (listDevice.size() != 0) {
                    boolean tidakAda = true;

                    for (int a = 0; a < listDevice.size(); a++) {
                        if (listDevice.get(a).getAddress().equals(device.getAddress())) {
                            tidakAda = false;
                        }
                    }

                    if (tidakAda) {
                        listDevice.add(device);
                    }
                } else {
                    listDevice.add(device);
                }

                adapterBluetooth.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver receiverConnected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    for (int a = 0; a < listDevice.size(); a++) {
                        if (listDevice.get(a).getAddress().equals(device.getAddress())) {
                            adapterBluetooth.notifyItemChanged(a);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.activity_get_koneksi);

        init();
        cekDevice();
        setAdapter();

        onClick();

    }

    private void cekDevice() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiverConnected, filter);
    }

    private void init() {
        cbEnable = (CheckBox) findViewById(R.id.cb_enable);
        cbVisible = (CheckBox) findViewById(R.id.cb_visible);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        rcBluetooth = (RecyclerView) findViewById(R.id.rcBluetooth);
        textNama = (TextView) findViewById(R.id.text_nama);

        textNama.setText(getLocalBluetoothName());

        textNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    outputStream = mmSocket.getOutputStream();
                    byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                    outputStream.write(printformat);
                    tampilanHeaderAtas(listPesanan);
                    tampilanInformasiPesanan(listPesanan);
                    for (int a = 0; a < listPesanan.getPesanan().size(); a++){
                        tampilanMenu(listPesanan.getPesanan().get(a));
                    }
                    tampilanPembayaran(listPesanan);
                    tampilanLink();
                    tampilNote();


                    printCustom("\n \n \n \n", 0, 0);
                } catch (IOException e) {
                    Log.e("Error", e.getMessage().toString());
                }
            }
        });
    }

    private void tampilanHeaderAtas(ModelPesanan listData) {
        //foto
        printPhoto(R.drawable.one_step);

        //nama warkop
        printCustom("\n" + listData.getNamaOutlet(), 3, 1);

        //alamat
        printCustom("\n" + listData.getAlamat() + "\n", 1, 1);
        printCustom(listData.getTelpon(), 1, 1);

        //kop bawah
        printCustom("\n \n", 1, 1);
        printCustom(garis(), 1, 1);
        printCustom("\n", 1, 1);
        printCustom("No Antrian : " + Integer.toString(listData.getNoPemesanan()), 1, 1);
        printCustom("\n", 1, 1);
        printCustom(garis(), 1, 1);
    }

    private void tampilanInformasiPesanan(ModelPesanan listPesanan) {
        printCustom("\n", 1, 1);
        if (!(listPesanan.getTanggalProses().isEmpty())) {
            printCustom(listPesanan.getTanggalProses() + getDataSpace(listPesanan.getTanggalProses().length()
                    , listPesanan.getWaktuProses().length(), sizeKertas) + listPesanan.getWaktuProses(), 1, 0);
            printCustom("\n", 1, 1);
        }
        printCustom("Receipt" + getDataSpace("Receipt".length()
                , listPesanan.getKodePemesanan().length(), sizeKertas) + listPesanan.getKodePemesanan(), 1, 0);
        printCustom("\n", 1, 1);
        printCustom("No. Pesanan" + getDataSpace("No. Pesanan".length()
                , Integer.toString(listPesanan.getNoPemesanan()).length(), sizeKertas) + Integer.toString(listPesanan.getNoPemesanan()), 1, 0);
        if (!(listPesanan.getNamaPelayan().isEmpty())) {
            printCustom("\n", 1, 1);
            printCustom("Pelayan" + getDataSpace("Pelayan".length()
                    , listPesanan.getNamaPelayan().length(), sizeKertas) + listPesanan.getNamaPelayan(), 1, 0);
        }
        printCustom("\n", 1, 1);
        printCustom("Kasir" + getDataSpace("Kasir".length()
                , listPesanan.getNamaUser().length(), sizeKertas) + listPesanan.getNamaUser(), 1, 0);
        printCustom("\n", 1, 1);
        printCustom(garis(), 1, 1);
    }

    private void tampilanMenu(Pesanan modelJenisPemesanan) {
        printCustom("\n", 0, 1);
        printCustom("*" + modelJenisPemesanan.getNamaTipePenjualan() + "*", 1, 1);
        for (int a = 0; a < modelJenisPemesanan.getMenu().size(); a++) {
            printCustom("\n", 0, 1);
            tampilanMakanan(modelJenisPemesanan.getMenu().get(a));
        }
    }

    private void tampilanMakanan(Menu modelMakanan) {
        String jumlah = modelMakanan.getJumlah() + "x   ";
        printCustom(modelMakanan.getNamaMenu(), 1, 0);
        printCustom("\n", 0, 0);
        if (!(modelMakanan.getNamaVariasiMenu().isEmpty())) {
            printCustom(modelMakanan.getNamaVariasiMenu(), 0, 0);
            printCustom("\n", 0, 0);
        }
        printCustom(jumlah + modelMakanan.getHarga() + getDataSpace(jumlah.length() +
                modelMakanan.getHarga().length(), modelMakanan.getTotal().length(), sizeKertas)
                + modelMakanan.getTotal(), 0, 0);
        printCustom("\n", 0, 0);
    }

    private void tampilanPembayaran(ModelPesanan listPesanan) {
        printCustom(garis(), 0, 0);

        printCustom("Subtotal" + getDataSpace("Subtotal".length(), listPesanan.getSubtotal().length(), sizeKertas)
                + listPesanan.getSubtotal(), 0, 0);
        printCustom("\n", 0, 1);

        if (!(listPesanan.getNamaDiskon().isEmpty())) {
            String tip = listPesanan.getNamaDiskon() + "(" + listPesanan.getJumlahDiskon() + ")";
            printCustom(tip + getDataSpace(tip.length(), listPesanan.getTotalDiskon().length(), sizeKertas)
                    + listPesanan.getTotalDiskon(), 0, 0);
            printCustom("\n", 0, 0);
        }

        if (!(listPesanan.getNamaBiayaTambahan().isEmpty())) {
            String tip = listPesanan.getNamaBiayaTambahan() + "(" + listPesanan.getJumlahBiayaTambahan() + ")";
            printCustom(tip + getDataSpace(tip.length(), listPesanan.getTotalBiayaTambahan().length(), sizeKertas)
                    + listPesanan.getTotalBiayaTambahan(), 0, 0);
            printCustom("\n", 0, 0);
        }

        if (!(listPesanan.getNamaPajak().isEmpty())) {
            String pajak = listPesanan.getNamaPajak() + "(" + listPesanan.getJumlahPajak() + ")";
            printCustom(pajak + getDataSpace(pajak.length(), listPesanan.getTotalPajak().length(), sizeKertas)
                    + listPesanan.getTotalPajak(), 0, 0);
            printCustom("\n", 0, 0);
        }
        printCustom(garis(), 0, 1);
        printCustom("\n", 0, 0);

        printCustom("Total" + getDataSpace("Total".length(), listPesanan.getTotal().length(), sizeKertas)
                + listPesanan.getTotal(), 1, 0);
        printCustom("\n", 0, 0);
        printCustom(garis(), 1, 0);

        printCustom("\n", 0, 0);

        printCustom("Cash" + getDataSpace("Cash".length(), listPesanan.getTunai().length(), sizeKertas)
                + listPesanan.getTunai(), 0, 0);

        printCustom("\n", 0, 0);

        printCustom("Change" + getDataSpace("Change".length(), listPesanan.getKembalian().length(), sizeKertas)
                + listPesanan.getKembalian(), 0, 0);
        printCustom("\n", 0, 0);

        printCustom(garis(), 1, 0);
    }

    private void tampilanLink() {
        if (!(listPesanan.getWebsite().isEmpty())) {
            printCustom("\n", 0, 0);
            printCustom("WS : " + listPesanan.getWebsite(), 0, 0);
        }
        if (!(listPesanan.getFacebook().isEmpty())){
            printCustom("\n", 0, 0);
            printCustom("FB : " + listPesanan.getFacebook(), 0, 0);
        }
        if (!(listPesanan.getTwitter().isEmpty())){
            printCustom("\n", 0, 0);
            printCustom("TW : " + listPesanan.getTwitter(), 0, 0);
        }
        if (!(listPesanan.getInstagram().isEmpty())){
            printCustom("\n", 0, 0);
            printCustom("IG : " + listPesanan.getInstagram(), 0, 0);
        }
    }

    private void tampilNote(){
        printCustom("\n", 0, 0);
        printCustom(garis(), 1, 1);

        printCustom("\n", 1, 1);
        printCustom(listPesanan.getCatata(), 1, 1);

        //alamat
        printCustom("", 1, 1);
    }

    private String getDataSpace(int lengthKiri, int lengthKanan, int size) {
        String hasil = "";
        int jumlah = 0, hasil_jumlah;

        jumlah = lengthKanan + lengthKiri;
        hasil_jumlah = size - jumlah;

        for (int a = 0; a < hasil_jumlah; a++) {
            hasil = hasil + " ";
        }

        return hasil;
    }

    private String garis() {
        String garis1 = "-";
        for (int a = 0; a < 31; a++) {
            garis1 = garis1 + "-";
        }
        return garis1;
    }

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                mmOutputStream.write(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", e.getMessage().toString());
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
                case 4:
                    outputStream.write(PrinterCommands.ESC_ITALIC);
                    break;
                case 5:
                    outputStream.write(PrinterCommands.SELECT_FONT_A);
                    break;
            }
            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setAdapter() {
        adapterBluetooth = new ListBluetooth(listDevice, GetKoneksi.this, GetKoneksi.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(GetKoneksi.this, 1, false);
        rcBluetooth.setLayoutManager(localLinearLayoutManager);
        rcBluetooth.setNestedScrollingEnabled(false);
        rcBluetooth.setAdapter(adapterBluetooth);

    }

    private void onClick() {
        cbEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    bluetoothAdapter.disable();
                    Toast.makeText(GetKoneksi.this, "Bluetooth Disable", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(GetKoneksi.this, "Turned On", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cbVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, 0);
                    Toast.makeText(GetKoneksi.this, "Visible for 2 min", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDevice.removeAll(listDevice);
                btnDiscover();
            }
        });

        ItemClickSupport.addTo(rcBluetooth).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                bluetoothAdapter.cancelDiscovery();
                adapterBluetooth.notifyItemChanged(position);

                if (listDevice.get(position).getBondState() == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(GetKoneksi.this, "Sudah terhubung", Toast.LENGTH_SHORT).show();
                    try {
                        openBT(position);
                    } catch (IOException e) {
                        Log.e("Error", e.getMessage().toString());
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        listDevice.get(position).createBond();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openBT(int position) throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = listDevice.get(position).createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Log.e("Bluetooth", "Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                                Log.e("Data", data);
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

    public void btnDiscover() {
        Log.e("Looking", "For unpaired Device");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.e("btn", "Cancel Discover");

            cekPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiverDevice, filter);
        }

        if (!bluetoothAdapter.isDiscovering()) {
            cekPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiverDevice, filter);
        }
    }

    private void cekPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.e("Tag", "No Need permission < Lollipop");
        }
    }

    private String getLocalBluetoothName() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        String name = bluetoothAdapter.getName();

        if (name == null) {
            name = bluetoothAdapter.getAddress();
        }

        return name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiverDevice);
        unregisterReceiver(receiverConnected);
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
