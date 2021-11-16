package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blogspot.skferdous.justdigitaldiary.Model.FeedModel;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

public class FeedBackAdapter extends ArrayAdapter<FeedModel> {
    private Activity context;
    private List<FeedModel> feedModelList;


    public FeedBackAdapter(Activity context, List<FeedModel> feedModelList) {
        super(context, R.layout.feed_row, feedModelList);
        this.context = context;
        this.feedModelList = feedModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.feed_row, null, true);

        TextView feedbackText = listViewItem.findViewById(R.id.feedbackText);

        FeedModel model = feedModelList.get(position);

        feedbackText.setText(model.getFeedBack());

        return listViewItem;
    }
}
