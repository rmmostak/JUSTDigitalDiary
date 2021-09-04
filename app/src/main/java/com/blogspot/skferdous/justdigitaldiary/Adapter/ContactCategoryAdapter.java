package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ContactCategoryAdapter extends RecyclerView.Adapter<ContactCategoryAdapter.ViewHolder> {

    List<String> cardModels;

    public ContactCategoryAdapter(List<String> cardModels) {
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public ContactCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node, parent, false);
        return new ContactCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = cardModels.get(position);
        holder.title.setText(title);
        holder.cardView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), ContactNode.class);
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("child", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("first_child", title);
            editor.commit();
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("node", title);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            v.getContext().startActivity(intent, options.toBundle());

        });
    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.contactCard);
            title = itemView.findViewById(R.id.title);
        }
    }
}
