package com.exomatik.kapcake.Featured;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.exomatik.kapcake.Model.Menu;
import com.exomatik.kapcake.Model.ModelPesanan;
import com.exomatik.kapcake.Model.Pesanan;
import com.exomatik.kapcake.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by IrfanRZ on 20/08/2019.
 */

public class Print {
    private int sizeKertas = 32;
    private BluetoothDevice bluetoothDevice;
    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private volatile boolean stopWorker;
    private BluetoothSocket mmSocket;
    private byte[] readBuffer;
    private int readBufferPosition;
    private Thread workerThread;

    public Print(BluetoothDevice bluetoothDevice, Activity activity, BluetoothAdapter bluetoothAdapter) {
        this.bluetoothDevice = bluetoothDevice;
        this.activity = activity;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void printData(ModelPesanan listPesanan){
        try {
            mmOutputStream= mmSocket.getOutputStream();
            byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
            mmOutputStream.write(printformat);
            tampilanHeaderAtas(listPesanan);
            tampilanInformasiPesanan(listPesanan);
            for (int a = 0; a < listPesanan.getPesanan().size(); a++){
                tampilanMenu(listPesanan.getPesanan().get(a));
            }
            tampilanPembayaran(listPesanan);
            tampilanLink(listPesanan);
            tampilNote(listPesanan);


            printCustom("\n \n \n \n", 0, 0);
        } catch (IOException e) {
            Log.e("Error", e.getMessage().toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void tesPrint(String namaOutlet, String hp, String alamat, String tipe, String macAddress){
        printPhoto(R.drawable.logo_kue);
        printCustom("\n", 3, 1);
        printCustom(namaOutlet, 3, 1);
        printCustom("\n", 0, 1);
        printCustom(alamat, 0, 1);
        printCustom("\n", 0, 1);
        printCustom(hp, 0, 1);
        printCustom("\n \n", 1, 1);
        printCustom("PRINTER TEST", 1, 1);
        printCustom("\n \n", 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String currentDateandTime = sdf.format(new Date());
        printCustom(currentDateandTime, 1, 1);
        printCustom("\n", 1, 1);
        printCustom(garis2(), 1, 1);
        printCustom("\n", 1, 1);
        printCustom(garis(), 1, 1);
        printCustom("\n", 1, 1);
        printCustom("Tipe : " + getDataSpace("Tipe : ".length(), tipe.length(), sizeKertas)
                + tipe, 0, 0);
        printCustom("\n", 1, 1);
        printCustom("Alamat : " + getDataSpace("Alamat : ".length(), macAddress.length(), sizeKertas)
                + macAddress, 0, 0);
        printCustom("\n", 1, 1);
        printCustom(garis2(), 1, 1);
        printCustom("\n", 1, 1);
        printCustom(garis(), 1, 1);
        printCustom("\n", 1, 1);


        printCustom("\n \n \n", 1, 1);
    }

    private void tampilanHeaderAtas(ModelPesanan listData) {
        //foto
        printPhoto(R.drawable.logo_kue);

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

    private void tampilanLink(ModelPesanan listPesanan) {
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

    private void tampilNote(ModelPesanan listPesanan){
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

    private String garis2() {
        String garis1 = "_";
        for (int a = 0; a < 31; a++) {
            garis1 = garis1 + "_";
        }
        return garis1;
    }

    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Log.e("Stopped", "Connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Log.e("Bluetooth", "Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginListenForData() {
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
                                                Log.e("Data Send to", data);
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

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
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
    public void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    mmOutputStream.write(cc);
                    break;
                case 1:
                    mmOutputStream.write(bb);
                    break;
                case 2:
                    mmOutputStream.write(bb2);
                    break;
                case 3:
                    mmOutputStream.write(bb3);
                    break;
                case 4:
                    mmOutputStream.write(PrinterCommands.ESC_ITALIC);
                    break;
                case 5:
                    mmOutputStream.write(PrinterCommands.SELECT_FONT_A);
                    break;
            }
            switch (align) {
                case 0:
                    //left align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    mmOutputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            mmOutputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void hubungkanDevice(){
        bluetoothAdapter.cancelDiscovery();

        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            Toast.makeText(activity, "Sudah terhubung", Toast.LENGTH_SHORT).show();
            try {
                Log.e("Connecting","trying");
                openBT();
            } catch (IOException e) {
                Log.e("Error", e.getMessage().toString());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bluetoothDevice.createBond();
            }
        }
    }
}
