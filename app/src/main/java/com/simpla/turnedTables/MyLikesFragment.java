package com.simpla.turnedTables;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simpla.turnedTables.Adapters.CommentsAdapter;
import com.simpla.turnedTables.Objects.CommentsObject;
import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

public class MyLikesFragment extends Fragment {

    private Button backButton;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private ArrayList<CommentsObject> list;
    private long cNumber = 0;
    private int numOfItems = 10;
    private boolean lastPage = false;
    private DatabaseReference mRef;
    private ArrayList<String> ids;
    private String myUid;
    private AdView banner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up_photo, container, false);
        findIds(v);
        return v;
    }

    private void findIds(View v){
        list = new ArrayList<>();
        ids = new ArrayList<>();
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = v.findViewById(R.id.signUpPhotoRw);
        backButton = v.findViewById(R.id.signUpPhotoBack);
        banner = v.findViewById(R.id.signUpPhotoBanner);
        adapter = new CommentsAdapter(getActivity(),list);
        TextView title = v.findViewById(R.id.signUpPhotoTitle);
        //change Title,
        title.setText(getResources().getString(R.string.myLikes));
        setAds();
        setListeners();
        setupRw();
    }

    private void setAds() {
        banner.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
    }

    private void setListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(MyLikesFragment.this).commit();
                getActivity().findViewById(R.id.editFrameLayout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.editLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRw(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
                            if((cNumber-numOfItems)>10){
                                numOfItems += 10;
                            }else if((cNumber-numOfItems)==10){
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
        loadRatings();
    }

    private void loadRatings(){
        mRef.child("users").child(myUid).child("myLikes").orderByValue().limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            addRating(d.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadMore(){
        final int number = list.size();
        String startKey = list.get(number - 1).getItemID();
        mRef.child("users").child(myUid).child("myLikes").orderByValue().endAt(startKey)
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
                        addRating(s);
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error2) {
                            Toast.makeText(getContext(),error2.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sort(){
        Collections.reverse(list);
    }

    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.userLikeNumber data){
        cNumber = data.getTotalNumber();
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
