package ru.besuglovs.nu.timetable.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import ru.besuglovs.nu.timetable.app.timetable.Timetable;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<String> {

    static final int LOADER_ID = 1;
    static final String Log_TAG = "myLogTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Log_TAG, "onCreate start downloding");
        DownloadTimeTable();
    }

    private void DownloadTimeTable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Load timetable
            LoaderManager loaderManager =  getLoaderManager();
            Log.d(Log_TAG, "initLoader");
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // Нету интернета
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Loader<String> loader = null;
        if (id == LOADER_ID) {
            loader = new TimeTableLoader(this);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(Log_TAG, "Done");
        DecodeJSONTask decodeTask = new DecodeJSONTask();
        decodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        int eprst = 999;
    }

    class DecodeJSONTask extends AsyncTask<String, Void, Timetable> {

        @Override
        protected Timetable doInBackground(String... params) {
            Log.d(Log_TAG, "JSON start");

            ObjectMapper mapper = new ObjectMapper();
            Timetable result = null;
            try {
                result = mapper.readValue(params[0], Timetable.class);
            }
            catch (JsonMappingException e) {
                Log.d(Log_TAG, "JSON mapping exception: " + e.getMessage());
                e.printStackTrace();
            } catch (JsonParseException e) {
                Log.d(Log_TAG, "JSON parse exception: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(Log_TAG, "io exception: " + e.getMessage());
                e.printStackTrace();
            }

            Log.d(Log_TAG, "JSON finish");
            return result;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
