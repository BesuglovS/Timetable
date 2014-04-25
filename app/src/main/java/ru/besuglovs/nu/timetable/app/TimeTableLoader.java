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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterInputStream;

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

        public final int BUFFER_SIZE = 10000000;

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d(MainActivity.Log_TAG, "DownloadTimeTable");
                return DownloadTimeTable();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            return "123";
        }

        private String ByteArrayToString(byte[] data) throws UnsupportedEncodingException {
            StringBuilder sb = new StringBuilder();
            final int CHUNK_SIZE = 100;
            final int CNT = data.length / CHUNK_SIZE;
            int p = 0;
            for ( int i = 0; i < CNT; ++i, p += CHUNK_SIZE )
            {
                int chuckSize = CHUNK_SIZE;
                if (p + CHUNK_SIZE > data.length)
                {
                    chuckSize = data.length - p;
                }
                final byte[] tmp = Arrays.copyOfRange(data, p, p + chuckSize);
                final String part = new String( tmp, 0, tmp.length, "UTF-8");
                sb.append(part);
            }
            return sb.toString();
        }

        private String DownloadTimeTable() throws IOException, DataFormatException {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://wiki.nayanova.edu/api.php");

            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            StringEntity params = new StringEntity("{\"Parameters\":{\"action\":\"bundle\"}}");
            post.setEntity(params);

            HttpResponse response = client.execute(post);

            InputStream inputStream = response.getEntity().getContent();
            Log.d(MainActivity.Log_TAG, "Got input stream");

            InflaterInputStream iis = new InflaterInputStream(inputStream);
            byte[] resultArray = new byte[BUFFER_SIZE];
            Integer offset = 0, bytesRead;
            while ((bytesRead = iis.read(resultArray, offset, BUFFER_SIZE - offset)) != -1)
            {
                offset += bytesRead;
            }
            Log.d(MainActivity.Log_TAG, "Read complete");

            // new String() takes forever ~ 10 min
            // String result = new String(Arrays.copyOf(resultArray, offset), "UTF-8");
            String result = ByteArrayToString(resultArray);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            FromTaskToLoader(result);
        }
    }
}
