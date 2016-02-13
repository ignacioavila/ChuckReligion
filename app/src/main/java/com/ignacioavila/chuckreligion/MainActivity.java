package com.ignacioavila.chuckreligion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int JOKES_LOADED_MIN = 3;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Joke> listaJokes = new ArrayList<Joke>();
    private RandomJokesAsyncTask randomJokesAsyncTask;
    private JokeAdapter materialPaletteAdapter;

    public String getUrlApi(){
        String urlRes = "";
        String jokesLoadedNum = Util.getPreference(this,Util.JOKES_LOADED);

        if("".equals(jokesLoadedNum)){
            urlRes = Constants.urlApiRandom + JOKES_LOADED_MIN;
        }else{
            urlRes = Constants.urlApiRandom + jokesLoadedNum;
        }
        return urlRes + getNameAndLastNameExtension();
    }

    private String getNameAndLastNameExtension(){
        if(Util.getPreferenceBoolean(this,Util.CHECK_NAME_CUSTOM) &&
                !"".equals(Util.getPreference(this,Util.NAME_CUSTOM)) &&
                !"".equals(Util.getPreference(this,Util.LASTNAME_CUSTOM))){
            String name = Util.getPreference(this,Util.NAME_CUSTOM);
            String lastName = Util.getPreference(this,Util.LASTNAME_CUSTOM);
            return "?firstName=" + name + "&lastName=" + lastName;
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initJokes();
                    }
                });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);

        listaJokes = new ArrayList<Joke>();
        initJokes();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        materialPaletteAdapter = new JokeAdapter(listaJokes, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
               Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.JOKE,listaJokes.get(position).getJoke());
                        startActivity(intent);
            }
        });
        recyclerView.setAdapter(materialPaletteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (randomJokesAsyncTask != null) {
            randomJokesAsyncTask.cancel(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                initJokes();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initJokes() {
        if(Util.isOnline(this)) {
            RandomJokesAsyncTask tarea = new RandomJokesAsyncTask(this);
            tarea.execute(getUrlApi());
        }else{
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(findViewById(R.id.coordinator_id), R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.red))
                    .setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        }
                    })
                    .show();
        }
    }

    private class RandomJokesAsyncTask extends AsyncTask<String, Void, Boolean> {

        HttpURLConnection urlConnection;
        BufferedReader br;
        List<Joke> listaJokesResult = new ArrayList<Joke>();
        Context context;

        public RandomJokesAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream is = urlConnection.getInputStream();
                StringBuffer sb = new StringBuffer();

                br = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = br.readLine() )!= null){
                    sb.append(line);
                }
                Gson gson = new Gson();
                JokesResponse myClassObj = gson.fromJson(sb.toString().replace("&quot;",""), JokesResponse.class);
                listaJokesResult = new ArrayList<Joke>(myClassObj.getValue());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            listaJokes.clear();
            listaJokes.addAll(listaJokesResult);
            recyclerView.getAdapter().notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onCancelled() {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
