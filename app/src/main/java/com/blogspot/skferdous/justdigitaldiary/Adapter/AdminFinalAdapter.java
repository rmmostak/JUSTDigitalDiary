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

import com.blogspot.skferdous.justdigitaldiary.Contact.ContactListActivity;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.SecondaryActivity.ADMIN_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class AdminFinalAdapter extends RecyclerView.Adapter<AdminFinalAdapter.ViewHolder> {

    Context context;
    List<String> modelList;
    DatabaseReference reference;

    public AdminFinalAdapter(Context context, List<String> modelList) {
        this.context = context;
        this.modelList = modelList;
    }


    @NonNull
    @Override
    public AdminFinalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_node_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFinalAdapter.ViewHolder holder, int position) {
        String title = modelList.get(position);
        holder.title.setText(title);
        reference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(FIRST_CHILD);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    reference.child(snapshot.getKey()).child(ADMIN_CHILD).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                for (DataSnapshot s : sn.getChildren()) {
                                    if (s.getKey().equals(title)) {
                                        holder.totalChild.setText(s.getChildrenCount() + " Person(s)");
                                        holder.totalChild.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_profile_line, 0, 0, 0);
                                        //Log.d("count", s.getKey() + s.getChildrenCount());
                                    }
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

        holder.nodeCard.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContactListActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lastChild", title);
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("child", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_child", title);
            editor.commit();
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            v.getContext().startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, totalChild;
        private CardView nodeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            totalChild = itemView.findViewById(R.id.totalChild);
            nodeCard = itemView.findViewById(R.id.contactNodeCard);
        }
    }
}
