package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    Context context;
    List<NoteModel> modelList;

    public NoteAdapter(Context context, List<NoteModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NoteModel model = modelList.get(position);
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        holder.title.setText(model.getTitle());
        holder.body.setText(model.getBody());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewNote.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("noteId", model.getId());
            ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
            v.getContext().startActivity(intent, options.toBundle());
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, date, title, body;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.noteTime);
            date = itemView.findViewById(R.id.noteDate);
            title = itemView.findViewById(R.id.noteTitle);
            body = itemView.findViewById(R.id.noteBody);
        }
    }
}
