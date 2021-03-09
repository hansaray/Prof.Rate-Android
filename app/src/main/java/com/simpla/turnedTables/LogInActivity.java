package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class LogInActivity extends AppCompatActivity {
    private Button backButton,logInButton;
    private EditText username,password;
    private TextView forgotPass;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;
    private FirebaseAuth.AuthStateListener listener;
    private FrameLayout frameLayout;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(LogInActivity.this);
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(LogInActivity.this);
            pd = new AlertDialog.Builder(LogInActivity.this);
        } else {
            ad = new AlertDialog.Builder(LogInActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(LogInActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setupAuthListener();
        findIds();
    }

    private void findIds(){
        backButton = findViewById(R.id.logInBack);
        logInButton = findViewById(R.id.logInButton);
        username = findViewById(R.id.logInUsername);
        password = findViewById(R.id.logInPassword);
        forgotPass = findViewById(R.id.logInForgot);
        layout = findViewById(R.id.logInLayout);
        frameLayout = findViewById(R.id.logInFrameLayout);
        progressDialog = getDialogProgressBar().create();
        setListeners();
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInProcess();
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.logInFrameLayout, new ForgotPasswordFragment()).commit();
            }
        });
    }

    private void logInProcess(){
        progressDialog.show();
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String loginName = username.getText().toString();
        final String logPassword = password.getText().toString();
        if(loginName.contains("@")){
            mRef.child("bannedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean banCheck = false;
                    if(snapshot.exists()){
                        for(DataSnapshot d : snapshot.getChildren()){
                            if(String.valueOf(d.child("email").getValue()).equals(loginName)){
                                banCheck = true;
                                break;
                            }
                        }
                    }
                    if(!banCheck){
                        mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean emailCheck = false;
                                if(snapshot.exists()){
                                    for(DataSnapshot d : snapshot.getChildren()){
                                        if(String.valueOf(d.child("email").getValue()).equals(loginName)){
                                            mAuth.signInWithEmailAndPassword(loginName,logPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.cancel();
                                                        fcmToken();
                                                        finish();
                                                        startActivity(new Intent(LogInActivity.this,MainActivity.class));
                                                    } else {
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.error));
                                                        ad.setMessage(getResources().getString(R.string.password_wrong));
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
                                            emailCheck = true;
                                            break;
                                        }
                                    }
                                    if(!emailCheck){
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
                                    ad.setMessage(getResources().getString(R.string.noUser));
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
                                Toast.makeText(LogInActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LogInActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mRef.child("bannedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean banCheck = false;
                    if(snapshot.exists()){
                        for(DataSnapshot d : snapshot.getChildren()){
                            if(String.valueOf(d.child("username").getValue()).equals(loginName)){
                                banCheck = true;
                                break;
                            }
                        }
                    }
                    if(!banCheck){
                        mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean emailCheck = false;
                                if(snapshot.exists()){
                                    for(DataSnapshot d : snapshot.getChildren()){
                                        if(String.valueOf(d.child("username").getValue()).equals(loginName)){
                                            String email = String.valueOf(d.child("email").getValue());
                                            mAuth.signInWithEmailAndPassword(email,logPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.cancel();
                                                        fcmToken();
                                                        finish();
                                                        startActivity(new Intent(LogInActivity.this,MainActivity.class));
                                                    } else {
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.error));
                                                        ad.setMessage(getResources().getString(R.string.password_wrong));
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
                                            emailCheck = true;
                                            break;
                                        }
                                    }
                                    if(!emailCheck){
                                        progressDialog.cancel();
                                        ad.setTitle(getResources().getString(R.string.error));
                                        ad.setMessage(getResources().getString(R.string.username_invalid));
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
                                    ad.setMessage(getResources().getString(R.string.noUser));
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
                                Toast.makeText(LogInActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LogInActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
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
                    Intent intent = new Intent(LogInActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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