package ru.vasichevda.sbertexjunior;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static ru.vasichevda.sbertexjunior.MainActivity.articlesArray;

class ArticlesAdapterRecycler extends RecyclerView.Adapter<ArticlesAdapterRecycler.ViewHolder> {

    /**
     * Список статей/книг для отображения
     */
    private ArrayList<Article> listItems;
    /**
     * Свойство - корзина - определение режима отображения полный/корзина
     */
    private Boolean isBasket;
    /**
     * Контекст - предоставляет доступ к базовым функциям приложения
     */
    private final Context ctx;

    /**
     * Конструктор
     *
     * @param listItems - список для отображения
     * @param isBasket  - режим общий/корзина
     * @param ctx       - контекст
     */
    ArticlesAdapterRecycler(ArrayList<Article> listItems, Boolean isBasket, Context ctx) {
        this.listItems = listItems;
        this.isBasket = isBasket;
        this.ctx = ctx;
    }

    /**
     * Создание карточки
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_article, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Заполнение карточки полями класса Article
     *
     * @param holder   - viewHolder
     * @param position - позиция в списке
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Article itemList = listItems.get(position);

        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDescription.setText(itemList.getDescription());
        holder.txtAuthor.setText(itemList.getAuthor());
        holder.txtCost.setText(itemList.getCostStr());
        if (!isBasket && (itemList.getInBasket())) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
            holder.txtOptionDigit.setVisibility(View.INVISIBLE);
        }

        /*
          Меню для карточки
         */
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) { //display option menu
                PopupMenu popupMenu = new PopupMenu(ctx, holder.txtOptionDigit);
                if (isBasket) {
                    popupMenu.inflate(R.menu.recycler_menu_option_del);
                } else {
                    popupMenu.inflate(R.menu.recycler_menu_option_add);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_add:
                                holder.itemView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
                                holder.txtOptionDigit.setVisibility(View.INVISIBLE);
                                //
                                listItems.get(position).setInBasket(true);
                                break;
                            case R.id.menu_item_del:
                                for (Article article : articlesArray) {
                                    if (listItems.get(position).equals(article)) {
                                        article.setInBasket(false);
                                    }
                                }
                                //
                                listItems.remove(position);
                                notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    /**
     * Получить размер списка
     */
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    /**
     * Инициализация карточки
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtTitle;
        final TextView txtDescription;
        final TextView txtAuthor;
        final TextView txtCost;
        //
        final Button txtOptionDigit;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtCost = itemView.findViewById(R.id.txtCost);
            //
            txtOptionDigit = itemView.findViewById(R.id.txtOptionDigit);
        }
    }

}