package com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.InviteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.NoteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote.ROOT_NOTE;

public class MyNoteFragment extends Fragment {

    private final Activity context = new NotePad();
    private View view;
    private TextView notice;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<NoteModel> modelList = new ArrayList<>();
    private NoteAdapter adapter;

    public MyNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        modelList.clear();
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_note, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        notice = view.findViewById(R.id.notice);
        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
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
            DatabaseReference reference;

            String user = auth.getUid();
            reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(user);
            //reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NoteModel model = snapshot.getValue(NoteModel.class);
                        modelList.add(model);
                    }
                    if (modelList.isEmpty()) {
                        notice.setVisibility(View.VISIBLE);
                        dialog.dismiss();

                    } else {
                        notice.setVisibility(View.GONE);
                        adapter = new NoteAdapter(context, modelList);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            ToastLong(context, e.getMessage());
        }
    }

}