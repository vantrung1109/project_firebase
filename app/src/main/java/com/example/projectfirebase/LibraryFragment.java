package com.example.projectfirebase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectfirebase.databinding.FragmentLibraryBinding;

public class LibraryFragment extends Fragment {
    FragmentLibraryBinding mFragmentLibraryBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        return mFragmentLibraryBinding.getRoot();
    }
}