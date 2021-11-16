package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.SecondaryActivity;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity.THIRD_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.SecondaryActivity.ADMIN_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class AdminNodeAdapter extends RecyclerView.Adapter<AdminNodeAdapter.ViewHolder> {

    List<String> cardModels;
    Context context;
    private DatabaseReference reference;

    public AdminNodeAdapter(List<String> cardModels) {
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public AdminNodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_node_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNodeAdapter.ViewHolder holder, int position) {

        String title = cardModels.get(position);

        holder.title.setText(title);
        reference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(FIRST_CHILD);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    reference.child(snapshot.getKey()).child(title).addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(title)) {
                                    //Log.d("child", sn.getKey() + sn.getChildrenCount());
                                    holder.totalChild.setText(dataSnapshot.getChildrenCount() + " Sub-Categories");
                                    holder.totalChild.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_account, 0, 0, 0);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            ToastLong(context.getApplicationContext(), databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ToastLong(context.getApplicationContext(), databaseError.getMessage());
            }
        });

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SecondaryActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("secondChild", title);
            intent.putExtra("firstChild", FIRST_CHILD);
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("child", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("second_child", title);
            editor.commit();
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            v.getContext().startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title, totalChild;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.contactNodeCard);
            title = itemView.findViewById(R.id.title);
            totalChild = itemView.findViewById(R.id.totalChild);
        }
    }
}
