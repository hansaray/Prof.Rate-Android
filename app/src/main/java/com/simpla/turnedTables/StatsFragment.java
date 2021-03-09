package com.simpla.turnedTables;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.simpla.turnedTables.Adapters.StatsAdapter;
import com.simpla.turnedTables.Objects.StatsObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button back;
    private RadioGroup group;
    private RadioButton city,uni,field;
    private TextView info,count,add;
    private AdView banner;
    private DatabaseReference mRef;
    private ArrayList<StatsObject> list;
    private StatsAdapter adapter;
    private int filterType = 0;
    private View v;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stats,container,false);
        findIds(v);
        return v;
    }

    private void findIds(View v) {
        recyclerView = v.findViewById(R.id.statsRw);
        back = v.findViewById(R.id.statsBack);
        group = v.findViewById(R.id.statsGroup);
        info = v.findViewById(R.id.statsInfo);
        banner = v.findViewById(R.id.statsBanner);
        count = v.findViewById(R.id.statsCount);
        city = v.findViewById(R.id.statsB1);
        uni = v.findViewById(R.id.statsB2);
        field = v.findViewById(R.id.statsB3);
        progressBar = v.findViewById(R.id.statsProgress);
        add = v.findViewById(R.id.statsAdd);
        mRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        adapter = new StatsAdapter(list);
        uni.setChecked(true);
        setAds();
        setListeners();
        setupRw();
        loadData();
    }

    private void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setListeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(StatsFragment.this).commit();
                getActivity().findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.mainFrameLayout).setVisibility(View.GONE);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),OfferProfActivity.class));
            }
        });
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedID = radioGroup.getCheckedRadioButtonId();
                if (city.getId() == selectedID) {
                    filterType = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    loadData();
                } else if (uni.getId() == selectedID) {
                    filterType = 2;
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    loadData();
                } else if (field.getId() == selectedID) {
                    filterType = 3;
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    loadData();
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AddProfActivity.class));
            }
        });
    }

    private void setupRw(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.setAdapter(adapter);
    }

    private void loadData(){
        list.clear();
        mRef.child("Professors").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long num = snapshot.getChildrenCount();
                num = num - 1;
                count.setText(v.getContext().getString(R.string.stats_count_1)+" "+num+" "+v.getContext().getString(R.string.stats_count_2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(filterType==1){
            addData(mRef.child("Cities"));
        }else if(filterType==3){
            addData(mRef.child("Faculties"));
        }else {
            addData(mRef.child("Universities"));
        }
    }

    private void addData(Query ref){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()) {
                    long num = snapshot.child(String.valueOf(d.getKey())).child("All professors").getChildrenCount();
                    if(filterType != 1) {
                        num = num - 1;
                    }
                    StatsObject object = new StatsObject(String.valueOf(d.getKey()),num);
                    list.add(object);
                    adapter.notifyDataSetChanged();
                    sort();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sort(){
        Collections.sort(list, new Comparator<StatsObject>() {
            @Override
            public int compare(StatsObject o1, StatsObject o2) {
                return Double.compare(o2.getNum(), o1.getNum());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
