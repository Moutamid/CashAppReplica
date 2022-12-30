package com.moutamid.cashapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.fxn.stash.Stash;
import com.moutamid.cashapp.databinding.ActivitySelectContactBinding;
import com.moutamid.cashapp.model.ContactModel;
import com.moutamid.cashapp.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectContactActivity extends AppCompatActivity {

    private ActivitySelectContactBinding b;
    private ArrayList<ContactModel> tasksArrayList = Stash.getArrayList(Constants.LIST, ContactModel.class);
    private ArrayList<ContactModel> tasksArrayListAll = Stash.getArrayList(Constants.LIST, ContactModel.class);

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.white));*/
        b = ActivitySelectContactBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.etTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                selected_code = "null";
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        b.topText.setText("$" + getIntent().getStringExtra(Constants.PARAMS));

        b.cashLayout.setOnLongClickListener(v -> {
            startActivity(new Intent(SelectContactActivity.this, EditListActivity.class));
            return false;
        });

        b.closeBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        b.payBtn.setOnClickListener(v -> {
            startActivity(new Intent(SelectContactActivity.this, LoadingActivity.class));
        });

        initRecyclerView();
    }

    private void initRecyclerView() {

        conversationRecyclerView = b.contactsRv;
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

//        if (adapter.getItemCount() != 0) {

        /*Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.profile);

        ContactModel model = new ContactModel();
        model.name = "Moutamid";
        model.code = "$hi.moutamid";

        model.image = getStringFromBitmap(icon);

        tasksArrayList.add(model);*/

//        Collections.reverse(tasksArrayList);
//        adapter.notifyDataSetChanged();
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        tasksArrayList =
                Stash.getArrayList(Constants.LIST, ContactModel.class);
        adapter.notifyDataSetChanged();
    }

    private String getStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return base64String;
    }

    private Bitmap getBitmapFromString(String base64String) {
        byte[] decodedByteArray = Base64.decode(base64String, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

    String selected_code = "null";

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> implements Filterable {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            ContactModel model = tasksArrayList.get(holder.getAdapterPosition());

            holder.name.setText(model.name);
            holder.code.setText(model.code);

            if (selected_code.equals(model.code))
                holder.tick.setVisibility(View.VISIBLE);
            else holder.tick.setVisibility(View.GONE);

            holder.imageView.setImageBitmap(getBitmapFromString(model.image));

            holder.layout.setOnClickListener(v -> {
                holder.tick.setVisibility(View.VISIBLE);

                selected_code = model.code;

                b.etTo.setText(model.code);
                b.payBtn.setTextColor(getResources().getColor(R.color.white));
            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    List<ContactModel> filterList = new ArrayList<>();

                    if (charSequence.toString().isEmpty()) {
                        filterList.addAll(tasksArrayListAll);
                    } else {
                        for (ContactModel data : tasksArrayList) {
                            if (data.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                filterList.add(data);
                            } else if (data.code.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                filterList.add(data);
                            }
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterList;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    tasksArrayList.clear();
                    tasksArrayList.addAll((Collection<? extends ContactModel>) filterResults.values);
                    notifyDataSetChanged();
                }
            };

        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            CircleImageView imageView;
            TextView name, code;
            RelativeLayout layout;
            ImageView tick;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                imageView = v.findViewById(R.id.itemProfile);
                name = v.findViewById(R.id.itemName);
                code = v.findViewById(R.id.itemCode);
                tick = v.findViewById(R.id.itemTick);
                layout = v.findViewById(R.id.parentLayout);

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(SelectContactActivity.this);
    }
}