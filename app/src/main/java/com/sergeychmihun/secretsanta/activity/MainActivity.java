package com.sergeychmihun.secretsanta.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.*;
import com.sergeychmihun.secretsanta.R;
import com.sergeychmihun.secretsanta.fragment.ProcessingFragment;
import com.sergeychmihun.secretsanta.fragment.ReceiversInfoContainerFragment;
import com.sergeychmihun.secretsanta.fragment.StartFragment;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ReceiversInfoContainerFragment receiversContainerFragment;
    private ProcessingFragment processingFragment;
    private ImageView imgDeer;
    private ImageView imgReturnArrow;
    private Button returnBtn;
    private ProgressBar progressBar;
    private TextView txtInfo;
    private ListView list;
    private TextView statusOfProcessing;
    private int numberOfMembers = 0;

    public ArrayList<ReceiverInfo> getReceiversArray() {
        return receiversArray;
    }

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
        //transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);  /* Animation of fragment. Doesn't work on API <21 */
        transaction.replace(R.id.container, receiversContainerFragment, ReceiversInfoContainerFragment.TAG);
        transaction.commitNow();

        list = (ListView) findViewById(R.id.list);

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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickNext(View v) {
        if (checkInputData()) {
            saveInputData();

            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, processingFragment, ProcessingFragment.TAG);
            transaction.commitNow();

            /** Initialize all fragments on current view */
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            imgReturnArrow = (ImageView) findViewById(R.id.imgReturnArrow);
            imgDeer = (ImageView) findViewById(R.id.imgDeer);
            statusOfProcessing = (TextView) findViewById(R.id.statusOfProcessing);
            returnBtn = (Button) findViewById(R.id.returnBtn);

            new MyProgressAsyncTask().execute();
        } else {
            txtInfo = (TextView) findViewById(R.id.txtInfo);
            txtInfo.setText("Please, fill all fields");
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkInputData() {
        int counter = 0;

        for (ReceiverInfo item : receiversArray) {
            if (item.getHolder().name.getText().toString().equals("")) {
                item.getHolder().name.setBackground(shapeCreator());
            } else if (item.getHolder().email.getText().toString().equals("")) {
                item.getHolder().email.setBackground(shapeCreator());
            } else
                counter++;
        }

        if (counter == receiversArray.size()) return true;
        else return false;
    }

    /** Fill receiver objects information with data from fields */
    public void saveInputData() {
        for (ReceiverInfo item : receiversArray) {
            item.setName(item.getHolder().name.getText().toString());
            item.setEmail(item.getHolder().email.getText().toString());
        }
    }

    /** Creating border shape for NOT filled fields */
    public ShapeDrawable shapeCreator() {
        // Create a border programmatically
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
            statusOfProcessing.setText("Congrats!!! \n\nAll is done. \n\nIt's high time to look for cool presents for your \nSecret Santa =)");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /** Randomizing executes very fast. It's imitation that it takes some time */
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /** Start randomizer */
            Randomizer random = new Randomizer(receiversArray);
            random.run();

            int serverPort = 7777;
            String address = "213.110.133.187";

            try {
                InetAddress ipAddress = InetAddress.getByName(address);
                Socket socket = new Socket(ipAddress, serverPort);

                InputStream inStr = socket.getInputStream();
                OutputStream outStr = socket.getOutputStream();

                DataInputStream in = new DataInputStream(inStr);
                DataOutputStream out = new DataOutputStream(outStr);

                for (Map.Entry<ReceiverInfo, ReceiverInfo> item : random.getResults().entrySet()) {
                    String str = item.getKey().getName() + " (" + item.getKey().getEmail() + ")" + " your Secret Santa is " + item.getValue().getName() + " (" + item.getValue().getEmail() + ")";
                    out.writeUTF(str);
                    out.flush();
                    while (true) {
                        if (in.readUTF().equals("OK")) break;
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
