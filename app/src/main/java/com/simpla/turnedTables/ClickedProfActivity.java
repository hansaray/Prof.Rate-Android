package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Adapters.CommentsAdapter;
import com.simpla.turnedTables.Objects.CommentsObject;
import com.simpla.turnedTables.Objects.SearchObject;
import com.simpla.turnedTables.Utils.DoubleFormat;
import com.simpla.turnedTables.Utils.RatingColor;
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

public class ClickedProfActivity extends AppCompatActivity {

    private SearchObject object,ratedObject;
    private Button backButton;
    private CircleImageView circleImageView;
    private TextView profName,fieldName,cityUniName,rating,rate1,rate2,rate3,rate4,rate5,total
            ,totalComments,noCommentButton;
    private ConstraintLayout ratingsLayout,titleButton,noCommentLayout;
    private ProgressBar progressBar;
    private ImageView drop,rateButton,verification;
    private DatabaseReference mRef;
    private long cNumber = 0;
    private int numOfItems = 10;
    private boolean lastPage = false;
    private CommentsAdapter adapter;
    private ArrayList<CommentsObject> list;
    private ArrayList<String> ids;
    private RecyclerView recyclerView;
    private AlertDialog.Builder ad;
    private String profID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(ClickedProfActivity.this);
        if(darkModeControl==1){
            ad = new AlertDialog.Builder(ClickedProfActivity.this);
        }else{
            ad = new AlertDialog.Builder(ClickedProfActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_prof);
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        object = (SearchObject) intent.getSerializableExtra("profData");
        ratedObject = (SearchObject) intent.getSerializableExtra("ratedProf");
        profID = intent.getStringExtra("cProfID");
        if(profID != null){
            FirebaseDatabase.getInstance().getReference().child("Professors").child(profID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String cityUniName = snapshot.child("city").getValue() +","
                            + snapshot.child("uni_name").getValue();
                    object = new SearchObject(String.valueOf(snapshot.child("prof_name").getValue())
                            ,String.valueOf(snapshot.child("field_name").getValue()),cityUniName,
                            String.valueOf(snapshot.child("photo").getValue())
                            ,Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue())),profID);
                    if(snapshot.child("title").exists()){
                        object.setTitle(String.valueOf(snapshot.child("title").getValue()));
                    }
                    findIds();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(ratedObject != null){
            object = ratedObject;
            findIds();
        }else{
            findIds();
        }
    }

