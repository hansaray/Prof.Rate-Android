package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.simpla.turnedTables.Adapters.CommentsAdapter;
import com.simpla.turnedTables.Objects.CommentsObject;
import com.simpla.turnedTables.Utils.SetTheme;
import com.simpla.turnedTables.Utils.UniversalImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout,filterButton,filterLayout,logoLayout;
    private FrameLayout frameLayout;
    private Button settingsButton,searchButton,cityButton,uniButton;
    private Spinner filterSpinner;
    private ArrayList<String> fList,idList;
    private ArrayList<CommentsObject> list;
    private ArrayList<String> ids;
    private DatabaseReference mRef;
    private String selectedFilter;
    private AlertDialog.Builder ad;
    private AdView banner;
    private ImageView clearFilter;
    private boolean fCheck = false;
    private CommentsAdapter adapter;
    private RecyclerView recyclerView;
    private long cNumber = 0;
    private int numOfItems = 20, sCheck = 0,fNumber = 0;
    private boolean lastPage = false;
    private TextView title;
    private RelativeLayout spinnerLayout;
    private SwipeRefreshLayout refreshLayout;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkModeControl = SetTheme.theme(MainActivity.this);
        if (darkModeControl == 1){
            ad = new AlertDialog.Builder(MainActivity.this);
        } else {
            ad = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustomDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        getIntentData();
        findIds();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String likeID = intent.getStringExtra("likeRatingIDm");
        String dislikeID = intent.getStringExtra("dislikeRatingIDm");
        if(likeID != null){
            Intent intent1 = new Intent(MainActivity.this,BiggerRatingActivity.class);
            intent1.putExtra("likeRatingID",likeID);
            startActivity(intent1);
        }else if(dislikeID != null){
            Intent intent1 = new Intent(MainActivity.this,BiggerRatingActivity.class);
            intent1.putExtra("dislikeRatingID",dislikeID);
            startActivity(intent1);
        }
    }

    private void findIds(){
        mainLayout = findViewById(R.id.feedLayout);
        frameLayout = findViewById(R.id.feedFrameLayout);
        settingsButton = findViewById(R.id.mainSettings);
        filterButton = findViewById(R.id.feedFilterButton);
        filterLayout = findViewById(R.id.feedFilterLayout);
        clearFilter = findViewById(R.id.feed_f_clear);
        cityButton = findViewById(R.id.feed_f_city);
        uniButton = findViewById(R.id.feed_f_uni);
        filterSpinner = findViewById(R.id.feedSpinner);
        searchButton = findViewById(R.id.feedSearch);
        banner = findViewById(R.id.feedBanner);
        recyclerView = findViewById(R.id.feedRw);
        title = findViewById(R.id.feedTitle);
        spinnerLayout = findViewById(R.id.feedRelative1);
        logoLayout = findViewById(R.id.logoLayout);
        refreshLayout = findViewById(R.id.feedRefresh);
        fList = new ArrayList<>();
        list = new ArrayList<>();
        ids = new ArrayList<>();
        idList = new ArrayList<>();
        adapter = new CommentsAdapter(MainActivity.this,list);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("ratings").child("withComment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cNumber = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userEntrance();
        setAds();
        setListeners();
        setupRw();
    }

    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists() && FirebaseAuth.getInstance().getCurrentUser() != null){
                boolean check = false;
                for(DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    if(d.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        check = true;
                        break;
                    }
                }
                if(check){
                    ad.setTitle(getResources().getString(R.string.information));
                    ad.setMessage(getResources().getString(R.string.banned_exp));
                    ad.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.create().cancel();
                        }
                    });
                    ad.create().show();
                    FirebaseAuth.getInstance().signOut();
                }else{
                    Map<String, String> time = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastEntrance").setValue(time);
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    private void userEntrance() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            mRef.child("bannedUsers").addValueEventListener(listener);
        }
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setSpinners(){
        fList.clear();
        fNumber = 0;
        if(fCheck){//City
            String firstItem = getResources().getString(R.string.filter_city);
            title.setText(getResources().getString(R.string.city_name_exp));
            fList.add(firstItem);
            spinnerData(mRef.child("Cities"));
        }else{//Uni
            String firstItem = getResources().getString(R.string.filter_uni);
            title.setText(getResources().getString(R.string.uni_name));
            fList.add(firstItem);
            spinnerData(mRef.child("Universities"));
        }
    }

    private void spinnerData(Query qRef){
        title.setVisibility(View.VISIBLE);
        spinnerLayout.setVisibility(View.VISIBLE);
        qRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    if(fCheck) {
                        fList.add(nameFix(String.valueOf(d.getKey())));
                    } else {
                        fList.add(String.valueOf(d.getKey()));
                    }
                }
                ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.simple_spinner_item, fList);
                cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                filterSpinner.setAdapter(cityArrayAdapter);
                filterSpinner.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners(){
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.feedFrameLayout,new SettingsFragment()).commit();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MainSearchActivity.class));
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View view) {
                if(i%2==0){
                    filterLayout.setVisibility(View.VISIBLE);
                }else{
                    filterLayout.setVisibility(View.GONE);
                    title.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                }
                sCheck = 0;
                i++;
            }
        });
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilter.setVisibility(View.GONE);
                selectedFilter = "";
                lastPage = false;
                fNumber = 0;
                idList.clear();
                list.clear();
                adapter.notifyDataSetChanged();
                loadData();
            }
        });
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fCheck = true;
                setSpinners();
            }
        });
        uniButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fCheck = false;
                setSpinners();
            }
        });
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    selectedFilter = "";
                    clearFilter.setVisibility(View.GONE);
                    lastPage = false;
                    if(sCheck != 0 ) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                        loadData();
                    }
                }else{
                    selectedFilter = fList.get(i);
                    clearFilter.setVisibility(View.VISIBLE);
                    loadFilteredData();
                    sCheck++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupRw(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastPage = false;
                list.clear();
                recyclerView.getRecycledViewPool().clear();
                adapter.notifyDataSetChanged();
                if(selectedFilter != null && !selectedFilter.isEmpty()){
                    idList.clear();
                    fNumber = 0;
                    loadFilteredData();
                }else{
                    numOfItems = 20;
                    loadData();
                }
                refreshLayout.setRefreshing(false);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                assert recyclerView.getAdapter() != null;
                if(dy>0 && layoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount()-1){
                    if(!lastPage){
                        if(selectedFilter != null && !selectedFilter.isEmpty()) {
                            if((idList.size()-fNumber) > 20) {
                                fNumber += 20;
                            } else if((idList.size()-fNumber)==20){
                                fNumber += 20;
                                lastPage = true;
                            } else {
                                fNumber += (idList.size()-fNumber);
                                lastPage = true;
                            }
                            addFiltered();
                        } else {
                            if(((list.size()+2)-numOfItems) >= 0){//100 //20 //0,20 -1
                                if((cNumber-numOfItems)>20){
                                    numOfItems += 20;
                                }else if((cNumber-numOfItems)==20){
                                    numOfItems += (cNumber-numOfItems);
                                    lastPage = true;
                                }else{
                                    numOfItems += (cNumber-numOfItems);
                                    lastPage = true;
                                }
                                loadMore();
                            }
                        }
                    }
                }
            }
        });
        loadData();
    }

    private void loadFilteredData(){
        list.clear();
        idList.clear();
        adapter.notifyDataSetChanged();
        mRef.child("ratings").child("withComment").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long[] fc = {0};
                final long tNum = snapshot.getChildrenCount();
                for(final DataSnapshot d : snapshot.getChildren()){
                    mRef.child("Professors").child(String.valueOf(d.child("profID").getValue()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    if(selectedFilter != null && !selectedFilter.isEmpty()) {
                                        if(fCheck){
                                            if(selectedFilter.equals(nameFix(String.valueOf(snapshot2.child("city").getValue())))){
                                                assert d.getKey() != null;
                                                idList.add(d.getKey());
                                            }
                                        }else{
                                            if(selectedFilter.equals(String.valueOf(snapshot2.child("uni_name").getValue()))){
                                                assert d.getKey() != null;
                                                idList.add(d.getKey());
                                            }
                                        }
                                    }
                                    fc[0]++;
                                    if(fc[0]==tNum) {
                                        Collections.reverse(idList);
                                        addFiltered();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addFiltered(){
        int count = 0;
        for(final String s : idList){
            if(count >= fNumber && ((count-fNumber) < 20)){
                addRating(s);
            }
            count++;
        }
    }

    private void loadData(){
        mRef.child("ratings").child("withComment").orderByKey().limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(final DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            addRating(d.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadMore(){
        mRef.child("ratings").child("withComment").orderByKey()
                .limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ids.clear();
                int count = 0;
                for(final DataSnapshot d : snapshot.getChildren()){
                    assert d.getKey() != null;
                    ids.add(d.getKey());
                }
                Collections.reverse(ids);
                for(final String s : ids){
                    if(count >= list.size()){
                        if(selectedFilter != null && !selectedFilter.isEmpty()) {
                            mRef.child("Professors").child(String.valueOf(snapshot.child(s).child("profID").getValue()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            if(fCheck){
                                                if(selectedFilter.equals(nameFix(String.valueOf(snapshot2.child("city").getValue())))){
                                                    addRating(s);
                                                }else {
                                                    numOfItems--;
                                                }
                                            }else{
                                                if(selectedFilter.equals(String.valueOf(snapshot2.child("uni_name").getValue()))){
                                                    addRating(s);
                                                }else {
                                                    numOfItems--;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            addRating(s);
                        }
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addRating(final String ratingID){
        mRef.child("ratings").child("withComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mRef.child("users").child(String.valueOf(snapshot.child("userID").getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            CommentsObject cObject = new CommentsObject(String.valueOf(snapshot.child("userID").getValue())
                                    ,String.valueOf(snapshot2.child("photo").getValue()),String.valueOf(snapshot2.child("username").getValue())
                                    ,String.valueOf(snapshot2.child("faculty").getValue()),String.valueOf(snapshot2.child("university").getValue())
                                    ,Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                                    ,Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue()))
                                    ,Double.parseDouble(String.valueOf(snapshot.child("difficulty").getValue()))
                                    ,Double.parseDouble(String.valueOf(snapshot.child("lecture").getValue()))
                                    ,String.valueOf(snapshot.child("comment").getValue()),Long.parseLong(String.valueOf(snapshot.child("time").getValue())));
                            if(snapshot.child("attendance").exists()){
                                cObject.setAttNum(Double.parseDouble(String.valueOf(snapshot.child("attendance").getValue())));
                            }
                            if(snapshot.child("textbook").exists()){
                                cObject.setTxtNum(Double.parseDouble(String.valueOf(snapshot.child("textbook").getValue())));
                            }
                            if(snapshot.child("likes").exists()){
                                cObject.setLikeNum(snapshot.child("likes").getChildrenCount());
                            }
                            if(snapshot.child("dislikes").exists()){
                                cObject.setDislikeNum(snapshot.child("dislikes").getChildrenCount());
                            }
                            cObject.setPopularity(Integer.parseInt(String.valueOf(snapshot.child("popularity").getValue())));
                            cObject.setItemID(ratingID);
                            cObject.setProfID(String.valueOf(snapshot.child("profID").getValue()));
                            cObject.setCheck(true);
                            if(snapshot2.child("myLikedNumber").exists()){
                                cObject.setUserLikedNum(Long.parseLong(String.valueOf(snapshot2.child("myLikedNumber").getValue())));
                            }
                            if(snapshot2.child("myDislikedNumber").exists()){
                                cObject.setUserDislikedNum(Long.parseLong(String.valueOf(snapshot2.child("myDislikedNumber").getValue())));
                            }
                            list.add(cObject);
                            adapter.notifyDataSetChanged();
                            sort();
                            logoLayout.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error2) {
                            Toast.makeText(MainActivity.this,error2.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    logoLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sort(){
        Collections.sort(list, new Comparator<CommentsObject>() {
            @Override
            public int compare(CommentsObject o1, CommentsObject o2) {
                return Double.compare(o2.getTime(), o1.getTime());
            }
        });
        adapter.notifyDataSetChanged();
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

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener != null){
            mRef.child("bannedUsers").removeEventListener(listener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listener == null && FirebaseAuth.getInstance().getCurrentUser() != null){
            mRef.child("bannedUsers").addValueEventListener(listener);
        }
    }

}