package com.epicture.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epicture.Epicture_global;
import com.epicture.Adapter.PersonalGalleryAdapter;
import com.epicture.R;

public class Fragment_photo extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photo, container, false);

        recyclerView = view.findViewById(R.id.personal_gallery);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Epicture_global.PersonalAdapter = new PersonalGalleryAdapter(view.getContext(), Epicture_global.PersonalGalleryData);
        Epicture_global.PersonalAdapter.setOnItemClickListener(new PersonalGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLikeClick(int position) {

            }
        });
        recyclerView.setAdapter(Epicture_global.PersonalAdapter);
        return view;
    }
}
