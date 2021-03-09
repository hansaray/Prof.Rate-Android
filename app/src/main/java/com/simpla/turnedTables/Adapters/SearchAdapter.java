package com.simpla.turnedTables.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.simpla.turnedTables.ClickedProfActivity;
import com.simpla.turnedTables.Objects.SearchObject;
import com.simpla.turnedTables.R;
import com.simpla.turnedTables.Utils.DoubleFormat;
import com.simpla.turnedTables.Utils.RatingColor;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterHolder> {
    private Context mContext;
    private ArrayList<SearchObject> list;

    public SearchAdapter(Context mContext, ArrayList<SearchObject> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
        return new SearchAdapter.SearchAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchAdapterHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SearchAdapterHolder extends RecyclerView.ViewHolder{
        private CircleImageView image;
        private TextView name,field,city,rating;
        private CardView cardView;
        public SearchAdapterHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.sItemImage);
            cardView = itemView.findViewById(R.id.sItemCard);
            name = itemView.findViewById(R.id.sItemName);
            field = itemView.findViewById(R.id.sItemFieldName);
            city = itemView.findViewById(R.id.sItemCityName);
            rating = itemView.findViewById(R.id.sItemRating);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setData(final SearchObject object){
            String pName = object.getProfName();
            if( object.getTitle() != null && !object.getTitle().isEmpty()) {
                String title = object.getTitle();
                if (title.equals(mContext.getResources().getString(R.string.p_title_1))) {
                    title = mContext.getResources().getString(R.string.p_title_1);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_2))) {
                    title = mContext.getResources().getString(R.string.p_title_2);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_3))) {
                    title = mContext.getResources().getString(R.string.p_title_3);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_4))) {
                    title = mContext.getResources().getString(R.string.p_title_4);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_5))) {
                    title = mContext.getResources().getString(R.string.p_title_5);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_6))) {
                    title = mContext.getResources().getString(R.string.p_title_6);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_7))) {
                    title = mContext.getResources().getString(R.string.p_title_7);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_8))) {
                    title = mContext.getResources().getString(R.string.p_title_8);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_9))) {
                    title = mContext.getResources().getString(R.string.p_title_9);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_10))) {
                    title = mContext.getResources().getString(R.string.p_title_10);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_11))) {
                    title = mContext.getResources().getString(R.string.p_title_11);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_12))) {
                    title = mContext.getResources().getString(R.string.p_title_12);
                } else if (title.equals(mContext.getResources().getString(R.string.p_title_13))) {
                    title = mContext.getResources().getString(R.string.p_title_13);
                }
                pName = title+" "+pName;
            }
            name.setText(pName);
            field.setText(object.getFieldName());
            city.setText(object.getCityUniName());
            rating.setText(DoubleFormat.setFormat(object.getRatingNumber()));
            if(object.getPhotoName().equals("1")){
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.teacher_man));
            } else {
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.teacher_woman));
            }
            rating.setTextColor(RatingColor.setColorRating(object.getRatingNumber(),mContext));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ClickedProfActivity.class);
                    intent.putExtra("profData",object);
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
