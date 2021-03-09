package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpla.turnedTables.Utils.SetTheme;

import java.util.ArrayList;

public class MainSearchActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private FrameLayout frameLayout;
    private Button backButton,searchButton;
    private Spinner city,uni,field;
    private ArrayList<String> cityList,uniList,uniLimitedList,fieldList,fieldLimitedList;
    private ArrayAdapter<String> uniArrayAdapter,fieldArrayAdapter;
    private DatabaseReference mRef;
    private String firstItemUni,firstItemField,selectedCity,selectedUni,selectedField;
    private EditText profName;
    private AlertDialog.Builder ad;
    private AdView banner;
    private ImageView stats;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(MainSearchActivity.this);
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(MainSearchActivity.this);
        } else {
            ad = new AlertDialog.Builder(MainSearchActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        findIds();
    }

    private void findIds(){
        mainLayout = findViewById(R.id.mainLayout);
        frameLayout = findViewById(R.id.mainFrameLayout);
        backButton = findViewById(R.id.mainSearchBack);
        city = findViewById(R.id.mainCitySpinner);
        uni = findViewById(R.id.mainUniSpinner);
        searchButton = findViewById(R.id.mainButton);
        profName = findViewById(R.id.editProf);
        field = findViewById(R.id.mainFieldSpinner);
        banner = findViewById(R.id.mainBanner);
        info = findViewById(R.id.mainInfo);
        stats = findViewById(R.id.mainStats);
        cityList = new ArrayList<>();
        uniList = new ArrayList<>();
        uniLimitedList = new ArrayList<>();
        fieldList = new ArrayList<>();
        fieldLimitedList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference();
        firstItemUni = getResources().getString(R.string.uni_name1);
        firstItemField = getResources().getString(R.string.field_name1);
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
        mRef.child("Cities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    cityList.add(nameFix(String.valueOf(d.getKey())));
                }
                ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<>(MainSearchActivity.this, R.layout.simple_spinner_item, cityList);
                cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                city.setAdapter(cityArrayAdapter);
                city.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Universities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    uniList.add(String.valueOf(d.getKey()));
                }
                uniArrayAdapter = new ArrayAdapter<>(MainSearchActivity.this, R.layout.simple_spinner_item, uniList);
                uniArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                uni.setAdapter(uniArrayAdapter);
                uni.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    fieldList.add(String.valueOf(d.getKey()));
                }
                fieldArrayAdapter = new ArrayAdapter<>(MainSearchActivity.this, R.layout.simple_spinner_item, fieldList);
                fieldArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                field.setAdapter(fieldArrayAdapter);
                field.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,new StatsFragment()).commit();
            }
        });
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,new StatsFragment()).commit();
            }
        });
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
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prof_name = profName.getText().toString();
                if(!prof_name.isEmpty() || !selectedCity.isEmpty() || !selectedUni.isEmpty() || !selectedField.isEmpty()){
                    Intent intent = new Intent(MainSearchActivity.this,SearchActivity.class);
                    intent.putExtra("cityName",selectedCity);
                    intent.putExtra("uniName",selectedUni);
                    intent.putExtra("fieldName",selectedField);
                    intent.putExtra("profName",prof_name);
                    startActivity(intent);
                } else {
                    ad.setTitle(getResources().getString(R.string.error));
                    ad.setMessage(getResources().getString(R.string.search_fill_one));
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
                Toast.makeText(MainSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                Toast.makeText(MainSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
}