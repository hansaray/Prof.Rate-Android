package com.simpla.turnedTables.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simpla.turnedTables.Objects.StatsObject;
import com.simpla.turnedTables.R;

import java.util.ArrayList;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsAdapterHolder> {
    private ArrayList<StatsObject> list;

    public StatsAdapter(ArrayList<StatsObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StatsAdapter.StatsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item,parent,false);
        return new StatsAdapter.StatsAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsAdapter.StatsAdapterHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StatsAdapterHolder extends RecyclerView.ViewHolder{

        private TextView name,num;

        public StatsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stItemName);
            num= itemView.findViewById(R.id.stItemNum);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setData(final StatsObject object){
            num.setText(String.valueOf(object.getNum()));
            name.setText(nameFix(object.getName()));
        }

        private String nameFix(String name){
            switch (name) {
                case "Canakkale":
                    return "Çanakkale";
                case "Çanakkale":
                    return "Canakkale";
                case "Cankırı":
                    return "Çankırı";
                case "Çankırı":
                    return "Cankırı";
                case "Corum":
                    return "Çorum";
                case "Çorum":
                    return "Corum";
                case "Istanbul":
                    return "İstanbul";
                case "İstanbul":
                    return "Istanbul";
                case "Izmir":
                    return "İzmir";
                case "İzmir":
                    return "Izmir";
                case "Sanlıurfa":
                    return "Şanlıurfa";
                case "Şanlıurfa":
                    return "Sanlıurfa";
                case "Sırnak":
                    return "Şırnak";
                case "Şırnak":
                    return "Sırnak";
                default:
                    return name;
            }
        }
    }
}
