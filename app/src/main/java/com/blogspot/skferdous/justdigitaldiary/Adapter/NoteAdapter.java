package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Model.AuthModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    Context context;
    List<NoteModel> modelList;
    List<String> nameString = new ArrayList<>();
    //StringBuilder nameString = new StringBuilder();
    StringBuilder uBuilder = new StringBuilder();
    String emailCheck = null;

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
        holder.body.setText(model.getBody());
        /*int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {*/
        if (model.getTitle().length() > 20) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                builder.append(model.getTitle().charAt(i));
            }
            holder.title.setText(builder.toString() + "...");
        } else {
            holder.title.setText(model.getTitle());
        }

        int count = 0;
        if (!model.getAttendees().equals("null")) {
            String check = model.getAttendees();
            for (int i = 0; i < check.length(); i++) {
                uBuilder.append(check.charAt(i));
                if (check.charAt(i) == ',') {
                    count++;
                }
            }
            holder.attendeeNo.setText(String.valueOf(count));

        } else if (model.getAttendees().equals("null")) {
            holder.attendeeNo.setVisibility(View.GONE);
            nameString.clear();
            uBuilder.delete(0, uBuilder.length());
        }

        int finalCount = count;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewNote.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("noteId", model.getId());
            intent.putExtra("attendees", nameString.toString());
            intent.putExtra("member", model.getAttendees());
            intent.putExtra("count", finalCount);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            v.getContext().startActivity(intent, options.toBundle());
        });
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, date, title, body;
        Button attendeeNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.noteTime);
            date = itemView.findViewById(R.id.noteDate);
            title = itemView.findViewById(R.id.noteTitle);
            body = itemView.findViewById(R.id.noteBody);
            attendeeNo = itemView.findViewById(R.id.attendeeNo);
        }
    }
}
