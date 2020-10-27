package com.epicture.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.epicture.Epicture_global;
import com.epicture.R;
import com.epicture.Adapter.ViewPagerAdapter;
import com.epicture.Callbacks.VolleyCallbacks;
import com.epicture.Fragment.Fragment_favorite;
import com.epicture.Fragment.Fragment_photo;
import com.google.android.material.tabs.TabLayout;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class profile extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView reputation;
    private TextView score;
    private TextView username;
    final Context ctx = this;
    SpaceNavigationView spaceNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(0, 0);

        tabLayout = (TabLayout) findViewById(R.id.tableLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        reputation = findViewById(R.id.textView5);
        score = findViewById(R.id.textView7);
        username = findViewById(R.id.username);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //profile image + url
        Epicture_global.getProfileImageUrl(ctx, new VolleyCallbacks() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                ImageView profil_image = findViewById(R.id.profile_image);
                ImageView cover_image = findViewById(R.id.cover);
                Picasso.with(ctx).load(Epicture_global.profileImageUrl).into(profil_image);
                Picasso.with(ctx).load(Epicture_global.bannerImageUrl).into(cover_image);
                reputation.setText(Epicture_global.reputation);
                score.setText(Epicture_global.score);
                username.setText(Epicture_global.username);
            }
        });

        Epicture_global.getPersonalGallery(ctx, new VolleyCallbacks() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                Epicture_global.getFavoriteGallery(ctx, new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        adapter.AddFragment(new Fragment_photo(), "My pictures");
                        adapter.AddFragment(new Fragment_favorite(), "My favourites");
                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
            }
        });

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.spaceProfile);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_person_black_24dp));
        spaceNavigationView.changeCurrentItem(1);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent i = new Intent(profile.this, upload_photo.class);
                startActivity(i);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    Intent i = new Intent(profile.this, dashboard.class);
                    startActivity(i);
                } else if (itemIndex == 1) {
                    return;
                } else {
                    Log.d("groot", "ERROR NAVIGATION");
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
    }
}