package com.example.projectfirebase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< Updated upstream
=======
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        List<ImageDownload> listImageDownload = new ArrayList<>();


        List<String> urls = new ArrayList<>();
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Lấy thông tin về tệp tin
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // uri chứa đường dẫn đến tệp tin ảnh
                                    String imageUrl = uri.toString();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Xử lý khi không thể lấy được đường dẫn tải xuống
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    adapter = new ImageAdapter(MainActivity.this,urls);
                                    binding.recyclerView.setAdapter(adapter);
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                }
                            });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Xử lý khi không thể lấy được danh sách tệp tin
                    }
                });

        Log.e("URLS", urls.toString());
>>>>>>> Stashed changes
    }
}