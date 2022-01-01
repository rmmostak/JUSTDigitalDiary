package com.blogspot.skferdous.justdigitaldiary.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blogspot.skferdous.justdigitaldiary.Model.AuthModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.SuperAdminControl;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class AdminControlAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> roleList;
    private List<String> detailsList;

    public AdminControlAdapter(Context context, List<String> roleList, List<String> detailsList) {
        this.context = context;
        this.roleList = roleList;
        this.detailsList = detailsList;
    }

    @Override
    public int getGroupCount() {
        return this.roleList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return this.roleList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return detailsList.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String listText = (String) getGroup(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.admin_title, null);
        }

        TextView title = view.findViewById(R.id.adminTitle);

        title.setText(listText);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String listText = (String) getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.admin_list_details, null);
        }

        TextView role = view.findViewById(R.id.adminRole);
        TextView desc = view.findViewById(R.id.adminDesc);

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            for (DataSnapshot s : sn.getChildren()) {
                                AuthModel model = s.getValue(AuthModel.class);
                                if (model != null) {
                                    if (model.getUid().equals(listText)) {
                                        role.setText(model.getName());
                                        desc.setText(model.getDept());
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
