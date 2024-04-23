package com.example.firebaseapp;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Image> arrayList;
    private ImageAdapter adapter;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayList = new ArrayList<>();
        adapter = new ImageAdapter(getContext(), arrayList);
        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(Image image) {
                showZoomedImage(image.getUrl());
                // Handle item click here
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void showZoomedImage(String imageUrl) {
        // Tạo một Dialog để hiển thị hình ảnh phóng to
        final Dialog zoomDialog = new Dialog(requireContext());
        zoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        zoomDialog.setContentView(R.layout.dialog_zoom_image);

        // Lấy ImageView và Button trong Dialog
        ImageView zoomImageView = zoomDialog.findViewById(R.id.zoom_image_view);
        Button deleteButton = zoomDialog.findViewById(R.id.delete_button);

        // Load hình ảnh vào ImageView sử dụng thư viện Picasso hoặc Glide
        Picasso.get().load(imageUrl).into(zoomImageView);

        // Gắn sự kiện click cho nút xóa
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện xóa ảnh từ Firebase Storage
                deleteImageFromFirebase(imageUrl);

                // Đóng Dialog sau khi xóa ảnh
                zoomDialog.dismiss();
            }
        });

        // Hiển thị Dialog
        zoomDialog.show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseApp.initializeApp(requireContext());

        FirebaseStorage.getInstance().getReference().child("images").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                arrayList.clear();
                for (StorageReference item : listResult.getItems()) {
                    Image image = new Image();
                    image.setName(item.getName());
                    item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String url = "https://" + task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token=" + task.getResult().getQueryParameters("token").get(0);
                                image.setUrl(url);
                                arrayList.add(image);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(requireContext(), "Failed to retrieve images", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to retrieve images", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteImageFromFirebase(String imageUrl) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requireContext(), "Ảnh đã được xóa", Toast.LENGTH_SHORT).show();

                // Tìm vị trí của hình ảnh trong danh sách arrayList
                int position = -1;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getUrl().equals(imageUrl)) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    arrayList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xảy ra lỗi khi xóa ảnh
                Toast.makeText(requireContext(), "Lỗi: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}