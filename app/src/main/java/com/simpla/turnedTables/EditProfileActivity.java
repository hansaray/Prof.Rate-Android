package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Objects.UserObject;
import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.simpla.turnedTables.Utils.SetProfilePicture;
import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private Button backButton,saveButton;
    private FirebaseAuth.AuthStateListener listener;
    private TextView username,myRates,myLikes,changePassword,myDislikes,deleteAccount;
    private CircleImageView circleImageView;
    private AlertDialog.Builder ad,pd;
    private String dUniName,dFieldName,dEmail,dPhoto,password,chosenPhotoStr;
    private EditText uniName,fieldName,email,input;
    private AlertDialog progressDialog;
    private ConstraintLayout layout;
    private FrameLayout frameLayout;
    private UserObject object;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(EditProfileActivity.this);
        input = new EditText(EditProfileActivity.this);
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(EditProfileActivity.this);
            pd = new AlertDialog.Builder(EditProfileActivity.this);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                input.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
            } else{
                input.setBackground(getResources().getDrawable(R.color.mDarkBlue));
            }
            input.setTextColor(getResources().getColor(R.color.black));
        } else {
            ad = new AlertDialog.Builder(EditProfileActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(EditProfileActivity.this, R.style.AlertDialogCustomDark);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                input.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
            } else{
                input.setBackground(getResources().getDrawable(R.color.mBlue));
            }
            input.setTextColor(getResources().getColor(R.color.white));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupAuthListener();
        EventBus.getDefault().register(this);
        getIntentData();
        findIds();
        setListeners();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        object = (UserObject) intent.getSerializableExtra("userData");
    }

    private void findIds(){
        backButton = findViewById(R.id.editBack);
        username = findViewById(R.id.editUsername);
        uniName = findViewById(R.id.editTxtUni);
        fieldName = findViewById(R.id.editTxtField);
        email = findViewById(R.id.editTxtEmail);
        myRates = findViewById(R.id.editMyRates);
        myLikes = findViewById(R.id.editMyLikes);
        myDislikes = findViewById(R.id.editMyDislikes);
        changePassword = findViewById(R.id.editChangePassword);
        circleImageView = findViewById(R.id.editPicture);
        saveButton = findViewById(R.id.editSaveButton);
        layout = findViewById(R.id.editLayout);
        frameLayout = findViewById(R.id.editFrameLayout);
        deleteAccount = findViewById(R.id.editDeleteAccount);
        progressDialog = getDialogProgressBar().create();
        if(object != null){
            setUserData();
        }else{
            loadUserData();
        }
    }

    private void setUserData(){
        dEmail = object.getEmail();
        dFieldName = object.getFieldName();
        dUniName = object.getUniName();
        dPhoto = object.getPhotoName();
        username.setText(object.getUsername());
        uniName.setHint(dUniName);
        fieldName.setHint(dFieldName);
        email.setHint(dEmail);
        SetProfilePicture.setPicture(dPhoto,circleImageView,EditProfileActivity.this);
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProcess();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                EventBus.getDefault().postSticky(new EventbusDataEvents.activityCheck(1));
                getSupportFragmentManager().beginTransaction().replace(R.id.editFrameLayout,new SignUpChoosePhotoFragment()).commit();
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.editFrameLayout,new ChangePasswordFragment()).commit();
            }
        });
        myRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                EventBus.getDefault().postSticky(new EventbusDataEvents.userData(object));
                getSupportFragmentManager().beginTransaction().replace(R.id.editFrameLayout,new MyRatingsFragment()).commit();
            }
        });
        myLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                EventBus.getDefault().postSticky(new EventbusDataEvents.userLikeNumber(object.getTotalLikes()));
                getSupportFragmentManager().beginTransaction().replace(R.id.editFrameLayout,new MyLikesFragment()).commit();
            }
        });
        myDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                EventBus.getDefault().postSticky(new EventbusDataEvents.userLikeNumber(object.getTotalDislikes()));
                getSupportFragmentManager().beginTransaction().replace(R.id.editFrameLayout,new MyDislikesFragment()).commit();
            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.setTitle(getResources().getString(R.string.delete_account));
                ad.setMessage(getResources().getString(R.string.deleted_exp));
                ad.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.show();
                        deleteProcess();
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
    }

    private void deleteProcess(){
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("myLikes").exists()){
                    for(DataSnapshot d : snapshot.child("myLikes").getChildren()){
                        assert d.getKey() != null;
                        mRef.child("ratings").child("withComment").child(d.getKey()).child("likes").child(myUid).removeValue();
                    }
                }
                if(snapshot.child("myDislikes").exists()){
                    for(DataSnapshot d : snapshot.child("myDislikes").getChildren()){
                        assert d.getKey() != null;
                        mRef.child("ratings").child("withComment").child(d.getKey()).child("dislikes").child(myUid).removeValue();
                    }
                }
                if(snapshot.child("myRatings").exists()){
                    for(final DataSnapshot d : snapshot.child("myRatings").getChildren()){
                        assert d.getKey() != null;
                        if(String.valueOf(d.getValue()).equals("true")){
                            mRef.child("ratings").child("noComment").child(d.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String profID = String.valueOf(snapshot.child("profID").getValue());
                                            mRef.child("Professors").child(String.valueOf(snapshot.child("profID").getValue()))
                                                    .child("ratings_total").child(d.getKey()).removeValue();
                                            mRef.child("ratings").child("noComment").child(d.getKey()).removeValue();
                                            updateRating(profID);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{
                            mRef.child("ratings").child("withComment").child(d.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String profID = String.valueOf(snapshot.child("profID").getValue());
                                            mRef.child("Professors").child(profID)
                                                    .child("ratings_comment").child(d.getKey()).removeValue();
                                            mRef.child("Professors").child(String.valueOf(snapshot.child("profID").getValue()))
                                                    .child("ratings_total").child(d.getKey()).removeValue();
                                            mRef.child("ratings").child("withComment").child(d.getKey()).removeValue();
                                            updateRating(profID);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        if(task.isSuccessful()){
                            mRef.child("users").child(myUid).removeValue();
                            Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.deleted_account),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.errorOccurred),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateRating(final String profID){
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
                Toast.makeText(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProcess(){
        progressDialog.show();
        final String lastUni = uniName.getText().toString();
        final String lastField = fieldName.getText().toString();
        final String lastEmail = email.getText().toString();
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(!lastUni.isEmpty() || !lastField.isEmpty() || !lastEmail.isEmpty() || !chosenPhotoStr.isEmpty()){
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean check = false;
                    if(!lastUni.isEmpty() && !lastUni.equals(dUniName)){
                        mRef.child("university").setValue(lastUni);
                    }
                    if(!lastField.isEmpty() && !lastField.equals(dFieldName)){
                        mRef.child("faculty").setValue(lastField);
                    }
                    if(!chosenPhotoStr.isEmpty() && !chosenPhotoStr.equals(dPhoto)){
                        mRef.child("photo").setValue(chosenPhotoStr);
                    }
                    if(!lastEmail.isEmpty() && !lastEmail.equals(dEmail) && isValidEmail(lastEmail)){
                        check = true;
                        emailUpdate(lastEmail);
                    }
                    if(!check){
                        progressDialog.cancel();
                        Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.Saved),Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loadUserData(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        mRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dEmail = String.valueOf(snapshot.child("email").getValue());
                        dFieldName = String.valueOf(snapshot.child("faculty").getValue());
                        dUniName = String.valueOf(snapshot.child("university").getValue());
                        dPhoto = String.valueOf(snapshot.child("photo").getValue());
                        username.setText(String.valueOf(snapshot.child("username").getValue()));
                        uniName.setHint(dUniName);
                        fieldName.setHint(dFieldName);
                        email.setHint(dEmail);
                        SetProfilePicture.setPicture(dPhoto,circleImageView,EditProfileActivity.this);
                        //setImage(dPhoto);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private static boolean isValidEmail (String controlEmail){
        if(controlEmail.isEmpty()){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(controlEmail).matches();
    }

    private void emailUpdate(String email){
        if(dEmail != null){
            if(!dEmail.isEmpty()){
                if(!dEmail.equals(email)){
                    ReAuthenticate(dEmail,email);
                }else{
                    progressDialog.cancel();
                    Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.Saved),Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            } else {
                progressDialog.cancel();
                Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.Saved),Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        } else {
            progressDialog.cancel();
            Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.Saved),Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void ReAuthenticate(final String loginEmail, final String newEmail){
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.cancel();
        if(mUser != null){
            ad.setTitle(getResources().getString(R.string.password));
            ad.setMessage(getResources().getString(R.string.ChangeEmailExp));
            input.setHintTextColor(getResources().getColor(R.color.hint));
            input.setHint(getResources().getString(R.string.password));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            input.setLayoutParams(lp);
            ad.setView(input);

            ad.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            password = input.getText().toString();
                            progressDialog.show();
                            if(!password.isEmpty()){
                                AuthCredential credential = EmailAuthProvider.getCredential(loginEmail,password);
                                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        assert FirebaseAuth.getInstance().getCurrentUser() != null;
                                                        DatabaseReference uRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        uRef.child("email").setValue(newEmail);
                                                        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    progressDialog.cancel();
                                                                    Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.EmailChangedVerificationSent),Toast.LENGTH_LONG).show();
                                                                    onBackPressed();
                                                                }else{
                                                                    progressDialog.cancel();
                                                                    Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.EmailChanged),Toast.LENGTH_LONG).show();
                                                                    onBackPressed();
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        progressDialog.cancel();
                                                        Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.errorOccurred),Toast.LENGTH_LONG).show();
                                                        onBackPressed();
                                                    }
                                                }
                                            });
                                        }else{
                                            progressDialog.cancel();
                                            Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.password_wrong),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                progressDialog.cancel();
                                Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.password_wrong),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            ad.setNegativeButton(getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            onBackPressed();
                        }
                    });
            ad.show();
        } else {
            Toast.makeText(EditProfileActivity.this,getResources().getString(R.string.Saved),Toast.LENGTH_LONG).show();
            onBackPressed();
        }
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
                    Intent intent = new Intent(EditProfileActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        };
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.chosenPhoto data){
        chosenPhotoStr = data.getPhotoName();
        if(chosenPhotoStr != null && !chosenPhotoStr.isEmpty() && circleImageView != null ) {
            SetProfilePicture.setPicture(chosenPhotoStr,circleImageView,EditProfileActivity.this);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chosenPhotoStr = "";
        EventBus.getDefault().unregister(this);
    }

}