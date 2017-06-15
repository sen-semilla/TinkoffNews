package com.sen_semilla.tinkoffnews;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sen_semilla.tinkoffnews.entities.Article;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class NewsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    int id;
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    TaskGetTinkoffArticle taskGetTinkoffArticle = new TaskGetTinkoffArticle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newsToolbar);
        toolbar.setTitle("Новость");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        webView = (WebView) findViewById(R.id.webView);
        id = getIntent().getIntExtra("id", 0);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.newsSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        taskGetTinkoffArticle.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        taskGetTinkoffArticle.cancel(false);
        finish();
    }

    @Override
    public void onRefresh() {
        taskGetTinkoffArticle = new TaskGetTinkoffArticle();
        taskGetTinkoffArticle.execute();
    }


    private class TaskGetTinkoffArticle extends AsyncTask<Void, String, Article> {

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
        protected Article doInBackground(Void... params) {
            URL url = null;
            Article article = null;
            try {
                url = new URL("https://api.tinkoff.ru/v1/news_content?id=" + id);
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
                JSONObject jsonObject1 = jsonObject.getJSONObject("payload");
                article = objectMapper.readValue(jsonObject1.toString(), Article.class);
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
            return article;
        }

        @Override
        protected void onPostExecute(Article article) {
            super.onPostExecute(article);
            swipeRefreshLayout.setRefreshing(false);
            if (article != null){
                webView.loadData(article.getContent(), "text/html; charset=utf-8", "UTF-8");
            }
        }
    }
}
