package com.simpla.turnedTables.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.simpla.turnedTables.EditProfileActivity;
import com.simpla.turnedTables.Objects.PhotoObject;
import com.simpla.turnedTables.R;
import com.simpla.turnedTables.SignUpActivity;
import com.simpla.turnedTables.Utils.EventbusDataEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class SignUpPhotoAdapter extends RecyclerView.Adapter<SignUpPhotoAdapter.SignUpPhotoAdapterHolder> {
    private Context mContext;
    private ArrayList<PhotoObject> list;

    public SignUpPhotoAdapter(Context mContext, ArrayList<PhotoObject> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public SignUpPhotoAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);
        return new SignUpPhotoAdapter.SignUpPhotoAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SignUpPhotoAdapterHolder holder, int position) {
        holder.setPhoto(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SignUpPhotoAdapterHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout layout;
        private ImageView image;
        private CardView cardView;
        public SignUpPhotoAdapterHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.photoItemLayout);
            image = itemView.findViewById(R.id.photoItemImage);
            cardView = itemView.findViewById(R.id.photoItemCard);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setPhoto(final PhotoObject object){
            final String photo = object.getPhotoName();
            switch (photo) {
                case "pp1":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp1));
                    break;
                case "pp2":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp2));
                    break;
                case "pp3":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp3));
                    break;
                case "pp4":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp4));
                    break;
                case "pp5":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp5));
                    break;
                case "pp6":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp6));
                    break;
                case "pp7":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp7));
                    break;
                case "pp8":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp8));
                    break;
                case "pp9":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp9));
                    break;
                case "pp10":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp10));
                    break;
                case "pp11":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp11));
                    break;
                case "pp12":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp12));
                    break;
                case "pp13":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp13));
                    break;
                case "pp14":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp14));
                    break;
                case "pp15":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp15));
                    break;
                case "pp16":
                    image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pp16));
                    break;
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    layout.setBackgroundColor(itemView.getResources().getColor(R.color.colorAccent));
                    chosenPhoto(photo,object.getActivityNumber());
                }
            });
        }

        private void chosenPhoto(String photo, int number){
            EventBus.getDefault().postSticky(new EventbusDataEvents.chosenPhoto(photo));
            if(number == 1){
                ((EditProfileActivity)mContext).findViewById(R.id.editFrameLayout).setVisibility(View.GONE);
                ((EditProfileActivity)mContext).findViewById(R.id.editLayout).setVisibility(View.VISIBLE);
            } else {
                ((SignUpActivity)mContext).findViewById(R.id.signUpFrameLayout).setVisibility(View.GONE);
                ((SignUpActivity)mContext).findViewById(R.id.signUpLayout).setVisibility(View.VISIBLE);
            }
        }
    }
}
