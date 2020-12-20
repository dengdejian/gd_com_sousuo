package com.example.my_gd_com_splites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;

public class RecyAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<PoiItem> list;

    public RecyAdapter(Context context, ArrayList<PoiItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.buju, parent, false);
        return new ViewHolder1(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PoiItem poiItem = list.get(position);
        ViewHolder1 viewHolder1 = (ViewHolder1) holder;
        viewHolder1.title.setText(poiItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ViewHolder1 extends RecyclerView.ViewHolder {

        private TextView title;

        public ViewHolder1(View inflate) {
            super(inflate);
            title = inflate.findViewById(R.id.title);

        }
    }
}
