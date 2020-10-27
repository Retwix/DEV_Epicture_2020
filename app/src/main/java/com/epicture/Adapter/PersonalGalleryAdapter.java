package com.epicture.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.epicture.Epicture_global;
import com.epicture.R;
import com.epicture.Callbacks.VolleyCallbacks;
import com.epicture.View.viewPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

public class PersonalGalleryAdapter extends RecyclerView.Adapter<PersonalGalleryAdapter.ViewHolder> {
    private Context context;
    ArrayList<GalleryDatasAdapter> images;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLikeClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PersonalGalleryAdapter(Context context, ArrayList<GalleryDatasAdapter> gallery) {
        this.context = context;
        this.images = gallery;
    }

    @Override
    public PersonalGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.views.setText(this.images.get(i).getViews());
        if (Epicture_global.FavoriteIds.contains(this.images.get(i).getId())) {
            viewHolder.like.setBackground(context.getDrawable(R.drawable.filled));
        } else {
            viewHolder.like.setBackground(context.getDrawable(R.drawable.unfilled));
        }
        Glide.with(context).load(this.images.get(i).getUrl()).into(viewHolder.image_url);
        //Picasso.with(context).load(this.images.get(i).getUrl()).into(viewHolder.image_url);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_url = itemView.findViewById(R.id.image_url);
        public TextView views = itemView.findViewById(R.id.views);
        public Button like = itemView.findViewById(R.id.heart);

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    try {
                        BitmapDrawable drawable = (BitmapDrawable) image_url.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        BitmapAdapter.getInstance().setBitmap(bitmap);
                        BitmapAdapter.getInstance().setBitmapPostion(position);
                        BitmapAdapter.getInstance().setFrom("personnalgallery");
                    } catch (ClassCastException e) {
                        Log.d("groot", e.getMessage());
                    }
                    if (BitmapAdapter.getInstance().getBitmap() != null) {
                        Intent intent = new Intent(context, viewPhoto.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, itemView.findViewById(R.id.image_url), "myImage");
                        context.startActivity(intent, optionsCompat.toBundle());
                    } else {
                        Toast.makeText(context, "NON", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (Epicture_global.FavoriteIds.contains(Epicture_global.PersonalGalleryData.get(position).getId())) {
                            Epicture_global.favoriteImage(context, Epicture_global.PersonalGalleryData.get(position).getId(), new VolleyCallbacks() {
                                @Override
                                public void onSuccessResponse(JSONObject response) {
                                    unfillHeart(like);
                                    like.setBackground(context.getDrawable(R.drawable.unfilled));
                                    Epicture_global.handleFavoriteIds(Epicture_global.PersonalGalleryData.get(position).getId(), "remove");
                                    Epicture_global.getFavoriteGallery(context, new VolleyCallbacks() {
                                        @Override
                                        public void onSuccessResponse(JSONObject response) {
                                            Epicture_global.FavoriteAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                        } else {
                            Epicture_global.favoriteImage(context, Epicture_global.PersonalGalleryData.get(position).getId(), new VolleyCallbacks() {
                                @Override
                                public void onSuccessResponse(JSONObject response) {
                                    fillHeart(like);
                                    like.setBackground(context.getDrawable(R.drawable.filled));
                                    Epicture_global.handleFavoriteIds(Epicture_global.PersonalGalleryData.get(position).getId(), "add");
                                    Epicture_global.FavoriteGalleryData.add(0, Epicture_global.PersonalGalleryData.get(position));
                                    Epicture_global.FavoriteAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        listener.onLikeClick(position);
                    }
                }
            });
        }
    }

    public static void unfillHeart(final Button view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        prepareAnimation(alphaAnimation);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(500);
        view.startAnimation(animationSet);
    }

    public static void fillHeart(final Button view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(500);
        view.startAnimation(animationSet);

    }

    private static Animation prepareAnimation(Animation animation) {
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}