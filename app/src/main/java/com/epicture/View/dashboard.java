package com.epicture.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epicture.Epicture_global;
import com.epicture.Adapter.GalleryAdapter;
import com.epicture.R;
import com.epicture.Callbacks.VolleyCallbacks;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

public class dashboard extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ImageView settings;
    SearchView searchbar;
    Spinner left;
    Spinner right;
    String urlFirstPart = null;
    String urlSecondPart = null;
    String searchQuery;
    final Context ctx = this;
    ArrayAdapter<String> rightSpinnerAdapter;
    ArrayAdapter<String> leftSpinnerAdapter;
    boolean isSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        left = findViewById(R.id.spinner1);
        right = findViewById(R.id.spinner2);
        rightSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        leftSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        isSearching = false;
        overridePendingTransition(0, 0);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        settings = findViewById(R.id.goSettings);
        if (Epicture_global.access_token == null) {
            spaceNavigationView.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
        }
        SharedPreferences mPrefs = getSharedPreferences("mature", 0);
        String mString = mPrefs.getString("mature", "false");
        if (Boolean.parseBoolean(mString)) {
            Epicture_global.mature = true;
        }
        searchbar = findViewById(R.id.search_view);

        SlidingUpPanelLayout layout = findViewById(R.id.slidingUp);
        final Context context = this;
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_person_black_24dp));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent i = new Intent(dashboard.this, upload_photo.class);
                startActivity(i);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex == 0) {
                    return;
                } else if (itemIndex == 1) {
                    Intent i = new Intent(dashboard.this, profile.class);
                    startActivity(i);
                } else {
                    Log.d("groot", "ERROR NAVIGATION");
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });

        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                findViewById(R.id.textView).setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //searchbar.clearFocus();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(dashboard.this, com.epicture.View.settings.class);
                startActivity(i);
            }
        });
        createRecyclerView();
        handleSearchBar();
        handleSpinners();

    }

    public void createRecyclerView() {
        //recyclerView
        recyclerView = findViewById(R.id.card_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Epicture_global.GalleryAdapter = new GalleryAdapter(this, Epicture_global.GalleryData);
        recyclerView.setAdapter(Epicture_global.GalleryAdapter);
        Epicture_global.GalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onLikeClick(int position) {

            }
        });
    }

    public void handleSearchBar() {
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                Epicture_global.getSearchGallery(ctx, urlFirstPart, urlSecondPart, query, new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        Epicture_global.GalleryAdapter.notifyDataSetChanged();
                        isSearching = true;
                        searchbar.clearFocus();
                    }
                });
                handleSpinners();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    Epicture_global.getGallery(ctx, urlFirstPart, urlSecondPart, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                            isSearching = false;
                            handleSpinners();
                        }
                    });
                } else {
                    isSearching = true;
                }
                return true;
            }
        });
        searchbar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.clearFocus();
            }
        });
    }

    public void handleSpinners() {
        if (!isSearching) {
            leftSpinnerAdapter.clear();
            leftSpinnerAdapter.add("Most Viral");
            leftSpinnerAdapter.add("User Submitted");
            leftSpinnerAdapter.add("Highest Scoring");
            leftSpinnerAdapter.notifyDataSetChanged();
            rightSpinnerAdapter.clear();
            rightSpinnerAdapter.add("Popular");
            rightSpinnerAdapter.add("Newest");
            rightSpinnerAdapter.add("Best");
            rightSpinnerAdapter.add("Random");
            rightSpinnerAdapter.notifyDataSetChanged();
            right.setAdapter(rightSpinnerAdapter);
            left.setAdapter(leftSpinnerAdapter);
            left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                    switch (selected) {
                        case "Most Viral":
                            urlFirstPart = "hot";
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("Popular");
                            rightSpinnerAdapter.add("Newest");
                            rightSpinnerAdapter.add("Best");
                            rightSpinnerAdapter.add("Random");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            break;
                        case "User Submitted":
                            urlFirstPart = "user";
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("Popular");
                            rightSpinnerAdapter.add("Rising");
                            rightSpinnerAdapter.add("Newest");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            break;
                        case "Highest Scoring":
                            urlFirstPart = "top";
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("Today");
                            rightSpinnerAdapter.add("Week");
                            rightSpinnerAdapter.add("Month");
                            rightSpinnerAdapter.add("Year");
                            rightSpinnerAdapter.add("All Time");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            break;
                    }
                    Epicture_global.getGallery(ctx, urlFirstPart, urlSecondPart, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            right.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                    switch (selected) {
                        case "Popular":
                            urlSecondPart = "viral";
                            break;
                        case "Newest":
                            urlSecondPart = "time";
                            break;
                        case "Best":
                            urlSecondPart = "top";
                            break;
                        case "Random":
                            urlFirstPart = "random";
                            urlSecondPart = "random";
                        case "Rising":
                            urlSecondPart = "rising";
                            break;
                        case "Today":
                            urlSecondPart = "viral/day";
                            break;
                        case "Week":
                            urlSecondPart = "viral/week";
                            break;
                        case "Month":
                            urlSecondPart = "viral/month";
                            break;
                        case "Year":
                            urlSecondPart = "viral/year";
                            break;
                        case "All Time":
                            urlSecondPart = "viral/all";
                            break;
                    }
                    Epicture_global.getGallery(ctx, urlFirstPart, urlSecondPart, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (isSearching) {
            leftSpinnerAdapter.clear();
            leftSpinnerAdapter.add("Highest Scoring");
            leftSpinnerAdapter.add("Most Revelant");
            leftSpinnerAdapter.add("Newest First");
            leftSpinnerAdapter.notifyDataSetChanged();
            rightSpinnerAdapter.clear();
            rightSpinnerAdapter.add("All Time");
            rightSpinnerAdapter.add("Today");
            rightSpinnerAdapter.add("This Week");
            rightSpinnerAdapter.add("This Month");
            rightSpinnerAdapter.add("This Year");
            rightSpinnerAdapter.notifyDataSetChanged();
            right.setAdapter(rightSpinnerAdapter);
            left.setAdapter(leftSpinnerAdapter);

            left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                    switch (selected) {
                        case "Highest Scoring":
                            urlFirstPart = "top";
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("All Time");
                            rightSpinnerAdapter.add("Today");
                            rightSpinnerAdapter.add("This Week");
                            rightSpinnerAdapter.add("This Month");
                            rightSpinnerAdapter.add("This Year");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            break;
                        case "Most Revelant":
                            urlFirstPart = "viral";
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("All Time");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            break;
                        case "Newest First":
                            rightSpinnerAdapter.clear();
                            rightSpinnerAdapter.add("All Time");
                            rightSpinnerAdapter.notifyDataSetChanged();
                            urlFirstPart = "time";
                            break;
                    }
                    Epicture_global.getSearchGallery(ctx, urlFirstPart, urlSecondPart, searchQuery, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            right.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                    switch (selected) {
                        case "All Time":
                            urlSecondPart = "all";
                            break;
                        case "Today":
                            urlSecondPart = "day";
                            break;
                        case "This Week":
                            urlSecondPart = "week";
                            break;
                        case "This Month":
                            urlFirstPart = "month";
                            break;
                        case "This Year":
                            urlSecondPart = "year";
                            break;
                    }
                    Epicture_global.getSearchGallery(ctx, urlFirstPart, urlSecondPart, searchQuery, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
