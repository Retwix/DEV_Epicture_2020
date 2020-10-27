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

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.epicture.Callbacks.VolleyCallbacks;
import com.epicture.Epicture_global;
import com.epicture.R;
import com.epicture.View.viewPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    final Context context;
    ArrayList<CommentsDatas> datas;
    private CommentsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onUpClick(int position);
        void onDownClick(int position);
    }

    public void setOnItemClickListener(CommentsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public CommentsAdapter(Context context, ArrayList<CommentsDatas> list) {
        this.context = context;
        this.datas = list;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comment, viewGroup, false);
        return new CommentsAdapter.ViewHolder(view, this.mListener);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.author.setText(this.datas.get(i).getUsername());
        viewHolder.comment.setText(this.datas.get(i).getContent());
        viewHolder.ups.setText(this.datas.get(i).ups);
        viewHolder.downs.setText(this.datas.get(i).down);

        if (this.datas.get(i).vote != null) {
            if (this.datas.get(i).vote.equals("up")) {
                viewHolder.upImage.setBackground(context.getDrawable(R.drawable.arrow_up_filled));
                viewHolder.downImage.setBackground(context.getDrawable(R.drawable.arrow_down_unfilled));
            } else if (this.datas.get(i).vote.equals("down")) {
                viewHolder.upImage.setBackground(context.getDrawable(R.drawable.arrow_up_unfilled));
                viewHolder.downImage.setBackground(context.getDrawable(R.drawable.arrow_down_filled));
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author = itemView.findViewById(R.id.author);
        public TextView comment = itemView.findViewById(R.id.comment);
        public TextView ups = itemView.findViewById(R.id.up);
        public TextView downs = itemView.findViewById(R.id.down);
        public ImageView upImage = itemView.findViewById(R.id.upImage);
        public ImageView downImage = itemView.findViewById(R.id.downImage);

        public ViewHolder(final View view, final CommentsAdapter.OnItemClickListener listener) {
            super(view);

            upImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    Epicture_global.voteComment(context, Epicture_global.CommentsDatasList.get(position).id, "up", new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            upImage.setBackground(context.getDrawable(R.drawable.arrow_up_filled));
                            downImage.setBackground(context.getDrawable(R.drawable.arrow_down_unfilled));
                        }
                    });
                    listener.onUpClick(position);
                }
            });

            downImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    Epicture_global.voteComment(context, Epicture_global.CommentsDatasList.get(position).id, "down", new VolleyCallbacks() {
                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            upImage.setBackground(context.getDrawable(R.drawable.arrow_up_unfilled));
                            downImage.setBackground(context.getDrawable(R.drawable.arrow_down_filled));
                        }
                    });
                    listener.onDownClick(position);
                }
            });
        }
    }
}
