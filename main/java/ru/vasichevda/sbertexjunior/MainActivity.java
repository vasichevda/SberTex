package ru.vasichevda.sbertexjunior;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    /**
     * общий Список статей/книг
     */
    public static ArrayList<Article> articlesArray;
    //
    //Widgets
    //
    /**
     * View для отображения карточек
     */
    private RecyclerView recyclerViewArticles;

    /**
     * Главный метод программы
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //
        //assign
        ButtonBarLayout buttonBarLayoutFilter = (ButtonBarLayout) findViewById(R.id.filterButton);
        recyclerViewArticles = (RecyclerView) findViewById(R.id.articlesRecyclerView);
        recyclerViewArticles.setHasFixedSize(true);
        recyclerViewArticles.setLayoutManager(new LinearLayoutManager(this));
        //
        NewsApiAsyncTask newsApiRequest_techcrunch = new NewsApiAsyncTask(getString(R.string.newsApi_source_value_techcrunch), recyclerViewArticles, this);
        newsApiRequest_techcrunch.execute();
        NewsApiAsyncTask newsApiRequest_hackerNews = new NewsApiAsyncTask(getString(R.string.newsApi_source_value_hacker_news), recyclerViewArticles, this);
        newsApiRequest_hackerNews.execute();
        NewsApiAsyncTask newsApiRequest_recode = new NewsApiAsyncTask(getString(R.string.newsApi_source_value_recode), recyclerViewArticles, this);
        newsApiRequest_recode.execute();
        //
        articlesArray = new ArrayList<>();
        //onClick
        buttonBarLayoutFilter.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Выбор пункта меню
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        if (id == R.id.nav_goods) {
            ArticlesAdapterRecycler adapterArticles = new ArticlesAdapterRecycler(articlesArray, false, this);
            recyclerViewArticles.setAdapter(adapterArticles);
        } else if (id == R.id.nav_basket) {
            ArrayList<Article> arr = new ArrayList<>();
            for (Article a : articlesArray) {
                if (a.getInBasket()) {
                    arr.add(a);
                }
            }
            ArticlesAdapterRecycler adapterArticles = new ArticlesAdapterRecycler(arr, true, this);
            recyclerViewArticles.setAdapter(adapterArticles);
        } else if (id == R.id.nav_delivery) {
            Integer totalCost = 0;
            for (Article article : articlesArray) {
                if (article.getInBasket()) {
                    totalCost += article.getCost();
                }
            }
            if (totalCost > 0) {
                showDialogDelivery(totalCost, this);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.dlg_delivery_warning), Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Вызов фильтра
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filterButton:
                showDialogFilter(this);
                break;
        }
    }

    /**
     * Диалог для установки параметров фильтра
     */
    public void showDialogFilter(final Context ctx) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        //
        alert.setTitle(ctx.getString(R.string.dlg_filter_title));
        alert.setMessage(ctx.getString(R.string.dlg_filter_message));
        //
        final EditText editTextAuthor = new EditText(ctx);
        editTextAuthor.setHint(ctx.getString(R.string.dlg_filter_author));
        editTextAuthor.setGravity(Gravity.CENTER);
        //
        final LinearLayout linearLayoutEnterCode = new LinearLayout(ctx);
        linearLayoutEnterCode.setOrientation(LinearLayout.VERTICAL);
        linearLayoutEnterCode.setGravity(Gravity.CENTER);
        linearLayoutEnterCode.setPadding(80, 10, 100, 10);
        linearLayoutEnterCode.addView(editTextAuthor);
        alert.setView(linearLayoutEnterCode);

        alert.setPositiveButton(ctx.getString(R.string.dlg_filter_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ArrayList<Article> filterArticleArray = new ArrayList<>();
                if (editTextAuthor.getText().length() > 0) {
                    for (Article article : articlesArray) {
                        if (article.getAuthor().contains(editTextAuthor.getText().toString())) {
                            filterArticleArray.add(article);
                        }
                    }
                }
                ArticlesAdapterRecycler adapterArticles = new ArticlesAdapterRecycler(filterArticleArray, true, ctx);
                recyclerViewArticles.setAdapter(adapterArticles);
            }
        });

        alert.show();
    }

    /**
     * Диалог доставки
     */
    public void showDialogDelivery(final Integer totalCost, final Context ctx) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        //
        alert.setTitle(ctx.getString(R.string.dlg_delivery_title));
        alert.setMessage(ctx.getString(R.string.dlg_delivery_message));
        //
        final EditText editTextAddress = new EditText(ctx);
        editTextAddress.setHint(ctx.getString(R.string.dlg_delivery_address));
        editTextAddress.setGravity(Gravity.CENTER);
        //
        final TextView textViewTotalCost = new TextView(ctx);
        textViewTotalCost.setText(ctx.getString(R.string.dlg_delivery_totalCost).concat(" ").concat(String.valueOf(totalCost)).concat(" ").concat(ctx.getString(R.string.dlg_delivery_totalCostRub)));
        //
        final LinearLayout linearLayoutEnterCode = new LinearLayout(ctx);
        linearLayoutEnterCode.setOrientation(LinearLayout.VERTICAL);
        linearLayoutEnterCode.setGravity(Gravity.CENTER);
        linearLayoutEnterCode.setPadding(80, 10, 100, 10);
        linearLayoutEnterCode.addView(editTextAddress);
        linearLayoutEnterCode.addView(textViewTotalCost);
        alert.setView(linearLayoutEnterCode);

        alert.setPositiveButton(ctx.getString(R.string.dlg_delivery_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (editTextAddress.getText().length() > 0) {
                    if (totalCost % 2 == 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.dlg_delivery_accepted), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.dlg_delivery_rejected), Toast.LENGTH_LONG).show();
                    }
                    //
                    for (Article article : articlesArray) {
                        article.setInBasket(false);
                    }
                    //
                    ArticlesAdapterRecycler adapterArticles = new ArticlesAdapterRecycler(articlesArray, false, ctx);
                    recyclerViewArticles.setAdapter(adapterArticles);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.dlg_delivery_address), Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.show();
    }

}
