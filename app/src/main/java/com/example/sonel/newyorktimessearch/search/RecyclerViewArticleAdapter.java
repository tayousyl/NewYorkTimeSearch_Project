package com.example.sonel.newyorktimessearch.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.sonel.newyorktimessearch.Modele.Article;
import com.example.sonel.newyorktimessearch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonel on 7/26/2017.
 */

public class RecyclerViewArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // The items to display in your RecyclerView
    private static List<Article> articles;
    private static Context mContext;

    private final int TEXTONLY = 0, THUMBNAIL = 1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewArticleAdapter(Context context, ArrayList<Article> articles) {
        this.articles = articles;
        mContext = context;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = articles.get(position);
        if (TextUtils.isEmpty(article.getArticleThumbnailUrl())){
            return TEXTONLY;
        }
        return THUMBNAIL;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case TEXTONLY:
                View v1 = inflater.inflate(R.layout.item_templatetext,
                        viewGroup, false);
                viewHolder = new TextArticleViewHolder(mContext, v1, articles);
                break;
            case THUMBNAIL:
                View v2 = inflater.inflate(R.layout.item_template,
                        viewGroup, false);
                viewHolder = new ThumbnailArticleViewHolder(mContext, v2, articles);
                break;
            default:
                v2 = inflater.inflate(R.layout.item_template,
                        viewGroup, false);
                viewHolder = new ThumbnailArticleViewHolder(mContext, v2, articles);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case TEXTONLY:
                TextArticleViewHolder vh1 = (TextArticleViewHolder) viewHolder;
                configureTextArticleViewHolder(vh1, position);
                break;
            case THUMBNAIL:
                ThumbnailArticleViewHolder vh2 = (ThumbnailArticleViewHolder) viewHolder;
                configureThumbnailArticleViewHolder(vh2, position);
                break;
            default:
                ThumbnailArticleViewHolder vh = (ThumbnailArticleViewHolder) viewHolder;
                configureThumbnailArticleViewHolder(vh, position);
                break;
        }
    }

    private void configureTextArticleViewHolder(TextArticleViewHolder viewHolder, int position) {
        Article article = articles.get(position);
        viewHolder.txtArticleText.setText(article.getHeadline().getMain());
    }

    private void configureThumbnailArticleViewHolder(ThumbnailArticleViewHolder viewHolder,
                                                     int position) {
        Article article = articles.get(position);

        viewHolder.imArticle.setImageResource(0); //clearoff
        if (!TextUtils.isEmpty(article.getArticleThumbnailUrl())) {
            Glide.with(mContext).load(article.getArticleThumbnailUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(viewHolder.imArticle);
        }
        viewHolder.txtArticle.setText(article.getHeadline().getMain());
    }

}
