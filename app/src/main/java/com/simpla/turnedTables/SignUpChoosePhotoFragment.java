package com.simpla.turnedTables;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.simpla.turnedTables.Adapters.SignUpPhotoAdapter;
import com.simpla.turnedTables.Objects.PhotoObject;
import com.simpla.turnedTables.Utils.EventbusDataEvents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class SignUpChoosePhotoFragment extends Fragment {
    private Button backButton;
    private RecyclerView recyclerView;
    private SignUpPhotoAdapter adapter;
    private ArrayList<PhotoObject> list;
    private int activityNumber = 0;
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
        recyclerView = v.findViewById(R.id.signUpPhotoRw);
        backButton = v.findViewById(R.id.signUpPhotoBack);
        banner = v.findViewById(R.id.signUpPhotoBanner);
        adapter = new SignUpPhotoAdapter(getActivity(),list);
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
                getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpChoosePhotoFragment.this).commit();
                if(activityNumber == 1){
                    getActivity().findViewById(R.id.editFrameLayout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.editLayout).setVisibility(View.VISIBLE);
                } else {
                    getActivity().findViewById(R.id.signUpFrameLayout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.signUpLayout).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRw(){
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        setupList();
    }

    private void setupList(){
        list.clear();
        for(int i = 1; i < 17 ; i++ ) {
            String name = "pp"+i;
            PhotoObject object = new PhotoObject(name,activityNumber);
            list.add(object);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(sticky = true)
    public void EventBusTakeInformation(EventbusDataEvents.activityCheck data){
        activityNumber = data.getNumber();
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