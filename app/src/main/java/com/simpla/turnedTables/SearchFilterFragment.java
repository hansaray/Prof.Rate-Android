package com.simpla.turnedTables;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchFilterFragment extends Fragment {
    private Button backButton;
    private LinearLayout checkBoxLayout1,checkBoxLayout2;
    private int darkModeControl;
    private ImageView drop1,drop2;
    private Spinner ratingSpinner;
    private int filterType = 0;
    private TextView title1,title2;
    private DatabaseReference mRef;
    private String cityName,uniName,fieldName,selectedRating;
    private ConstraintLayout filterLayout1,filterLayout2;
    private ArrayList<String> sCityList,sUniList,sFieldList,spinnerList;
    private AdView banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_filter, container, false);
        assert getActivity() != null;
        SharedPreferences settings = getActivity().getSharedPreferences("PREFS_NAME",0);
        darkModeControl = settings.getInt("darkMode",0);
        findIds(v);
        return v;
    }

    private void findIds(View v) {
        backButton = v.findViewById(R.id.filterBack);
        checkBoxLayout1 = v.findViewById(R.id.filterCheckBoxLayout1);
        checkBoxLayout2 = v.findViewById(R.id.filterCheckBoxLayout2);
        drop1 = v.findViewById(R.id.filterDrop1);
        drop2 = v.findViewById(R.id.filterDrop2);
        ratingSpinner = v.findViewById(R.id.filterSpinner);
        filterLayout1 = v.findViewById(R.id.filterLayout1);
        filterLayout2 = v.findViewById(R.id.filterLayout2);
        title1 = v.findViewById(R.id.filterTitle2);
        title2 = v.findViewById(R.id.filterTitle3);
        banner = v.findViewById(R.id.filterBanner);
        sCityList = new ArrayList<>();
        sUniList = new ArrayList<>();
        sFieldList = new ArrayList<>();
        spinnerList = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference();
        setAds();
        setSpinner();
        setListeners();
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
        filterLayout1.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    checkBoxLayout1.setVisibility(View.VISIBLE);
                    drop1.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    checkBoxLayout1.setVisibility(View.GONE);
                    drop1.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }
                i++;
            }
        });
        filterLayout2.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    checkBoxLayout2.setVisibility(View.VISIBLE);
                    drop2.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                }else{
                    checkBoxLayout2.setVisibility(View.GONE);
                    drop2.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                }
                i++;
            }
        });
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    selectedRating="";
                }else{
                    selectedRating=spinnerList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        typeControl();
    }

    private void typeControl() {
        if(filterType==1){
            type1();
        }else if(filterType==2){
            type2();
        }else if(filterType==31){
            type31();
        }else if(filterType==32){
            type32();
        }
    }

    private void setSpinner(){
        spinnerList.add("");
        spinnerList.add(getResources().getString(R.string.sort_high));
        spinnerList.add(getResources().getString(R.string.sort_low));
        assert getContext() != null;
        ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, spinnerList);
        filterSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(filterSpinnerAdapter);
        ratingSpinner.setSelection(0);
    }

    private void type1(){ //According to City
        filterLayout1.setVisibility(View.VISIBLE);
        filterLayout2.setVisibility(View.VISIBLE);
        title1.setText(getResources().getString(R.string.filter_uni));
        title2.setText(getResources().getString(R.string.filter_field));
        sUniList.clear();
        sFieldList.clear();
        //First CheckBox layout (Uni names)
        mRef.child("Cities").child(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long control = snapshot.getChildrenCount();
                if(control>1){
                    for(DataSnapshot d : snapshot.getChildren()){
                        assert d.getKey() != null;
                        if(!d.getKey().equals("All professors")){
                            final String name = String.valueOf(d.getKey());
                            CheckBox checkBox = new CheckBox(getContext());
                            if (darkModeControl == 1){
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                                }
                                checkBox.setTextColor(getResources().getColor(R.color.black));
                            } else {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                                }
                                checkBox.setTextColor(getResources().getColor(R.color.white));
                            }
                            checkBox.setText(name);
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if(b){
                                        sUniList.add(name);
                                    } else {
                                        sUniList.remove(name);
                                    }
                                }
                            });
                            checkBoxLayout1.addView(checkBox);
                        }
                    }
                }else{
                    filterLayout1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        //Second CheckBox layout (Faculty names)
        mRef.child("Faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> fList = new ArrayList<>();
                for(DataSnapshot d : snapshot.getChildren()){
                    for(DataSnapshot d1 : d.getChildren()){
                        assert d1.getKey() != null;
                        if(d1.getKey().equals(cityName)){
                            fList.add(d.getKey());
                        }
                    }
                }
                Set<String> set = new HashSet<>(fList);
                fList.clear();
                fList.addAll(set);
                for(final String s : fList) {
                    CheckBox checkBox = new CheckBox(getContext());
                    if (darkModeControl == 1){
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                        }
                        checkBox.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                        }
                        checkBox.setTextColor(getResources().getColor(R.color.white));
                    }
                    checkBox.setText(s);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                sFieldList.add(s);
                            } else {
                                sFieldList.remove(s);
                            }
                        }
                    });
                    checkBoxLayout2.addView(checkBox);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void type2(){ //According to University
        filterLayout1.setVisibility(View.VISIBLE);
        title1.setText(getResources().getString(R.string.filter_field));
        //First CheckBox layout (Faculty names)
        mRef.child("Universities").child(uniName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(!d.getKey().equals("All professors")){
                        final String name = String.valueOf(d.getKey());
                        CheckBox checkBox = new CheckBox(getContext());
                        if (darkModeControl == 1){
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                            }
                            checkBox.setTextColor(getResources().getColor(R.color.black));
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                            }
                            checkBox.setTextColor(getResources().getColor(R.color.white));
                        }
                        checkBox.setText(name);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b){
                                    sFieldList.add(name);
                                } else {
                                    sFieldList.remove(name);
                                }
                            }
                        });
                        checkBoxLayout1.addView(checkBox);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void type31(){ //City based Faculty
        filterLayout1.setVisibility(View.VISIBLE);
        title1.setText(getResources().getString(R.string.filter_uni));
        //First CheckBox layout (Uni names)
        final ArrayList<String> uList = new ArrayList<>();
        mRef.child("Cities").child(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long limit = snapshot.getChildrenCount();
                int i = 0;
                for(final DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(!d.getKey().equals("All professors")){
                        final int finalI = i;
                        mRef.child("Universities").child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot d1 : snapshot.getChildren()){
                                    assert d1.getKey() != null;
                                    if(!d1.getKey().equals("All professors")){
                                        if(d1.getKey().equals(fieldName)){
                                            uList.add(d.getKey());
                                        }
                                    }
                                }
                                if(finalI ==limit-1){
                                    for(final String s : uList) {
                                        CheckBox checkBox = new CheckBox(getContext());
                                        if (darkModeControl == 1){
                                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                                            }
                                            checkBox.setTextColor(getResources().getColor(R.color.black));
                                        } else {
                                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                                            }
                                            checkBox.setTextColor(getResources().getColor(R.color.white));
                                        }
                                        checkBox.setText(s);
                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if(b){
                                                    sUniList.add(s);
                                                } else {
                                                    sUniList.remove(s);
                                                }
                                            }
                                        });
                                        checkBoxLayout1.addView(checkBox);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                assert getContext() != null;
                                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void type32(){ //Just Faculty based
        filterLayout1.setVisibility(View.VISIBLE);
        filterLayout2.setVisibility(View.VISIBLE);
        title1.setText(getResources().getString(R.string.filter_city));
        title2.setText(getResources().getString(R.string.filter_uni));
        //First CheckBox layout (City names)
        mRef.child("Faculties").child(fieldName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(!d.getKey().equals("All professors")){
                        final String name = String.valueOf(d.getKey());
                        CheckBox checkBox = new CheckBox(getContext());
                        if (darkModeControl == 1){
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                            }
                            checkBox.setTextColor(getResources().getColor(R.color.black));
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                            }
                            checkBox.setTextColor(getResources().getColor(R.color.white));
                        }
                        checkBox.setText(name);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b){
                                    sCityList.add(name);
                                } else {
                                    sCityList.remove(name);
                                }
                            }
                        });
                        checkBoxLayout1.addView(checkBox);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        //Second CheckBox layout (Uni names)
        final ArrayList<String> uList = new ArrayList<>();
        mRef.child("Universities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    for(DataSnapshot d1 : d.getChildren()){
                        assert d1.getKey() != null;
                        if(d1.getKey().equals(fieldName)){
                            uList.add(d.getKey());
                        }
                    }
                }
                for(final String s : uList) {
                    CheckBox checkBox = new CheckBox(getContext());
                    if (darkModeControl == 1){
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mDarkBlue)));
                        }
                        checkBox.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.mBlue)));
                        }
                        checkBox.setTextColor(getResources().getColor(R.color.white));
                    }
                    checkBox.setText(s);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                sUniList.add(s);
                            } else {
                                sUniList.remove(s);
                            }
                        }
                    });
                    checkBoxLayout2.addView(checkBox);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assert getContext() != null;
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onBackPressed() {
        if (!sCityList.isEmpty() || !sUniList.isEmpty() || !sFieldList.isEmpty() || !selectedRating.isEmpty()  ) {
            EventBus.getDefault().post(new EventbusDataEvents.filterTypeBack(true,sCityList,sUniList,sFieldList,selectedRating));
        }
        assert getActivity() != null;
        getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFilterFragment.this).commit();
        getActivity().findViewById(R.id.searchFrameLayout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.searchLayout).setVisibility(View.VISIBLE);
    }

    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.filterType data){
        filterType = data.getNumber();
        cityName = data.getCityName();
        uniName = data.getUniName();
        fieldName = data.getFieldName();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}