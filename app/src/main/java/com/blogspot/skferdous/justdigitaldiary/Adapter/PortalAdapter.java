package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Explore.AddPortalActivity;
import com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity;
import com.blogspot.skferdous.justdigitaldiary.Explore.PortalActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.PortalModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.List;

public class PortalAdapter extends RecyclerView.Adapter<PortalAdapter.ViewHolder> {

    private final Context context;
    private final List<PortalModel> portalModelList;

    public PortalAdapter(Context context, List<PortalModel> portalModelList) {
        this.context = context;
        this.portalModelList = portalModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portal_layout, parent, false);
        return new PortalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PortalModel model = portalModelList.get(position);

        holder.progressBar.setVisibility(View.VISIBLE);
        Runnable r = () -> holder.progressBar.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(r, 2000);

        Picasso.get().load(model.getOthers()).into(holder.image);

        holder.title.setText(model.getTitle());
        holder.link.setText(model.getLink());

        holder.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(model.getTitle(), model.getLink());
            clipboard.setPrimaryClip(data);
            Toast.makeText(context, "Link Copied!", Toast.LENGTH_SHORT).show();
        });

        holder.go.setOnClickListener(v -> {
            Uri uri = Uri.parse(model.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            v.getContext().startActivity(intent);
        });

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            AdminModel model1 = sn.getValue(AdminModel.class);

                            assert model1 != null;
                            if (model1.getId().equals(auth.getUid()) && model1.getIdentifier().equals("Super Admin")) {
                                holder.portalCard.setOnLongClickListener(view -> {
                                    Intent intent = new Intent(context, AddPortalActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("id", model.getId());
                                    intent.putExtra("title", model.getTitle());
                                    intent.putExtra("desc", model.getDesc());
                                    intent.putExtra("link", model.getLink());
                                    intent.putExtra("others", model.getOthers());
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out);
                                    context.startActivity(intent, options.toBundle());

                                    return false;
                                });
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return portalModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView portalCard;
        ImageView image;
        TextView title, link;
        ImageButton go, copy;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            portalCard = itemView.findViewById(R.id.portalCard);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            link = itemView.findViewById(R.id.link);
            go = itemView.findViewById(R.id.go);
            copy = itemView.findViewById(R.id.copy);
            progressBar = itemView.findViewById(R.id.imgProgress);
        }
    }
}
