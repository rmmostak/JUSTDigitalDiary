package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.AdminNodeAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.SearchAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class ContactNode extends AppCompatActivity {

    RecyclerView recyclerView, searchReView;
    SearchAdapter searchAdapter;
    private RecyclerView.Adapter adapter;
    List<ChildModel> childModels;

    private DatabaseReference databaseReference;
    private List<String> keyList;
    public static String FIRST_CHILD = "";
    public boolean role = false;
    public String id = null, path = null, count = "", title = null, identifier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_node);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        searchReView = findViewById(R.id.searchReView);
        childModels = new ArrayList<>();

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
                            if ((model.getId().equals(auth.getUid()) && FIRST_CHILD.equals("Administrative Offices")) || (model.getId().equals(auth.getUid()) && FIRST_CHILD.equals("Faculty Members"))) {
                                if (model.getIdentifier().equals("Super Admin") || model.getIdentifier().equals("Editor")) {
                                    role = true;
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ContactNode.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ContactNode.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);

            FIRST_CHILD = preferences.getString("first_ref", null);
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            bar.setTitle(ContactNode.FIRST_CHILD);

            recyclerView = findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(ContactNode.this);
            recyclerView.setLayoutManager(manager);

            keyList = new ArrayList<>();
            showContactList();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showContactList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Loading, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(FIRST_CHILD);
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String ch = sn.getKey();
                            keyList.add(ch);
                        }
                        path = dataSnapshot.getRef().getPath().toString();
                        count = snapshot.getKey();
                        if (FIRST_CHILD.equals("Administrative Offices")) {

                            adapter = new AdminNodeAdapter(keyList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter = new ContactAdapter(keyList);
                            recyclerView.setAdapter(adapter);
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ContactNode.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ContactNode.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContactNode.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search People");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        findThis(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //findThis(newText);
        return true;
    }

    private void findThis(String newText) {

    keyList.clear();
        keyList.add(newText);

        adapter = new ContactAdapter(keyList);
        recyclerView.setAdapter(adapter);
        newText = newText.toLowerCase();

        if (newText.length() > 0 && newText != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore");
            Query query = reference.orderByChild("topic").startAt(newText).endAt(newText + "\uf8ff");
            String finalNewText = newText;
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("Search", dataSnapshot.getKey());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GalleryModel model = snapshot.getValue(GalleryModel.class);
                        if (model.getTopic().toLowerCase().contains(finalNewText)) {
                            Log.d("gallery", model.getTopic());
                            keyList.clear();
                            keyList.add(model.getTopic());
                        }
                    }
                    adapter=new ContactAdapter(keyList);
                    recyclerView.setAdapter(adapter);
                    while (dataSnapshot.getChildren().iterator().hasNext()) {
                    GalleryModel model=dataSnapshot.getValue(GalleryModel.class);
                        Log.d("topic", dataSnapshot.getChildren().iterator().next().getChildren().iterator().next().getRef().getRoot().toString());
                        if (model.getTopic().toLowerCase().contains(finalNewText)) {

                    }
                    if (model.getTopic().toLowerCase().contains(finalNewText)) {
                        Log.d("Search", model.getTopic());
                    }
                        if (dataSnapshot.child("topic").toString().toLowerCase().contains(finalNewText)) {
                            Log.d("topic", dataSnapshot.getRef().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(getApplicationContext(), databaseError.getMessage());
                }
            });
        }
    }*/

    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_edit, menu);

        mMenu = menu;

        mMenu.findItem(R.id.adminEdit).setVisible(role);

        MenuItem searchItem = mMenu.findItem(R.id.searchItem);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search People");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerView.setVisibility(View.GONE);
                searchReView.setVisibility(View.VISIBLE);
                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Updated").child("JUST Digital Diary").child(FIRST_CHILD);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            childModels.clear();
                            for (DataSnapshot sn0001 : dataSnapshot.getChildren()) {
                                for (DataSnapshot sn001 : sn0001.getChildren()) {
                                    for (DataSnapshot sn01 : sn001.getChildren()) {
                                        for (DataSnapshot snapshot : sn01.getChildren()) {
                                            for (DataSnapshot required : snapshot.getChildren()) {
                                                ChildModel model = required.getValue(ChildModel.class);
                                                if (model != null) {
                                                    if (model.getName().toLowerCase().contains(s.toLowerCase())) {
                                                        childModels.add(model);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            searchAdapter = new SearchAdapter(ContactNode.this, childModels);
                            RecyclerView.LayoutManager manager = new LinearLayoutManager(ContactNode.this);
                            searchReView.setLayoutManager(manager);
                            searchReView.setAdapter(searchAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ContactNode.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(ContactNode.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            searchReView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return false;
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.adminEdit) {
            if (FIRST_CHILD.equals("Administrative Offices")) {
                addAdminNode();
            } else {
                addFacultyNode();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void addFacultyNode() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_node, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Add Faculty");
        dialogBuilder.setIcon(R.drawable.logo);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        EditText nodeName = dialogView.findViewById(R.id.nodeName);
        Button nodeNext = dialogView.findViewById(R.id.nodeNext);
        Button nodeCancel = dialogView.findViewById(R.id.nodeCancel);
        TextView hint = dialogView.findViewById(R.id.hint);
        hint.setText("Please enter node name like 'Faculty of Engineering and Technology'");

        nodeCancel.setOnClickListener(view -> alertDialog.dismiss());
        nodeNext.setOnClickListener(v -> {
            String node = nodeName.getText().toString().trim();
            if (!TextUtils.isEmpty(node) && node.toLowerCase().startsWith("faculty of")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inf = getLayoutInflater();
                View view = inf.inflate(R.layout.add_dept, null);
                builder.setView(view);

                builder.setTitle("Add Department");
                builder.setIcon(R.drawable.logo);

                AlertDialog dialog = builder.create();
                dialog.show();

                EditText deptName = view.findViewById(R.id.deptName);
                Button deptNext = view.findViewById(R.id.deptNext);
                Button deptCancel = view.findViewById(R.id.deptCancel);
                TextView deptHint = view.findViewById(R.id.deptHint);
                deptHint.setText("Please enter department name in this pattern, 'XYZ Dept'");

                deptCancel.setOnClickListener(view1 -> dialog.dismiss());
                deptNext.setOnClickListener(view1 -> {
                    String deptSt = deptName.getText().toString().trim();
                    if (deptSt.isEmpty()) {
                        deptName.setError("Please enter department name!");
                        deptName.requestFocus();
                        return;
                    } else {
                        if (deptSt.endsWith("Dept")) {
                            AlertDialog.Builder blder = new AlertDialog.Builder(this);
                            LayoutInflater inflater1 = getLayoutInflater();
                            View view2 = inflater1.inflate(R.layout.child_update_layout, null);
                            blder.setView(view2);

                            blder.setTitle("Add Teacher");
                            blder.setIcon(R.drawable.logo);

                            EditText name = view2.findViewById(R.id.name);
                            EditText desg = view2.findViewById(R.id.designation);
                            EditText email = view2.findViewById(R.id.email);
                            EditText phoneHome = view2.findViewById(R.id.phoneHome);
                            EditText phonePer = view2.findViewById(R.id.phonePer);
                            EditText pbx = view2.findViewById(R.id.pbx);
                            EditText others = view2.findViewById(R.id.others);
                            Button update = view2.findViewById(R.id.update);
                            Button delete = view2.findViewById(R.id.delete);
                            Button cancel = view2.findViewById(R.id.cancel);

                            delete.setVisibility(View.GONE);
                            update.setText("Add");

                            AlertDialog dLog = blder.create();
                            dLog.show();

                            cancel.setOnClickListener(view3 -> dLog.dismiss());
                            update.setOnClickListener(view3 -> {
                                String nameSt = name.getText().toString().trim();
                                String desgSt = desg.getText().toString().trim();
                                String emailSt = email.getText().toString().trim();
                                String phoneHomeSt = phoneHome.getText().toString().trim();
                                String phonePerSt = phonePer.getText().toString().trim();
                                String pbxSt = pbx.getText().toString().trim();
                                String othersSt = others.getText().toString().trim();

                                if (TextUtils.isEmpty(nameSt)) {
                                    name.setError("Please enter name!");
                                    name.requestFocus();
                                    return;
                                }
                                if (TextUtils.isEmpty(desgSt)) {
                                    desg.setError("Please enter designation!");
                                    desg.requestFocus();
                                    return;
                                }
                                if (emailSt.isEmpty()) {
                                    emailSt = "null";
                                }
                                if (phoneHomeSt.isEmpty()) {
                                    phoneHomeSt = "null";
                                }
                                if (phonePerSt.isEmpty()) {
                                    phonePerSt = "null";
                                }
                                if (pbxSt.isEmpty()) {
                                    pbxSt = "null";
                                }
                                if (othersSt.isEmpty()) {
                                    othersSt = "null";
                                }

                                int id = Integer.parseInt(count) + 1;
                                StringBuilder pre = new StringBuilder();
                                if (count.length() > String.valueOf(id).length()) {
                                    for (int i = 0; i < (count.length() - String.valueOf(id).length()); i++) {
                                        pre.append('0');
                                    }
                                }
                                count = pre.append(id).toString();

                                String child = path + "/" + count + "/" + node + "/001/" + deptSt;
                                ChildModel model = new ChildModel("01", nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);
                                //Log.d("ref", child);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(child).child("01");
                                reference.setValue(model).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(ContactNode.this, ContactNode.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                        startActivity(intent, options.toBundle());
                                    } else {
                                        Log.d("task", task.getException().getMessage());
                                    }
                                });
                                dLog.dismiss();
                                alertDialog.dismiss();
                            });
                        } else {
                            deptName.setError("Please enter department name in given pattern!");
                            deptName.requestFocus();
                        }
                    }
                });
            } else {
                nodeName.setError("Please enter a name starts with 'Faculty of' keyword!");
                nodeName.requestFocus();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void addAdminNode() {
        Log.d("tag", "path=" + path + "\tcount=" + count);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_node, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Add Node");
        dialogBuilder.setIcon(R.drawable.logo);

        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        EditText nodeName = dialogView.findViewById(R.id.nodeName);
        Button nodeNext = dialogView.findViewById(R.id.nodeNext);
        Button nodeCancel = dialogView.findViewById(R.id.nodeCancel);
        TextView hint = dialogView.findViewById(R.id.hint);
        hint.setText("Please enter node name like 'Offices, Cell'");

        nodeCancel.setOnClickListener(view -> alertDialog.dismiss());
        nodeNext.setOnClickListener(v -> {
            String node = nodeName.getText().toString().trim();
            if (!TextUtils.isEmpty(node)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inf = getLayoutInflater();
                View view = inf.inflate(R.layout.add_category, null);
                builder.setView(view);

                builder.setTitle("Add Category");
                builder.setIcon(R.drawable.logo);

                AlertDialog dialog = builder.create();
                dialog.show();

                EditText catName = view.findViewById(R.id.catName);
                Button catNext = view.findViewById(R.id.catNext);
                Button catCancel = view.findViewById(R.id.catCancel);

                catCancel.setOnClickListener(view1 -> dialog.dismiss());

                catNext.setOnClickListener(view1 -> {
                    String catSt = catName.getText().toString().trim();
                    if (catSt.isEmpty()) {
                        catName.setError("Please enter category name!");
                        catName.requestFocus();
                    } else {
                        AlertDialog.Builder blder = new AlertDialog.Builder(this);
                        LayoutInflater inflater1 = getLayoutInflater();
                        View view2 = inflater1.inflate(R.layout.child_update_layout, null);
                        blder.setView(view2);

                        blder.setTitle("Add Person Information");
                        blder.setIcon(R.drawable.logo);

                        EditText name = view2.findViewById(R.id.name);
                        EditText desg = view2.findViewById(R.id.designation);
                        EditText email = view2.findViewById(R.id.email);
                        EditText phoneHome = view2.findViewById(R.id.phoneHome);
                        EditText phonePer = view2.findViewById(R.id.phonePer);
                        EditText pbx = view2.findViewById(R.id.pbx);
                        EditText others = view2.findViewById(R.id.others);
                        Button update = view2.findViewById(R.id.update);
                        Button delete = view2.findViewById(R.id.delete);
                        Button cancel = view2.findViewById(R.id.cancel);

                        delete.setVisibility(View.GONE);

                        AlertDialog dLog = blder.create();
                        dLog.show();

                        cancel.setOnClickListener(view3 -> dLog.dismiss());
                        update.setOnClickListener(view3 -> {
                            String nameSt = name.getText().toString().trim();
                            String desgSt = desg.getText().toString().trim();
                            String emailSt = email.getText().toString().trim();
                            String phoneHomeSt = phoneHome.getText().toString().trim();
                            String phonePerSt = phonePer.getText().toString().trim();
                            String pbxSt = pbx.getText().toString().trim();
                            String othersSt = others.getText().toString().trim();

                            if (TextUtils.isEmpty(nameSt)) {
                                name.setError("Please enter name!");
                                name.requestFocus();
                                return;
                            }
                            if (TextUtils.isEmpty(desgSt)) {
                                desg.setError("Please enter designation!");
                                desg.requestFocus();
                                return;
                            }
                            if (emailSt.isEmpty()) {
                                emailSt = "null";
                            }
                            if (phoneHomeSt.isEmpty()) {
                                phoneHomeSt = "null";
                            }
                            if (phonePerSt.isEmpty()) {
                                phonePerSt = "null";
                            }
                            if (pbxSt.isEmpty()) {
                                pbxSt = "null";
                            }
                            if (othersSt.isEmpty()) {
                                othersSt = "null";
                            }

                            //Log.d("tag", count + "\t" + node + "\t" + catSt + "\t" + nameSt);

                            int id = Integer.parseInt(count) + 1;
                            StringBuilder pre = new StringBuilder();
                            if (count.length() > String.valueOf(id).length()) {
                                for (int i = 0; i < (count.length() - String.valueOf(id).length()); i++) {
                                    pre.append('0');
                                }
                            }
                            count = pre.append(id).toString();

                            String child = path + "/" + count + "/" + node + "/001/" + catSt;
                            ChildModel model = new ChildModel("01", nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);
                            //Log.d("ref", child);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(child).child("01");
                            reference.setValue(model).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(ContactNode.this, ContactNode.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                    startActivity(intent, options.toBundle());
                                } else {
                                    Log.d("task", task.getException().getMessage());
                                }
                            });
                            dLog.dismiss();
                            alertDialog.dismiss();
                        });
                    }
                });
            } else {
                nodeName.setError("Please enter a valid name!");
                nodeName.requestFocus();
            }
        });
    }

}