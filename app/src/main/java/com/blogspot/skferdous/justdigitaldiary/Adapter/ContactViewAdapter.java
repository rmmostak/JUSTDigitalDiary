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

import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

public class ContactViewAdapter extends ArrayAdapter<ChildModel> {

    private final Activity context;
    private final List<ChildModel> childModelList;

    public ContactViewAdapter(Activity context, List<ChildModel> childModelList) {
        super(context, R.layout.contact_list_item, childModelList);
        this.context = context;
        this.childModelList = childModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.contact_list_item, null, true);

        TextView name = listViewItem.findViewById(R.id.title);
        TextView desg = listViewItem.findViewById(R.id.totalChild);

        ChildModel model = childModelList.get(position);

        name.setText(model.getName());
        desg.setText(model.getDesignation());

        return listViewItem;
    }
}
