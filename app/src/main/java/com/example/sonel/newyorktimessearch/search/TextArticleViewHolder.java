package com.example.sonel.newyorktimessearch.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonel.newyorktimessearch.MainActivity;
import com.example.sonel.newyorktimessearch.Modele.Article;
import com.example.sonel.newyorktimessearch.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sonel on 7/26/2017.
 */

class TextArticleViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

    @Bind(R.id.txtArticleText) TextView txtArticleText;
    List<Article> articles;
    Context mContext;

    public TextArticleViewHolder(Context context, View view, List<Article> mArticles) {
        super(view);

        this.articles = mArticles;
        this.mContext = context;
        // Attach a click listener to the entire row view
        view.setOnClickListener(this);
        ButterKnife.bind(this, view);
    }

    // Handles the row being being clicked
    @Override
    public void onClick(View view) {
        int position = getLayoutPosition(); // gets item position
        Article article = articles.get(position);
        // We can access the data within the views
        Toast.makeText(mContext, "Loading article...", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(mContext, MainActivity.class);
        i.putExtra("webUrl", article.webUrl);
        mContext.startActivity(i);
    }

    public TextView getTvArticleText() {
        return txtArticleText;
    }

    public void setTxtArticleText(TextView txtArticleText) {
        this.txtArticleText = txtArticleText;
    }



}
