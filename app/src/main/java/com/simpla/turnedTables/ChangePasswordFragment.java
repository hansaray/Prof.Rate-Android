package com.simpla.turnedTables;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private EditText current,newPassword,newAgain;
    private Button doneButton,backButton;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        assert getActivity() != null;
        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("PREFS_NAME",0);
        int darkModeControl = settings.getInt("darkMode",0);
        assert getContext() != null;
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(getContext());
            pd = new AlertDialog.Builder(getContext());
        } else {
            ad = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomDark);
        }
        findIds(v);
        return v;
    }

    private void findIds(View v){
        current = v.findViewById(R.id.changePassCurrent);
        newPassword = v.findViewById(R.id.changePassNew);
        newAgain = v.findViewById(R.id.changePassNewAgain);
        doneButton = v.findViewById(R.id.changePassButton);
        backButton = v.findViewById(R.id.changePassBack);
        progressDialog = getDialogProgressBar().create();
        setListeners();
    }

    private void setListeners(){
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String currentPassword = current.getText().toString();
                final String newPasswordString = newPassword.getText().toString();
                final String newPasswordAgain = newAgain.getText().toString();
                if(!currentPassword.isEmpty()){
                    final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(mUser != null){
                        assert mUser.getEmail() != null;
                        AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(),currentPassword);
                        mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    if(newPasswordString.equals(newPasswordAgain)){
                                        if(newPasswordString.length()>5){
                                            mUser.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @SuppressLint("UseCompatLoadingForDrawables")
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.successful));
                                                        ad.setMessage(getResources().getString(R.string.password_changed));
                                                        ad.show();
                                                        onBackPressed();
                                                    }else{
                                                        progressDialog.cancel();
                                                        ad.setTitle(getResources().getString(R.string.error));
                                                        ad.setMessage(getResources().getString(R.string.errorOccurred));
                                                        ad.show();
                                                    }
                                                }
                                            });
                                        }else{
                                            progressDialog.cancel();
                                            ad.setTitle(getResources().getString(R.string.error));
                                            ad.setMessage(getResources().getString(R.string.password_short));
                                            ad.show();
                                        }
                                    }else{
                                        progressDialog.cancel();
                                        ad.setTitle(getResources().getString(R.string.error));
                                        ad.setMessage(getResources().getString(R.string.password_not_match));
                                        ad.show();
                                    }
                                }else{
                                    progressDialog.cancel();
                                    ad.setTitle(getResources().getString(R.string.error));
                                    ad.setMessage(getResources().getString(R.string.password_wrong));
                                    ad.show();
                                }
                            }
                        });
                    }
                }else{
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(getResources().getString(R.string.empty_form));
                    ad.show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void onBackPressed(){
        assert getActivity() != null;
        getActivity().getSupportFragmentManager().beginTransaction().remove(ChangePasswordFragment.this).commit();
        getActivity().findViewById(R.id.editFrameLayout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.editLayout).setVisibility(View.VISIBLE);
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