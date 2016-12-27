package com.sergeychmihun.secretsanta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sergeychmihun.secretsanta.R;

/**
 * Created by Sergey.Chmihun on 12/26/2016.
 */
public class ProcessingFragment extends Fragment{

    public final static String TAG = "ProcessingFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }
}
