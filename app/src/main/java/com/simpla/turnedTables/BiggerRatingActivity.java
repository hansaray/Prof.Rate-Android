package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Objects.NotificationObject;
import com.simpla.turnedTables.Objects.UserObject;
import com.simpla.turnedTables.Utils.DoubleFormat;
import com.simpla.turnedTables.Utils.LargeNumberConverter;
import com.simpla.turnedTables.Utils.RatingColor;
import com.simpla.turnedTables.Utils.SetProfilePicture;
import com.simpla.turnedTables.Utils.SetTheme;
import com.simpla.turnedTables.Utils.TimeConverter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BiggerRatingActivity extends AppCompatActivity {

    private CircleImageView image;
    private TextView name,field,uni,rating,help,diff,lec,att,txt,comment,likeNum,dislikeNum,time
            ,attTitle,txtTitle,profName;
    private ImageView like,dislike,delete;
    private ConstraintLayout profLayout;
    private Button backButton;
    private DatabaseReference mRef;
    private NotificationObject object;
    private UserObject userObject;
    private int titleColor,textColor;
    private AlertDialog.Builder ad;
    private AdView banner;
    private String notRatingID,notRatingID2,deletedControl;
    private double leNum,heNum,diNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(BiggerRatingActivity.this);
        if(darkModeControl == 1){
            titleColor = getResources().getColor(R.color.mDarkBlue);
            textColor = getResources().getColor(R.color.black);
            ad = new AlertDialog.Builder(BiggerRatingActivity.this);
        }else{
            titleColor = getResources().getColor(R.color.mBlue);
            textColor = getResources().getColor(R.color.white);
            ad = new AlertDialog.Builder(BiggerRatingActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigger_rating);
        getIntentData();
        findIds();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        object = (NotificationObject) intent.getSerializableExtra("biggerRatingObject");
        notRatingID = intent.getStringExtra("likeRatingID");
        notRatingID2 = intent.getStringExtra("dislikeRatingID");
        deletedControl = intent.getStringExtra("deletedBiggerObject");
        if(object != null){
            userObject = object.getObject();
        }
    }

    private void findIds() {
        backButton = findViewById(R.id.bRatingBack);
        image = findViewById(R.id.bRatingPicture);
        name = findViewById(R.id.bRatingUsername);
        field = findViewById(R.id.bRatingUserField);
        uni = findViewById(R.id.bRatingUserUni);
        rating = findViewById(R.id.bRatingAvgNumber);
        help = findViewById(R.id.bRatingHelpNum);
        diff = findViewById(R.id.bRatingDiffNum);
        lec = findViewById(R.id.bRatingLecNum);
        att = findViewById(R.id.bRatingAttNum);
        txt = findViewById(R.id.bRatingTxtNum);
        comment = findViewById(R.id.bRatingComment);
        likeNum = findViewById(R.id.bRatingLikeNumber);
        dislikeNum = findViewById(R.id.bRatingDislikeNumber);
        time = findViewById(R.id.bRatingTime);
        like = findViewById(R.id.bRatingLike);
        dislike = findViewById(R.id.bRatingDislike);
        attTitle = findViewById(R.id.bRatingTitle5);
        txtTitle = findViewById(R.id.bRatingTitle6);
        delete = findViewById(R.id.bRatingDelete);
        profLayout = findViewById(R.id.bRatingProfLayout);
        profName = findViewById(R.id.bRatingProfName);
        banner = findViewById(R.id.bRatingBanner);
        mRef = FirebaseDatabase.getInstance().getReference();
        setAds();
        if(object != null){
            if(deletedControl != null && !deletedControl.isEmpty()){
                delete.setVisibility(View.GONE);
                loadDeletedData();
            }else{
                loadData();
            }
        }else if(notRatingID != null && !notRatingID.isEmpty()){
            loadDataFromNotification(notRatingID);
        }else if(notRatingID2 != null && !notRatingID2.isEmpty()){
            loadDataFromNotification(notRatingID2);
        }
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void loadData() {
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef.child("ratings").child("withComment").child(object.getRatingID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profID = String.valueOf(snapshot.child("profID").getValue());
                String commentTxt = null;
                if(snapshot.child("comment").exists()){
                    commentTxt = String.valueOf(snapshot.child("comment").getValue());
                    comment.setText(commentTxt);
                }
                final long likeNumber = snapshot.child("likes").getChildrenCount();
                final long dislikeNumber = snapshot.child("dislikes").getChildrenCount();
                name.setText(userObject.getUsername());
                field.setText(userObject.getFieldName());
                uni.setText(userObject.getUniName());
                SetProfilePicture.setPicture(userObject.getPhotoName(),image,BiggerRatingActivity.this);
                rating.setText(DoubleFormat.setFormat(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))));
                rating.setTextColor(RatingColor.setColorRating(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                        ,BiggerRatingActivity.this));
                help.setText(String.valueOf(snapshot.child("helpfulness").getValue()));
                lec.setText(String.valueOf(snapshot.child("lecture").getValue()));
                diff.setText(String.valueOf(snapshot.child("difficulty").getValue()));
                help.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                lec.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                diff.setTextColor(RatingColor.setColorRatingOpposite
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                if(snapshot.child("attendance").exists()){
                    if(String.valueOf(snapshot.child("attendance").getValue()).equals("1")){
                        att.setText(getResources().getString(R.string.not_mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        att.setText(getResources().getString(R.string.mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    att.setVisibility(View.GONE);
                    attTitle.setVisibility(View.GONE);
                }
                if(snapshot.child("textbook").exists()){
                    if(String.valueOf(snapshot.child("textbook").getValue()).equals("1")){
                        txt.setText(getResources().getString(R.string.not_mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        txt.setText(getResources().getString(R.string.mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    txt.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);
                }
                likeNum.setText(LargeNumberConverter.format(likeNumber));
                dislikeNum.setText(LargeNumberConverter.format(dislikeNumber));
                time.setText(TimeConverter.convert(Long.parseLong
                        (String.valueOf(snapshot.child("time").getValue())),BiggerRatingActivity.this));
                final String finalCommentTxt = commentTxt;
                mRef.child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        long myLikedNum = 0,myDislikedNum = 0;
                        if(snapshot2.child("myLikedNumber").exists()){
                            myLikedNum = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                        }
                        if(snapshot2.child("myDislikedNumber").exists()){
                            myDislikedNum = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                        }
                        boolean likeCheck = false;
                        boolean dislikeCheck = false;
                        if(snapshot2.child("myLikes").exists()){
                            for(DataSnapshot d : snapshot2.child("myLikes").getChildren()){
                                assert d.getKey() != null;
                                if(d.getKey().equals(object.getItemID())){
                                    likeCheck = true;
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like_full));
                                }
                            }
                        }
                        if(!likeCheck){
                            like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like));
                            if(snapshot2.child("myDislikes").exists()){
                                for(DataSnapshot d : snapshot2.child("myDislikes").getChildren()){
                                    assert d.getKey() != null;
                                    if(d.getKey().equals(object.getItemID())){
                                        dislikeCheck = true;
                                        dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                                    }
                                }
                            }
                            if(!dislikeCheck){
                                dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike));
                            }
                        }
                        mRef.child("Professors").child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String pName = object.getProfName();
                                if(snapshot.child("title").exists()){
                                    String title = String.valueOf(snapshot.child("title").getValue());
                                    if (title.equals(getResources().getString(R.string.p_title_1))) {
                                        title = getResources().getString(R.string.p_title_1);
                                    } else if (title.equals(getResources().getString(R.string.p_title_2))) {
                                        title = getResources().getString(R.string.p_title_2);
                                    } else if (title.equals(getResources().getString(R.string.p_title_3))) {
                                        title = getResources().getString(R.string.p_title_3);
                                    } else if (title.equals(getResources().getString(R.string.p_title_4))) {
                                        title = getResources().getString(R.string.p_title_4);
                                    } else if (title.equals(getResources().getString(R.string.p_title_5))) {
                                        title = getResources().getString(R.string.p_title_5);
                                    } else if (title.equals(getResources().getString(R.string.p_title_6))) {
                                        title = getResources().getString(R.string.p_title_6);
                                    } else if (title.equals(getResources().getString(R.string.p_title_7))) {
                                        title = getResources().getString(R.string.p_title_7);
                                    } else if (title.equals(getResources().getString(R.string.p_title_8))) {
                                        title = getResources().getString(R.string.p_title_8);
                                    } else if (title.equals(getResources().getString(R.string.p_title_9))) {
                                        title = getResources().getString(R.string.p_title_9);
                                    } else if (title.equals(getResources().getString(R.string.p_title_10))) {
                                        title = getResources().getString(R.string.p_title_10);
                                    } else if (title.equals(getResources().getString(R.string.p_title_11))) {
                                        title = getResources().getString(R.string.p_title_11);
                                    } else if (title.equals(getResources().getString(R.string.p_title_12))) {
                                        title = getResources().getString(R.string.p_title_12);
                                    } else if (title.equals(getResources().getString(R.string.p_title_13))) {
                                        title = getResources().getString(R.string.p_title_13);
                                    }
                                    pName = title+" "+pName;
                                }
                                Spannable spannable1 = new SpannableString(getResources().getString(R.string.prof_name)+" "+pName);
                                spannable1.setSpan(new ForegroundColorSpan(titleColor), 0, getResources().getString(R.string.prof_name)
                                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable1.setSpan(new ForegroundColorSpan(textColor), getResources().getString(R.string.prof_name)
                                        .length()+1, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                profName.setText(spannable1);
                                heNum= Double.parseDouble(String.valueOf(snapshot.child("ratings").child("helpfulness").getValue()));
                                diNum = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("difficulty").getValue()));
                                leNum = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("lecture").getValue()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        setListeners(myLikedNum, myDislikedNum, likeNumber, dislikeNumber, object.getRatingID()
                                , finalCommentTxt, profID, myUid, likeCheck, dislikeCheck );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error2) {
                        Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDeletedData(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        mRef.child("deletedComments").child(object.getRatingID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String commentTxt;
                if(snapshot.child("comment").exists()){
                    commentTxt = String.valueOf(snapshot.child("comment").getValue());
                    comment.setText(commentTxt);
                }
                final long likeNumber = Long.parseLong(String.valueOf(snapshot.child("likeNumber").getValue()));
                final long dislikeNumber = Long.parseLong(String.valueOf(snapshot.child("dislikeNumber").getValue()));
                name.setText(userObject.getUsername());
                field.setText(userObject.getFieldName());
                uni.setText(userObject.getUniName());
                SetProfilePicture.setPicture(userObject.getPhotoName(),image,BiggerRatingActivity.this);
                Spannable spannable1 = new SpannableString(getResources().getString(R.string.prof_name)+" "+object.getProfName());
                spannable1.setSpan(new ForegroundColorSpan(titleColor), 0, getResources().getString(R.string.prof_name)
                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable1.setSpan(new ForegroundColorSpan(textColor), getResources().getString(R.string.prof_name)
                        .length()+1, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                profName.setText(spannable1);
                rating.setText(DoubleFormat.setFormat(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))));
                rating.setTextColor(RatingColor.setColorRating(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                        ,BiggerRatingActivity.this));
                help.setText(String.valueOf(snapshot.child("helpfulness").getValue()));
                lec.setText(String.valueOf(snapshot.child("lecture").getValue()));
                diff.setText(String.valueOf(snapshot.child("difficulty").getValue()));
                help.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                lec.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                diff.setTextColor(RatingColor.setColorRatingOpposite
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                if(snapshot.child("attendance").exists()){
                    if(String.valueOf(snapshot.child("attendance").getValue()).equals("1")){
                        att.setText(getResources().getString(R.string.not_mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        att.setText(getResources().getString(R.string.mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    att.setVisibility(View.GONE);
                    attTitle.setVisibility(View.GONE);
                }
                if(snapshot.child("textbook").exists()){
                    if(String.valueOf(snapshot.child("textbook").getValue()).equals("1")){
                        txt.setText(getResources().getString(R.string.not_mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        txt.setText(getResources().getString(R.string.mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    txt.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);
                }
                likeNum.setText(LargeNumberConverter.format(likeNumber));
                dislikeNum.setText(LargeNumberConverter.format(dislikeNumber));
                time.setText(TimeConverter.convert(Long.parseLong
                        (String.valueOf(snapshot.child("time").getValue())),BiggerRatingActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDataFromNotification(final String ratingID) {
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef.child("ratings").child("withComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profID = String.valueOf(snapshot.child("profID").getValue());
                String commentTxt = null;
                if(snapshot.child("comment").exists()){
                    commentTxt = String.valueOf(snapshot.child("comment").getValue());
                    comment.setText(commentTxt);
                }
                final long likeNumber = snapshot.child("likes").getChildrenCount();
                final long dislikeNumber = snapshot.child("dislikes").getChildrenCount();
                rating.setText(DoubleFormat.setFormat(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))));
                rating.setTextColor(RatingColor.setColorRating(Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                        ,BiggerRatingActivity.this));
                help.setText(String.valueOf(snapshot.child("helpfulness").getValue()));
                lec.setText(String.valueOf(snapshot.child("lecture").getValue()));
                diff.setText(String.valueOf(snapshot.child("difficulty").getValue()));
                help.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                lec.setTextColor(RatingColor.setColorRating
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                diff.setTextColor(RatingColor.setColorRatingOpposite
                        (Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue())),BiggerRatingActivity.this));
                if(snapshot.child("attendance").exists()){
                    if(String.valueOf(snapshot.child("attendance").getValue()).equals("1")){
                        att.setText(getResources().getString(R.string.not_mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        att.setText(getResources().getString(R.string.mandatory));
                        att.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    att.setVisibility(View.GONE);
                    attTitle.setVisibility(View.GONE);
                }
                if(snapshot.child("textbook").exists()){
                    if(String.valueOf(snapshot.child("textbook").getValue()).equals("1")){
                        txt.setText(getResources().getString(R.string.not_mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingGreen));
                    }else{
                        txt.setText(getResources().getString(R.string.mandatory));
                        txt.setTextColor(getResources().getColor(R.color.ratingRed));
                    }
                }else{
                    txt.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);
                }
                likeNum.setText(LargeNumberConverter.format(likeNumber));
                dislikeNum.setText(LargeNumberConverter.format(dislikeNumber));
                time.setText(TimeConverter.convert(Long.parseLong
                        (String.valueOf(snapshot.child("time").getValue())),BiggerRatingActivity.this));
                final String finalCommentTxt = commentTxt;
                mRef.child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        name.setText(String.valueOf(snapshot2.child("username").getValue()));
                        field.setText(String.valueOf(snapshot2.child("faculty").getValue()));
                        uni.setText(String.valueOf(snapshot2.child("university").getValue()));
                        SetProfilePicture.setPicture(String.valueOf(snapshot2.child("photo").getValue()),image
                                ,BiggerRatingActivity.this);
                        long myLikedNum = 0,myDislikedNum = 0;
                        if(snapshot2.child("myLikedNumber").exists()){
                            myLikedNum = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                        }
                        if(snapshot2.child("myDislikedNumber").exists()){
                            myDislikedNum = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                        }
                        boolean likeCheck = false;
                        boolean dislikeCheck = false;
                        if(snapshot2.child("myLikes").exists()){
                            for(DataSnapshot d : snapshot2.child("myLikes").getChildren()){
                                assert d.getKey() != null;
                                if(d.getKey().equals(object.getItemID())){
                                    likeCheck = true;
                                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like_full));
                                }
                            }
                        }
                        if(!likeCheck){
                            like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like));
                            if(snapshot2.child("myDislikes").exists()){
                                for(DataSnapshot d : snapshot2.child("myDislikes").getChildren()){
                                    assert d.getKey() != null;
                                    if(d.getKey().equals(object.getItemID())){
                                        dislikeCheck = true;
                                        dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                                    }
                                }
                            }
                            if(!dislikeCheck){
                                dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike));
                            }
                        }
                        final long finalMyLikedNum = myLikedNum;
                        final long finalMyDislikedNum = myDislikedNum;
                        final boolean finalLikeCheck = likeCheck;
                        final boolean finalDislikeCheck = dislikeCheck;
                        mRef.child("Professors").child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                String profNameStr = String.valueOf(snapshot3.child("prof_name").getValue());
                                Spannable spannable1 = new SpannableString(getResources().getString(R.string.prof_name)+" "+profNameStr);
                                spannable1.setSpan(new ForegroundColorSpan(titleColor), 0, getResources().getString(R.string.prof_name)
                                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable1.setSpan(new ForegroundColorSpan(textColor), getResources().getString(R.string.prof_name)
                                        .length()+1, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                profName.setText(spannable1);
                                setListeners(finalMyLikedNum, finalMyDislikedNum, likeNumber, dislikeNumber, ratingID
                                        , finalCommentTxt, profID, myUid, finalLikeCheck, finalDislikeCheck);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error3) {
                                Toast.makeText(BiggerRatingActivity.this,error3.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error2) {
                        Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners(final long likedNum, long dislikedNum, long likeNum1, long dislikeNum1
            , final String ratingID, final String comment, final String profID, final String userID, final boolean likeCheck1, final boolean dislikeCheck1){
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final boolean[] likeCheck = {likeCheck1};
        final boolean[] dislikeCheck = {dislikeCheck1};
        final long[] newNum = new long[1];
        final long[] newNumD = new long[1];
        final long[] newLiked = new long[1];
        final long[] newDisliked = new long[1];
        newNum[0] = likeNum1;
        newNumD[0] = dislikeNum1;
        newLiked[0] = likedNum;
        newDisliked[0] = dislikedNum;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(comment != null){
                    assert mAuth.getCurrentUser() != null;
                    if(likeCheck[0]){ //delete like, likeCheck=false
                        mRef.child("ratings").child("withComment").child(ratingID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        if(snapshot.child("likes").exists()){
                                            for(DataSnapshot d : snapshot.child("likes").getChildren()){
                                                assert d.getKey() != null;
                                                if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("likes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                    long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                    long newNum = snapshot.child("likes").getChildrenCount();
                                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                            .child("myLikes").child(ratingID).removeValue();
                                                                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like));
                                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                    popNum = popNum - 1;
                                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                                            .child("popularity").setValue(popNum);
                                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                                            .child(ratingID).setValue(popNum);
                                                                    likeCheck[0] = false;
                                                                    newNum = newNum - 1;
                                                                    newLiked = newLiked - 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myLikedNumber").setValue(newLiked);
                                                                    likeNum.setText(LargeNumberConverter.format(newNum));
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error2) {
                                                                    Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            }
                                        }else{
                                            like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }else{ //add like, likeCheck=true, dislike control
                        Map<String, String> time = ServerValue.TIMESTAMP;
                        mRef.child("ratings").child("withComment").child(object.getItemID()).child("likes")
                                .child(mAuth.getCurrentUser().getUid()).setValue(true);
                        mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myLikes")
                                .child(ratingID).setValue(time);
                        likeCheck[0] = true;
                        like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like_full));
                        mRef.child("ratings").child("withComment").child(ratingID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                        if(snapshot.child("dislikes").exists()){
                                            boolean dCheck = false;
                                            for(DataSnapshot d : snapshot.child("dislikes").getChildren()){
                                                assert d.getKey() != null;
                                                if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                    dCheck = true;
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("dislikes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                    long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                    long newNum = snapshot.child("likes").getChildrenCount();
                                                                    long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                    long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                            .child("myDislikes").child(ratingID).removeValue();
                                                                    dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike));
                                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                    popNum = popNum + 2;
                                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                                            .child("popularity").setValue(popNum);
                                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                                            .child(ratingID).setValue(popNum);
                                                                    dislikeCheck[0] = false;
                                                                    newLiked = newLiked + 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myLikedNumber").setValue(newLiked);
                                                                    newDisliked = newDisliked - 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myDislikedNumber").setValue(newDisliked);
                                                                  //  newNum = newNum + 1;
                                                                    likeNum.setText(LargeNumberConverter.format(newNum));
                                                                    newNumD = newNumD - 1;
                                                                    dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error2) {
                                                                    Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            }
                                            if(!dCheck) {
                                                assert mAuth.getCurrentUser() != null;
                                                mRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                        long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                        long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                        long newNum = snapshot.child("likes").getChildrenCount();
                                                        popNum = popNum + 1;
                                                        mRef.child("ratings").child("withComment").child(ratingID)
                                                                .child("popularity").setValue(popNum);
                                                        mRef.child("Professors").child(profID).child("ratings_comment")
                                                                .child(ratingID).setValue(popNum);
                                                        newLiked = newLiked + 1;
                                                        mRef.child("users").child(userID)
                                                                .child("myLikedNumber").setValue(newLiked);
                                                        likeNum.setText(LargeNumberConverter.format(newNum));
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }else{
                                            assert mAuth.getCurrentUser() != null;
                                            mRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                    long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                    long newNum = snapshot.child("likes").getChildrenCount();
                                                    popNum = popNum + 1;
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("popularity").setValue(popNum);
                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                            .child(ratingID).setValue(popNum);
                                                    newLiked = newLiked + 1;
                                                    mRef.child("users").child(userID)
                                                            .child("myLikedNumber").setValue(newLiked);
                                                    likeNum.setText(LargeNumberConverter.format(newNum));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(comment != null){
                    assert mAuth.getCurrentUser() != null;
                    if(dislikeCheck[0]){ //delete dislike, dislikeCheck=false
                        mRef.child("ratings").child("withComment").child(ratingID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        if(snapshot.child("dislikes").exists()){
                                            for(DataSnapshot d : snapshot.child("dislikes").getChildren()){
                                                assert d.getKey() != null;
                                                if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("dislikes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                    long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                    long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                            .child("myDislikes").child(ratingID).removeValue();
                                                                    dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike));
                                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                    popNum = popNum + 1;
                                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                                            .child("popularity").setValue(popNum);
                                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                                            .child(ratingID).setValue(popNum);
                                                                    dislikeCheck[0] = false;
                                                                    newDisliked = newDisliked - 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myDislikedNumber").setValue(newDisliked);
                                                                    newNumD = newNumD - 1;
                                                                    dislikeNum.setText(LargeNumberConverter.format(newNumD));

                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error2) {
                                                                    Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            }
                                        }else{
                                            dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }else{ //add dislike, dislikeCheck=true, like control
                        Map<String, String> time = ServerValue.TIMESTAMP;
                        mRef.child("ratings").child("withComment").child(ratingID).child("dislikes")
                                .child(mAuth.getCurrentUser().getUid()).setValue(true);
                        mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myDislikes")
                                .child(ratingID).setValue(time);
                        dislikeCheck[0] = true;
                        dislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                        mRef.child("ratings").child("withComment").child(ratingID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                        if(snapshot.child("likes").exists()){
                                            boolean lCheck = false;
                                            for(DataSnapshot d : snapshot.child("likes").getChildren()){
                                                assert d.getKey() != null;
                                                if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                    lCheck = true;
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("likes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                    long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                    long newNum = snapshot.child("likes").getChildrenCount();
                                                                    long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                    long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                    mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                            .child("myLikes").child(ratingID).removeValue();
                                                                    like.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_like));
                                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                    popNum = popNum - 2;
                                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                                            .child("popularity").setValue(popNum);
                                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                                            .child(ratingID).setValue(popNum);
                                                                    likeCheck[0] = false;
                                                                    newDisliked = newDisliked + 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myDislikedNumber").setValue(newDisliked);
                                                                    newLiked = newLiked - 1;
                                                                    mRef.child("users").child(userID)
                                                                            .child("myLikedNumber").setValue(newLiked);
                                                                    newNum = newNum - 1;
                                                                    likeNum.setText(LargeNumberConverter.format(newNum));
                                                                   // newNumD = newNumD + 1;
                                                                    dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error2) {
                                                                    Toast.makeText(BiggerRatingActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            }
                                            if(!lCheck) {
                                                assert mAuth.getCurrentUser() != null;
                                                mRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                        long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                        long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                        long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                        popNum = popNum - 1;
                                                        mRef.child("ratings").child("withComment").child(ratingID)
                                                                .child("popularity").setValue(popNum);
                                                        mRef.child("Professors").child(profID).child("ratings_comment")
                                                                .child(ratingID).setValue(popNum);
                                                        newDisliked = newDisliked + 1;
                                                        mRef.child("users").child(userID)
                                                                .child("myDislikedNumber").setValue(newDisliked);
                                                       // newNumD = newNumD + 1;
                                                        dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }else{
                                            assert mAuth.getCurrentUser() != null;
                                            mRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                    long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                    long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                    long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                    popNum = popNum - 1;
                                                    mRef.child("ratings").child("withComment").child(ratingID)
                                                            .child("popularity").setValue(popNum);
                                                    mRef.child("Professors").child(profID).child("ratings_comment")
                                                            .child(ratingID).setValue(popNum);
                                                    newDisliked = newDisliked + 1;
                                                    mRef.child("users").child(userID)
                                                            .child("myDislikedNumber").setValue(newDisliked);
                                                    // newNumD = newNumD + 1;
                                                    dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                ad.setTitle(getResources().getString(R.string.delete));
                ad.setMessage(getResources().getString(R.string.delete_exp));
                ad.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        assert mAuth.getCurrentUser() != null;
                        mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                .child("myRatings").child(ratingID).removeValue();
                        mRef.child("ratings").child("withComment").child(ratingID).removeValue();
                        mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                .child("myRatings").child(ratingID).removeValue();
                        mRef.child("Professors").child(profID)
                                .child("ratings_comment").child(ratingID).removeValue();
                        mRef.child("Professors").child(profID)
                                .child("ratings_total").child(ratingID).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ad.create().cancel();
                                        if(task.isSuccessful()){
                                            Toast.makeText(BiggerRatingActivity.this,getResources().getString(R.string.delete_done)
                                                    ,Toast.LENGTH_LONG).show();
                                            updateRating(profID,heNum,diNum,leNum);
                                        }else{
                                            Toast.makeText(BiggerRatingActivity.this,getResources().getString(R.string.errorOccurred)
                                                    ,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                    }
                });
                ad.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ad.create().cancel();
                    }
                });
                ad.create().show();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BiggerRatingActivity.this, ClickedUserActivity.class);
                intent.putExtra("cUserID",userID);
                startActivity(intent);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BiggerRatingActivity.this, ClickedUserActivity.class);
                intent.putExtra("cUserID",userID);
                startActivity(intent);
            }
        });
        profLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BiggerRatingActivity.this, ClickedProfActivity.class);
                intent.putExtra("cProfID",profID);
                startActivity(intent);
            }
        });
    }

    private void updateRating(final String profID, final double helpNum, final double diffNum, final double lecNum){
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Professors").child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = snapshot.child("ratings_total").getChildrenCount();
                double ratings = 0.0;
                for(DataSnapshot d : snapshot.child("ratings_total").getChildren()){
                    ratings = ratings + Double.parseDouble(String.valueOf(d.getValue()));
                }
                double finalRating = ratings/total;
                double help = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("helpfulness").getValue()));
                help = help - helpNum;
                double diff = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("difficulty").getValue()));
                diff = diff - diffNum;
                double lec = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("lecture").getValue()));
                lec = lec - lecNum;
                mRef.child("Professors").child(profID).child("ratings").child("helpfulness").setValue(help);
                mRef.child("Professors").child(profID).child("ratings").child("difficulty").setValue(diff);
                mRef.child("Professors").child(profID).child("ratings").child("lecture").setValue(lec);
                mRef.child("Professors").child(profID).child("avg_rating").setValue(finalRating);
                mRef.child("Universities").child(String.valueOf(snapshot.child("uni_name").getValue()))
                        .child("All professors").child(profID).setValue(finalRating);
                mRef.child("Universities").child(String.valueOf(snapshot.child("uni_name").getValue()))
                        .child(String.valueOf(snapshot.child("field_name").getValue())).child(profID).setValue(finalRating);
                mRef.child("Faculties").child(String.valueOf(snapshot.child("field_name").getValue()))
                        .child(String.valueOf(snapshot.child("city").getValue())).child(profID).setValue(finalRating);
                mRef.child("Faculties").child(String.valueOf(snapshot.child("field_name").getValue()))
                        .child("All professors").child(profID).setValue(finalRating);
                mRef.child("Cities").child(String.valueOf(snapshot.child("city").getValue()))
                        .child("All professors").child(profID).setValue(finalRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BiggerRatingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}