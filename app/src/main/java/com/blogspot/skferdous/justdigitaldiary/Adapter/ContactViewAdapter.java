package com.blogspot.skferdous.justdigitaldiary.Adapter;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;

import java.util.List;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class ContactViewAdapter extends RecyclerView.Adapter<ContactViewAdapter.ViewHolder> {

    Context context;
    List<ChildModel> childModelList;

    public ContactViewAdapter(Context context, List<ChildModel> childModelList) {
        this.context = context;
        this.childModelList = childModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChildModel model = childModelList.get(position);

        holder.name.setText(model.getName());
        holder.desg.setText(model.getDesignation());
        if (model.getEmail().equals("null")) {
            holder.email.setText("");
            holder.email.setVisibility(View.GONE);
        } else {
            holder.email.setText(model.getEmail());
        }
        if (model.getPhonePer().equals("null") || model.getPhonePer().equals("")) {
            holder.phone.setText(model.getPhoneHome());
        } else {
            holder.phone.setText(model.getPhoneHome() + ", " + model.getPhonePer());
        }
        if (model.getPhoneHome().equals("null") || model.getPhoneHome().equals("")) {
            holder.phone.setText("");
            holder.phone.setVisibility(View.GONE);
        } else {
            holder.phone.setText(model.getPhoneHome());
        }
        if (model.getPbx().equals("null") || model.getPbx().equals("")) {
            holder.pbx.setText("");
            holder.pbx.setVisibility(View.GONE);
        } else {
            holder.pbx.setText(model.getPbx());
        }
        if (model.getOthers().equals("null") || model.getOthers().equals("")) {
            holder.others.setText("");
            holder.others.setVisibility(View.GONE);
        } else {
            holder.others.setText(model.getOthers());
        }

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = model.getPhoneHome();
                if (number.isEmpty()) {
                    Toast.makeText(context, "Sorry, This field is empty!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + number));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(context, "Unissued call permission!", Toast.LENGTH_LONG).show();
                            requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 10);
                            return;
                        }
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
                        v.getContext().startActivity(intent, options.toBundle());
                    }
                }
            }
        });

        holder.mail.setOnClickListener(v -> {

            Toast.makeText(context, "You are going to send an email!", Toast.LENGTH_LONG).show();
            String sendTo = model.getEmail();
            String sendSub = "Subject";
            String sendBody = "Message";
            if (sendTo.isEmpty() && sendSub.isEmpty() && sendBody.isEmpty()) {
                Toast.makeText(context, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.msg.setOnClickListener(v -> {
            Toast.makeText(context, "You are going to send a short message!", Toast.LENGTH_LONG).show();
            String number = model.getPhoneHome();
            String MSG = "Assalamu alaykum, ";
            if (number.isEmpty() && MSG.isEmpty()) {
                Toast.makeText(context, "Sorry, we couldn't find any number to send message!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:" + number));
                intent.putExtra("sms_body", MSG);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                try {
                    context.startActivity(Intent.createChooser(intent, "Choose a message client... "));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Sorry, messaging address is not found!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name, desg, email, phone, pbx, others;
        ImageView call, mail, msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.contactListCard);
            name = itemView.findViewById(R.id.name);
            desg = itemView.findViewById(R.id.designation);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            pbx = itemView.findViewById(R.id.pbx);
            others = itemView.findViewById(R.id.others);

            call = itemView.findViewById(R.id.call);
            mail = itemView.findViewById(R.id.mail);
            msg = itemView.findViewById(R.id.msg);
        }
    }
}
