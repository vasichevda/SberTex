package ru.vasichevda.sbertexjunior;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static ru.vasichevda.sbertexjunior.MainActivity.articlesArray;


class NewsApiAsyncTask extends AsyncTask<Void, Void, String> {

    /**
     * Поле - ресурс - ресурся для взятия статей/книг - TechCrunch / HackerNews / Recode
     */
    private final String newsResource;
    /**
     * Контекст - предоставляет доступ к базовым функциям приложения
     */
    private final Context ctx;
    /**
     * RecyclerView - View с карточками
     */
    private RecyclerView recyclerViewArticles;
    //
    /**
     * ProgressDialog - View, чтобы показать прогресс выполнения  запроса к newsapi
     */
    private ProgressDialog pDlg;

    /**
     * Конструктор
     *
     * @param newsResource         - ресурс новостей
     * @param recyclerViewArticles - View для карточек
     * @param ctx                  - контекст
     */
    NewsApiAsyncTask(String newsResource, RecyclerView recyclerViewArticles, Context ctx) {
        this.newsResource = newsResource;
        this.ctx = ctx;
        this.recyclerViewArticles = recyclerViewArticles;
    }

    /**
     * Выполнение операций до запроса - запуск ProgressDialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDlg = new ProgressDialog(ctx);
        pDlg.setMessage(ctx.getString(R.string.dlg_execution));
        pDlg.setIndeterminate(true);//spin all time
        pDlg.setCancelable(false);//can't close
        pDlg.show();
    }

    /**
     * Выполнение запроса к newsapi
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(Void... voids) {
        //
        if (isOnline(ctx)) {
            try {
                RestTemplate restTemplate = new RestTemplate();//create a new RestTemplate instance
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());//add the String message converter
                // Make the HTTP GET request, marshaling the response to a String
                return restTemplate.getForObject(getUrl(), String.class, "Android");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Выполнение операций после запроса - парсинг вернувшегося JSONObject, заполнение списка articlesArray, заполнение recyclerViewArticles
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //
        pDlg.dismiss();
        //
        if (result != null) {
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(result);
                if ("ok".equals(jsonResponse.getString(ctx.getString(R.string.newsApiParse_status)))) {
                    //
                    JSONArray jsonResponseArticlesArray = jsonResponse.getJSONArray(ctx.getString(R.string.newsApiParse_articles));
                    //
                    for (int i = 0; i < jsonResponseArticlesArray.length(); i++) {
                        Article a = new Article();
                        a.setTitle(((JSONObject) (jsonResponseArticlesArray.get(i))).getString(ctx.getString(R.string.newsApiParse_title)));
                        a.setDescription(((JSONObject) (jsonResponseArticlesArray.get(i))).getString(ctx.getString(R.string.newsApiParse_description)));
                        a.setAuthor(((JSONObject) (jsonResponseArticlesArray.get(i))).getString(ctx.getString(R.string.newsApiParse_author)));
                        a.setCost(((JSONObject) (jsonResponseArticlesArray.get(i))).getString(ctx.getString(R.string.newsApiParse_publishedAt)));
                        articlesArray.add(a);
                    }
                    //
                    ArticlesAdapterRecycler adapterArticles = new ArticlesAdapterRecycler(articlesArray, false, ctx);//set adapter
                    recyclerViewArticles.setAdapter(adapterArticles);
                    //
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.FunnyError), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("eNewsApiAsyncTask", e.toString());
                Toast.makeText(ctx, ctx.getString(R.string.FunnyError).concat(e.toString()), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Взять ссылку для запроса к newsapi
     */
    private String getUrl() {
        return ctx.getString(R.string.newsApi_URL) +
                ctx.getString(R.string.newsApi_source) + newsResource +
                ctx.getString(R.string.newsApi_apiKey) + ctx.getString(R.string.newsApi_apiKey_value);
    }

    /**
     * Проверка интернет соединения
     */
    private boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

