package com.sergeychmihun.secretsanta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sergeychmihun.secretsanta.R;

public class StartFragment extends Fragment{

    public final static String TAG = "StartFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        Button button = (Button) getActivity().findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, null, null);
                transaction.commit();
            }
        });
    }*/
}
