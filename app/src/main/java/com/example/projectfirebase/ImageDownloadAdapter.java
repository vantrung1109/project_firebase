package com.example.projectfirebase;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageDownloadAdapter extends RecyclerView.Adapter<ImageDownloadAdapter.ImageDownloadViewHolder>{

    List<ImageDownload> imageDownloads;

    public ImageDownloadAdapter(List<ImageDownload> imageDownloads) {
        this.imageDownloads = imageDownloads;
    }

    @NonNull
    @Override
    public ImageDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageDownloadViewHolder(View.inflate(parent.getContext(), R.layout.image_download_item, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageDownloadViewHolder holder, int position) {
        if (imageDownloads == null || imageDownloads.size() == 0) return;

        holder.imageView.setImageResource(imageDownloads.get(position).getResource());
    }

    @Override
    public int getItemCount() {
        if (imageDownloads == null) return 0;
        return imageDownloads.size();
    }

    public class ImageDownloadViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public ImageDownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
