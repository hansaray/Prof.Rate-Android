package com.simpla.turnedTables;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.simpla.turnedTables.Adapters.NotificationsAdapter;
import com.simpla.turnedTables.Objects.NotificationObject;
import com.simpla.turnedTables.Objects.UserObject;
import com.simpla.turnedTables.Utils.SetTheme;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotificationsActivity extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private long cNumber = 0;
    private int numOfItems = 15;
    private boolean lastPage = false;
    private DatabaseReference mRef;
    private NotificationsAdapter adapter;
    private ArrayList<NotificationObject> list;
    private ArrayList<String> ids;
    private UserObject userObject;
    private AdView banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetTheme.theme(NotificationsActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getIntentData();
        findIds();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userObject = (UserObject) intent.getSerializableExtra("biggerUserData");
    }

    private void findIds() {
        backButton = findViewById(R.id.notBack);
        recyclerView = findViewById(R.id.notRw);
        banner = findViewById(R.id.notBanner);
        list = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new NotificationsAdapter(NotificationsActivity.this,list);
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("myNotifications");
        setAds();
        setupRw();
        setListeners();
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setupRw(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationsActivity.this);
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
                    if(!lastPage){
                        if(((list.size()+1)-numOfItems) >= 0){
                            if((cNumber-numOfItems)>15){
                                numOfItems += 15;
                            }else if((cNumber-numOfItems)==15){
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
        });
        numCheck();
        loadNotifications();
    }

    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void loadNotifications() {
        mRef.orderByChild("time").limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            addNotification(d.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NotificationsActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadMore() {
        final int number = list.size();
        String startKey = list.get(number - 1).getItemID();
        mRef.orderByChild("time").endAt(startKey)
                .limitToLast(numOfItems).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                ids.clear();
                for(final DataSnapshot d : snapshot.getChildren()){
                    ids.add(d.getKey());
                }
                Collections.reverse(ids);
                for(final String s : ids){
                    if(count >= number){
                        addNotification(s);
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationsActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addNotification(final String key) {
        mRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NotificationObject object = new NotificationObject(String.valueOf(snapshot.child("profName").getValue())
                        ,String.valueOf(snapshot.child("picName").getValue())
                        ,String.valueOf(snapshot.child("ratingID").getValue()),
                        Long.parseLong(String.valueOf(snapshot.child("time").getValue())),key);
                if(snapshot.child("totalLikes").exists()){
                    object.setTotalLikes(Long.parseLong(String.valueOf(snapshot.child("totalLikes").getValue())));
                }
                if(snapshot.child("totalDislikes").exists()){
                    object.setTotalDislike(Long.parseLong(String.valueOf(snapshot.child("totalDislikes").getValue())));
                }
                if(userObject != null){
                    object.setObject(userObject);
                }
                list.add(object);
                adapter.notifyDataSetChanged();
                sort();
                setSeenInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationsActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void numCheck(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cNumber = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sort(){
        Collections.sort(list, new Comparator<NotificationObject>() {
            @Override
            public int compare(NotificationObject o1, NotificationObject o2) {
                return Double.compare(o2.getTime(), o1.getTime());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void setSeenInfo(){
        if(list.size() != 0){
            for(NotificationObject o : list){
                mRef.child(o.getItemID()).child("seen").setValue(true);
            }
        }
    }

}