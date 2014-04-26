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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://wiki.nayanova.edu/api.php");

            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            StringEntity params = new StringEntity("{\"Parameters\":{\"action\":\"bundle\"}}");
            post.setEntity(params);

            HttpResponse response = client.execute(post);

            InputStream inputStream = response.getEntity().getContent();
            Log.d(MainActivity.Log_TAG, "Got input stream");

            InflaterInputStream iis = new InflaterInputStream(inputStream);
            return iis;

            /*
            byte[] resultArray = new byte[BUFFER_SIZE];
            Integer offset = 0, bytesRead;
            while ((bytesRead = iis.read(resultArray, offset, BUFFER_SIZE - offset)) != -1)
            {
                offset += bytesRead;
            }*/
            //Log.d(MainActivity.Log_TAG, "Read complete");

            /*
            FileOutputStream fos = taskContext.openFileOutput("json.tmp", Context.MODE_PRIVATE);
            fos.write(Arrays.copyOf(resultArray, offset));
            fos.close();
            */
        }

        @Override
        protected void onPostExecute(InputStream result) {
            super.onPostExecute(result);

            FromTaskToLoader(result);
        }
    }
}
