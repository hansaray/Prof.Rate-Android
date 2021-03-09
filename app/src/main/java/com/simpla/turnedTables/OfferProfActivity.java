package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferProfActivity extends AppCompatActivity {

    private Button backButton,offerButton;
    private Spinner city,uni,field,title;
    private EditText profName;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;
    private ArrayList<String> cityList,uniList,uniLimitedList,fieldList,fieldLimitedList,titleList;
    private ArrayAdapter<String> uniArrayAdapter,fieldArrayAdapter;
    private DatabaseReference mRef;
    private String firstItemUni,firstItemField,selectedCity,selectedUni,selectedField,selectedTitle = "";
    private AdView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeCheck = SetTheme.theme(OfferProfActivity.this);
        if(darkModeCheck == 1){
            ad = new AlertDialog.Builder(OfferProfActivity.this);
            pd = new AlertDialog.Builder(OfferProfActivity.this);
        }else{
            ad = new AlertDialog.Builder(OfferProfActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(OfferProfActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_prof);
        findIds();
    }

    private void findIds(){
        city = findViewById(R.id.offerCitySpinner);
        uni = findViewById(R.id.offerUniSpinner);
        offerButton = findViewById(R.id.offerButton);
        backButton = findViewById(R.id.offerBack);
        profName = findViewById(R.id.offerProfName);
        field = findViewById(R.id.offerFieldSpinner);
        banner = findViewById(R.id.offerBanner);
        title = findViewById(R.id.offerTitleSpinner);
        cityList = new ArrayList<>();
        uniList = new ArrayList<>();
        uniLimitedList = new ArrayList<>();
        fieldList = new ArrayList<>();
        fieldLimitedList = new ArrayList<>();
        titleList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference();
        firstItemUni = getResources().getString(R.string.uni_name1);
        firstItemField = getResources().getString(R.string.field_name1);
        progressDialog = getDialogProgressBar().create();
        setAds();
        setSpinners();
        setListeners();
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setSpinners(){
        String firstItemCity = getResources().getString(R.string.city_name_exp);
        cityList.add(firstItemCity);
        uniList.add(firstItemUni);
        fieldList.add(firstItemField);
        titleList.add("");
        titleList.add(getResources().getString(R.string.p_title_1));
        titleList.add(getResources().getString(R.string.p_title_2));
        titleList.add(getResources().getString(R.string.p_title_3));
        titleList.add(getResources().getString(R.string.p_title_4));
        titleList.add(getResources().getString(R.string.p_title_5));
        titleList.add(getResources().getString(R.string.p_title_6));
        titleList.add(getResources().getString(R.string.p_title_7));
        titleList.add(getResources().getString(R.string.p_title_8));
        titleList.add(getResources().getString(R.string.p_title_9));
        titleList.add(getResources().getString(R.string.p_title_10));
        titleList.add(getResources().getString(R.string.p_title_11));
        titleList.add(getResources().getString(R.string.p_title_12));
        titleList.add(getResources().getString(R.string.p_title_13));
        ArrayAdapter<String> titleArrayAdapter = new ArrayAdapter<>(OfferProfActivity.this, R.layout.simple_spinner_item, titleList);
        titleArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        title.setAdapter(titleArrayAdapter);
        title.setSelection(0);
        mRef.child("Cities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    cityList.add(nameFix(String.valueOf(d.getKey())));
                }
                ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<>(OfferProfActivity.this, R.layout.simple_spinner_item, cityList);
                cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                city.setAdapter(cityArrayAdapter);
                city.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OfferProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Universities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    uniList.add(String.valueOf(d.getKey()));
                }
                uniArrayAdapter = new ArrayAdapter<>(OfferProfActivity.this, R.layout.simple_spinner_item, uniList);
                uniArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                uni.setAdapter(uniArrayAdapter);
                uni.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OfferProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    fieldList.add(String.valueOf(d.getKey()));
                }
                fieldArrayAdapter = new ArrayAdapter<>(OfferProfActivity.this, R.layout.simple_spinner_item, fieldList);
                fieldArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                field.setAdapter(fieldArrayAdapter);
                field.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OfferProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners(){
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                uni.setSelection(0);
                field.setSelection(0);
                if(position==0){
                    selectedCity = "";
                    if(!uniLimitedList.isEmpty()){
                        uniList.clear();
                        uniList.addAll(uniLimitedList);
                        uniArrayAdapter.notifyDataSetChanged();
                    }
                }else{
                    setLimitedUniList(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        uni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                field.setSelection(0);
                if(position==0){
                    selectedUni = "";
                    if(!fieldLimitedList.isEmpty()){
                        fieldList.clear();
                        fieldList.addAll(fieldLimitedList);
                        fieldArrayAdapter.notifyDataSetChanged();
                    }
                }else{
                    setLimitedFieldList(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if(position==0){
                    selectedField = "";
                }else{
                    selectedField = fieldList.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    selectedTitle = "";
                } else if (i ==1) {
                    selectedTitle = getResources().getString(R.string.p_title_1);
                } else if (i ==2) {
                    selectedTitle = getResources().getString(R.string.p_title_2);
                } else if (i ==3) {
                    selectedTitle = getResources().getString(R.string.p_title_3);
                } else if (i ==4) {
                    selectedTitle = getResources().getString(R.string.p_title_4);
                } else if (i ==5) {
                    selectedTitle = getResources().getString(R.string.p_title_5);
                } else if (i ==6) {
                    selectedTitle = getResources().getString(R.string.p_title_6);
                } else if (i ==7) {
                    selectedTitle = getResources().getString(R.string.p_title_7);
                } else if (i ==8) {
                    selectedTitle = getResources().getString(R.string.p_title_8);
                } else if (i ==9) {
                    selectedTitle = getResources().getString(R.string.p_title_9);
                } else if (i ==10) {
                    selectedTitle = getResources().getString(R.string.p_title_10);
                } else if (i ==11) {
                    selectedTitle = getResources().getString(R.string.p_title_11);
                } else if (i ==12) {
                    selectedTitle = getResources().getString(R.string.p_title_12);
                } else if (i ==13) {
                    selectedTitle = getResources().getString(R.string.p_title_13);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prof_name = profName.getText().toString();
                if(!prof_name.isEmpty() && !selectedUni.isEmpty() && !selectedField.isEmpty()){
                    progressDialog.show();
                    Map<String, String> time = ServerValue.TIMESTAMP;
                    String key = mRef.child("profOffer").push().getKey();
                    HashMap<String,Object> offer = new HashMap<>();
                    offer.put("prof_name",prof_name);
                    offer.put("uni_name",selectedUni);
                    offer.put("field_name",selectedField);
                    offer.put("time",time);
                    offer.put("title",selectedTitle);
                    if(!selectedCity.isEmpty()){
                        offer.put("city_name",selectedCity);
                    }
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        offer.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                    assert key != null;
                    mRef.child("profOffer").child(key).setValue(offer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.cancel();
                                Toast.makeText(OfferProfActivity.this,getResources().getString(R.string.successful),Toast.LENGTH_LONG).show();
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

    private void setLimitedUniList(int position){
        String cityName = nameFix(cityList.get(position));
        selectedCity = cityName;
        if(uniLimitedList.isEmpty()){
            uniLimitedList.addAll(uniList);
        }
        uniList.clear();
        uniList.add(firstItemUni);
        mRef.child("Cities").child(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(!d.getKey().equals("All professors")){
                        uniList.add(String.valueOf(d.getKey()));
                    }
                }
                uniArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OfferProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLimitedFieldList(int position){
        String uniName = uniList.get(position);
        selectedUni = uniName;
        if(fieldLimitedList.isEmpty()){
            fieldLimitedList.addAll(fieldList);
        }
        fieldList.clear();
        fieldList.add(firstItemField);
        mRef.child("Universities").child(uniName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(!d.getKey().equals("All professors")){
                        fieldList.add(String.valueOf(d.getKey()));
                    }
                }
                fieldArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OfferProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private String nameFix(String name){
        switch (name) {
            case "Canakkale":
                return "Çanakkale";
            case "Çanakkale":
                return "Canakkale";
            case "Cankırı":
                return "Çankırı";
            case "Çankırı":
                return "Cankırı";
            case "Corum":
                return "Çorum";
            case "Çorum":
                return "Corum";
            case "Istanbul":
                return "İstanbul";
            case "İstanbul":
                return "Istanbul";
            case "Izmir":
                return "İzmir";
            case "İzmir":
                return "Izmir";
            case "Sanlıurfa":
                return "Şanlıurfa";
            case "Şanlıurfa":
                return "Sanlıurfa";
            case "Sırnak":
                return "Şırnak";
            case "Şırnak":
                return "Sırnak";
            default:
                return name;
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