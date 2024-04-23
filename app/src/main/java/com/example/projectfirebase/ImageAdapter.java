package com.example.projectfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ImageDownload> mImageUrls;

    public ImageAdapter(Context context, List<ImageDownload> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_download_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        //String imageUrl = mImageUrls.get(position);
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
