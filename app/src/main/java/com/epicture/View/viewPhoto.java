package com.epicture.View;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epicture.Adapter.BitmapAdapter;
import com.epicture.Adapter.CommentsAdapter;
import com.epicture.Epicture_global;
import com.epicture.R;
import com.epicture.Callbacks.VolleyCallbacks;

import org.json.JSONException;
import org.json.JSONObject;

public class viewPhoto extends AppCompatActivity {

    ImageView imageView;
    String galleryId;
    String imageId;
    String voteValue;
    String upvotes;
    String downvotes;
    TextView upvoteTextView;
    TextView downvoteTextView;
    TextView title;
    TextView description;
    TextView favoritesTextView;
    ImageView upvoteImage;
    ImageView downvoteImage;
    ImageView favoriteImage;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    final Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        imageView = (ImageView) findViewById(R.id.imageExpand);
        upvoteTextView = findViewById(R.id.ups);
        downvoteTextView = findViewById(R.id.downs);
        upvoteImage = findViewById(R.id.arrowUp);
        downvoteImage = findViewById(R.id.arrowDown);
        favoriteImage = findViewById(R.id.like_zoom);
        favoritesTextView = findViewById(R.id.favorites);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        imageView.setImageBitmap(BitmapAdapter.getInstance().getBitmap());

        String tmpTitle = null;
        String tmpDescription = null;

        //Get Votes Values
        if (BitmapAdapter.getInstance().getFrom().equals("gallery")) {
            galleryId = "gallery/" + Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getGalleryId();
            imageId = Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getId();
            voteValue = Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getVote();
            if (Epicture_global.FavoriteIds.contains(imageId)) {
                favoriteImage.setBackground(this.getDrawable(R.drawable.filled));
            }
            getImageVotes();
            getGalleryInfos();
            tmpTitle = Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getTitle();
            tmpDescription = Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getDescription();
        } else if (BitmapAdapter.getInstance().getFrom().equals("personnalgallery")) {
            imageId = Epicture_global.PersonalGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getId();
            galleryId = "image/" + imageId;
            voteValue = Epicture_global.PersonalGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getVote();
            upvoteImage.setVisibility(View.GONE);
            downvoteImage.setVisibility(View.GONE);
            favoriteImage.setVisibility(View.GONE);
            tmpTitle = Epicture_global.PersonalGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getTitle();
            tmpDescription = Epicture_global.PersonalGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getDescription();
        } else if (BitmapAdapter.getInstance().getFrom().equals("favorite")) {
            imageId = Epicture_global.FavoriteGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getId();
            galleryId = "image/" + imageId;
            upvoteImage.setVisibility(View.GONE);
            downvoteImage.setVisibility(View.GONE);
            favoriteImage.setVisibility(View.GONE);
            voteValue = Epicture_global.FavoriteGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getVote();
            tmpTitle = Epicture_global.FavoriteGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getTitle();
            tmpDescription = Epicture_global.FavoriteGalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).getDescription();
        }
        if (tmpTitle != null) {
            title.setText(tmpTitle);
        } else {
            title.setText("No title");
        }
        if (tmpDescription != null) {
            description.setText(tmpDescription);
        } else {
            description.setText("No description");
        }

        setClickListeners();
        Log.d("groot", galleryId);

        Epicture_global.getImageComments(ctx, galleryId, new VolleyCallbacks() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                createRecyclerView();
            }
        });

    }

    public void getImageVotes() {
        Epicture_global.getImageVote(this, galleryId, new VolleyCallbacks() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    upvotes = response.getString("ups");
                    downvotes = response.getString("downs");
                    upvoteTextView.setText(upvotes);
                    downvoteTextView.setText(downvotes);
                } catch (JSONException e) {
                    Log.d("groot", e.getMessage());
                }
            }
        });
    }

    public void getGalleryInfos() {
        Epicture_global.getGalleryInfo(ctx, galleryId, new VolleyCallbacks() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    favoritesTextView.setText(response.getString("favorite_count"));
                    if (response.getString("vote").equals("up")) {
                        Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).setVote(response.getString("vote"));
                        upvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_up_filled));
                    } else if (response.getString("vote").equals("down")) {
                        Epicture_global.GalleryData.get(BitmapAdapter.getInstance().getBitmapPosition()).setVote(response.getString("vote"));
                        downvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_down_filled));
                    }
                } catch (JSONException e) {
                    Log.d("groot", e.getMessage());
                }
            }
        });
    }

    public void setClickListeners() {
        upvoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Epicture_global.voteImage(ctx, galleryId, "up", new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        upvoteTextView.setText(String.valueOf(Integer.valueOf(upvoteTextView.getText().toString()) + 1));
                        downvoteTextView.setText(String.valueOf(Integer.valueOf(upvoteTextView.getText().toString()) - 1));
                        downvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_down_unfilled));
                        upvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_up_filled));
                    }
                });
            }
        });

        downvoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Epicture_global.voteImage(ctx, galleryId, "down", new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        upvoteTextView.setText(String.valueOf(Integer.valueOf(upvoteTextView.getText().toString()) - 1));
                        downvoteTextView.setText(String.valueOf(Integer.valueOf(upvoteTextView.getText().toString()) + 1));
                        upvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_up_unfilled));
                        downvoteImage.setBackground(ctx.getDrawable(R.drawable.arrow_down_filled));
                    }
                });
            }
        });

        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Epicture_global.favoriteImage(ctx, imageId, new VolleyCallbacks() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        if (Epicture_global.FavoriteIds.contains(imageId)) {
                            favoriteImage.setBackground(ctx.getDrawable(R.drawable.unfilled));
                            Epicture_global.FavoriteIds.remove(imageId);
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                            if (Epicture_global.FavoriteAdapter != null && Epicture_global.FavoriteAdapter != null) {
                                Epicture_global.FavoriteAdapter.notifyDataSetChanged();
                                Epicture_global.PersonalAdapter.notifyDataSetChanged();
                            }
                        } else if (!Epicture_global.FavoriteIds.contains(imageId)) {
                            favoriteImage.setBackground(ctx.getDrawable(R.drawable.filled));
                            Epicture_global.FavoriteIds.add(imageId);
                            Epicture_global.GalleryAdapter.notifyDataSetChanged();
                            if (Epicture_global.FavoriteAdapter != null && Epicture_global.FavoriteAdapter != null) {
                                Epicture_global.FavoriteAdapter.notifyDataSetChanged();
                                Epicture_global.PersonalAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
    }

    public void createRecyclerView() {
        //recyclerView
        recyclerView = findViewById(R.id.comment_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Epicture_global.CommentsAdapter = new CommentsAdapter(this, Epicture_global.CommentsDatasList);
        recyclerView.setAdapter(Epicture_global.CommentsAdapter);
        Epicture_global.CommentsAdapter.setOnItemClickListener(new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onUpClick(int position) {
            }

            @Override
            public void onDownClick(int position) {
            }
        });
    }
}
