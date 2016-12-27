package com.sergeychmihun.secretsanta.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
    private TextView statusOfProcessing;
    private int numberOfMembers = 0;
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
            adapter.notifyDataSetChanged();
        }
    }

    /** This method collects all data from fields and switches to the next page. Launches additional thread to show progress bar */
    public void onClickNext(View v) {
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
    }

    /** This method returns to the start page */
    public void onClickReturn(View v) {
        numberOfMembers = 0;
        transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new StartFragment()).addToBackStack("start fragment");
        transaction.commit();
    }

    /** This method launches app and shows first page */
    private void initFragmentStart() {
        transaction = manager.beginTransaction();
        transaction.add(R.id.container, new StartFragment()).addToBackStack("start fragment");
        transaction.commit();
    }

    /** This method creates new receiver object*/
    private void createReceiver() {
        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo.setNumber(numberOfMembers);
        receiversArray.add(receiverInfo);
    }

    /** Fill receiver objects information with data from fields */
    public void saveInputData() {
        for (ReceiverInfo item : receiversArray) {
            item.setName(item.getHolder().name.getText().toString());
            item.setEmail(item.getHolder().email.getText().toString());
        }
    }

    /** This method shows progress bar and does data processing and mails sending in background */
    class MyProgressAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            //Toast.makeText(MainActivity.this, "Beginning of the process", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            imgDeer.setVisibility(View.VISIBLE);
            imgReturnArrow.setVisibility(View.VISIBLE);
            returnBtn.setVisibility(View.VISIBLE);
            statusOfProcessing.setText("Congrats!!! \n\nAll is done. \n\nIt's high time to look for cool presents for your \nSecret Santa =)");
            //Toast.makeText(MainActivity.this, "Process successfully finished", Toast.LENGTH_SHORT).show();
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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /** Start randomizer */
            Randomizer random = new Randomizer(receiversArray);
            random.run();

           /*StringBuilder sb = new StringBuilder();

            for (Map.Entry<ReceiverInfo, ReceiverInfo> item : random.getResults().entrySet()) {
                sb.append(item.getKey().getName() + " (" + item.getKey().getEmail() + ")" + " your Secret Santa is " + item.getValue().getName() + " (" + item.getValue().getEmail() + ")\n");
            }

            test.setText(sb.toString());

            try {
                //sendEmail();
            } catch (MessagingException e) {
                e.printStackTrace();
            }*/

            return null;
        }

        /** This method performs sending of results to users - don't work silently (needs users help - solution not for us).
         * Alternative way - build client-server architecture, where data from phone will be send to server. Server will send emails */
        /*public void sendEmail() throws MessagingException {
            Log.i("check","start");

            String host = "smtp.gmail.com";
            String from = "serega4sa@gmail.com"; //sender email, this is our website email
            String pass = "777serg777"; //password of sender email

            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true"); // added this line
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            Log.i("check","done pops ");


//creating session
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            Log.i("check","done sessions ");

            InternetAddress toAddress;
            toAddress = new InternetAddress("serega4sa@yandex.ru");
            message.addRecipient(Message.RecipientType.TO, toAddress);
            Log.i("check","add recipante ");

            message.setSubject("Secret Santa");
            message.setText("This is my app");

            Log.i("check","transport");

            Transport transport = session.getTransport("smtp");

//connecting..
            Log.i("check","connecting");
            transport.connect(host, from, pass);
//sending...
            Log.i("check","wana send");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            Log.i("check","sent");
        }*/
    }
}
