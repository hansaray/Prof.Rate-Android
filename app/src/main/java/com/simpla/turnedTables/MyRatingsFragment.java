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
import com.simpla.turnedTables.Objects.UserObject;
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
import java.util.Comparator;

public class MyRatingsFragment extends Fragment {

    private UserObject object;
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
        title.setText(getResources().getString(R.string.myRates));
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
                getActivity().getSupportFragmentManager().beginTransaction().remove(MyRatingsFragment.this).commit();
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
        mRef.child("users").child(myUid).child("myRatings").orderByKey().limitToLast(numOfItems)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot d : snapshot.getChildren()){
                            assert d.getKey() != null;
                            if(Boolean.parseBoolean(String.valueOf(d.getValue()))){
                                addRating(d.getKey(),true);
                            }else{
                                addRating(d.getKey(),false);
                            }
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
        mRef.child("users").child(myUid).child("myRatings").orderByKey()
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
                        if(Boolean.parseBoolean(String.valueOf(snapshot.child(s).getValue()))){
                            addRating(s,true);
                        }else{
                            addRating(s,false);
                        }
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

    private void addRating(final String ratingID,boolean type){
        if(type){
            mRef.child("ratings").child("withComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    CommentsObject cObject = new CommentsObject(myUid
                            ,object.getPhotoName(),object.getUsername()
                            ,object.getFieldName(),object.getUniName()
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
                    cObject.setUserLikedNum(object.getTotalLiked());
                    cObject.setUserDislikedNum(object.getTotalDisliked());
                    list.add(cObject);
                    adapter.notifyDataSetChanged();
                    sort();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            mRef.child("ratings").child("noComment").child(ratingID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    CommentsObject cObject = new CommentsObject(myUid
                            ,object.getPhotoName(),object.getUsername()
                            ,object.getFieldName(),object.getUniName()
                            ,Double.parseDouble(String.valueOf(snapshot.child("avg_rating").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("helpfulness").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("difficulty").getValue()))
                            ,Double.parseDouble(String.valueOf(snapshot.child("lecture").getValue()))
                            ,null,Long.parseLong(String.valueOf(snapshot.child("time").getValue())));
                    if(snapshot.child("attendance").exists()){
                        cObject.setAttNum(Double.parseDouble(String.valueOf(snapshot.child("attendance").getValue())));
                    }
                    if(snapshot.child("textbook").exists()){
                        cObject.setTxtNum(Double.parseDouble(String.valueOf(snapshot.child("textbook").getValue())));
                    }
                    cObject.setPopularity(0);
                    cObject.setItemID(ratingID);
                    cObject.setProfID(String.valueOf(snapshot.child("profID").getValue()));
                    cObject.setCheck(true);
                    list.add(cObject);
                    adapter.notifyDataSetChanged();
                    sort();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
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

    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.userData data){
        object = data.getObject();
        cNumber = object.getTotalRatings();
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
