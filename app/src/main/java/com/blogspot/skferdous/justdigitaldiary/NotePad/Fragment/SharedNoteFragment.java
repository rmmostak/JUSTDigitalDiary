package com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.skferdous.justdigitaldiary.Adapter.InviteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.NoteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.InvitedNotePad.NOTE_INVITED;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.InvitedNotePad.NOTE_ROOT;

public class SharedNoteFragment extends Fragment {

    private Context context;
    private View view;
    private List<InvitedModel> modelList = new ArrayList<>();
    private InviteAdapter adapter;
    private TextView notice;
    private RecyclerView recyclerView;

    public SharedNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_shared_note, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        notice = view.findViewById(R.id.notice);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        showNoteList();

        return view;
    }

    private void showNoteList() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        try {
            modelList.clear();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_INVITED).child(auth.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        InvitedModel model = snapshot.getValue(InvitedModel.class);
                        modelList.add(model);
                        //getNotes(model.getNoteId());
                    }
                    if (modelList.isEmpty()) {
                        notice.setVisibility(View.VISIBLE);
                        dialog.dismiss();

                    } else {
                        notice.setVisibility(View.GONE);
                        adapter = new InviteAdapter(context, modelList);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(context, databaseError.getMessage());
                }
            });
            dialog.dismiss();
        } catch (Exception e) {
            ToastLong(context, e.getMessage());
        }
    }

}