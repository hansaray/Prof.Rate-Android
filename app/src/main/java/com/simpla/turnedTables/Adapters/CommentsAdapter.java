package com.simpla.turnedTables.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.simpla.turnedTables.ClickedProfActivity;
import com.simpla.turnedTables.ClickedUserActivity;
import com.simpla.turnedTables.LogInActivity;
import com.simpla.turnedTables.Objects.CommentsObject;
import com.simpla.turnedTables.R;
import com.simpla.turnedTables.SignUpActivity;
import com.simpla.turnedTables.Utils.DoubleFormat;
import com.simpla.turnedTables.Utils.LargeNumberConverter;
import com.simpla.turnedTables.Utils.RatingColor;
import com.simpla.turnedTables.Utils.SetProfilePicture;
import com.simpla.turnedTables.Utils.SetTheme;
import com.simpla.turnedTables.Utils.TimeConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsAdapterHolder>{

    private Context mContext;
    private ArrayList<CommentsObject> list;
    private AlertDialog.Builder ad;
    private boolean check;


    public CommentsAdapter(Context mContext, ArrayList<CommentsObject> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        int darkModeControl = SetTheme.theme(mContext);
        if(darkModeControl==1){
            ad = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            check = true;
        }else{
            check = false;
            ad = new androidx.appcompat.app.AlertDialog.Builder(mContext,R.style.AlertDialogCustomDark);
        }
        return new CommentsAdapter.CommentsAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentsAdapterHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentsAdapterHolder extends RecyclerView.ViewHolder{
        private CircleImageView image;
        private TextView name,field,uni,rating,help,diff,lec,att,txt,comment,likeNum,dislikeNum,time
        ,attTitle,txtTitle,profName;
        private ImageView like,dislike,report,delete;
        private ConstraintLayout layout,profLayout;

        public CommentsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cItemPicture);
            name = itemView.findViewById(R.id.cItemUsername);
            field = itemView.findViewById(R.id.cItemUserField);
            uni = itemView.findViewById(R.id.cItemUserUni);
            rating = itemView.findViewById(R.id.cItemAvgNumber);
            help = itemView.findViewById(R.id.cItemHelpNum);
            diff = itemView.findViewById(R.id.cItemDiffNum);
            lec = itemView.findViewById(R.id.cItemLecNum);
            att = itemView.findViewById(R.id.cItemAttNum);
            txt = itemView.findViewById(R.id.cItemTxtNum);
            comment = itemView.findViewById(R.id.cItemComment);
            likeNum = itemView.findViewById(R.id.cItemLikeNumber);
            dislikeNum = itemView.findViewById(R.id.cItemDislikeNumber);
            time = itemView.findViewById(R.id.cItemTime);
            like = itemView.findViewById(R.id.cItemLike);
            dislike = itemView.findViewById(R.id.cItemDislike);
            report = itemView.findViewById(R.id.cItemReport);
            attTitle = itemView.findViewById(R.id.cItemTitle5);
            txtTitle = itemView.findViewById(R.id.cItemTitle6);
            delete = itemView.findViewById(R.id.cItemDelete);
            layout = itemView.findViewById(R.id.cItemLayout);
            profLayout = itemView.findViewById(R.id.cItemProfLayout);
            profName = itemView.findViewById(R.id.cItemProfName);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setData(final CommentsObject object){
            //set layout
            if(object.isCheck()){
                profLayout.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference().child("Professors").child(object.getProfID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String pName = String.valueOf(snapshot.child("prof_name").getValue());
                        if(snapshot.child("title").exists()){
                            String title = String.valueOf(snapshot.child("title").getValue());
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
                        int titleColor;
                        int textColor;
                        if(check){
                            titleColor = mContext.getResources().getColor(R.color.mDarkBlue);
                            textColor = mContext.getResources().getColor(R.color.black);
                        }else{
                            titleColor = mContext.getResources().getColor(R.color.mBlue);
                            textColor = mContext.getResources().getColor(R.color.white);
                        }
                        Spannable spannable1 = new SpannableString(mContext.getResources().getString(R.string.prof_name)+" "+pName);
                        spannable1.setSpan(new ForegroundColorSpan(titleColor), 0, mContext.getResources().getString(R.string.prof_name)
                                .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable1.setSpan(new ForegroundColorSpan(textColor), mContext.getResources().getString(R.string.prof_name)
                                .length()+1, spannable1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        profName.setText(spannable1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                profLayout.setVisibility(View.GONE);
            }
            //like or dislike check
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if(mAuth.getCurrentUser() != null){
                if(object.getUserID().equals(mAuth.getCurrentUser().getUid())){
                    delete.setVisibility(View.VISIBLE);
                    report.setVisibility(View.GONE);
                }else{
                    delete.setVisibility(View.GONE);
                    report.setVisibility(View.VISIBLE);
                }
                mRef.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean likeCheck = false;
                        boolean dislikeCheck = false;
                        if(snapshot.child("myLikes").exists()){
                            for(DataSnapshot d : snapshot.child("myLikes").getChildren()){
                                assert d.getKey() != null;
                                if(d.getKey().equals(object.getItemID())){
                                    likeCheck = true;
                                    like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like_full));
                                }
                            }
                        }
                        if(!likeCheck){
                            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like));
                            if(snapshot.child("myDislikes").exists()){
                                for(DataSnapshot d : snapshot.child("myDislikes").getChildren()){
                                    assert d.getKey() != null;
                                    if(d.getKey().equals(object.getItemID())){
                                        dislikeCheck = true;
                                        dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                                    }
                                }
                            }
                            if(!dislikeCheck){
                                dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike));
                            }
                        }
                        setListeners(object,likeCheck,dislikeCheck);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.setTitle(mContext.getResources().getString(R.string.information));
                        ad.setMessage(mContext.getResources().getString(R.string.likeDislike_exp));
                        ad.setPositiveButton(mContext.getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, LogInActivity.class));
                            }
                        });
                        ad.setNegativeButton(mContext.getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, SignUpActivity.class));
                            }
                        });
                        ad.create().show();
                    }
                });
                dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.setTitle(mContext.getResources().getString(R.string.information));
                        ad.setMessage(mContext.getResources().getString(R.string.likeDislike_exp));
                        ad.setPositiveButton(mContext.getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, LogInActivity.class));
                            }
                        });
                        ad.setNegativeButton(mContext.getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, SignUpActivity.class));
                            }
                        });
                        ad.create().show();
                    }
                });
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.setTitle(mContext.getResources().getString(R.string.information));
                        ad.setMessage(mContext.getResources().getString(R.string.reportLogin_exp));
                        ad.setPositiveButton(mContext.getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, LogInActivity.class));
                            }
                        });
                        ad.setNegativeButton(mContext.getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mContext.startActivity(new Intent(mContext, SignUpActivity.class));
                            }
                        });
                        ad.create().show();
                    }
                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ClickedUserActivity.class);
                        intent.putExtra("cUserID",object.getUserID());
                        mContext.startActivity(intent);
                    }
                });
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ClickedUserActivity.class);
                        intent.putExtra("cUserID",object.getUserID());
                        mContext.startActivity(intent);
                    }
                });
                profLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ClickedProfActivity.class);
                        intent.putExtra("cProfID",object.getProfID());
                        mContext.startActivity(intent);
                    }
                });
            }
            //set information to textViews
            if(object.getComment() != null){
                comment.setText(object.getComment());
            }else{
                comment.setVisibility(View.GONE);
            }
            name.setText(object.getUsername());
            field.setText(object.getFieldName());
            uni.setText(object.getUniName());
            rating.setText(DoubleFormat.setFormat(object.getAvgRating()));
            rating.setTextColor(RatingColor.setColorRating(object.getAvgRating(),mContext));
            SetProfilePicture.setPicture(object.getPhotoName(),image,mContext);
            help.setText(DoubleFormat.setFormat(object.getHelpNum()));
            diff.setText(DoubleFormat.setFormat(object.getDiffNum()));
            lec.setText(DoubleFormat.setFormat(object.getLecNum()));
            help.setTextColor(RatingColor.setColorRating(object.getHelpNum(),mContext));
            diff.setTextColor(RatingColor.setColorRatingOpposite(object.getDiffNum(),mContext));
            lec.setTextColor(RatingColor.setColorRating(object.getLecNum(),mContext));
            if(object.getAttNum() == 1){
                att.setText(mContext.getResources().getString(R.string.not_mandatory));
                att.setTextColor(mContext.getResources().getColor(R.color.ratingGreen));
            }else if(object.getAttNum() == 2){
                att.setText(mContext.getResources().getString(R.string.mandatory));
                att.setTextColor(mContext.getResources().getColor(R.color.ratingRed));
            }else{
               att.setVisibility(View.GONE);
               attTitle.setVisibility(View.GONE);
            }
            if(object.getTxtNum() == 1){
                txt.setText(mContext.getResources().getString(R.string.not_mandatory));
                txt.setTextColor(mContext.getResources().getColor(R.color.ratingGreen));
            }else if(object.getTxtNum() == 2){
                txt.setText(mContext.getResources().getString(R.string.mandatory));
                txt.setTextColor(mContext.getResources().getColor(R.color.ratingRed));
            }else{
                txt.setVisibility(View.GONE);
                txtTitle.setVisibility(View.GONE);
            }
            likeNum.setText(LargeNumberConverter.format(object.getLikeNum()));
            dislikeNum.setText(LargeNumberConverter.format(object.getDislikeNum()));
            time.setText(TimeConverter.convert(object.getTime(),mContext));
        }

        private void setListeners(final CommentsObject object, final boolean likeCheck1, final boolean dislikeCheck1){
            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final boolean[] likeCheck = {likeCheck1};
            final boolean[] dislikeCheck = {dislikeCheck1};
            like.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View view) {
                    if(object.getComment() != null){
                        assert mAuth.getCurrentUser() != null;
                        if(likeCheck[0]){ //delete like, likeCheck=false
                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                if(snapshot.child("likes").exists()){
                                                    for(DataSnapshot d : snapshot.child("likes").getChildren()){
                                                        assert d.getKey() != null;
                                                        if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("likes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                            mRef.child("users").child(object.getUserID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                            long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                            long newNum = snapshot.child("likes").getChildrenCount();
                                                                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                                    .child("myLikes").child(object.getItemID()).removeValue();
                                                                            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like));
                                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                            popNum = popNum - 1;
                                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                                    .child("popularity").setValue(popNum);
                                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                                    .child(object.getItemID()).setValue(popNum);
                                                                            likeCheck[0] = false;
                                                                            newLiked = newLiked - 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myLikedNumber").setValue(newLiked);
                                                                            newNum = newNum - 1;
                                                                            likeNum.setText(LargeNumberConverter.format(newNum));
                                                                            deleteNotification(1,object.getUserID(),object.getItemID());
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error2) {
                                                                            Toast.makeText(mContext,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }else{
                                                    like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like));
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{ //add like, likeCheck=true, dislike control
                            Map<String, String> time = ServerValue.TIMESTAMP;
                            mRef.child("ratings").child("withComment").child(object.getItemID()).child("likes")
                                    .child(mAuth.getCurrentUser().getUid()).setValue(true);
                            mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myLikes")
                                    .child(object.getItemID()).setValue(time);
                            likeCheck[0] = true;
                            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like_full));
                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                if(snapshot.child("dislikes").exists()){
                                                    boolean dCheck = false;
                                                    for(DataSnapshot d : snapshot.child("dislikes").getChildren()){
                                                        assert d.getKey() != null;
                                                        if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                            dCheck = true;
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("dislikes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                            mRef.child("users").child(object.getUserID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                            long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                            long newNum = snapshot.child("likes").getChildrenCount();
                                                                            long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                            long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                                    .child("myDislikes").child(object.getItemID()).removeValue();
                                                                            dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike));
                                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                            popNum = popNum + 2;
                                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                                    .child("popularity").setValue(popNum);
                                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                                    .child(object.getItemID()).setValue(popNum);
                                                                            dislikeCheck[0] = false;
                                                                            newLiked = newLiked + 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myLikedNumber").setValue(newLiked);
                                                                            newDisliked = newDisliked - 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myDislikedNumber").setValue(newDisliked);
                                                                          //  newNum = newNum + 1;
                                                                            likeNum.setText(LargeNumberConverter.format(newNum));
                                                                            newNumD = newNumD - 1;
                                                                            dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                            addNotification(object.getProfID(),1,object.getUserID(),object.getItemID());
                                                                            deleteNotification(2,object.getUserID(),object.getItemID());
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error2) {
                                                                            Toast.makeText(mContext,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                    if(!dCheck){
                                                        mRef.child("users").child(object.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                long newNum = snapshot.child("likes").getChildrenCount();
                                                                popNum = popNum + 1;
                                                                mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                        .child("popularity").setValue(popNum);
                                                                mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                        .child(object.getItemID()).setValue(popNum);
                                                                newLiked = newLiked + 1;
                                                                mRef.child("users").child(object.getUserID())
                                                                        .child("myLikedNumber").setValue(newLiked);
                                                                likeNum.setText(LargeNumberConverter.format(newNum));
                                                                addNotification(object.getProfID(),1,object.getUserID(),object.getItemID());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }else{
                                                    mRef.child("users").child(object.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                            long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                            long newNum = snapshot.child("likes").getChildrenCount();
                                                            popNum = popNum + 1;
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("popularity").setValue(popNum);
                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                    .child(object.getItemID()).setValue(popNum);
                                                            newLiked = newLiked + 1;
                                                            mRef.child("users").child(object.getUserID())
                                                                    .child("myLikedNumber").setValue(newLiked);
                                                            likeNum.setText(LargeNumberConverter.format(newNum));
                                                            addNotification(object.getProfID(),1,object.getUserID(),object.getItemID());
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
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
                    if(object.getComment() != null){
                        assert mAuth.getCurrentUser() != null;
                        if(dislikeCheck[0]){ //delete dislike, dislikeCheck=false
                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                if(snapshot.child("dislikes").exists()){
                                                    for(DataSnapshot d : snapshot.child("dislikes").getChildren()){
                                                        assert d.getKey() != null;
                                                        if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("dislikes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                            mRef.child("users").child(object.getUserID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                            long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                            long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                                    .child("myDislikes").child(object.getItemID()).removeValue();
                                                                            dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike));
                                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                            popNum = popNum + 1;
                                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                                    .child("popularity").setValue(popNum);
                                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                                    .child(object.getItemID()).setValue(popNum);
                                                                            dislikeCheck[0] = false;
                                                                            newDisliked = newDisliked - 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myDislikedNumber").setValue(newDisliked);
                                                                            newNumD = newNumD - 1;
                                                                            dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                            deleteNotification(2,object.getUserID(),object.getItemID());
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error2) {
                                                                            Toast.makeText(mContext,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }else{
                                                    dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike));
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{ //add dislike, dislikeCheck=true, like control
                            Map<String, String> time = ServerValue.TIMESTAMP;
                            mRef.child("ratings").child("withComment").child(object.getItemID()).child("dislikes")
                                    .child(mAuth.getCurrentUser().getUid()).setValue(true);
                            mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myDislikes")
                                    .child(object.getItemID()).setValue(time);
                            dislikeCheck[0] = true;
                            dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_dislike_full));
                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                if(snapshot.child("likes").exists()){
                                                    boolean lCheck = false;
                                                    for(DataSnapshot d : snapshot.child("likes").getChildren()){
                                                        assert d.getKey() != null;
                                                        if(d.getKey().equals(mAuth.getCurrentUser().getUid())){
                                                            lCheck = true;
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("likes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                                            mRef.child("users").child(object.getUserID())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                            long newLiked = Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue()));
                                                                            long newNum = snapshot.child("likes").getChildrenCount();
                                                                            long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                            long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                                                                    .child("myLikes").child(object.getItemID()).removeValue();
                                                                            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_like));
                                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                            popNum = popNum - 2;
                                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                                    .child("popularity").setValue(popNum);
                                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                                    .child(object.getItemID()).setValue(popNum);
                                                                            likeCheck[0] = false;
                                                                            newDisliked = newDisliked + 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myDislikedNumber").setValue(newDisliked);
                                                                            newLiked = newLiked - 1;
                                                                            mRef.child("users").child(object.getUserID())
                                                                                    .child("myLikedNumber").setValue(newLiked);
                                                                            newNum = newNum - 1;
                                                                            likeNum.setText(LargeNumberConverter.format(newNum));
                                                                          //  newNumD = newNumD + 1;
                                                                            dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                            addNotification(object.getProfID(),2,object.getUserID(),object.getItemID());
                                                                            deleteNotification(1,object.getUserID(),object.getItemID());
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error2) {
                                                                            Toast.makeText(mContext,error2.getMessage(),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                    if(!lCheck) {
                                                        mRef.child("users").child(object.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                                long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                                long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                                long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                                popNum = popNum - 1;
                                                                mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                        .child("popularity").setValue(popNum);
                                                                mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                        .child(object.getItemID()).setValue(popNum);
                                                                newDisliked = newDisliked + 1;
                                                                mRef.child("users").child(object.getUserID())
                                                                        .child("myDislikedNumber").setValue(newDisliked);
                                                                dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                                addNotification(object.getProfID(),2,object.getUserID(),object.getItemID());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }else{
                                                    mRef.child("users").child(object.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            long popNum = Long.parseLong(String.valueOf(snapshot.child("popularity").getValue()));
                                                            long newDisliked = Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue()));
                                                            long newNumD = snapshot.child("dislikes").getChildrenCount();
                                                            popNum = popNum - 1;
                                                            mRef.child("ratings").child("withComment").child(object.getItemID())
                                                                    .child("popularity").setValue(popNum);
                                                            mRef.child("Professors").child(object.getProfID()).child("ratings_comment")
                                                                    .child(object.getItemID()).setValue(popNum);
                                                            newDisliked = newDisliked + 1;
                                                            mRef.child("users").child(object.getUserID())
                                                                    .child("myDislikedNumber").setValue(newDisliked);
                                                            dislikeNum.setText(LargeNumberConverter.format(newNumD));
                                                            addNotification(object.getProfID(),2,object.getUserID(),object.getItemID());
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
            });
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.setTitle(mContext.getResources().getString(R.string.report));
                    ad.setMessage(mContext.getResources().getString(R.string.report_exp));
                    ad.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String key = FirebaseDatabase.getInstance().getReference().child("reports").push().getKey();
                                Map<String, String> time = ServerValue.TIMESTAMP;
                                final HashMap<String,Object> report = new HashMap<>();
                                report.put("time",time);
                                assert mAuth.getCurrentUser() != null;
                                report.put("userID",mAuth.getCurrentUser().getUid());
                                report.put("commentID",object.getItemID());
                                assert key != null;
                                FirebaseDatabase.getInstance().getReference().child("reports").child(key).setValue(report)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                ad.create().cancel();
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext,mContext.getResources().getString(R.string.report_done)
                                                            ,Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(mContext,mContext.getResources().getString(R.string.errorOccurred)
                                                            ,Toast.LENGTH_LONG).show();
                                                } }
                                        });
                            }
                        });
                        ad.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ad.create().cancel();
                            }
                        });
                    ad.create().show();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                    ad.setTitle(mContext.getResources().getString(R.string.delete));
                    ad.setMessage(mContext.getResources().getString(R.string.delete_exp));
                    ad.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            assert mAuth.getCurrentUser() != null;
                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                    .child("myRatings").child(object.getItemID()).removeValue();
                            mRef.child("ratings").child("withComment").child(object.getItemID()).removeValue();
                            mRef.child("users").child(mAuth.getCurrentUser().getUid())
                                    .child("myRatings").child(object.getItemID()).removeValue();
                            mRef.child("Professors").child(object.getProfID())
                                    .child("ratings_comment").child(object.getItemID()).removeValue();
                            mRef.child("Professors").child(object.getProfID())
                                    .child("ratings_total").child(object.getItemID()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ad.create().cancel();
                                    if(task.isSuccessful()){
                                        layout.setVisibility(View.GONE);
                                        Toast.makeText(mContext,mContext.getResources().getString(R.string.delete_done)
                                                ,Toast.LENGTH_LONG).show();
                                        updateRating(object.getProfID(),object);
                                    }else{
                                        Toast.makeText(mContext,mContext.getResources().getString(R.string.errorOccurred)
                                                ,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }
                    });
                    ad.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
                    Intent intent = new Intent(mContext, ClickedUserActivity.class);
                    intent.putExtra("cUserID",object.getUserID());
                    mContext.startActivity(intent);
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ClickedUserActivity.class);
                    intent.putExtra("cUserID",object.getUserID());
                    mContext.startActivity(intent);
                }
            });
            profLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ClickedProfActivity.class);
                    intent.putExtra("cProfID",object.getProfID());
                    mContext.startActivity(intent);
                }
            });
        }

        private void updateRating(final String profID, final CommentsObject object){
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
                    help = help - object.getHelpNum();
                    double diff = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("difficulty").getValue()));
                    diff = diff - object.getDiffNum();
                    double lec = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("lecture").getValue()));
                    lec = lec - object.getLecNum();
                    mRef.child("Professors").child(object.getProfID()).child("ratings").child("helpfulness").setValue(help);
                    mRef.child("Professors").child(object.getProfID()).child("ratings").child("difficulty").setValue(diff);
                    mRef.child("Professors").child(object.getProfID()).child("ratings").child("lecture").setValue(lec);
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
                    Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

        private void addNotification(final String profID, final int type, final String userID, final String ratingID){
            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("ratings").child("withComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    mRef.child("Professors").child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            HashMap<String,Object> not = new HashMap<>();
                            not.put("ratingID",ratingID);
                            assert FirebaseAuth.getInstance().getCurrentUser() != null;
                            not.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            not.put("profName",String.valueOf(snapshot2.child("prof_name").getValue()));
                            not.put("picName",String.valueOf(type));
                            if(type==1){
                                not.put("totalLikes",snapshot.child("likes").getChildrenCount());
                            }else{
                                not.put("totalDislikes",snapshot.child("dislikes").getChildrenCount());
                            }
                            String key = mRef.child("users").child(userID).child("myNotifications").push().getKey();
                            assert key != null;
                            Map<String, String> time = ServerValue.TIMESTAMP;
                            not.put("time",time);
                            mRef.child("users").child(userID).child("myNotifications").child(key).setValue(not);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error2) {
                            Toast.makeText(mContext,error2.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

        private void deleteNotification(final int type, final String userID, final String ratingID){
            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("myNotifications");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot d : snapshot.getChildren()){
                            if(String.valueOf(d.child("ratingID").getValue()).equals(ratingID) &&
                                    String.valueOf(d.child("userID").getValue()).equals(userID) &&
                                    String.valueOf(d.child("picName").getValue()).equals(String.valueOf(type))){
                                assert d.getKey() != null;
                                mRef.child(d.getKey()).removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
