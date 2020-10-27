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

public class FavoriteGalleryAdapter extends RecyclerView.Adapter<FavoriteGalleryAdapter.ViewHolder> {
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

    public FavoriteGalleryAdapter(Context context, ArrayList<GalleryDatasAdapter> gallery) {
        this.context = context;
        this.images = gallery;
    }

    @Override
    public FavoriteGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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
                        BitmapAdapter.getInstance().setFrom("favorite");
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
                    final String id = Epicture_global.FavoriteGalleryData.get(position).getId();
                    Epicture_global.favoriteImage(context, id, new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            Epicture_global.handleFavoriteIds(id, "remove");
                            Epicture_global.FavoriteGalleryData.remove(position);
                            listener.onLikeClick(position);
                            Epicture_global.getPersonalGallery(context, new VolleyCallbacks() {
                                @Override
                                public void onSuccessResponse(JSONObject response) {
                                    Epicture_global.PersonalAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
