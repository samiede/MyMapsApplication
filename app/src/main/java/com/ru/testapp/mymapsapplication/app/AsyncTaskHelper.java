package com.ru.testapp.mymapsapplication.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by Sami on 08.11.14.
 *
 */
public abstract class AsyncTaskHelper extends AsyncTask<String, String, String> {

    public Activity callingActivity;
    private ProgressDialog progressDialog;

    public AsyncTaskHelper(Activity context) {
        callingActivity = context;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        progressDialog = new ProgressDialog(callingActivity);
        progressDialog.setMessage("Performing Database Request...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    protected abstract void onFinished();

    protected abstract void doTask();

    protected String doInBackground(String... args) {
        doTask();
        return null;
    }

    @Override
    protected void onPostExecute(String file_url) {

        progressDialog.dismiss();
        onFinished();

/*        callingActivity.runOnUiThread(new Runnable() {
            public void run() {
                onFinished();
            }
        });*/
    }
}
