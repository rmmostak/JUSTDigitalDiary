package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.PortalModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.SplashScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PortalAdapter extends RecyclerView.Adapter<PortalAdapter.ViewHolder> {

    private Context context;
    private List<PortalModel> portalModelList;

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
        Runnable r = () -> {
            holder.progressBar.setVisibility(View.GONE);
        };

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
    }

    @Override
    public int getItemCount() {
        return portalModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, link;
        ImageButton go, copy;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            link = itemView.findViewById(R.id.link);
            go = itemView.findViewById(R.id.go);
            copy = itemView.findViewById(R.id.copy);
            progressBar = itemView.findViewById(R.id.imgProgress);
        }
    }
}
