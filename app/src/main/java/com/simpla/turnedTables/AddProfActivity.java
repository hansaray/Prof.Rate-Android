package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpla.turnedTables.Objects.SearchObject;
import com.simpla.turnedTables.Utils.SetTheme;

import java.util.ArrayList;
import java.util.HashMap;

public class AddProfActivity extends AppCompatActivity {

    private Button backButton,offerButton;
    private Spinner city,uni,field,title,gender;
    private EditText profName;
    private AlertDialog.Builder ad,pd;
    private AlertDialog progressDialog;
    private ArrayList<String> cityList,uniList,uniLimitedList,fieldList,fieldLimitedList,titleList,genderList;
    private ArrayAdapter<String> uniArrayAdapter,fieldArrayAdapter;
    private DatabaseReference mRef;
    private String firstItemUni,firstItemField,selectedCity,selectedUni,selectedField,selectedTitle = "";
    private AdView banner;
    private int selectedGender;
    private String prof_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeCheck = SetTheme.theme(AddProfActivity.this);
        if(darkModeCheck == 1){
            ad = new AlertDialog.Builder(AddProfActivity.this);
            pd = new AlertDialog.Builder(AddProfActivity.this);
        }else{
            ad = new AlertDialog.Builder(AddProfActivity.this,R.style.AlertDialogCustomDark);
            pd = new AlertDialog.Builder(AddProfActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prof);
        findIds();
    }

