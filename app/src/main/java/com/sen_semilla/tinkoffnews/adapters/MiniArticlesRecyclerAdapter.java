package com.sen_semilla.tinkoffnews.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen_semilla.tinkoffnews.NewsActivity;
import com.sen_semilla.tinkoffnews.R;
import com.sen_semilla.tinkoffnews.entities.MiniArticle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MiniArticlesRecyclerAdapter extends
        RecyclerView.Adapter<MiniArticlesRecyclerAdapter.MiniArticleHolder>{

    class MiniArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewArticleName, textViewArticleType, textViewArticleCreationDate;
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        MiniArticleHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewArticleType = (TextView) itemView.findViewById(R.id.textViewArticleType);
            textViewArticleCreationDate = (TextView) itemView.findViewById(R.id.textViewArticleCreationDate);
            textViewArticleName = (TextView) itemView.findViewById(R.id.textViewArticleName);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NewsActivity.class);
            intent.putExtra("id", id);
            mContext.startActivity(intent);
        }
    }

    private Context mContext;
    private List<MiniArticle> mMiniArticleList;

    public MiniArticlesRecyclerAdapter(List<MiniArticle> miniArticleList, Context context) {
        mMiniArticleList = miniArticleList;
        mContext = context;
    }

    @Override
    public MiniArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mini_article_element, parent, false);
        return new MiniArticleHolder(view);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(MiniArticleHolder holder, int position) {
        holder.setId(mMiniArticleList.get(position).getId());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.textViewArticleName.setText(Html.fromHtml(mMiniArticleList.get(position).getText(),Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.textViewArticleName.setText(Html.fromHtml(mMiniArticleList.get(position).getText()));
        }
        holder.textViewArticleCreationDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date(mMiniArticleList.get(position).getPublicationDate().getMilliseconds())));
        if (mMiniArticleList.get(position).getBankInfoTypeId() == 1){
            holder.textViewArticleType.setText(mContext.getString(R.string.article_type_1));
        }
        else if (mMiniArticleList.get(position).getBankInfoTypeId() == 2){
            holder.textViewArticleType.setText(mContext.getString(R.string.article_type_2));
        }
        else if (mMiniArticleList.get(position).getBankInfoTypeId() == 3){
            holder.textViewArticleType.setText(mContext.getString(R.string.article_type_3));
        }
    }

    @Override
    public int getItemCount() {
        return mMiniArticleList.size();
    }

    public MiniArticle getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("Item position is out of adapter's range");
        } else {
            return mMiniArticleList.get(position);
        }
    }
}
