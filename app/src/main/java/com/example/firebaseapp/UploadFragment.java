package com.example.firebaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectfirebase.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

public class UploadFragment extends Fragment {

    private ArrayList<Uri> arrayList;
    private TextView textView;
    private MaterialButton select, upload;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == getActivity().RESULT_OK) {
            handleResult(result.getData());
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        FirebaseApp.initializeApp(requireContext());

        arrayList = new ArrayList<>();

        select = view.findViewById(R.id.selectImages);
        upload = view.findViewById(R.id.uploadImages);
        textView = view.findViewById(R.id.selectedTv);

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
            uploadImages(arrayList);
        });

        return view;
    }

    private void handleResult(Intent data) {
        arrayList.clear();
        if (data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Single image selection
                arrayList.add(imageUri);
            } else {
                // Multiple image selection
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        arrayList.add(uri);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                upload.setEnabled(true);
                textView.setText(MessageFormat.format("{0} Images selected.", arrayList.size()));
            }
        }
    }

    private void uploadImages(@NonNull ArrayList<Uri> imageUriList) {
        LinearLayout progressLayout = requireView().findViewById(R.id.progressLayout);
        for (int i = 0; i < imageUriList.size(); i++) {
            Uri uri = imageUriList.get(i);
            // Create a new layout for each image (Image + ProgressBar + Pause/Continue button)
            LinearLayout imageLayout = new LinearLayout(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, dpToPx(10), 0, 0); // Add top margin to separate progress layouts
            imageLayout.setLayoutParams(layoutParams);
            imageLayout.setOrientation(LinearLayout.HORIZONTAL);

            // ImageView for the image
            // Note: You need to add an ImageView in your layout and set its ID
            ImageView imageView = new ImageView(requireContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(60)); // Convert dp to pixels
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Scale type to maintain aspect ratio
            imageView.setImageURI(uri); // Set the image to be uploaded
            imageLayout.addView(imageView);

            // ProgressBar
            ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            progressBar.setLayoutParams(progressBarParams);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBarParams.setMargins(dpToPx(10), 0, dpToPx(10), 0); // Set margins for the ProgressBar
            imageLayout.addView(progressBar);

            // TextView to display progress percentage
            TextView progressText = new TextView(requireContext());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressText.setLayoutParams(textParams);
            progressText.setText("0%");
            imageLayout.addView(progressText);

            // Pause/Continue button
            MaterialButton pauseButton = new MaterialButton(requireContext());
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pauseButton.setLayoutParams(buttonParams);
            pauseButton.setText("Pause");
            buttonParams.setMargins(dpToPx(10), 0, dpToPx(10), 0); // Set margins for the button
            imageLayout.addView(pauseButton);

            progressLayout.addView(imageLayout); // Add the imageLayout to the parent layout

            // Upload each image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(UUID.randomUUID().toString());
            UploadTask uploadTask = storageReference.putFile(uri);

            final int finalI = i; // Need to make the variable final to access inside the listener

            // Add click listener for pause button
            pauseButton.setOnClickListener(view -> {
                if (pauseButton.getText().equals("Pause")) {
                    uploadTask.pause(); // Pause the upload task
                    pauseButton.setText("Continue");
                } else {
                    uploadTask.resume(); // Continue the upload task
                    pauseButton.setText("Pause");
                }
            });

            // Add progress listener for each upload task
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                progressText.setText((int) progress + "%"); // Update progress text
            }).addOnSuccessListener(taskSnapshot -> {
                // Handle success event if needed
                Toast.makeText(requireContext(), "Image " + (finalI + 1) + " uploaded successfully!", Toast.LENGTH_SHORT).show();
                // Change the text of the pauseButton to "Done"
                pauseButton.setText("Done");
                if (finalI == imageUriList.size() - 1) { // If it's the last image
                    upload.setText("Upload Images");
                    upload.setEnabled(true);
                }
            }).addOnFailureListener(e -> {
                // Handle failure event if needed
                Toast.makeText(requireContext(), "Failed to upload image " + (finalI + 1), Toast.LENGTH_SHORT).show();
                if (finalI == imageUriList.size() - 1) { // If it's the last image
                    upload.setText("Upload Images");
                    upload.setEnabled(true);
                }
            });
        }
    }

    private String formatSize(long bytes) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f", size) + units[unitIndex];
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
