package com.simpla.turnedTables;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordFragment extends Fragment {

    private Button backButton,resetButton;
    private EditText resetEmail;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        assert getActivity() != null;
        SharedPreferences settings = getActivity().getSharedPreferences("PREFS_NAME",0);
        int darkModeControl = settings.getInt("darkMode",0);
        assert getContext() != null;
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(getContext());
            pd = new AlertDialog.Builder(getContext());
        } else {
            ad = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustomDark);
        }
        findIds(v);
        return v;
    }

    private void findIds(View v){
        backButton = v.findViewById(R.id.forgotBack);
        resetButton = v.findViewById(R.id.forgotButton);
        resetEmail = v.findViewById(R.id.forgotEmail);
        progressDialog = getDialogProgressBar().create();
        setListeners();
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(ForgotPasswordFragment.this).commit();
                getActivity().findViewById(R.id.logInFrameLayout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.logInLayout).setVisibility(View.VISIBLE);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String givenName = resetEmail.getText().toString();
                if(!givenName.isEmpty()){
                    resetProcess(givenName);
                } else {
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
        });
    }

    private void resetProcess(final String givenName){
        progressDialog.show();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        if(givenName.contains("@")){
            mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userCheck = false;
                    for(DataSnapshot d : snapshot.getChildren()){
                        if(String.valueOf(d.child("email").getValue()).equals(givenName)){
                            userCheck = true;
                            resetPassword(givenName);
                            break;
                        }
                    }
                    if(!userCheck){
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
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(error.getMessage());
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                        }
                    });
                    ad.create().show();
                }
            });
        } else {
            mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userCheck = false;
                    for (DataSnapshot d : snapshot.getChildren()) {
                        if (String.valueOf(d.child("username").getValue()).equals(givenName)) {
                            userCheck = true;
                            resetPassword(String.valueOf(d.child("email").getValue()));
                            break;
                        }
                    }
                    if (!userCheck) {
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
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(error.getMessage());
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                        }
                    });
                    ad.create().show();
                }
            });
        }
    }

    private void resetPassword(String address){
        FirebaseAuth.getInstance().sendPasswordResetEmail(address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.password_reset));
                    ad.setMessage(getResources().getString(R.string.reset_sent));
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                            assert getActivity() != null;
                            getActivity().findViewById(R.id.logInFrameLayout).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.logInLayout).setVisibility(View.VISIBLE);
                        }
                    });
                    ad.setCancelable(false);
                    ad.create().show();

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

    private AlertDialog.Builder getDialogProgressBar() {
        if (pd != null) {
            pd.setTitle(getResources().getString(R.string.loading));
            final ProgressBar progressBar = new ProgressBar(getContext());
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