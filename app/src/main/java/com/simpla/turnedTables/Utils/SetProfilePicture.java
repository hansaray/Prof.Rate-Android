package com.simpla.turnedTables.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.simpla.turnedTables.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetProfilePicture {

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setPicture(String photoName, CircleImageView circleImageView, Context mContext){
        switch (photoName) {
            case "pp1":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp1));
                break;
            case "pp2":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp2));
                break;
            case "pp3":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp3));
                break;
            case "pp4":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp4));
                break;
            case "pp5":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp5));
                break;
            case "pp6":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp6));
                break;
            case "pp7":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp7));
                break;
            case "pp8":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp8));
                break;
            case "pp9":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp9));
                break;
            case "pp10":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp10));
                break;
            case "pp11":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp11));
                break;
            case "pp12":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp12));
                break;
            case "pp13":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp13));
                break;
            case "pp14":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp14));
                break;
            case "pp15":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp15));
                break;
            case "pp16":
                circleImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pp16));
                break;
            default:
                break;
        }
    }
}
