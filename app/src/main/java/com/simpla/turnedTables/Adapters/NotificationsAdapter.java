package com.simpla.turnedTables.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpla.turnedTables.BiggerRatingActivity;
import com.simpla.turnedTables.Objects.NotificationObject;
import com.simpla.turnedTables.R;
import com.simpla.turnedTables.Utils.SetTheme;
import com.simpla.turnedTables.Utils.TimeConverter;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsAdapterHolder>{

    private Context mContext;
    private ArrayList<NotificationObject> list;
    private boolean check;

    public NotificationsAdapter(Context mContext, ArrayList<NotificationObject> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationsAdapter.NotificationsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_item,parent,false);
        int darkModeControl = SetTheme.theme(mContext);
        check = darkModeControl == 1;
        return new NotificationsAdapter.NotificationsAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.NotificationsAdapterHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NotificationsAdapterHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView textView,time;
        private ConstraintLayout layout;

        public NotificationsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.nItemPicture);
            textView = itemView.findViewById(R.id.nItemText);
            time = itemView.findViewById(R.id.nItemTime);
            layout = itemView.findViewById(R.id.nItemLayout);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setData(final NotificationObject object){
            Spannable spannable1;
            time.setText(TimeConverter.convert(object.getTime(),mContext));
            int cProf,cText;
            if(check){
                cProf = mContext.getResources().getColor(R.color.mDarkBlue);
                cText = mContext.getResources().getColor(R.color.black);
            }else{
                cProf = mContext.getResources().getColor(R.color.mBlue);
                cText = mContext.getResources().getColor(R.color.white);
            }
            String space= "\n";
            if(object.getPicName().equals("1")){
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like_full));
                spannable1 = new SpannableString(object.getProfName()+" "
                        +mContext.getResources().getString(R.string.not_like)+space
                        +mContext.getResources().getString(R.string.not_like_total)+" "+object.getTotalLikes());
                spannable1.setSpan(new ForegroundColorSpan(cProf), 0, object.getProfName()
                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(cText), object.getProfName()
                        .length()+1, object.getProfName().length()+1+mContext.getResources().getString(R.string.not_like).length()
                        +space.length() +mContext.getResources().getString(R.string.not_like_total).length()+1
                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.ratingGreen))
                        , object.getProfName().length()+1+mContext.getResources().getString(R.string.not_like).length()
                                +space.length() +mContext.getResources().getString(R.string.not_like_total).length()+1
                        , spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else if(object.getPicName().equals("2")){
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                spannable1 = new SpannableString(object.getProfName()+" "
                        +mContext.getResources().getString(R.string.not_dislike)+space
                        +mContext.getResources().getString(R.string.not_dislike_total)+" "+object.getTotalDislike());
                spannable1.setSpan(new ForegroundColorSpan(cProf), 0, object.getProfName()
                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(cText), object.getProfName()
                        .length()+1, object.getProfName().length()+1+mContext.getResources().getString(R.string.not_dislike).length()
                        +space.length() +mContext.getResources().getString(R.string.not_dislike_total).length()+1
                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.ratingRed))
                        , object.getProfName().length()+1+mContext.getResources().getString(R.string.not_dislike).length()
                                +space.length() +mContext.getResources().getString(R.string.not_dislike_total).length()+1
                        , spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                spannable1 = new SpannableString(object.getProfName()+" "
                        +mContext.getResources().getString(R.string.not_deleted));
                spannable1.setSpan(new ForegroundColorSpan(cProf), 0, object.getProfName()
                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(cText), object.getProfName()
                                .length()+1, spannable1.length()
                        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(spannable1);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BiggerRatingActivity.class);
                    intent.putExtra("biggerRatingObject",object);
                    if(object.getPicName().equals("3")){
                        intent.putExtra("deletedBiggerObject","deleted");
                    }
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
