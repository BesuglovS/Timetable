package ru.besuglovs.nu.timetable.app;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by bs on 20.04.2014.
 */
public class TimeTableLoader extends Loader<String> {

    LoadTimeWIthAPI LoadingTask;

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    public TimeTableLoader(Context context) {
        super(context);
        if (LoadingTask != null)
            LoadingTask.cancel(true);
        LoadingTask = new LoadTimeWIthAPI();
        LoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void FromTaskToLoader(String result) {
        deliverResult(result);
    }

    class LoadTimeWIthAPI extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d(MainActivity.Log_TAG, "DownloadTimeTable");
                return DownloadTimeTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "123";
        }

        private String DownloadTimeTable() throws IOException {
            InputStream inputStream = null;
            int bufferLength = 1024000;

            try {
                URL url = new URL("http://wiki.nayanova.edu/api.php");
                String urlParameters = "{\"Parameters\":{\"action\":\"bundle\"}}";

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);

                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                // Starts the query
                Log.d(MainActivity.Log_TAG, "connect");
                connection.connect();

                inputStream = connection.getInputStream();

                // Convert the InputStream into a string
                return readIt(inputStream, bufferLength);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int bufferLength) throws IOException {
            Reader reader;
            reader = new InputStreamReader(stream, "utf-8");
            char[] buffer = new char[bufferLength];
            StringBuilder sb = new StringBuilder();
            Integer byteRead;


            while( (byteRead = (reader.read(buffer))) != -1 ) {
                if (byteRead == bufferLength) {
                    sb.append(buffer);
                    Log.d(MainActivity.Log_TAG, "readIt = " + sb.length());
                }
                else {
                    sb.append(Arrays.copyOf(buffer, byteRead));
                }
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            FromTaskToLoader(result);
        }

    }
}
