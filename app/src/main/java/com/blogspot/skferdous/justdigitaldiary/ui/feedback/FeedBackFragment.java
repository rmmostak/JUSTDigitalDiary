package com.blogspot.skferdous.justdigitaldiary.ui.feedback;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.FeedBackAdapter;
import com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.FeedBack;
import com.blogspot.skferdous.justdigitaldiary.Model.FeedModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;

import java.util.ArrayList;
import java.util.List;

public class FeedBackFragment extends Fragment {

    private EditText feedBack;
    private Button sendFeedBack;
    private LinearLayout adminView, userView;
    private ListView feedList;
    private List<FeedModel> feedModelList;
    private FeedBackAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feeback, container, false);

        adminView = root.findViewById(R.id.adminView);
        userView = root.findViewById(R.id.userView);

        feedList = root.findViewById(R.id.feedList);
        feedModelList = new ArrayList<>();

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            AdminModel model = sn.getValue(AdminModel.class);

                            assert model != null;
                            if (model.getId().equals(auth.getUid()) && model.getIdentifier().equals("all")) {

                                userView.setVisibility(View.GONE);
                                adminView.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FeedBack");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            FeedModel model = sn.getValue(FeedModel.class);
                            if (model != null) {
                                //Rest of codes will place here
                                feedModelList.add(model);
                                Log.d("feed", model.getFeedBack());
                            }
                        }

                    }
                    adapter = new FeedBackAdapter(getActivity(), feedModelList);
                    feedList.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        feedList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Waring!");
            builder.setIcon(R.drawable.logo);
            builder.setMessage("Are you sure to delete this feedback?");
            builder.setPositiveButton("OK", (dialog1, which) -> {
                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FeedBack");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot sn : snapshot.getChildren()) {
                                    FeedModel model = sn.getValue(FeedModel.class);
                                    if (model != null) {
                                        if (model.getId().equals(feedModelList.get(i).getId())) {
                                            reference.child(snapshot.getKey()).child(model.getId()).removeValue();
                                            //Log.d("Root", "//" + reference.child(snapshot.getKey()).child(model.getId()));//.removeValue();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i1) -> {
                return;
            });
            builder.show();

            return false;
        });

        feedBack = root.findViewById(R.id.feedBack);
        sendFeedBack = root.findViewById(R.id.sendFeedBack);
        sendFeedBack.setOnClickListener(v -> {
            String feed = feedBack.getText().toString().trim();
            if (!feed.isEmpty()) {
                try {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FeedBack").child(auth.getUid());
                    String key = reference.push().getKey();
                    FeedBack feedBack = new FeedBack(key, feed);
                    reference.child(key).setValue(feedBack).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setIcon(R.drawable.logo);
                            builder.setTitle("Alert!");
                            builder.setMessage("Thank you for sending your valuable opinion!");
                            builder.setPositiveButton("Close", (dialog, which) -> {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
                                startActivity(intent, options.toBundle());
                            }).show();
                        }
                    });
                } catch (Exception e) {
                    ToastLong(getActivity(), e.getMessage());
                }
            } else {
                feedBack.setError("Please add something!");
                feedBack.requestFocus();
                return;
            }

        });
        return root;
    }

}