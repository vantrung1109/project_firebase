package com.example.projectfirebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class MyViewPage2Adapter extends FragmentStateAdapter {
    public MyViewPage2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new UploadFragment();
            case 1:
                return new LibraryFragment();
            default:
                return new LibraryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}