package com.example.projectfirebase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectfirebase.databinding.FragmentUploadBinding;


public class UploadFragment extends Fragment {

    FragmentUploadBinding mFragmentUploadBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentUploadBinding = FragmentUploadBinding.inflate(inflater, container, false);

        return mFragmentUploadBinding.getRoot();
    }
}