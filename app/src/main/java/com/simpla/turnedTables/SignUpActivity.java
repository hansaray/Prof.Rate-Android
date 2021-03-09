package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private Button backButton,signUp;
    private FrameLayout frameLayout;
    private ConstraintLayout layout;
    private DatabaseReference mRef;
    private EditText username,email,password,passwordAgain,uniName,fieldName;
    private AlertDialog.Builder ad,pd;
    private String chosenPhotoStr = "";
    private CircleImageView circleImageView;
    private TextView chooseTitle;
    private AlertDialog progressDialog;
    private int darkModeControl = 0;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        darkModeControl = SetTheme.theme(SignUpActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupAuthListener();
        EventBus.getDefault().register(this);
        findIds();
        setListeners();
    }

    private void findIds(){
        backButton = findViewById(R.id.signUpBack);
        frameLayout = findViewById(R.id.signUpFrameLayout);
        layout = findViewById(R.id.signUpLayout);
        username = findViewById(R.id.signUpTxtUsername);
        email = findViewById(R.id.signUpTxtEmail);
        password = findViewById(R.id.signUpTxtPassword);
        passwordAgain = findViewById(R.id.signUpTxtPasswordAgain);
        signUp = findViewById(R.id.signUpButton);
        uniName = findViewById(R.id.signUpUniName);
        fieldName = findViewById(R.id.signUpFieldName);
        circleImageView = findViewById(R.id.signUpPicture);
        chooseTitle = findViewById(R.id.signUpTitle5);
        mRef = FirebaseDatabase.getInstance().getReference();
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(SignUpActivity.this);
            pd = new AlertDialog.Builder(SignUpActivity.this);
        } else {
            ad = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogCustomDark);
        }
        progressDialog = getDialogProgressBar().create();
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpProcess();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.signUpFrameLayout,new SignUpChoosePhotoFragment()).commit();
                frameLayout.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
            }
        });
    }

    private void signUpProcess(){
        progressDialog.show();
        final String emailStr = email.getText().toString();
        final String usernameStr = username.getText().toString();
        final String uniNameStr = uniName.getText().toString();
        final String fieldNameStr = fieldName.getText().toString();
        if(!(usernameStr.length() < 2)) {
            if(!(usernameStr.contains("@"))){
                if(!(uniNameStr.isEmpty() || fieldNameStr.isEmpty() || (chosenPhotoStr != null && chosenPhotoStr.isEmpty()) )){
                    if(isValidEmail(emailStr)){
                        mRef.child("bannedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean banCheck = false;
                                if(snapshot.exists()){
                                    for(DataSnapshot d : snapshot.getChildren()){
                                        if(String.valueOf(d.child("email").getValue()).equals(emailStr)){
                                            banCheck = true;
                                            break;
                                        }
                                    }
                                }
                                if(!banCheck){
                                    mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean control = false;
                                            if(snapshot.exists()) {
                                                for(DataSnapshot d : snapshot.getChildren()) {
                                                    if(String.valueOf(d.child("username").getValue()).equals(usernameStr)){
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.error));
                                                        ad.setMessage(getResources().getString(R.string.username_exist));
                                                        ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                ad.create().cancel();
                                                            }
                                                        });
                                                        ad.create().show();
                                                        control = true;
                                                        break;
                                                    }
                                                    if(String.valueOf(d.child("email").getValue()).equals(emailStr)){
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.error));
                                                        ad.setMessage(getResources().getString(R.string.email_exist));
                                                        ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                ad.create().cancel();
                                                            }
                                                        });
                                                        ad.create().show();
                                                        control = true;
                                                        break;
                                                    }

                                                }
                                                if(!control){
                                                    lastStep(usernameStr,emailStr,chosenPhotoStr,uniNameStr,fieldNameStr);
                                                }
                                            } else {
                                                lastStep(usernameStr,emailStr,chosenPhotoStr,uniNameStr,fieldNameStr);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SignUpActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    progressDialog.cancel();
                                    ad.setTitle(getResources().getString(R.string.information));
                                    ad.setMessage(getResources().getString(R.string.banned_sign_up));
                                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ad.create().cancel();
                                        }
                                    });
                                    ad.create().show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SignUpActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        progressDialog.cancel();
                        ad.setTitle(getResources().getString(R.string.error));
                        ad.setMessage(getResources().getString(R.string.email_invalid));
                        ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ad.create().cancel();
                            }
                        });
                        ad.create().show();
                    }
                } else {
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(getResources().getString(R.string.empty_form));
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                        }
                    });
                    ad.create().show();
                }
            } else {
                progressDialog.cancel();
                ad.setTitle(getResources().getString(R.string.error));
                ad.setMessage(getResources().getString(R.string.username_at_error));
                ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ad.create().cancel();
                    }
                });
                ad.create().show();
            }
        } else {
            progressDialog.cancel();
            ad.setTitle(getResources().getString(R.string.error));
            ad.setMessage(getResources().getString(R.string.username_short));
            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ad.create().cancel();
                }
            });
            ad.create().show();
        }
    }

    private void lastStep(final String usernameStr, final String emailStr, final String photoStr, final String uniStr, final String fieldStr){
        String passwordStr = password.getText().toString();
        String passwordAgainStr = passwordAgain.getText().toString();
        if(passwordStr.equals(passwordAgainStr)){
            if(!(passwordStr.length() < 6)) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            HashMap<String,Object> userModel = new HashMap<>();
                            userModel.put("username",usernameStr);
                            userModel.put("email",emailStr);
                            userModel.put("photo",photoStr);
                            userModel.put("university",uniStr);
                            userModel.put("faculty",fieldStr);
                            assert FirebaseAuth.getInstance().getCurrentUser() != null;
                            mRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.cancel();
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        fcmToken();
                                        finish();
                                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                    } else {
                                        progressDialog.cancel();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
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
                        } else {
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
            } else {
                progressDialog.cancel();
                ad.setTitle(getResources().getString(R.string.error));
                ad.setMessage(getResources().getString(R.string.password_short));
                ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ad.create().cancel();
                    }
                });
                ad.create().show();
            }
        } else {
            progressDialog.cancel();
            ad.setTitle(getResources().getString(R.string.error));
            ad.setMessage(getResources().getString(R.string.password_not_match));
            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ad.create().cancel();
                }
            });
            ad.create().show();
        }
    }

    private static boolean isValidEmail (String controlEmail){
        if(controlEmail.isEmpty()){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(controlEmail).matches();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.chosenPhoto data){
        chosenPhotoStr = data.getPhotoName();
        if(chosenPhotoStr != null && !chosenPhotoStr.isEmpty() && circleImageView != null ) {
            if(chooseTitle != null){
                chooseTitle.setVisibility(View.GONE);
            }
            switch (chosenPhotoStr) {
                case "pp1":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp1));
                    break;
                case "pp2":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp2));
                    break;
                case "pp3":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp3));
                    break;
                case "pp4":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp4));
                    break;
                case "pp5":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp5));
                    break;
                case "pp6":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp6));
                    break;
                case "pp7":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp7));
                    break;
                case "pp8":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp8));
                    break;
                case "pp9":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp9));
                    break;
                case "pp10":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp10));
                    break;
                case "pp11":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp11));
                    break;
                case "pp12":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp12));
                    break;
                case "pp13":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp13));
                    break;
                case "pp14":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp14));
                    break;
                case "pp15":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp15));
                    break;
                case "pp16":
                    circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.pp16));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chosenPhotoStr = "";
        EventBus.getDefault().unregister(this);
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

    private void fcmToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                writeToStorage(token);
            }
        });
    }

    private void writeToStorage(String token){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcm_token").setValue(token);
        }
    }

    private void setupAuthListener() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
                if(myUser != null){
                    finish();
                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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