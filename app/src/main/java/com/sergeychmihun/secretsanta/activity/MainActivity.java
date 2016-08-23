package com.sergeychmihun.secretsanta.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.sergeychmihun.secretsanta.R;
import com.sergeychmihun.secretsanta.fragment.ReceiverInfoFragment;
import com.sergeychmihun.secretsanta.fragment.ReceiversInfoContainerFragment;
import com.sergeychmihun.secretsanta.fragment.StartFragment;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MainActivity extends FragmentActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ReceiversInfoContainerFragment receiversContainerFragment;
    private ImageView imgDone;
    private ProgressBar progressBar;
    private int numberOfMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiversContainerFragment = new ReceiversInfoContainerFragment();
        manager = getSupportFragmentManager();

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //imgDone = (ImageView) findViewById(R.id.imgDone);

        initFragmentStart();
    }

    private void initFragmentStart() {
        transaction = manager.beginTransaction();
        transaction.add(R.id.container, new StartFragment());
        transaction.commit();
    }

    public void onClickButtonStart(View v) {
        transaction = manager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);  /* Animation of fragment. Doesn't work on API <21 */
        transaction.replace(R.id.container, receiversContainerFragment, ReceiversInfoContainerFragment.TAG);
        transaction.add(R.id.mainContainer, new ReceiverInfoFragment());
        transaction.commit();
    }

    public void onClickAddMember(View v) {
        numberOfMembers++;
        transaction = manager.beginTransaction();
        transaction.add(R.id.mainContainer, new ReceiverInfoFragment());
        transaction.commit();
    }

    /*public void sendEmail(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"serega4sa@yandex.ru"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Your Secret Santa");
        i.putExtra(Intent.EXTRA_TEXT   , "Your Secret Santa is Dima");
        try {
            startActivity(i);
            //startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void onSendButton(View v) {
        new MyProgressAsyncTask().execute();
    }

    class MyProgressAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Beginning of the process", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //imgDone.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Process successfully finished", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //progressBar.setProgress(values[0]);
            //txtState.setText(values[0] + " %");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*while (progressBarValue < 100) {
                progressBarValue++;
                publishProgress(progressBarValue);
                SystemClock.sleep(200);
            }*/

            try {
                sendEmail();
            } catch (MessagingException e) {
                e.printStackTrace();
            }


            return null;
        }

        public void sendEmail() throws MessagingException {
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
        }
    }
}
