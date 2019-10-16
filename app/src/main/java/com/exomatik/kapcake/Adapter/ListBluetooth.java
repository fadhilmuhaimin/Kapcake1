package com.exomatik.kapcake.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.kapcake.R;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class ListBluetooth extends RecyclerView.Adapter<ListBluetooth.bidangViewHolder> {
    private ArrayList<BluetoothDevice> dataList;
    private Context context;
    private ProgressDialog progressDialog = null;
    private Activity activity;

    public ListBluetooth(ArrayList<BluetoothDevice> dataList, Context context, Activity activity) {
        this.dataList = dataList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_device, parent, false);

        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(bidangViewHolder holder, final int position) {
        holder.textNama.setText(dataList.get(position).getName());
        holder.textMac.setText("Mac Address : " + dataList.get(position).getAddress());

        if (dataList.get(position).getBondState() == BluetoothDevice.BOND_BONDED){
            holder.imgBonded.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgBonded.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama, textMac;
        private ImageView imgBonded;

        public bidangViewHolder(View itemView) {
            super(itemView);

            textNama = (TextView) itemView.findViewById(R.id.textNama);
            textMac = (TextView) itemView.findViewById(R.id.textMac);
            imgBonded = (ImageView) itemView.findViewById(R.id.imgBonded);
        }
    }
}
