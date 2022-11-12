package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private final Context mContext;
    private static final String DATE_SEPARATOR = "T";
    private static final String TIME_SEPARATOR = "Z";
    public NewsAdapter(@NonNull Context context, List<News> newsdata) {
        super(context, 0, newsdata);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate
                    (R.layout.news_list_item, parent, false);
        }

        News currentNews=getItem(position);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.newsImage);

        Glide.with(mContext)
                .load(currentNews.getmUrlToImage())
                .placeholder(R.drawable.backdrop_noimage)
                .into(imageView);



        TextView news=listItemView.findViewById(R.id.newsTitle);
        news.setText(currentNews.getWebTitle());

        TextView textViewSectionName = (TextView) listItemView.findViewById(R.id.sectionName);
        textViewSectionName.setText(currentNews.getSectionName());

        String publishedAt = currentNews.getWebPublicationDate();

        String date = "";
        String time = "";
        String actualTime = "";

        if(publishedAt.contains(DATE_SEPARATOR)){
            String[] parts = publishedAt.split(DATE_SEPARATOR);
            date = parts[0];
            time = parts[1];
        }

        if(time.contains(TIME_SEPARATOR)){
            String[] parts = time.split(TIME_SEPARATOR);
            actualTime = parts[0];
        }

        TextView textViewDate = (TextView) listItemView.findViewById(R.id.newsDate);
        textViewDate.setText(date);

        TextView textViewTime = (TextView) listItemView.findViewById(R.id.newsTime);
        textViewTime.setText(actualTime);


        return listItemView;
    }
}
