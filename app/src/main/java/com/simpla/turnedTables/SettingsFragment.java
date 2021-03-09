package com.simpla.turnedTables;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Objects.UserObject;
import com.simpla.turnedTables.Utils.LocaleHelper;
import com.simpla.turnedTables.Utils.RatingColor;
import com.simpla.turnedTables.Utils.SetProfilePicture;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private Button backButton,changeButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch darkModeSwitch;
    private MaterialCardView langView;
    private AlertDialog.Builder ad;
    private TextView langTxt,previewTitle,signOut,signUp,login,username,uniName,fieldName,rateNumber
    ,likeNumber,likedNumber,avgNumber,dislikeNumber,dislikedNumber,offerProf,contactUs,addProf;
    private ConstraintLayout cardNoUser, cardUser;
    private CircleImageView circleImageView;
    private UserObject object;
    private ImageView notificationButton;
    private DatabaseReference nRef;
    private boolean listenerControl = false;
    private boolean userControl = false;
    private AdView banner;
    private SharedPreferences settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        assert getActivity() != null;
        settings = getActivity().getApplicationContext().getSharedPreferences("PREFS_NAME",0);
        findIds(v);
        return v;
    }

    private void findIds(View v){
        backButton = v.findViewById(R.id.settingsBack);
        darkModeSwitch = v.findViewById(R.id.settingsSwitch);
        langView = v.findViewById(R.id.settingsLanguage);
        langTxt = v.findViewById(R.id.settingsLangTxt);
        changeButton = v.findViewById(R.id.settingsChangeButton);
        previewTitle = v.findViewById(R.id.settingsTitle8);
        cardNoUser = v.findViewById(R.id.settingsCard2);
        cardUser = v.findViewById(R.id.settingsCard1);
        signOut = v.findViewById(R.id.settingsSignOut);
        signUp = v.findViewById(R.id.settingsSignUp);
        login = v.findViewById(R.id.settingsLogIn);
        username = v.findViewById(R.id.settingsUsername);
        uniName = v.findViewById(R.id.settingsUniName);
        fieldName = v.findViewById(R.id.settingsFieldName);
        rateNumber = v.findViewById(R.id.settingsRateNumber);
        likeNumber = v.findViewById(R.id.settingsLikeNumber);
        likedNumber = v.findViewById(R.id.settingsLikedNumber);
        dislikeNumber = v.findViewById(R.id.settingsDislikeNumber);
        dislikedNumber = v.findViewById(R.id.settingsDislikedNumber);
        avgNumber = v.findViewById(R.id.settingsAvgNumber);
        notificationButton = v.findViewById(R.id.settingsNotificationButton);
        circleImageView = v.findViewById(R.id.settingsPicture);
        offerProf = v.findViewById(R.id.settingsOfferProf);
        contactUs = v.findViewById(R.id.settingsContactUs);
        addProf = v.findViewById(R.id.settingsAddProf);
        banner = v.findViewById(R.id.settingsBanner);
        setViews();
    }

    private void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setViews(){
        assert getActivity() != null;
        SharedPreferences control = getActivity().getApplicationContext().getSharedPreferences("PREFS_NAME",0);
        banner.setVisibility(View.VISIBLE);
        setAds();
        int darkModeControl = control.getInt("darkMode",0);
        assert getContext() != null;
        if (darkModeControl == 1){
            darkModeSwitch.setChecked(false);
            ad = new AlertDialog.Builder(getContext());
        } else {
            darkModeSwitch.setChecked(true);
            ad = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustomDark);
        }
        int langControl = control.getInt("appLang",0);
        if (langControl == 1){
            langTxt.setText(getResources().getString(R.string.en));
        } else {
            langTxt.setText(getResources().getString(R.string.tr));
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            cardNoUser.setVisibility(View.GONE);
            cardUser.setVisibility(View.VISIBLE);
            previewTitle.setVisibility(View.VISIBLE);
            changeButton.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.VISIBLE);
            notificationButton.setVisibility(View.VISIBLE);
            addProf.setVisibility(View.VISIBLE);
            userControl = true;
            nRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("myNotifications");
            iconChange();
            loadUserData();
        } else {
            cardUser.setVisibility(View.GONE);
            cardNoUser.setVisibility(View.VISIBLE);
            previewTitle.setVisibility(View.GONE);
            changeButton.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);
            notificationButton.setVisibility(View.GONE);
            addProf.setVisibility(View.GONE);
            userControl = false;
        }
        setListeners();
    }

    private void loadUserData(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        mRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = String.valueOf(snapshot.child("username").getValue());
                String unName = String.valueOf(snapshot.child("university").getValue());
                String fName = String.valueOf(snapshot.child("faculty").getValue());
                String pName = String.valueOf(snapshot.child("photo").getValue());
                String email = String.valueOf(snapshot.child("email").getValue());
                username.setText(uName);
                uniName.setText(unName);
                fieldName.setText(fName);
                if(getContext() != null){
                    SetProfilePicture.setPicture(pName,circleImageView,getContext());
                }
                object = new UserObject(uName,unName,fName,pName,email);
                if(snapshot.child("myRatings").exists()){
                    rateNumber.setText(String.valueOf(snapshot.child("myRatings").getChildrenCount()));
                    object.setTotalRatings(snapshot.child("myRatings").getChildrenCount());
                } else {
                    rateNumber.setText("0");
                    object.setTotalRatings(0);
                }
                if(snapshot.child("myLikes").exists()){
                    likeNumber.setText(String.valueOf(snapshot.child("myLikes").getChildrenCount()));
                    object.setTotalLikes(snapshot.child("myLikes").getChildrenCount());
                } else {
                    likeNumber.setText("0");
                    object.setTotalLikes(0);
                }
                if(snapshot.child("myDislikes").exists()){
                    dislikeNumber.setText(String.valueOf(snapshot.child("myDislikes").getChildrenCount()));
                    object.setTotalDislikes(snapshot.child("myDislikes").getChildrenCount());
                } else {
                    dislikeNumber.setText("0");
                    object.setTotalDislikes(0);
                }
                long myLiked,myDisliked;
                if(snapshot.child("myLikedNumber").exists()){
                    myLiked = Long.parseLong(String.valueOf(snapshot.child("myLikedNumber").getValue()));
                    likedNumber.setText(String.valueOf(myLiked));
                    object.setTotalLiked(myLiked);
                } else {
                    likedNumber.setText("0");
                    myLiked = 0;
                    object.setTotalLiked(0);
                }
                if(snapshot.child("myDislikedNumber").exists()){
                    myDisliked = Long.parseLong(String.valueOf(snapshot.child("myDislikedNumber").getValue()));
                    dislikedNumber.setText(String.valueOf(myDisliked));
                    object.setTotalDisliked(myDisliked);
                } else {
                    dislikedNumber.setText("0");
                    myDisliked = 0;
                    object.setTotalDisliked(0);
                }
                long finalAvg = myLiked - myDisliked;
                avgNumber.setText("("+finalAvg+")");
                if(getContext() != null){
                    avgNumber.setTextColor(RatingColor.setAvgColor(finalAvg,getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(SettingsFragment.this).commit();
                getActivity().findViewById(R.id.feedLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.feedFrameLayout).setVisibility(View.GONE);
            }
        });
        addProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AddProfActivity.class));
            }
        });
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                assert getActivity() != null;
                if (b) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("darkMode",0);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().finish();
                    startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("darkMode",1);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().finish();
                    startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }
        });
        langView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.setTitle(getResources().getString(R.string.language));
                ad.setMessage(getResources().getString(R.string.lang_exp));
                ad.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        assert getActivity() != null;
                        if (langTxt.getText().equals("TR")){
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("appLang",1);
                            editor.apply();
                            LocaleHelper.setLocale(getContext(),"en");
                        } else {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("appLang",0);
                            editor.apply();
                            LocaleHelper.setLocale(getContext(),"tr");
                        }
                        getActivity().finish();
                        startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
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
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),EditProfileActivity.class);
                if(object != null){
                    intent.putExtra("userData",object);
                }
                startActivity(intent);
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.setTitle(getResources().getString(R.string.signOut));
                ad.setMessage(getResources().getString(R.string.signOut_exp));
                ad.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        assert getActivity() != null;
                        getActivity().finish();
                        startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
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
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SignUpActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LogInActivity.class));
            }
        });
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),NotificationsActivity.class);
                if(object != null){
                    intent.putExtra("biggerUserData",object);
                }
                startActivity(intent);
            }
        });
        offerProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),OfferProfActivity.class));
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ContactUsActivity.class);
                if(object != null){
                    intent.putExtra("contactEmail",object.getEmail());
                }
                startActivity(intent);
            }
        });
    }

    private void iconChange(){
        if(!listenerControl){
            listenerControl=true;
            nRef.orderByChild("time").addChildEventListener(childEventListener);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            if(dataSnapshot.exists()){
                if(dataSnapshot.child("seen").exists()){
                    notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_empty));
                }else{
                    notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_full));
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            if(dataSnapshot.exists()){
                if(dataSnapshot.child("seen").exists()){
                    notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_empty));
                }else{
                    notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification_full));
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(listenerControl){
            listenerControl=false;
            nRef.removeEventListener(childEventListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!listenerControl && userControl){
            listenerControl=true;
            nRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nRef.orderByChild("time").addChildEventListener(childEventListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}