    private void findIds(){
        city = findViewById(R.id.addCitySpinner);
        uni = findViewById(R.id.addUniSpinner);
        offerButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.addBack);
        profName = findViewById(R.id.addProfName);
        field = findViewById(R.id.addFieldSpinner);
        banner = findViewById(R.id.addBanner);
        title = findViewById(R.id.addTitleSpinner);
        gender = findViewById(R.id.addGenderSpinner);
        cityList = new ArrayList<>();
        uniList = new ArrayList<>();
        uniLimitedList = new ArrayList<>();
        fieldList = new ArrayList<>();
        fieldLimitedList = new ArrayList<>();
        titleList = new ArrayList<>();
        genderList = new ArrayList<>();
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
        genderList.add("");
        genderList.add(getResources().getString(R.string.male));
        genderList.add(getResources().getString(R.string.female));
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
        ArrayAdapter<String> titleArrayAdapter = new ArrayAdapter<>(AddProfActivity.this, R.layout.simple_spinner_item, titleList);
        titleArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        title.setAdapter(titleArrayAdapter);
        title.setSelection(0);
        ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<>(AddProfActivity.this, R.layout.simple_spinner_item, genderList);
        genderArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderArrayAdapter);
        gender.setSelection(0);
        mRef.child("Cities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    cityList.add(nameFix(String.valueOf(d.getKey())));
                }
                ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<>(AddProfActivity.this, R.layout.simple_spinner_item, cityList);
                cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                city.setAdapter(cityArrayAdapter);
                city.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Universities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    uniList.add(String.valueOf(d.getKey()));
                }
                uniArrayAdapter = new ArrayAdapter<>(AddProfActivity.this, R.layout.simple_spinner_item, uniList);
                uniArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                uni.setAdapter(uniArrayAdapter);
                uni.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        mRef.child("Faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    fieldList.add(String.valueOf(d.getKey()));
                }
                fieldArrayAdapter = new ArrayAdapter<>(AddProfActivity.this, R.layout.simple_spinner_item, fieldList);
                fieldArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                field.setAdapter(fieldArrayAdapter);
                field.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = i;
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
                progressDialog.show();
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    prof_name = "";
                    boolean check = false;
                    if(!profName.getText().toString().isEmpty()) {
                        prof_name = fixName(profName.getText().toString());
                        if(prof_name.equals("wrong")) {
                            prof_name = "";
                            check = true;
                            progressDialog.cancel();
                            ad.setTitle(getResources().getString(R.string.error));
                            ad.setMessage(getResources().getString(R.string.add_prof_space));
                            ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ad.create().cancel();
                                }
                            });
                            ad.create().show();
                        } else {
                            profName.setText("");
                            profName.setHint(prof_name);
                        }
                    }
                    if(!prof_name.isEmpty() && !selectedCity.isEmpty() && !selectedUni.isEmpty()
                            && !selectedField.isEmpty() && selectedGender != 0 && !selectedTitle.isEmpty() && !check){
                        mRef.child("Universities").child(selectedUni).child(selectedField).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                mRef.child("Professors").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        boolean tCheck = false;
                                        for(DataSnapshot d : snapshot.getChildren()) {
                                            assert d.getKey() != null;
                                            if(snapshot2.child(d.getKey()).exists()){
                                                if(String.valueOf(snapshot2.child(d.getKey()).child("prof_name").getValue()).toLowerCase().equals(prof_name.toLowerCase())
                                                        && String.valueOf(snapshot2.child(d.getKey()).child("photo").getValue()).equals(String.valueOf(selectedGender))) {
                                                    tCheck = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if(!tCheck){
                                            final String key = mRef.child("Professors").push().getKey();
                                                   HashMap<String,Object> map = new HashMap<>();
                                                   map.put("avg_rating",0.0);
                                                   map.put("city",selectedCity);
                                                   map.put("field_name",selectedField);
                                                   map.put("prof_name",prof_name);
                                                   map.put("uni_name",selectedUni);
                                                   map.put("photo",selectedGender);
                                                   map.put("title",selectedTitle);
                                                   assert key != null;
                                                   mRef.child("Professors").child(key).setValue(map);
                                                   mRef.child("Universities").child(selectedUni).child("All professors").child(key).setValue(0.0);
                                                   mRef.child("Universities").child(selectedUni).child(selectedField).child(key).setValue(0.0);
                                                   mRef.child("Faculties").child(selectedField).child("All professors").child(key).setValue(0.0);
                                                   mRef.child("Faculties").child(selectedField).child(selectedCity).child(key).setValue(0.0);
                                                   mRef.child("Cities").child(selectedCity).child("All professors").child(key).setValue(0.0);
                                                   mRef.child("addedProf").child(key).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){
                                                               String cityUniName = selectedCity +","
                                                                       + selectedUni;
                                                               SearchObject object = new SearchObject(prof_name
                                                                       ,selectedField,cityUniName,
                                                                       String.valueOf(selectedGender)
                                                                       ,0.0,key);
                                                               object.setTitle(selectedTitle);
                                                               Toast.makeText(AddProfActivity.this,getResources().getString(R.string.successful),Toast.LENGTH_LONG).show();
                                                               Intent intent = new Intent(AddProfActivity.this,RateActivity.class);
                                                               intent.putExtra("rateProfData",object);
                                                               startActivity(intent);
                                                               finish();
                                                           }else {
                                                               Toast.makeText(AddProfActivity.this,getResources().getString(R.string.errorOccurred),Toast.LENGTH_LONG).show();
                                                           }
                                                       }
                                                   });
                                        }else{
                                            progressDialog.cancel();
                                            ad.setTitle(getResources().getString(R.string.error));
                                            ad.setMessage(getResources().getString(R.string.add_info3));
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
                                        Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        if(!check) {
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
                } else {
                    progressDialog.cancel();
                    ad.setTitle(getResources().getString(R.string.information));
                    ad.setMessage(getResources().getString(R.string.add_info2));
                    ad.setPositiveButton(getResources().getString(R.string.logIn_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(AddProfActivity.this,LogInActivity.class));
                        }
                    });
                    ad.setNegativeButton(getResources().getString(R.string.signUp), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(AddProfActivity.this,SignUpActivity.class));
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
                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddProfActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private String fixName(String givenString) {
        String s = givenString.toLowerCase();
        if(s.startsWith(" ")) {
            return "wrong";
        } else {
            int spaceCount = 0;
            char cs = 0;
            for (char c : s.toCharArray()) {
                if (c == ' ') {
                    spaceCount++;
                }
                if(cs != 0){
                    if(c == cs){
                        return "wrong";
                    }
                }
                cs = c;
            }
            if(spaceCount >= 8) {
                return "wrong";
            }
        }
        String[] arr = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String value : arr) {
            if(String.valueOf(value.charAt(0)).equals("i") || String.valueOf(value.charAt(0)).equals("İ")) {
                sb.append("İ").append(value.substring(1)).append(" ");
            } else {
                sb.append(Character.toUpperCase(value.charAt(0)))
                        .append(value.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
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