package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Contact.ContactListActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode;
import com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ADMIN_TAG;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.FACULTY_TAG;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    List<String> cardModels;

    public ContactAdapter(List<String> cardModels) {
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_node_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {

        String title = cardModels.get(position);
        if (title.length() > 30) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 30; i++) {
                builder.append(title.charAt(i));
            }
            holder.title.setText(builder + "...");
        } else {
            holder.title.setText(title);
        }
        holder.cardView.setOnClickListener(v -> {
            if (title.equals(ADMIN_TAG) || title.equals(FACULTY_TAG)) {
                Intent intent = new Intent(v.getContext(), ContactNode.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("node", title);
                ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
                v.getContext().startActivity(intent, options.toBundle());

            } else if (title.startsWith("Faculty of")) {

                Intent intent = new Intent(v.getContext(), DeptActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("thirdChild", title);
                ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
                v.getContext().startActivity(intent, options.toBundle());

            } else if (title.endsWith("Dept")) {

                Intent intent = new Intent(v.getContext(), FacultyListActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("finalChild", title);
                ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
                v.getContext().startActivity(intent, options.toBundle());

            } else {

                Intent intent = new Intent(v.getContext(), ContactListActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("secondChild", title);
                intent.putExtra("firstChild", FIRST_CHILD);
                ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.contactNodeCard);
            title = itemView.findViewById(R.id.title);
        }
    }
}
