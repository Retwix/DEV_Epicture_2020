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
import com.epicture.Adapter.FavoriteGalleryAdapter;
import com.epicture.R;

public class Fragment_favorite extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.favoris_gallery);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Epicture_global.FavoriteAdapter = new FavoriteGalleryAdapter(view.getContext(), Epicture_global.FavoriteGalleryData);
        Epicture_global.FavoriteAdapter.setOnItemClickListener(new FavoriteGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLikeClick(int position) {
                Epicture_global.FavoriteAdapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(Epicture_global.FavoriteAdapter);
        return view;
    }
}
