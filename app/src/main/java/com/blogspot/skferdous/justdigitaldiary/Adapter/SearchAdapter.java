package com.blogspot.skferdous.justdigitaldiary.Adapter;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Contact.ContactListActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<ChildModel> childModelList;

    public SearchAdapter(Context context, List<ChildModel> childModelList) {
        this.context = context;
        this.childModelList = childModelList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        ChildModel model = childModelList.get(position);

        holder.name.setText(model.getName());
        holder.desc.setText(model.getDesignation());
        holder.dept.setText(model.getOthers());

        holder.msg.setOnClickListener(v -> {
            if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                Toast.makeText(v.getContext(),  "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
            } else {
                makeMessage(model.getPhoneHome());
            }
        });

        holder.call.setOnClickListener(v -> {
            if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                Toast.makeText(v.getContext(), "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
            } else {
                makeCall(model.getPhoneHome());
            }
        });

        holder.mail.setOnClickListener(v -> {
            if (model.getEmail().isEmpty() || model.getEmail().toLowerCase().equals("null")) {
                Toast.makeText(v.getContext(), "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
            } else {
                makeMail(model.getEmail());
            }
        });
    }

    @Override
    public int getItemCount() {
        return childModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, dept;
        ImageView call, msg, mail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.designation);
            dept = itemView.findViewById(R.id.dept);
            call = itemView.findViewById(R.id.call);
            msg = itemView.findViewById(R.id.msg);
            mail = itemView.findViewById(R.id.mail);
        }
    }

    private void makeCall(String number) {
        if (number.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Sorry, This field is empty!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(context, "Unissued call permission!", Toast.LENGTH_LONG).show();
                    requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE}, 10);
                    return;
                }
            }
            ActivityOptions options = ActivityOptions.makeCustomAnimation(context.getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                context.startActivity(intent, options.toBundle());
            //}
        }

    }

    private void makeMessage(String number) {
        String MSG = "Hello, ";
        if (number.isEmpty() && MSG.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Sorry, we couldn't find any number to send message!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:" + number));
            intent.putExtra("sms_body", MSG);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            try {
                context.startActivity(Intent.createChooser(intent, "Choose a message client... "));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(), "Sorry, messaging address is not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void makeMail(String email) {
        String sendTo = email;
        String sendSub = "Subject";
        String sendBody = "Message";
        if (sendTo.isEmpty() && sendSub.isEmpty() && sendBody.isEmpty()) {
            Toast.makeText(context.getApplicationContext(), "Sorry, email address is not found", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
            intent.putExtra(Intent.EXTRA_SUBJECT, sendSub);
            intent.putExtra(Intent.EXTRA_TEXT, sendBody);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(Intent.createChooser(intent, "Choose an email client..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(), "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
