package com.moutamid.cashapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.cashapp.databinding.ActivityEditListBinding;
import com.moutamid.cashapp.model.ContactModel;
import com.moutamid.cashapp.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditListActivity extends AppCompatActivity {
    private ArrayList<ContactModel> tasksArrayList =
            Stash.getArrayList(Constants.LIST, ContactModel.class);

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private ActivityEditListBinding b;
    CircleImageView circleImageView;
    Uri imageUri;

    ContactModel contactModel = new ContactModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.addNewContact.setOnClickListener(v -> {

            Dialog dialog = new Dialog(EditListActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add);
            dialog.setCancelable(true);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            EditText nameEt = dialog.findViewById(R.id.dialogName);
            EditText codeEt = dialog.findViewById(R.id.dialogCode);

            circleImageView = dialog.findViewById(R.id.dialogProfile);

            circleImageView.setOnClickListener(v1 -> {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 9999);
            });

            dialog.findViewById(R.id.dialogAddbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = nameEt.getText().toString();
                    String code = codeEt.getText().toString();

                    if (name.isEmpty())
                        return;

                    if (code.isEmpty())
                        return;

                    if (contactModel.image == null)
                        return;

                    contactModel.name = name;
                    contactModel.code = code;

                    tasksArrayList.add(contactModel);
                    adapter.notifyDataSetChanged();

                    Stash.put(Constants.LIST, tasksArrayList);

                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.getWindow().setAttributes(layoutParams);

        });

        initRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 9999) {
                imageUri = data.getData();

                circleImageView.setImageURI(imageUri);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.profile);
                }

                contactModel.image = getStringFromBitmap(bitmap);

            }
        }
    }

    private void initRecyclerView() {

        conversationRecyclerView = b.listRv;
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

        // Extend the Callback class
        ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
            //and in your imlpementaion of
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(tasksArrayList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                Stash.put(Constants.LIST, tasksArrayList);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(ithCallback);
        ith.attachToRecyclerView(conversationRecyclerView);
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

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public RecyclerViewAdapterMessages.ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_edit, parent, false);
            return new RecyclerViewAdapterMessages.ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapterMessages.ViewHolderRightMessage holder, int position) {
            ContactModel model = tasksArrayList.get(holder.getAdapterPosition());

            holder.name.setText(model.name);
            holder.code.setText(model.code);

            holder.imageView.setImageBitmap(getBitmapFromString(model.image));

            holder.editDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(EditListActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you really wanna delete it?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            tasksArrayList.remove(holder.getAdapterPosition());
                            adapter.notifyDataSetChanged();
                            Stash.put(Constants.LIST, tasksArrayList);
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();

            });

            holder.drag.setOnClickListener(v -> {
                Toast.makeText(EditListActivity.this, "Long press and then swipe up or down to change position", Toast.LENGTH_LONG).show();
            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            CircleImageView imageView;
            TextView name, code;
            ImageView editDelete, drag;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                imageView = v.findViewById(R.id.itemProfile);
                name = v.findViewById(R.id.itemName);
                code = v.findViewById(R.id.itemCode);
                editDelete = v.findViewById(R.id.itemDelete);
                drag = v.findViewById(R.id.itemDrag);

            }
        }

    }

}