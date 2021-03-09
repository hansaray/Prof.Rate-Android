package com.simpla.turnedTables.Utils;

import android.content.Context;

import com.simpla.turnedTables.R;

public class RatingColor {

    public static int setColorRating(double number, Context mContext){
        if(number == 0){
            return mContext.getResources().getColor(R.color.hint);
        }else if (number >= 0.1 && number <= 1.49){  // 0.1 - 1.4
            return mContext.getResources().getColor(R.color.ratingRed);
        }else if (number >= 1.5 && number <= 2.49){ // 1.5 - 2,4
            return mContext.getResources().getColor(R.color.ratingOrange);
        }else if (number >= 2.5 && number <= 3.49){  // 2.5 - 3.4
            return mContext.getResources().getColor(R.color.ratingYellow);
        }else if (number >= 3.5 && number <= 4.29){  // 3.5 - 4.2
            return mContext.getResources().getColor(R.color.ratingDiffGreen);
        }else if (number >= 4.3 && number <= 5.0){ // 4.3  - 5.0
            return mContext.getResources().getColor(R.color.ratingGreen);
        }else {
            return mContext.getResources().getColor(R.color.hint);
        }
    }

    public static int setColorRatingOpposite(double number, Context mContext){
        if(number == 0){
            return mContext.getResources().getColor(R.color.hint);
        }else if (number >= 0.1 && number <= 1.49){  // 0.1 - 1.4
            return mContext.getResources().getColor(R.color.ratingGreen);
        }else if (number >= 1.5 && number <= 2.49){ // 1.5 - 2,4
            return mContext.getResources().getColor(R.color.ratingDiffGreen);
        }else if (number >= 2.5 && number <= 3.49){  // 2.5 - 3.4
            return mContext.getResources().getColor(R.color.ratingYellow);
        }else if (number >= 3.5 && number <= 4.29){  // 3.5 - 4.2
            return mContext.getResources().getColor(R.color.ratingOrange);
        }else if (number >= 4.3 && number <= 5.0){ // 4.3  - 5.0
            return mContext.getResources().getColor(R.color.ratingRed);
        }else {
            return mContext.getResources().getColor(R.color.hint);
        }
    }

    public static int setAvgColor(long number,Context mContext){
        if(number < 0){
            return mContext.getResources().getColor(R.color.ratingRed);
        }else if(number == 0){
            return mContext.getResources().getColor(R.color.hint);
        }else{
            return mContext.getResources().getColor(R.color.ratingGreen);
        }
    }

}
