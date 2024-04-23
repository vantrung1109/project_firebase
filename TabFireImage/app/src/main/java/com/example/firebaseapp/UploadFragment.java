package com.example.firebaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class UploadFragment extends Fragment {

    private ArrayList<Uri> arrayList;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ProgressBar uploadProgressBar;
    private TextView textView;
    private MaterialButton select, upload;
    private TextView progressText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar);
        FirebaseApp.initializeApp(requireContext());

        arrayList = new ArrayList<>();

        select = view.findViewById(R.id.selectImages);
        upload = view.findViewById(R.id.uploadImages);
        textView = view.findViewById(R.id.selectedTv);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == AppCompatActivity.RESULT_OK) {
                arrayList.clear();
                if (o.getData() != null && o.getData().getClipData() != null) {
                    int count = o.getData().getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = o.getData().getClipData().getItemAt(i).getUri();
                        arrayList.add(imageUri);
                    }
                    if (arrayList.size() >= 1) {
                        upload.setEnabled(true);
                        textView.setText(MessageFormat.format("{0} Images selected.", arrayList.size()));
                    }
                }
            }
        });

        select.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        upload.setOnClickListener(v -> {
            upload.setText("Uploading images...");
            upload.setEnabled(false);

            // Show the progress bar
            uploadProgressBar.setVisibility(View.VISIBLE);

            uploadImages(new ArrayList<>(), arrayList);
        });
    }
    private long getFileSize(Uri uri) {
        long fileSize = 0;
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                fileSize = inputStream.available(); // Lấy kích thước của tệp
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    private void uploadImages(@NonNull ArrayList<String> imagesUrl, ArrayList<Uri> imageUriList) {
        // Calculate total file size in bytes
        long totalFileSizeBytes = 0;
        for (Uri uri : imageUriList) {
            totalFileSizeBytes += getFileSize(uri);
        }

        // Create a final variable to use in the lambda expression
        final long finalTotalFileSizeBytes = totalFileSizeBytes;

        // Create a variable to track the total bytes transferred
        final long[] totalBytesTransferred = {0};

        for (Uri uri : imageUriList) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(UUID.randomUUID().toString());
            storageReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Update the total bytes transferred
                        totalBytesTransferred[0] += taskSnapshot.getBytesTransferred();

                        // Calculate progress percentage
                        double progress = (100.0 * totalBytesTransferred[0]) / finalTotalFileSizeBytes;

                        // Update the progress of the progress bar
                        uploadProgressBar.setProgress((int) progress);

                        // Get the download URL of the uploaded image and add it to imagesUrl
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(downloadUri -> {
                                    imagesUrl.add(downloadUri.toString());

                                    // Check if all images have been uploaded
                                    if (imagesUrl.size() == imageUriList.size()) {
                                        // Hide the progress bar and enable the upload button
                                        uploadProgressBar.setVisibility(View.GONE);
                                        upload.setEnabled(true);

                                        // Show upload success message
                                        Toast.makeText(requireContext(), "Images uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Hide the progress bar and enable the upload button
                        uploadProgressBar.setVisibility(View.GONE);
                        upload.setEnabled(true);

                        // Show upload failure message
                        Toast.makeText(requireContext(), "Failed to upload images", Toast.LENGTH_SHORT).show();
                    });
        }
    }


}

