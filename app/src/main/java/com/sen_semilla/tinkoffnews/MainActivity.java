package com.sen_semilla.tinkoffnews;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sen_semilla.tinkoffnews.adapters.MiniArticlesRecyclerAdapter;
import com.sen_semilla.tinkoffnews.entities.MiniArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MiniArticlesRecyclerAdapter miniArticlesRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.BLACK);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        new TaskGetTinkoffNews().execute();
    }

    @Override
    public void onRefresh() {
        new TaskGetTinkoffNews().execute();
    }

    private class TaskGetTinkoffNews extends AsyncTask<Void, String, List<MiniArticle>> {

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        String errorMessage = null;

        @Override
        protected void onProgressUpdate(String... params){
            super.onProgressUpdate(params);
            Toast.makeText(getApplicationContext(), params[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<MiniArticle> doInBackground(Void... params) {
            URL url = null;
            List <MiniArticle> miniArticleList = null;
            try {
                url = new URL("https://api.tinkoff.ru/v1/news");
                System.setProperty("http.agent", "gzip");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Encoding", "gzip");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.setChunkedStreamingMode(1);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(gzipInputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
                ObjectMapper objectMapper = new ObjectMapper();
                JSONObject jsonObject = new JSONObject(resultJson);
                JSONArray jsonArray = jsonObject.getJSONArray("payload");
                miniArticleList = Arrays.asList(objectMapper.readValue(jsonArray.toString(), MiniArticle[].class));
                Collections.sort(miniArticleList, new Comparator<MiniArticle>() {
                    @Override
                    public int compare(MiniArticle o1, MiniArticle o2) {
                        return (int) Math.ceil((o2.getPublicationDate().getMilliseconds() - o1.getPublicationDate().getMilliseconds()) / 1000);
                    }
                });
                FileOutputStream fos = getApplicationContext().openFileOutput("miniArticles", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(miniArticleList);
                os.close();
                fos.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }  catch (JsonMappingException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.json_mapping_exception);
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.connection_exception) + url.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.json_exception);
            } finally {
                if (errorMessage != null){
                    publishProgress(errorMessage);
                }
                urlConnection.disconnect();
            }
            return miniArticleList;
        }

        @Override
        protected void onPostExecute(List <MiniArticle> miniArticles) {
            super.onPostExecute(miniArticles);
            swipeRefreshLayout.setRefreshing(false);
            try {
                if (miniArticles==null){
                    FileInputStream fis = getApplicationContext().openFileInput("miniArticles");
                    if (fis!=null) {
                        ObjectInputStream is = new ObjectInputStream(fis);
                        System.out.println(is.toString());
                        miniArticles = (List<MiniArticle>) is.readObject();
                        Log.d("OLOLO", String.valueOf(miniArticles.size()));
                        is.close();
                        fis.close();
                    }
                }
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                miniArticlesRecyclerAdapter = new MiniArticlesRecyclerAdapter(miniArticles, MainActivity.this);
                recyclerView.setAdapter(miniArticlesRecyclerAdapter);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.cache_exception), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.io_exception), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
