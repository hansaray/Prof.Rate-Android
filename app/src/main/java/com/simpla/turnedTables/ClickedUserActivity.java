package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Adapters.CommentsAdapter;
import com.simpla.turnedTables.Objects.CommentsObject;
import com.simpla.turnedTables.Utils.RatingColor;
import com.simpla.turnedTables.Utils.SetProfilePicture;
import com.simpla.turnedTables.Utils.SetTheme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickedUserActivity extends AppCompatActivity {

    private Button backButton;
    private TextView username,uniName,fieldName,rateNumber,likeNumber,likedNumber,avgNumber
            ,dislikeNumber,dislikedNumber;
    private CircleImageView circleImageView;
    private RecyclerView recyclerView;
    private ConstraintLayout noRating;
    private ProgressBar progressBar;
    private long cNumber = 0;
    private int numOfItems = 10;
    private boolean lastPage = false;
    private CommentsAdapter adapter;
    private ArrayList<CommentsObject> list;
    private ArrayList<String> ids;
    private DatabaseReference mRef;
    private String uid,uName,unName,fName,pName;
    private long myLiked,myDisliked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetTheme.theme(ClickedUserActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_user);
        getIntentData();
        findIds();
        setListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("cUserID");
    }

    private void findIds() {
        backButton = findViewById(R.id.cUserBack);
        username = findViewById(R.id.cUserUsername);
        uniName = findViewById(R.id.cUserUniName);
        fieldName = findViewById(R.id.cUserFieldName);
        rateNumber = findViewById(R.id.cUserRateNumber);
        likeNumber = findViewById(R.id.cUserLikeNumber);
        likedNumber = findViewById(R.id.cUserLikedNumber);
        dislikeNumber = findViewById(R.id.cUserDislikeNumber);
        dislikedNumber = findViewById(R.id.cUserDislikedNumber);
        avgNumber = findViewById(R.id.cUserAvgNumber);
        circleImageView = findViewById(R.id.cUserPicture);
        recyclerView = findViewById(R.id.cUserRw);
        progressBar = findViewById(R.id.cUserProgressBar);
        noRating = findViewById(R.id.noRatingLayout);
        mRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new CommentsAdapter(ClickedUserActivity.this,list);
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        loadUserData();
        setupRw();
    }

    private void loadUserData(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        mRef.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cNumber = snapshot.child("myRatings").getChildrenCount();
                        uName = String.valueOf(snapshot.child("username").getValue());
                        unName = String.valueOf(snapshot.child("university").getValue());
                        fName = String.valueOf(snapshot.child("faculty").getValue());
                        pName = String.valueOf(snapshot.child("photo").getValue());
                        username.setText(uName);
                        uniName.setText(unName);
                        fieldName.setText(fName);
                        SetProfilePicture.setPicture(pName,circleImageView,ClickedUserActivity.this);
                        if(snapshot.child("myRatings").exists()){
                            rateNumber.setText(String.valueOf(snapshot.child("myRatings").getChildrenCount()));
                        } else {
                            rateNumber.setText("0");
                        }
                        if(snapshot.child("myLikes").exists()){
                            likeNumber.setText(String.valueOf(snapshot.child("myLikes").getChildrenCount()));
                        } else {
                            likeNumber.setText("0");
                        }
                        if(snapshot.child("myDislikes").exists()){
                            dislikeNumber.setText(String.valueOf(snapshot.child("myDislikes").getChildrenCount()));
                        } else {
                            dislikeNumber.setText("0");
                        }
                        if(snapshot.child("myLikedNumber").exists()){
                            myLiked = Long.parseLong(String.valueOf(snapshot.child("myLikedNumber").getValue()));
                            likedNumber.setText(String.valueOf(myLiked));
                        } else {
                            likedNumber.setText("0");
                            myLiked = 0;
                        }
                        if(snapshot.child("myDislikedNumber").exists()){
                            myDisliked = Long.parseLong(String.valueOf(snapshot.child("myDislikedNumber").getValue()));
                            dislikedNumber.setText(String.valueOf(myDisliked));
                        } else {
                            dislikedNumber.setText("0");
                            myDisliked = 0;
                        }
                        long finalAvg = myLiked - myDisliked;
                        avgNumber.setText("("+finalAvg+")");
                        avgNumber.setTextColor(RatingColor.setAvgColor(finalAvg,ClickedUserActivity.this));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClickedUserActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupRw(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(ClickedUserActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                assert recyclerView.getAdapter() != null;
                if(dy>0 && layoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount()-1){
                    if(!lastPage){
                        if(((list.size()+1)-numOfItems) >= 0){
                            if((cNumber-numOfItems)>10){
                                numOfItems += 10;
                            }else if((cNumber-numOfItems)==10){
                                numOfItems += (cNumber-numOfItems);
                                lastPage = true;
                            }else{
                                numOfItems += (cNumber-numOfItems);
                                lastPage = true;
                            }
                            loadMore();
                        }
                    }
                }
            }
        });
        loadRatings();
    }

    private void loadRatings(){
        mRef.child("users").child(uid).child("myRatings").orderByKey().limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        long num = snapshot.getChildrenCount();
                        if(num==0){
                            noRating.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            if(Boolean.parseBoolean(String.valueOf(d.getValue()))){
                                addRating(d.getKey(),true);
                            }else{
                                addRating(d.getKey(),false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClickedUserActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadMore(){
        final int number = list.size();
        mRef.child("users").child(uid).child("myRatings").orderByKey()
                .limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                ids.clear();
                for(final DataSnapshot d : snapshot.getChildren()){
                    ids.add(d.getKey());
                }
                Collections.reverse(ids);
                for(final String s : ids){
                    if(count >= number){
                        if(Boolean.parseBoolean(String.valueOf(snapshot.child(s).getValue()))){
                            addRating(s,true);
                        }else{
                            addRating(s,false);
                        }
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClickedUserActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addRating(final String ratingID,boolean type){
        if(type){
            mRef.child("ratings").child("withComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    CommentsObject cObject = new CommentsObject(uid
                            ,pName,uName,fName,unName
                            ,Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("difficulty").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("lecture").getValue()))
                            ,String.valueOf(snapshot.child("comment").getValue()),Long.parseLong(String.valueOf(snapshot.child("time").getValue())));
                    if(snapshot.child("attendance").exists()){
                        cObject.setAttNum(Double.parseDouble(String.valueOf(snapshot.child("attendance").getValue())));
                    }
                    if(snapshot.child("textbook").exists()){
                        cObject.setTxtNum(Double.parseDouble(String.valueOf(snapshot.child("textbook").getValue())));
                    }
                    if(snapshot.child("likes").exists()){
                        cObject.setLikeNum(snapshot.child("likes").getChildrenCount());
                    }
                    if(snapshot.child("dislikes").exists()){
                        cObject.setDislikeNum(snapshot.child("dislikes").getChildrenCount());
                    }
                    cObject.setPopularity(Integer.parseInt(String.valueOf(snapshot.child("popularity").getValue())));
                    cObject.setItemID(ratingID);
                    cObject.setProfID(String.valueOf(snapshot.child("profID").getValue()));
                    cObject.setCheck(true);
                    cObject.setUserLikedNum(myLiked);
                    cObject.setUserDislikedNum(myDisliked);
                    list.add(cObject);
                    adapter.notifyDataSetChanged();
                    sort();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    noRating.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClickedUserActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            mRef.child("ratings").child("noComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    CommentsObject cObject = new CommentsObject(uid
                            ,pName,uName,fName,unName
                            ,Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("difficulty").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("lecture").getValue()))
                            ,null,Long.parseLong(String.valueOf(snapshot.child("time").getValue())));
                    if(snapshot.child("attendance").exists()){
                        cObject.setAttNum(Double.parseDouble(String.valueOf(snapshot.child("attendance").getValue())));
                    }
                    if(snapshot.child("textbook").exists()){
                        cObject.setTxtNum(Double.parseDouble(String.valueOf(snapshot.child("textbook").getValue())));
                    }
                    cObject.setPopularity(0);
                    cObject.setItemID(ratingID);
                    cObject.setProfID(String.valueOf(snapshot.child("profID").getValue()));
                    cObject.setCheck(true);
                    list.add(cObject);
                    adapter.notifyDataSetChanged();
                    sort();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClickedUserActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sort(){
        Collections.sort(list, new Comparator<CommentsObject>() {
            @Override
            public int compare(CommentsObject o1, CommentsObject o2) {
                return Double.compare(o2.getTime(), o1.getTime());
            }
        });
        adapter.notifyDataSetChanged();
    }
}