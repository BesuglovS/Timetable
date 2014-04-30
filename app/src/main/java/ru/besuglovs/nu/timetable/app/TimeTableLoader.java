package ru.besuglovs.nu.timetable.app;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterInputStream;

/**
 * Created by bs on 20.04.2014.
 */
public class TimeTableLoader extends Loader<InputStream> {

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
        LoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.getContext());
    }

    private void FromTaskToLoader(InputStream result) {
        deliverResult(result);
    }

    class LoadTimeWIthAPI extends AsyncTask<Context, Void, InputStream> {

        public final int BUFFER_SIZE = 10000000;

        Context taskContext = null;

        @Override
        protected InputStream doInBackground(Context... params) {
            try {
                Log.d(MainActivity.Log_TAG, "DownloadTimeTable");
                taskContext = params[0];
                return DownloadTimeTable();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            return null;
        }

        private InputStream DownloadTimeTable() throws IOException, DataFormatException {
            int bufferLength = 10240000;

            URL url = new URL("http://wiki.nayanova.edu/api.php");
            String urlParameters = "{\"Parameters\":{\"action\":\"bundle\"}}";

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
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

            Log.d(MainActivity.Log_TAG, "Ready to connect");
            connection.connect();

            InflaterInputStream iis = new InflaterInputStream(connection.getInputStream());

            byte[] resultArray = new byte[bufferLength];
            Integer offset = 0, bytesRead;
            while ((bytesRead = iis.read(resultArray, offset, bufferLength - offset)) != -1)
            {
                offset += bytesRead;
            }


            FileOutputStream fos = taskContext.openFileOutput("json.tmp", Context.MODE_PRIVATE);
            fos.write(Arrays.copyOf(resultArray, offset));
            fos.close();
            Log.d(MainActivity.Log_TAG, "File was written");

            return iis;
        }

        @Override
        protected void onPostExecute(InputStream result) {
            super.onPostExecute(result);

            FromTaskToLoader(result);
        }
    }
}
