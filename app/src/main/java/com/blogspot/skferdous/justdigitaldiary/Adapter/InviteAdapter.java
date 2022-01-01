package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ShareNoteView;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.ContactsContract.CommonDataKinds.Note.NOTE;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.NOTE_ROOT;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private Context context;
    private List<InvitedModel> invitedModels;

    public InviteAdapter(Context context, List<InvitedModel> invitedModels) {
        this.context = context;
        this.invitedModels = invitedModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_note_list, parent, false);
        return new InviteAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InvitedModel model = invitedModels.get(position);
        holder.senderName.setText(model.getSenderName());
        int counter = 0;
        StringBuilder builder = new StringBuilder();
        String st = model.getAttendees();
        for (int i = 0; i < st.length(); i++) {
            if (st.charAt(i) == ',') {
                counter++;
            }
        }
        if (counter > 2) {
            int count = 0;
            for (int i = 0; i < st.length(); i++) {
                builder.append(st.charAt(i));
                if (st.charAt(i) == ',') {
                    count++;
                }
                if (count == 2) {
                    holder.attendees.setText("With - " + builder.substring(0, builder.length() - 1) + " and +" + (counter - count));
                    break;
                }
            }

        } else {
            holder.attendees.setText("With- " + st.replace(',', ' '));
        }
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_NODE).child(model.getSenderId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(snapshot.getKey()).equals(model.getNoteId())) {
                            NoteModel model1 = snapshot.getValue(NoteModel.class);

                            if (model1 != null) {
                                if (model1.getTitle().length() > 20) {
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 0; i < 20; i++) {
                                        builder.append(model1.getTitle().charAt(i));
                                    }
                                    holder.noteTitle.setText(builder.toString() + "...");
                                } else {
                                    holder.noteTitle.setText(model1.getTitle());
                                }
                                holder.dateTime.setText(model1.getDate() + ", " + model1.getTime());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(context, databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            ToastLong(context, e.getMessage());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ShareNoteView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("inNoteId", model.getNoteId());
            intent.putExtra("inUserId", model.getSenderId());
            intent.putExtra("inPermission", model.getPermission());
            intent.putExtra("inSenderName", model.getSenderName());
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            v.getContext().startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return invitedModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, senderName, attendees, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.noteTitle);
            senderName = itemView.findViewById(R.id.senderName);
            attendees = itemView.findViewById(R.id.attendees);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}
