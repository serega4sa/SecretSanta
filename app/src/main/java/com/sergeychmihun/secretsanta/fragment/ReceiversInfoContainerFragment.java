package com.sergeychmihun.secretsanta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sergeychmihun.secretsanta.R;

public class ReceiversInfoContainerFragment extends Fragment{

    public final static String TAG = "ReceiversContainerFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receivers_container, container, false);
    }
}
