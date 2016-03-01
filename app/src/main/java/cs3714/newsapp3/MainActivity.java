package cs3714.newsapp3;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListFragment.OnFragmentInteractionListener, ControlFragment.OnFragmentInteractionListener {

    public final static String INFORMATION = "information";
    private boolean started;
    private Button forward, back, startstop, lap, reset;
    private TextView timertext;
    SpinAsyncTask spinAsyncTask;
    int currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            forward = (Button) findViewById(R.id.forward);
            forward.setOnClickListener(this);
            back = (Button) findViewById(R.id.back);
            back.setOnClickListener(this);
        }

        startstop = (Button) findViewById(R.id.start_stop);
        lap = (Button) findViewById(R.id.lap);
        reset = (Button) findViewById(R.id.reset);
        timertext = (TextView) findViewById(R.id.time_text);
        spinAsyncTask = new SpinAsyncTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (spinAsyncTask != null && spinAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            outState.putBoolean("TREAD_RUNNING", true);
        } else {
            outState.putBoolean("TREAD_RUNNING", false);
        }
        outState.putInt("CURRENT_TIME", currTime);
        outState.putString("START_TEXT", startstop.getText().toString());
        outState.putString("LIST", ((ListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.listFrag)).getText());
        outState.putBoolean("STARTED", started);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        boolean isRunning = savedInstanceState.getBoolean("TREAD_RUNNING");
        if (isRunning) {
            int step = savedInstanceState.getInt("CURRENT_TIME");
            spinAsyncTask = new SpinAsyncTask();
            spinAsyncTask.execute(step);
        }

        started = savedInstanceState.getBoolean("STARTED");
        startstop.setText(savedInstanceState.getString("START_TEXT"));
        String list = savedInstanceState.getString("LIST");
        ((ListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.listFrag)).setText(list);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (spinAsyncTask != null && spinAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            spinAsyncTask.cancel(true);
            spinAsyncTask = null;
        }
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == forward.getId()) {
            findViewById(R.id.controlFrag).setVisibility(View.GONE);
            findViewById(R.id.listFrag).setVisibility(View.VISIBLE);
            forward.setVisibility(View.INVISIBLE);
            back.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.controlFrag).setVisibility(View.VISIBLE);
            findViewById(R.id.listFrag).setVisibility(View.GONE);
            forward.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onButtonClicked(int buttonID) {
        if (buttonID == 0) {
            started = !started;
            if (started) {
                startstop.setText("STOP");
                if (spinAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                    spinAsyncTask.execute(0);
                }
            } else {
                startstop.setText("START");
            }
        } else if (buttonID == 1) {
            spinAsyncTask.cancel(true);
            spinAsyncTask = null;
            spinAsyncTask = new SpinAsyncTask();
            started = false;
            startstop.setText("START");
            timertext.setText("00:00:00");
            ListFragment listFragment =
                    (ListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.listFrag);
            if (listFragment != null && listFragment.isInLayout()) {
                listFragment.clearText();
            } else {
                Intent intent = new Intent(this, InformationActivity.class);
                intent.putExtra(INFORMATION, "");
            }
        } else if (buttonID == 2) {
            ListFragment listFragment =
                    (ListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.listFrag);
            if (listFragment != null && listFragment.isInLayout()) {
                String txt = timertext.getText().toString() + "\n";
                listFragment.setText(txt);
            } else {
                Intent intent = new Intent(this, InformationActivity.class);
                String txt = timertext.getText().toString() + "\n";
                intent.putExtra(INFORMATION, txt);
                listFragment.setText(txt);
            }
        }


    }

    private class SpinAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int currTime = integers[0];
            try {
                while (true) {
                    if (started) {
                        Thread.sleep(1000);
                        currTime++;
                        int hr, min, sec, r;
                        hr = currTime / 3600;
                        r = currTime % 3600;
                        min = r / 60;
                        sec = r % 60;
                        publishProgress(
                                currTime,
                                hr,
                                min,
                                sec
                        );
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            currTime = values[0];
            String txt = String.format("%02d:%02d:%02d", values[1], values[2], values[3]);
            timertext.setText(txt);
            super.onProgressUpdate(values);
        }
    }
}
