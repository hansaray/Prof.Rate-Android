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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Objects.SearchObject;
import com.simpla.turnedTables.Utils.DoubleFormat;
import com.simpla.turnedTables.Utils.RatingColor;
import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class RateActivity extends AppCompatActivity {

    private SearchObject object;
    private CircleImageView circleImageView;
    private TextView profName,fieldName,cityUniName,rating,title1,title2,title3;
    private Spinner helpfulness,difficulty,attendance,lecture,textbook;
    private ConstraintLayout buttonLayout1,buttonLayout2,pointsLayout,commentLayout;
    private ImageView drop1,drop2;
    private Button backButton,doneButton;
    private EditText commentTxt;
    private ArrayList<String> ratingList,ratingListString;
    private String sHelp,sDiff,sAtt,sLec,sText;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;
    private FirebaseAuth.AuthStateListener listener;
    private AdView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeCheck = SetTheme.theme(RateActivity.this);
        if(darkModeCheck == 1){
            ad = new AlertDialog.Builder(RateActivity.this);
            pd = new AlertDialog.Builder(RateActivity.this);
        }else{
            ad = new AlertDialog.Builder(RateActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(RateActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        setupAuthListener();
        getIntentData();
        findIds();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        object = (SearchObject) intent.getSerializableExtra("rateProfData");
    }

    private void findIds() {
        circleImageView = findViewById(R.id.ratePicture);
        profName = findViewById(R.id.rateName);
        fieldName = findViewById(R.id.rateField);
        cityUniName = findViewById(R.id.rateCityUni);
        rating = findViewById(R.id.rateRating);
        helpfulness = findViewById(R.id.rateSpinner1);
        difficulty = findViewById(R.id.rateSpinner2);
        lecture = findViewById(R.id.rateSpinner3);
        attendance = findViewById(R.id.rateSpinner4);
        textbook = findViewById(R.id.rateSpinner5);
        buttonLayout1 = findViewById(R.id.rateLayout);
        buttonLayout2 = findViewById(R.id.rateLayout2);
        pointsLayout = findViewById(R.id.ratePointsLayout);
        commentLayout = findViewById(R.id.rateCommentLayout);
        drop1 = findViewById(R.id.rateDrop1);
        drop2 = findViewById(R.id.rateDrop2);
        backButton = findViewById(R.id.rateBack);
        doneButton = findViewById(R.id.rateDoneButton);
        commentTxt = findViewById(R.id.rateCommentTxt);
        title1 = findViewById(R.id.rateTitle1);
        title2 = findViewById(R.id.rateTitle2);
        title3 = findViewById(R.id.rateTitle3);
        banner = findViewById(R.id.rateBanner);
        progressDialog = getDialogProgressBar().create();
        ratingList = new ArrayList<>();
        ratingListString = new ArrayList<>();
        setAds();
        setInfo();
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
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
        rating.setTextColor(RatingColor.setColorRating(object.getRatingNumber(),RateActivity.this));
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
        setTextSpannable();
        setListeners();
        setSpinners();
    }

    private void setTextSpannable(){
        Spannable spannable = new SpannableString(" "+getResources().getString(R.string.scale));
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.hint)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title1.append(spannable);
        title3.append(spannable);
        Spannable spannable1 = new SpannableString(" "+getResources().getString(R.string.scale_opposite));
        spannable1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.hint)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title2.append(spannable1);
    }

    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneControl();
            }
        });
        buttonLayout1.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    pointsLayout.setVisibility(View.VISIBLE);
                    drop1.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    pointsLayout.setVisibility(View.GONE);
                    drop1.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }
                i++;
            }
        });
        buttonLayout2.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    commentLayout.setVisibility(View.VISIBLE);
                    drop2.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    commentLayout.setVisibility(View.GONE);
                    drop2.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }
                i++;
            }
        });
        helpfulness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sHelp="";
                }else{
                    sHelp=ratingList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sDiff="";
                }else{
                    sDiff=ratingList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        lecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sLec="";
                }else{
                    sLec=ratingList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        attendance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sAtt="";
                }else{
                    sAtt=ratingListString.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        textbook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    sText="";
                }else{
                    sText=ratingListString.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinners() {
        ratingList.add("");
        ratingList.add("1");
        ratingList.add("2");
        ratingList.add("3");
        ratingList.add("4");
        ratingList.add("5");
        ArrayAdapter<String> numberSpinnerAdapter = new ArrayAdapter<>(RateActivity.this, R.layout.simple_spinner_item, ratingList);
        numberSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        helpfulness.setAdapter(numberSpinnerAdapter);
        helpfulness.setSelection(0);
        difficulty.setAdapter(numberSpinnerAdapter);
        difficulty.setSelection(0);
        lecture.setAdapter(numberSpinnerAdapter);
        lecture.setSelection(0);
        ratingListString.add("");
        ratingListString.add(getResources().getString(R.string.not_mandatory));
        ratingListString.add(getResources().getString(R.string.mandatory));
        ArrayAdapter<String> stringSpinnerAdapter = new ArrayAdapter<>(RateActivity.this, R.layout.simple_spinner_item, ratingListString);
        stringSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        attendance.setAdapter(stringSpinnerAdapter);
        attendance.setSelection(0);
        textbook.setAdapter(stringSpinnerAdapter);
        textbook.setSelection(0);
    }

    private void doneControl() {
        progressDialog.show();
        if((sHelp != null && !sHelp.isEmpty()) && (sDiff != null && !sDiff.isEmpty())
                && (sLec != null && !sLec.isEmpty())){
            if(commentTxt.getText().toString().isEmpty()){
                progressDialog.cancel();
                ad.setTitle(getResources().getString(R.string.error));
                ad.setMessage(getResources().getString(R.string.rate_no_comment));
                ad.setPositiveButton(getResources().getString(R.string.rate_title3), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ad.create().cancel();
                    }
                });
                ad.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(slangWordCheck(commentTxt.getText().toString())){
                            progressDialog.cancel();
                            ad.setTitle(getResources().getString(R.string.error));
                            ad.setMessage(getResources().getString(R.string.slang_exp));
                            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ad.create().cancel();
                                }
                            });
                            ad.create().show();
                        }else{
                            progressDialog.show();
                            uploadToDB(false);
                        }
                    }
                });
                ad.create().show();
            }else{
                if(slangWordCheck(commentTxt.getText().toString())){
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(getResources().getString(R.string.slang_exp));
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                        }
                    });
                    ad.create().show();
                }else{
                    uploadToDB(true);
                }
            }
        }else{
            progressDialog.cancel();
            ad.setTitle(getResources().getString(R.string.error));
            ad.setMessage(getResources().getString(R.string.rate_fill_all));
            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ad.create().cancel();
                }
            });
            ad.create().show();
        }
    }

    private void uploadToDB(boolean check){
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        assert mAuth.getCurrentUser() != null;
        final String key;
        Map<String, String> time = ServerValue.TIMESTAMP;
        final HashMap<String,Object> rate = new HashMap<>();
        rate.put("helpfulness",sHelp);
        rate.put("difficulty",sDiff);
        rate.put("lecture",sLec);
        rate.put("time",time);
        rate.put("userID",mAuth.getCurrentUser().getUid());
        rate.put("profID",object.getItemID());
        if(!check){
            key = mRef.child("ratings").child("noComment").push().getKey();
        }else{
            key = mRef.child("ratings").child("withComment").push().getKey();
            rate.put("comment",commentTxt.getText().toString());
            rate.put("popularity",0);
        }
        double avg = calculateRating();
        if(sAtt != null && !sAtt.isEmpty()){
            if(sAtt.equals(getResources().getString(R.string.mandatory))){
                rate.put("attendance",2);
            }else{
                rate.put("attendance",1);
            }
        }
        if(sText != null && !sText.isEmpty()){
            if(sText.equals(getResources().getString(R.string.mandatory))){
                rate.put("textbook",2);
            }else{
                rate.put("textbook",1);
            }
        }
        rate.put("avg_rating",avg);
        assert key != null;
        final double finalAvg = avg;
        if(check){
            mRef.child("ratings").child("withComment").child(key)
                    .setValue(rate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myRatings")
                                .child(key).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRef.child("Professors").child(object.getItemID()).child("ratings_total")
                                            .child(key).setValue(finalAvg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mRef.child("Professors").child(object.getItemID()).child("ratings_comment")
                                                        .child(key).setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            updateRatings();
                                                        }else{
                                                            mRef.child("Professors").child(object.getItemID()).child("ratings_total")
                                                                    .child(key).removeValue();
                                                            mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myRatings")
                                                                    .child(key).removeValue();
                                                            mRef.child("ratings").child("withComment").child(key)
                                                                    .removeValue();
                                                            progressDialog.cancel();
                                                            ad.setTitle(getResources().getString(R.string.error));
                                                            ad.setMessage(getResources().getString(R.string.errorOccurred));
                                                            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    ad.create().cancel();
                                                                }
                                                            });
                                                            ad.create().show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myRatings")
                                                        .child(key).removeValue();
                                                mRef.child("ratings").child("withComment").child(key)
                                                        .removeValue();
                                                progressDialog.cancel();
                                                ad.setTitle(getResources().getString(R.string.error));
                                                ad.setMessage(getResources().getString(R.string.errorOccurred));
                                                ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        ad.create().cancel();
                                                    }
                                                });
                                                ad.create().show();
                                            }
                                        }
                                    });
                                }else{
                                    mRef.child("ratings").child("withComment").child(key)
                                            .removeValue();
                                    progressDialog.cancel();
                                    ad.setTitle(getResources().getString(R.string.error));
                                    ad.setMessage(getResources().getString(R.string.errorOccurred));
                                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ad.create().cancel();
                                        }
                                    });
                                    ad.create().show();
                                }
                            }
                        });
                    }else{
                        progressDialog.cancel();
                        ad.setTitle(getResources().getString(R.string.error));
                        ad.setMessage(getResources().getString(R.string.errorOccurred));
                        ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ad.create().cancel();
                            }
                        });
                        ad.create().show();
                    }
                }
            });
        }else{
            mRef.child("ratings").child("noComment").child(key)
                    .setValue(rate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myRatings")
                                .child(key).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRef.child("Professors").child(object.getItemID()).child("ratings_total")
                                            .child(key).setValue(finalAvg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                updateRatings();
                                            }else{
                                                mRef.child("users").child(mAuth.getCurrentUser().getUid()).child("myRatings")
                                                        .child(key).removeValue();
                                                mRef.child("ratings").child("noComment").child(key)
                                                        .removeValue();
                                                progressDialog.cancel();
                                                ad.setTitle(getResources().getString(R.string.error));
                                                ad.setMessage(getResources().getString(R.string.errorOccurred));
                                                ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        ad.create().cancel();
                                                    }
                                                });
                                                ad.create().show();
                                            }
                                        }
                                    });
                                }else{
                                    mRef.child("ratings").child("noComment").child(key)
                                            .removeValue();
                                    progressDialog.cancel();
                                    ad.setTitle(getResources().getString(R.string.error));
                                    ad.setMessage(getResources().getString(R.string.errorOccurred));
                                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ad.create().cancel();
                                        }
                                    });
                                    ad.create().show();
                                }
                            }
                        });
                    }else{
                        progressDialog.cancel();
                        ad.setTitle(getResources().getString(R.string.error));
                        ad.setMessage(getResources().getString(R.string.errorOccurred));
                        ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ad.create().cancel();
                            }
                        });
                        ad.create().show();
                    }
                }
            });
        }
    }

    private double calculateRating(){
        double value = Double.parseDouble(sDiff);
        if(value == 5){
            value = 1;
        }else if(value == 4){
            value = 2;
        }else if(value == 2){
            value = 4;
        }else if(value == 1){
            value = 5;
        }
        double avg = (Double.parseDouble(sHelp)+value+Double.parseDouble(sLec)) / 3;
        if(sAtt != null && !sAtt.isEmpty()){
            if(avg > 0.3 && avg < 4.8 ){
                if(sAtt.equals(getResources().getString(R.string.mandatory))){
                    avg = avg-0.3;
                }else{
                    avg = avg+0.3;
                }
            }
        }
        if(sText != null && !sText.isEmpty()){
            if(avg > 0.3 && avg < 4.8 ){
                if(sText.equals(getResources().getString(R.string.mandatory))){
                    avg = avg-0.3;
                }else{
                    avg = avg+0.3;
                }
            }
        }
        return avg;
    }

    private void updateRatings(){
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Professors").child(object.getItemID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("ratings_total").exists()){
                        if(snapshot.child("ratings").child("helpfulness").exists()){
                            double help = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("helpfulness").getValue()));
                            help = help + Double.parseDouble(sHelp);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("helpfulness").setValue(help);
                        }else{
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("helpfulness").setValue(Double.parseDouble(sHelp));
                        }
                        if(snapshot.child("ratings").child("difficulty").exists()){
                            double diff = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("difficulty").getValue()));
                            diff = diff + Double.parseDouble(sDiff);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("difficulty").setValue(diff);
                        }else{
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("difficulty").setValue(Double.parseDouble(sDiff));
                        }
                        if(snapshot.child("ratings").child("lecture").exists()){
                            double lec = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("lecture").getValue()));
                            lec = lec + Double.parseDouble(sLec);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("lecture").setValue(lec);
                        }else{
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("lecture").setValue(Double.parseDouble(sLec));
                        }
                        double attNumber;
                        if(sAtt.equals(getResources().getString(R.string.mandatory))){
                            attNumber = 2;
                        }else{
                            attNumber = 1;
                        }
                        if(snapshot.child("ratings").child("attendance").exists()){
                            double att = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("attendance")
                                    .child("rating").getValue()));
                            int num = Integer.parseInt(String.valueOf(snapshot.child("ratings").child("attendance")
                                    .child("times").getValue()));
                            num = num + 1;
                            att = att + attNumber;
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("attendance").child("rating").setValue(att);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("attendance").child("times").setValue(num);
                        }else{
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("attendance").child("rating").setValue(attNumber);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("attendance").child("times").setValue(1);
                        }
                        double txtNumber;
                        if(sText.equals(getResources().getString(R.string.mandatory))){
                            txtNumber = 2;
                        }else{
                            txtNumber = 1;
                        }
                        if(snapshot.child("ratings").child("textbook").exists()){
                            double txt = Double.parseDouble(String.valueOf(snapshot.child("ratings").child("textbook")
                                    .child("rating").getValue()));
                            int num = Integer.parseInt(String.valueOf(snapshot.child("ratings").child("textbook")
                                    .child("times").getValue()));
                            num = num + 1;
                            txt = txt + txtNumber;
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("textbook").child("rating").setValue(txt);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("textbook").child("times").setValue(num);
                        }else{
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("textbook").child("rating").setValue(txtNumber);
                            mRef.child("Professors").child(object.getItemID()).child("ratings").child("textbook").child("times").setValue(1);
                        }
                        double total = snapshot.child("ratings_total").getChildrenCount();
                        double ratings = 0.0;
                        for(DataSnapshot d : snapshot.child("ratings_total").getChildren()){
                            ratings = ratings + Double.parseDouble(String.valueOf(d.getValue()));
                        }
                        double finalRating = ratings/total;
                        mRef.child("Professors").child(object.getItemID()).child("avg_rating").setValue(finalRating);
                        mRef.child("Universities").child(String.valueOf(snapshot.child("uni_name").getValue()))
                                .child("All professors").child(object.getItemID()).setValue(finalRating);
                        mRef.child("Universities").child(String.valueOf(snapshot.child("uni_name").getValue()))
                                .child(String.valueOf(snapshot.child("field_name").getValue())).child(object.getItemID()).setValue(finalRating);
                        mRef.child("Faculties").child(String.valueOf(snapshot.child("field_name").getValue()))
                                .child(String.valueOf(snapshot.child("city").getValue())).child(object.getItemID()).setValue(finalRating);
                        mRef.child("Faculties").child(String.valueOf(snapshot.child("field_name").getValue()))
                                .child("All professors").child(object.getItemID()).setValue(finalRating);
                        mRef.child("Cities").child(String.valueOf(snapshot.child("city").getValue()))
                                .child("All professors").child(object.getItemID()).setValue(finalRating);
                        progressDialog.cancel();
                        object.setRatingNumber(finalRating);
                        Intent intent = new Intent(RateActivity.this,ClickedProfActivity.class);
                        intent.putExtra("ratedProf",object);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RateActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean slangWordCheck(String commentText){
        ArrayList<String> words = new ArrayList<>();
        words.add(getResources().getString(R.string.slang_1));
        words.add(getResources().getString(R.string.slang_2));
        words.add(getResources().getString(R.string.slang_3));
        words.add(getResources().getString(R.string.slang_4));
        words.add(getResources().getString(R.string.slang_5));
        words.add(getResources().getString(R.string.slang_6));
        words.add(getResources().getString(R.string.slang_7));
        words.add(getResources().getString(R.string.slang_8));
        words.add(getResources().getString(R.string.slang_9));
        words.add(getResources().getString(R.string.slang_10));
        words.add(getResources().getString(R.string.slang_11));
        words.add(getResources().getString(R.string.slang_12));
        words.add(getResources().getString(R.string.slang_13));
        words.add(getResources().getString(R.string.slang_14));
        words.add(getResources().getString(R.string.slang_15));
        words.add(getResources().getString(R.string.slang_16));
        words.add(getResources().getString(R.string.slang_17));
        words.add(getResources().getString(R.string.slang_18));
        words.add(getResources().getString(R.string.slang_19));
        words.add(getResources().getString(R.string.slang_20));
        boolean check = false;
        for(String s : words){
            if(commentText.contains(s)){
                check = true;
                break;
            }
        }
        return check;
    }

    private AlertDialog.Builder getDialogProgressBar() {
        if (pd != null) {
            pd.setTitle(getResources().getString(R.string.loading));
            final ProgressBar progressBar = new ProgressBar(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            pd.setView(progressBar);
            pd.setCancelable(false);
        }
        return pd;
    }

    private void setupAuthListener() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
                if(myUser == null){
                    finish();
                    Intent intent = new Intent(RateActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(listener);
        }
    }


}