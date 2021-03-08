package com.blogspot.skferdous.justdigitaldiary.ui.feedback;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.FeedBack;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;

public class FeedBackFragment extends Fragment {

    private EditText feedBack;
    private Button sendFeedBack;

    public static FeedBackFragment newInstance() {
        return new FeedBackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.search_fragment, container, false);
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
                            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
                                    startActivity(intent, options.toBundle());
                                }
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