package com.sergeychmihun.secretsanta.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.*;
import com.sergeychmihun.secretsanta.R;
import com.sergeychmihun.secretsanta.fragment.ProcessingFragment;
import com.sergeychmihun.secretsanta.fragment.ReceiversInfoContainerFragment;
import com.sergeychmihun.secretsanta.fragment.StartFragment;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ReceiversInfoContainerFragment receiversContainerFragment;
    private ProcessingFragment processingFragment;
    private ImageView imgDeer;
    private ImageView imgReturnArrow;
    private Button returnBtn;
    private ProgressBar progressBar;
    //private ListView list;
    private TextView statusOfProcessing;
    private int numberOfMembers = 0;
    private boolean isOK = true;

    private ArrayList<ReceiverInfo> receiversArray;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiversContainerFragment = new ReceiversInfoContainerFragment();
        processingFragment = new ProcessingFragment();
        manager = getSupportFragmentManager();

        initFragmentStart();
    }

    /** This method perform switching to next page after clicking on the 'Start' button. By default creates 3 receiver objects (minimum requirement)*/
    public void onClickButtonStart(View v) {
        transaction = getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);  /* Animation of fragment. Doesn't work on API <21 */
        }
        transaction.replace(R.id.container, receiversContainerFragment, ReceiversInfoContainerFragment.TAG);
        transaction.commitNow();

        //list = (ListView) findViewById(R.id.list);   needed for feature that scrolls list down after adding new item

        onClickAddMember(v);
    }

    /** This method adds new field (receiver object) */
    public void onClickAddMember(View v) {
        if (numberOfMembers == 0) {
            receiversArray = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                numberOfMembers++;
                createReceiver();
            }
            ListView lv = (ListView) findViewById(R.id.list);
            adapter = new CustomListAdapter(this, receiversArray);
            lv.setAdapter(adapter);
        } else {
            numberOfMembers++;
            createReceiver();
            //list.smoothScrollToPosition(adapter.getCount() - 1);
            adapter.notifyDataSetChanged();
        }
    }

    /** This method collects all data from fields and switches to the next page. Launches additional thread to show progress bar */
    public void onClickNext(View v) {
        if (checkInputData()) {
            saveInputData();

            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, processingFragment, ProcessingFragment.TAG);
            transaction.commitNow();

            /* Initialize all fragments on current view */
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            imgReturnArrow = (ImageView) findViewById(R.id.imgReturnArrow);
            imgDeer = (ImageView) findViewById(R.id.imgDeer);
            statusOfProcessing = (TextView) findViewById(R.id.statusOfProcessing);
            returnBtn = (Button) findViewById(R.id.returnBtn);

            new MyProgressAsyncTask().execute();
        } else {
            TextView txtInfo = (TextView) findViewById(R.id.txtInfo);
            txtInfo.setText(getString(R.string.empty_fields));
            txtInfo.setTextColor(Color.RED);
        }
    }

    /** This method returns to the start page */
    public void onClickReturn(View v) {
        numberOfMembers = 0;
        transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new StartFragment());
        transaction.commit();
    }

    /** This method launches app and shows first page */
    private void initFragmentStart() {
        transaction = manager.beginTransaction();
        transaction.add(R.id.container, new StartFragment());
        transaction.commit();
    }

    /** This method creates new receiver object*/
    private void createReceiver() {
        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo.setNumber(numberOfMembers);
        receiversArray.add(receiverInfo);
    }

    /** Check that all fields are filled */
    private boolean checkInputData() {
        int counter = 0;

        for (ReceiverInfo item : receiversArray) {
            if (item.getHolder().name.getText().toString().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    item.getHolder().name.setBackground(shapeCreator());
                }
            } if (item.getHolder().email.getText().toString().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    item.getHolder().email.setBackground(shapeCreator());
                }
            } else
                counter++;
        }

        return counter == receiversArray.size();
    }

    /** Fill receiver objects information with data from fields */
    private void saveInputData() {
        for (ReceiverInfo item : receiversArray) {
            item.setName(item.getHolder().name.getText().toString());
            item.setEmail(item.getHolder().email.getText().toString());
        }
    }

    /** Creating border shape for NOT filled fields */
    private ShapeDrawable shapeCreator() {
        /* Create a border programmatically */
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.GREEN);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);

        return shape;
    }

    /** This method shows progress bar and does data processing and mails sending in background */
    class MyProgressAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            imgDeer.setVisibility(View.VISIBLE);
            imgReturnArrow.setVisibility(View.VISIBLE);
            returnBtn.setVisibility(View.VISIBLE);
            if (isOK) {
                statusOfProcessing.setText(getString(R.string.congrats));
            } else {
                statusOfProcessing.setText(getString(R.string.failed));
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                int serverPort = 7777;
                //String serverIP = "213.110.133.187";  home IP
                String serverIP = "10.0.10.73";
                InetAddress ipAddress = InetAddress.getByName(serverIP);

                Socket socket = new Socket(ipAddress, serverPort);

                InputStream inStr = socket.getInputStream();
                ObjectOutputStream outStr = new ObjectOutputStream(socket.getOutputStream());

                DataInputStream in = new DataInputStream(inStr);

                /* Send our array with receivers info until server approve receiving */
                while (true) {
                    outStr.writeObject(receiversArray);
                    outStr.flush();
                    if (in.readUTF().equals("OK")) break;
                }

                /* Wait for answer from server that emails are sent successfully */
                while (true) {
                    String answer = in.readUTF();
                    if (answer.equals("SUCCESSFUL")) {
                        break;
                    } else if (answer.equals("FAILED")) {
                        isOK = false;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
