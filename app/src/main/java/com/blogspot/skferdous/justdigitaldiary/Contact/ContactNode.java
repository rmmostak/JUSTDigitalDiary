package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.AdminNodeAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactCategoryAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactViewAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.Model.GalleryModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity.THIRD_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class ContactNode extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference;
    private List<String> keyList;
    public static String FIRST_CHILD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_node);

        Log.d("Activity", getLocalClassName());

        try {
            SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
            String child = preferences.getString("first_ref", null);

            FIRST_CHILD = child;
            ActionBar bar = getSupportActionBar();
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
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        for (DataSnapshot sn : snapshot.getChildren()) {
                            String ch = sn.getKey();
                            keyList.add(ch);
                        }

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


        /*keyList.clear();
        keyList.add(newText);

        adapter = new ContactAdapter(keyList);
        recyclerView.setAdapter(adapter);*/
        newText = newText.toLowerCase();

        if (newText.length() > 0 && newText != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore");
            Query query = reference.orderByChild("topic").startAt(newText).endAt(newText + "\uf8ff");
            String finalNewText = newText;
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("Search", dataSnapshot.getKey());
/*                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GalleryModel model = snapshot.getValue(GalleryModel.class);
                        if (model.getTopic().toLowerCase().contains(finalNewText)) {
                            Log.d("gallery", model.getTopic());
                            keyList.clear();
                            keyList.add(model.getTopic());
                        }
                    }
                    adapter=new ContactAdapter(keyList);
                    recyclerView.setAdapter(adapter);*/
                    while (dataSnapshot.getChildren().iterator().hasNext()) {
                    GalleryModel model=dataSnapshot.getValue(GalleryModel.class);
                        Log.d("topic", dataSnapshot.getChildren().iterator().next().getChildren().iterator().next().getRef().getRoot().toString());
                        /*if (model.getTopic().toLowerCase().contains(finalNewText)) {

                    }
                    if (model.getTopic().toLowerCase().contains(finalNewText)) {
                        Log.d("Search", model.getTopic());
                    }
                        if (dataSnapshot.child("topic").toString().toLowerCase().contains(finalNewText)) {
                            Log.d("topic", dataSnapshot.getRef().toString());
                        }*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(getApplicationContext(), databaseError.getMessage());
                }
            });
        }
    }
}