    private void findIds() {
        backButton = findViewById(R.id.clickedProfBack);
        circleImageView = findViewById(R.id.clickedProfPicture);
        profName = findViewById(R.id.clickedProfName);
        fieldName = findViewById(R.id.clickedProfField);
        cityUniName = findViewById(R.id.clickedProfCityUni);
        rating = findViewById(R.id.clickedProfRating);
        ratingsLayout = findViewById(R.id.clickedProfRatingsLayout);
        drop = findViewById(R.id.clickedProfDrop);
        titleButton = findViewById(R.id.clickedProfButtonLayout);
        rateButton = findViewById(R.id.clickedProfRateButton);
        rate1 = findViewById(R.id.clickedProfRT1);
        rate2 = findViewById(R.id.clickedProfRT2);
        rate3 = findViewById(R.id.clickedProfRT3);
        rate4 = findViewById(R.id.clickedProfRT4);
        rate5 = findViewById(R.id.clickedProfRT5);
        total = findViewById(R.id.clickedProfTotalNumber);
        totalComments = findViewById(R.id.clickedProfTotalComments);
        recyclerView = findViewById(R.id.clickedProfRw);
        progressBar = findViewById(R.id.clickedProfProgressBar);
        noCommentLayout = findViewById(R.id.noCommentLayout);
        noCommentButton = findViewById(R.id.noCommentButton);
        verification = findViewById(R.id.clickedProfVerification);
        list = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new CommentsAdapter(ClickedProfActivity.this,list);
        mRef = FirebaseDatabase.getInstance().getReference();
        setInfo();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setInfo() {
        if(object.getPhotoName().equals("1")){
            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.teacher_man));
        }else{
            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.teacher_woman));
        }
        fieldName.setText(object.getFieldName());
        cityUniName.setText(object.getCityUniName());
        rating.setText(DoubleFormat.setFormat(object.getRatingNumber()));
        rating.setTextColor(RatingColor.setColorRating(object.getRatingNumber(),ClickedProfActivity.this));
        String pName = object.getProfName();
        if(object.getTitle() != null && !object.getTitle().isEmpty()) {
            String title = object.getTitle();
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
        profName.setText(pName);
        mRef.child("addedProf").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean check = false;
                    for(DataSnapshot d : snapshot.getChildren()){
                        if(object.getItemID().equals(d.getKey())) {
                            verification.setImageDrawable(getResources().getDrawable(R.drawable.ic_nonverified));
                            check = true;
                            break;
                        }
                    }
                    if(!check){
                        verification.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                    }
                } else {
                    verification.setImageDrawable(getResources().getDrawable(R.drawable.ic_verified));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        setupRw();
        setListeners();
    }

    private void setupRw(){
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ClickedProfActivity.this);
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
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratedObject != null){
                    startActivity(new Intent(ClickedProfActivity.this,MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK) );
                }else{
                    onBackPressed();
                }
            }
        });
        titleButton.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    ratingsLayout.setVisibility(View.VISIBLE);
                    drop.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    ratingsLayout.setVisibility(View.GONE);
                    drop.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }
                i++;
            }
        });
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(ClickedProfActivity.this,RateActivity.class);
                    intent.putExtra("rateProfData",object);
                    startActivity(intent);
                }else{
                    ad.setTitle(getResources().getString(R.string.information));
                    ad.setMessage(getResources().getString(R.string.rate_login_exp));
                    ad.setPositiveButton(getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(ClickedProfActivity.this,LogInActivity.class));
                        }
                    });
                    ad.setNegativeButton(getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(ClickedProfActivity.this,SignUpActivity.class));
                        }
                    });
                    ad.create().show();
                }
            }
        });
        noCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(ClickedProfActivity.this,RateActivity.class);
                    intent.putExtra("rateProfData",object);
                    startActivity(intent);
                }else{
                    ad.setTitle(getResources().getString(R.string.information));
                    ad.setMessage(getResources().getString(R.string.rate_login_exp));
                    ad.setPositiveButton(getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(ClickedProfActivity.this,LogInActivity.class));
                        }
                    });
                    ad.setNegativeButton(getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(ClickedProfActivity.this,SignUpActivity.class));
                        }
                    });
                    ad.create().show();
                }
            }
        });
        loadData();
    }

    private void loadData() {
        mRef.child("Professors").child(object.getItemID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    total.setText(snapshot.child("ratings_total").getChildrenCount()+" "
                            +getResources().getString(R.string.total_ratings));
                    cNumber = snapshot.child("ratings_comment").getChildrenCount();
                    totalComments.setText(cNumber+" "+getResources().getString(R.string.total_ratings));
                    String zero = "0.0";
                    String zeroString = "-";
                    if(snapshot.child("ratings").exists()){
                        if(snapshot.child("ratings").child("helpfulness").exists()){
                            double value = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("helpfulness").getValue()));
                            double total = snapshot.child("ratings_total").getChildrenCount();
                            double finalValue = value/total;
                            rate1.setText(DoubleFormat.setFormat(finalValue));
                            rate1.setTextColor(RatingColor.setColorRating(finalValue,ClickedProfActivity.this));
                        }else{
                            rate1.setText(zero);
                            rate1.setTextColor(getResources().getColor(R.color.hint));
                        }
                        if(snapshot.child("ratings").child("attendance").exists()){
                            double value = Double.parseDouble(String.valueOf(snapshot.child("ratings")
                                    .child("attendance").child("rating").getValue()));
                            double times = Double.parseDouble(String.valueOf(snapshot.child("ratings")
                                    .child("attendance").child("times").getValue()));
                            double finalValue = value/times;
                            if(finalValue <= 1.5){
                                rate2.setText(getResources().getString(R.string.not_mandatory));
                                rate2.setTextColor(getResources().getColor(R.color.ratingGreen));
                            }else{
                                rate2.setText(getResources().getString(R.string.mandatory));
                                rate2.setTextColor(getResources().getColor(R.color.ratingRed));
                            }
                        }else{
                            rate2.setText(zeroString);
                            rate2.setTextColor(getResources().getColor(R.color.hint));
                        }
                        if(snapshot.child("ratings").child("difficulty").exists()){
                            double value = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("difficulty").getValue()));
                            double total = snapshot.child("ratings_total").getChildrenCount();
                            double finalValue = value/total;
                            rate3.setText(DoubleFormat.setFormat(finalValue));
                            rate3.setTextColor(RatingColor.setColorRatingOpposite(finalValue,ClickedProfActivity.this));
                        }else{
                            rate3.setText(zero);
                            rate3.setTextColor(getResources().getColor(R.color.hint));
                        }
                        if(snapshot.child("ratings").child("lecture").exists()){
                            double value = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("lecture").getValue()));
                            double total = snapshot.child("ratings_total").getChildrenCount();
                            double finalValue = value/total;
                            rate4.setText(DoubleFormat.setFormat(finalValue));
                            rate4.setTextColor(RatingColor.setColorRating(finalValue,ClickedProfActivity.this));
                        }else{
                            rate4.setText(zero);
                            rate4.setTextColor(getResources().getColor(R.color.hint));
                        }
                        if(snapshot.child("ratings").child("textbook").exists()){
                            double value = Double.parseDouble(String.valueOf(snapshot.child("ratings")
                                    .child("textbook").child("rating").getValue()));
                            double times = Double.parseDouble(String.valueOf(snapshot.child("ratings")
                                    .child("textbook").child("times").getValue()));
                            double finalValue = value/times;
                            if(finalValue <= 1.5){
                                rate5.setText(getResources().getString(R.string.not_mandatory));
                                rate5.setTextColor(getResources().getColor(R.color.ratingGreen));
                            }else{
                                rate5.setText(getResources().getString(R.string.mandatory));
                                rate5.setTextColor(getResources().getColor(R.color.ratingRed));
                            }
                        }else{
                            rate5.setText(zeroString);
                            rate5.setTextColor(getResources().getColor(R.color.hint));
                        }
                    }else{
                        rate1.setText(zero);
                        rate2.setText(zeroString);
                        rate3.setText(zero);
                        rate4.setText(zero);
                        rate5.setText(zeroString);
                        rate1.setTextColor(getResources().getColor(R.color.hint));
                        rate2.setTextColor(getResources().getColor(R.color.hint));
                        rate3.setTextColor(getResources().getColor(R.color.hint));
                        rate4.setTextColor(getResources().getColor(R.color.hint));
                        rate5.setTextColor(getResources().getColor(R.color.hint));
                    }
                    if(cNumber > 0){
                        loadComments();
                    } else {
                        noCommentLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    noCommentLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments(){
        mRef.child("Professors").child(object.getItemID()).child("ratings_comment").orderByValue().limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            addComment(d.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadMore(){
        final int number = list.size();
        String startKey = list.get(number - 1).getItemID();
        mRef.child("Professors").child(object.getItemID()).child("ratings_comment").orderByValue()
                .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        addComment(s);
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addComment(final String commentID){
        mRef.child("ratings").child("withComment").child(commentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                mRef.child("users").child(String.valueOf(snapshot.child("userID").getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        CommentsObject cObject = new CommentsObject(String.valueOf(snapshot.child("userID").getValue())
                                ,String.valueOf(snapshot2.child("photo").getValue()),String.valueOf(snapshot2.child("username").getValue())
                                ,String.valueOf(snapshot2.child("faculty").getValue()),String.valueOf(snapshot2.child("university").getValue())
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
                        if(snapshot2.child("myLikedNumber").exists()){
                            cObject.setUserLikedNum(Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue())));
                        }
                        if(snapshot2.child("myDislikedNumber").exists()){
                            cObject.setUserDislikedNum(Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue())));
                        }
                        cObject.setPopularity(Integer.parseInt(String.valueOf(snapshot.child("popularity").getValue())));
                        cObject.setItemID(commentID);
                        cObject.setProfID(object.getItemID());
                        list.add(cObject);
                        adapter.notifyDataSetChanged();
                        sort();
                        progressBar.setVisibility(View.GONE);
                        noCommentLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error2) {
                        Toast.makeText(ClickedProfActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClickedProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sort(){
        Collections.sort(list, new Comparator<CommentsObject>() {
            @Override
            public int compare(CommentsObject o1, CommentsObject o2) {
                return Double.compare(o2.getPopularity(), o1.getPopularity());
            }
        });
        adapter.notifyDataSetChanged();
    }

}