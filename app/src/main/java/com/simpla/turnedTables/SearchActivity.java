package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpla.turnedTables.Adapters.SearchAdapter;
import com.simpla.turnedTables.Objects.SearchObject;
import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.simpla.turnedTables.Utils.SetTheme;
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
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private ConstraintLayout layout,filterButton,emptyLayout,addLayout;
    private FrameLayout frameLayout;
    private String cityName,uniName,fieldName,profName;
    private ProgressBar progressBar;
    private TextView add,add2;
    private boolean checkCity = false;
    private boolean checkUni = false;
    private boolean checkField = false;
    private boolean checkProf = false;
    private DatabaseReference mRef;
    private SearchAdapter adapter;
    private ArrayList<SearchObject> list;
    private int numOfItems = 20;
    private boolean lastPage = false;
    private ArrayList<String> ids = new ArrayList<>();
    private long num = 0;
    private int moreCheck = 0;
    private ArrayList<String> fCityList,fUniList,fFieldList;
    private boolean fCheck = false;
    private String selectedRating = "";
    private ImageView filterClear;
    private AdView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetTheme.theme(SearchActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EventBus.getDefault().register(this);
        getIntentData();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        uniName = intent.getStringExtra("uniName");
        fieldName = intent.getStringExtra("fieldName");
        profName = intent.getStringExtra("profName");
        findIds();
    }

    private void findIds(){
        backButton = findViewById(R.id.searchBack);
        filterButton = findViewById(R.id.filterLayout);
        recyclerView = findViewById(R.id.searchRw);
        layout = findViewById(R.id.searchLayout);
        frameLayout = findViewById(R.id.searchFrameLayout);
        filterClear = findViewById(R.id.searchClearFilter);
        banner = findViewById(R.id.searchBanner);
        emptyLayout = findViewById(R.id.emptyLayout);
        add = findViewById(R.id.emptyAdd);
        addLayout = findViewById(R.id.searchAddLayout);
        add2 = findViewById(R.id.searchAddInfo);
        progressBar = findViewById(R.id.searchProgressBar);
        mRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        fCityList = new ArrayList<>();
        fUniList = new ArrayList<>();
        fFieldList = new ArrayList<>();
        adapter = new SearchAdapter(SearchActivity.this,list);
        setAds();
        setListeners();
        setupRw();
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void numCheck(){
        if(moreCheck==1){
            mRef.child("Cities").child(cityName).child("All professors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    num = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(moreCheck==2){
            mRef.child("Universities").child(uniName).child("All professors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    num = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(moreCheck==3){
            mRef.child("Universities").child(uniName).child(fieldName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    num = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(moreCheck==31){
            mRef.child("Faculties").child(fieldName).child(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    num = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(moreCheck==32){
            mRef.child("Faculties").child(fieldName).child("All professors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    num = snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new EventbusDataEvents.filterType(moreCheck,cityName,uniName,fieldName));
                getSupportFragmentManager().beginTransaction().replace(R.id.searchFrameLayout,new SearchFilterFragment()).commit();
                layout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        });
        filterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfItems = 20;
                fCheck = false;
                selectedRating = "";
                lastPage = false;
                list.clear();
                adapter.notifyDataSetChanged();
                filterClear.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                addLayout.setVisibility(View.GONE);
                loadData();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,AddProfActivity.class));
            }
        });
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,AddProfActivity.class));
            }
        });
    }

    private void setupRw(){
        final LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                assert recyclerView.getAdapter() != null;
                if(dy>0 && layoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount()-1){
                    if(!lastPage && moreCheck != 4 && !fCheck){
                        if(((list.size()+1)-numOfItems) >= 0) {
                            addLayout.setVisibility(View.VISIBLE);
                            if((num-numOfItems)>20){
                                numOfItems += 20;
                            }else if((num-numOfItems)==20){
                                numOfItems += (num-numOfItems);
                                lastPage = true;
                            }else{
                                numOfItems += (num-numOfItems);
                                lastPage = true;
                            }
                            loadMore();
                        }
                    }
                }
            }
        });
        loadData();
        if(moreCheck != 4){
            numCheck();
        }
    }

    private void loadData() {
        addLayout.setVisibility(View.GONE);
        if(cityName != null && !cityName.isEmpty()){
            checkCity = true;
        }
        if(uniName != null && !uniName.isEmpty()){
            checkUni = true;
        }
        if(fieldName != null && !fieldName.isEmpty()){
            checkField = true;
        }
        if(profName != null && !profName.isEmpty()){
            checkProf = true;
        }

        if(checkCity && !checkUni && !checkField && !checkProf ) { //According to city ("C---")
            moreCheck = 1;
            mRef.child("Cities").child(cityName).child("All professors").orderByValue().limitToLast(numOfItems)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    long eNum = snapshot.getChildrenCount();
                    if(eNum == 0){
                        emptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else if (eNum < numOfItems) {
                        addLayout.setVisibility(View.VISIBLE);
                    }
                    for(DataSnapshot d : snapshot.getChildren()){
                        assert d.getKey() != null;
                        addProfessors(d.getKey());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        } else if((checkCity && checkUni && !checkField && !checkProf) || (!checkCity && checkUni && !checkField && !checkProf)) {
            //According to University ("CU--","-U--")
            moreCheck = 2;
            mRef.child("Universities").child(uniName).child("All professors").orderByValue().limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    long eNum = snapshot.getChildrenCount();
                    if(eNum < 2){
                        emptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else if (eNum < numOfItems) {
                        addLayout.setVisibility(View.VISIBLE);
                    }
                    for(DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            assert d.getKey() != null;
                            addProfessors(d.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        } else if((checkCity && checkUni && checkField && !checkProf) || (!checkCity && checkUni && checkField && !checkProf)
                || (checkCity && !checkUni && checkField && !checkProf) || (!checkCity && !checkUni && checkField && !checkProf)) {
            //According to Faculty ("CUF-","-UF-","C-F-","--F-")
            if((checkCity && checkUni) || (!checkCity && checkUni)){ //University based Faculty ("CUF-","-UF-")
                moreCheck = 3;
                mRef.child("Universities").child(uniName).child(fieldName)
                        .orderByValue().limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        long eNum = snapshot.getChildrenCount();
                        if(eNum < 2){
                            emptyLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else if (eNum < numOfItems) {
                            addLayout.setVisibility(View.VISIBLE);
                        }
                        for(DataSnapshot d : snapshot.getChildren()){
                            if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                assert d.getKey() != null;
                                addProfessors(d.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else if(checkCity){ //City based Faculty ("C-F-")
                moreCheck = 31;
                mRef.child("Faculties").child(fieldName).child(cityName)
                        .orderByValue().limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        long eNum = snapshot.getChildrenCount();
                        if(eNum == 0){
                            emptyLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else if (eNum < numOfItems) {
                            addLayout.setVisibility(View.VISIBLE);
                        }
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            addProfessors(d.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else { //Just Faculty based ("--F-")
                moreCheck = 32;
                mRef.child("Faculties").child(fieldName).child("All professors").orderByValue()
                        .limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    long eNum = snapshot.getChildrenCount();
                    if(eNum < 2){
                        emptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else if (eNum < numOfItems) {
                        addLayout.setVisibility(View.VISIBLE);
                    }
                    for(DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            assert d.getKey() != null;
                            addProfessors(d.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
                });
            }
        } else { //According to Professor
            moreCheck=4;
            mRef.child("Professors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for(DataSnapshot d : snapshot.getChildren()){
                        if((String.valueOf(d.child("prof_name").getValue()).toLowerCase().contains(profName.toLowerCase())) ||
                                (String.valueOf(d.child("prof_name").getValue()).toLowerCase().equals(profName.toLowerCase()))){
                            if(!checkCity && !checkUni && !checkField){
                                String cityUniName = d.child("city").getValue() +","
                                        + d.child("uni_name").getValue();
                                SearchObject searchObject = new SearchObject(String.valueOf(d.child("prof_name").getValue())
                                        ,String.valueOf(d.child("field_name").getValue()),cityUniName,
                                        String.valueOf(d.child("photo").getValue())
                                        ,Double.parseDouble(String.valueOf(d.child("avg_rating").getValue())),d.getKey());
                                if(d.child("title").exists()){
                                    searchObject.setTitle(String.valueOf(d.child("title").getValue()));
                                }
                                list.add(searchObject);
                                adapter.notifyDataSetChanged();
                                sort(false);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else{
                                int control = 0;
                                if(checkCity){
                                    if(cityName.equals(String.valueOf(d.child("city").getValue()))){
                                        control = 1;
                                    }
                                }
                                if(checkUni){
                                    if(uniName.equals(String.valueOf(d.child("uni_name").getValue()))){
                                        control = 1;
                                    }else{
                                        control = 0;
                                    }
                                }
                                if(checkField){
                                    if(fieldName.equals(String.valueOf(d.child("field_name").getValue()))){
                                        control = 1;
                                    }else{
                                        control = 0;
                                    }
                                }
                                if(control==1){
                                    String cityUniName = d.child("city").getValue() +","
                                            + d.child("uni_name").getValue();
                                    SearchObject searchObject = new SearchObject(String.valueOf(d.child("prof_name").getValue())
                                            ,String.valueOf(d.child("field_name").getValue()),cityUniName,
                                            String.valueOf(d.child("photo").getValue())
                                            ,Double.parseDouble(String.valueOf(d.child("avg_rating").getValue())),d.getKey());
                                    if(d.child("title").exists()){
                                        searchObject.setTitle(String.valueOf(d.child("title").getValue()));
                                    }
                                    list.add(searchObject);
                                    adapter.notifyDataSetChanged();
                                    sort(false);
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    if(list.isEmpty()){
                        emptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else if (list.size() < numOfItems) {
                        addLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sort(boolean type){
        //true = lower first, false = higher first
        if(type){
            Collections.sort(list, new Comparator<SearchObject>() {
                @Override
                public int compare(SearchObject o1, SearchObject o2) {
                    return Double.compare(o2.getRatingNumber(), o1.getRatingNumber());
                }
            });
            Collections.reverse(list);
        }else{
            Collections.sort(list, new Comparator<SearchObject>() {
                @Override
                public int compare(SearchObject o1, SearchObject o2) {
                    return Double.compare(o2.getRatingNumber(), o1.getRatingNumber());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMore(){
        final int number = list.size();
        String startKey = list.get(number - 1).getItemID();
        if(moreCheck==1){ //According to City
            mRef.child("Cities").child(cityName).child("All professors").orderByValue()
                    .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    ids.clear();
                    for(final DataSnapshot d : snapshot.getChildren()){
                        ids.add(d.getKey());
                    }
                    if(!(!selectedRating.isEmpty() && (selectedRating.equals(getResources().getString(R.string.sort_low))))){
                        Collections.reverse(ids);
                    }
                    for(final String s : ids){
                        if(count >= number){
                            addProfessors(s);
                        }
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(moreCheck==2){ //According to University
            mRef.child("Universities").child(uniName).child("All professors").orderByValue()
                    .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    ids.clear();
                    for(final DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            ids.add(d.getKey());
                        }
                    }
                    if(!(!selectedRating.isEmpty() && (selectedRating.equals(getResources().getString(R.string.sort_low))))){
                        Collections.reverse(ids);
                    }
                    for(final String s : ids){
                        if(count >= number){
                            addProfessors(s);
                        }
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(moreCheck==3){
            //According to Faculty (UniBased)
            mRef.child("Universities").child(uniName).child(fieldName).orderByValue()
                    .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    ids.clear();
                    for(final DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            ids.add(d.getKey());
                        }
                    }
                    //filtered rating style check
                    if(!(!selectedRating.isEmpty() && (selectedRating.equals(getResources().getString(R.string.sort_low))))){
                        Collections.reverse(ids);
                    }
                    for(final String s : ids){
                        if(count >= number){
                            addProfessors(s);
                        }
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(moreCheck==31){ //According to Faculty (CityBased)
            mRef.child("Faculties").child(fieldName).child(cityName).orderByValue()
                    .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    ids.clear();
                    for(final DataSnapshot d : snapshot.getChildren()){
                        ids.add(d.getKey());
                    }
                    if(!(!selectedRating.isEmpty() && (selectedRating.equals(getResources().getString(R.string.sort_low))))){
                        Collections.reverse(ids);
                    }
                    for(final String s : ids){
                        if(count >= number){
                            addProfessors(s);
                        }
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(moreCheck==32){ //According to Faculty (JustFaculty)
            mRef.child("Faculties").child(fieldName).child("All professors").orderByValue()
                    .endAt(startKey).limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    ids.clear();
                    for(final DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            ids.add(d.getKey());
                        }
                    }
                    if(!(!selectedRating.isEmpty() && (selectedRating.equals(getResources().getString(R.string.sort_low))))){
                        Collections.reverse(ids);
                    }
                    for(final String s : ids){
                        if(count >= number){
                            addProfessors(s);
                        }
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void loadFilteredResult() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if(moreCheck==1){
            if(!fUniList.isEmpty() && !fFieldList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fUniList){
                    mRef.child("Universities").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                assert d.getKey() != null;
                                if(!d.getKey().equals("All professors")){
                                    for(String s1 : fFieldList){
                                        if(d.getKey().equals(s1)){
                                           for(DataSnapshot d1 : d.getChildren()){
                                               if(!String.valueOf(d1.getKey()).equals("aaaaa")) {
                                                   addProfessors(d1.getKey());
                                               }
                                           }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else if(!fUniList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fUniList){
                    mRef.child("Universities").child(s).child("All professors").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else if(!fFieldList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fFieldList){
                    mRef.child("Faculties").child(s).child(cityName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=10;
                    mRef.child("Cities").child(cityName).child("All professors").orderByValue().limitToFirst(numOfItems)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    list.clear();
                                    adapter.notifyDataSetChanged();
                                    long eNum = snapshot.getChildrenCount();
                                    if(eNum == 0){
                                        emptyLayout.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else if (eNum < numOfItems) {
                                        addLayout.setVisibility(View.VISIBLE);
                                    }
                                    for(final DataSnapshot d : snapshot.getChildren()){
                                        assert d.getKey() != null;
                                        addProfessors(d.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                }else if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_high))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=20;
                    list.clear();
                    adapter.notifyDataSetChanged();
                    loadData();
                }
            }
        }else if(moreCheck==2){
            if(!fFieldList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fFieldList){
                    mRef.child("Universities").child(uniName).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=20;
                    mRef.child("Universities").child(uniName).child("All professors").orderByValue().limitToFirst(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(final DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    assert d.getKey() != null;
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }else if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_high))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=20;
                    list.clear();
                    adapter.notifyDataSetChanged();
                    loadData();
                }
            }
        }else if(moreCheck==3){
            fCheck=false;
            lastPage = false;
            numOfItems=20;
            mRef.child("Universities").child(uniName).child(fieldName)
                    .orderByValue().limitToFirst(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    long eNum = snapshot.getChildrenCount();
                    if(eNum < 2){
                        emptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else if (eNum < numOfItems) {
                        addLayout.setVisibility(View.VISIBLE);
                    }
                    for(final DataSnapshot d : snapshot.getChildren()){
                        if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                            assert d.getKey() != null;
                            addProfessors(d.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else if(moreCheck==31){
            if(!fUniList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fUniList){
                    mRef.child("Universities").child(s).child(fieldName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    assert d.getKey() != null;
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=20;
                    mRef.child("Faculties").child(fieldName).child(cityName)
                            .orderByValue().limitToFirst(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(final DataSnapshot d : snapshot.getChildren()){
                                assert d.getKey() != null;
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }else if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_high))){
                    fCheck=false;
                    lastPage = false;
                    numOfItems=20;
                    list.clear();
                    adapter.notifyDataSetChanged();
                    loadData();
                }
            }
        }else if(moreCheck==32){
            if(!fCityList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fCityList){
                    mRef.child("Faculties").child(fieldName).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            if(!fUniList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
                for(String s : fUniList){
                    mRef.child("Universities").child(s).child(fieldName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long eNum = snapshot.getChildrenCount();
                            if(eNum < 2){
                                emptyLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (eNum < numOfItems) {
                                addLayout.setVisibility(View.VISIBLE);
                            }
                            for(DataSnapshot d : snapshot.getChildren()){
                                if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                    assert d.getKey() != null;
                                    addProfessors(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            if((fUniList.isEmpty() && fCityList.isEmpty()) &&
                    (!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low)))){
                fCheck=false;
                lastPage = false;
                numOfItems=20;
                mRef.child("Faculties").child(fieldName).child("All professors").orderByValue()
                        .limitToFirst(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                        long eNum = snapshot.getChildrenCount();
                        if(eNum < 2){
                            emptyLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else if (eNum < numOfItems) {
                            addLayout.setVisibility(View.VISIBLE);
                        }
                        for(final DataSnapshot d : snapshot.getChildren()){
                            if(!String.valueOf(d.getKey()).equals("aaaaa")) {
                                assert d.getKey() != null;
                                addProfessors(d.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else if((fUniList.isEmpty() && fCityList.isEmpty()) &&
                    (!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_high)))){
                fCheck=false;
                lastPage = false;
                numOfItems=20;
                list.clear();
                adapter.notifyDataSetChanged();
                loadData();
            }
        }else if(moreCheck==4){
            if(!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low))){
                sort(true);
            }else{
                sort(false);
            }
        }
    }

    private void addProfessors(final String profID){
        mRef.child("Professors").child(profID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                String cityUniName = snapshot2.child("city").getValue() +","
                        + snapshot2.child("uni_name").getValue();
                SearchObject searchObject = new SearchObject(String.valueOf(snapshot2.child("prof_name").getValue())
                        ,String.valueOf(snapshot2.child("field_name").getValue()),cityUniName,
                        String.valueOf(snapshot2.child("photo").getValue())
                        ,Double.parseDouble(String.valueOf(snapshot2.child("avg_rating").getValue())),profID);
                if(snapshot2.child("title").exists()){
                    searchObject.setTitle(String.valueOf(snapshot2.child("title").getValue()));
                }
                list.add(searchObject);
                adapter.notifyDataSetChanged();
                if((!fCheck && (!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low)))) ||
                        (!selectedRating.isEmpty() && selectedRating.equals(getResources().getString(R.string.sort_low)))){
                    sort(true);
                }else{
                    sort(false);
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error2) {
                Toast.makeText(SearchActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe
    public void EventBusTakeInformation(EventbusDataEvents.filterTypeBack data){
        fCheck = data.getCheck();
        fCityList = data.getCityList();
        fUniList = data.getUniList();
        fFieldList = data.getFieldList();
        selectedRating = data.getRatingSytle();
        if(fCheck){
            filterClear.setVisibility(View.VISIBLE);
            loadFilteredResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}