package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {

    private Button backButton,sendButton;
    private EditText mail,title,text;
    private String currentMail;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;
    private AdView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeCheck = SetTheme.theme(ContactUsActivity.this);
        if(darkModeCheck == 1){
            ad = new AlertDialog.Builder(ContactUsActivity.this);
            pd = new AlertDialog.Builder(ContactUsActivity.this);
        }else{
            ad = new AlertDialog.Builder(ContactUsActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(ContactUsActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getIntentData();
        findIds();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        currentMail = intent.getStringExtra("contactEmail");
    }

    private void findIds() {
        backButton = findViewById(R.id.contactBack);
        sendButton = findViewById(R.id.contactSend);
        mail = findViewById(R.id.contactMail);
        title = findViewById(R.id.contactTitle2);
        text = findViewById(R.id.contactText);
        banner = findViewById(R.id.contactBanner);
        progressDialog = getDialogProgressBar().create();
        setMail();
        setAds();
        setListeners();
    }

    private void setMail() {
        if(currentMail != null){
            mail.setText(currentMail);
        }
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneControl();
            }
        });
    }

    private void doneControl() {
        if((mail != null && !mail.getText().toString().isEmpty()) && (title != null && !title.getText().toString().isEmpty())
                && (text != null && !text.getText().toString().isEmpty())){
            String email = mail.getText().toString();
            String title_Str = title.getText().toString();
            String text_Str =  text.getText().toString();
            Map<String, String> time = ServerValue.TIMESTAMP;
            String key = FirebaseDatabase.getInstance().getReference().child("contactUs").push().getKey();
            HashMap<String,Object> content = new HashMap<>();
            content.put("email",email);
            content.put("title",title_Str);
            content.put("message",text_Str);
            content.put("time",time);
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                content.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
            assert key != null;
            FirebaseDatabase.getInstance().getReference().child("contactUs").child(key).setValue(content)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressDialog.cancel();
                        Toast.makeText(ContactUsActivity.this,getResources().getString(R.string.successful),Toast.LENGTH_LONG).show();
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
}