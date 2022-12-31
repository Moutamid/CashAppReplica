package com.moutamid.cashapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.fxn.stash.Stash;
import com.moutamid.cashapp.databinding.ActivityMainBinding;
import com.moutamid.cashapp.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        String profile = Stash.getString(Constants.PROFILE_IMAGE, Constants.NULL);
        if (!profile.equals(Constants.NULL))
            b.profileImage.setImageBitmap(getBitmapFromString(profile));

        b.profileImage.setOnLongClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 9999);
            return false;
        });

        b.payBtn.setOnClickListener(v -> {
            Stash.put(Constants.SENT_AMOUNT, b.currentCount.getText().toString());
            startActivity(new Intent(MainActivity.this, SelectContactActivity.class));
            Animatoo.INSTANCE.animateSlideUp(MainActivity.this);
        });

        b.scanImg.setOnLongClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_last_screen_amount);
            dialog.setCancelable(true);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            EditText editText = dialog.findViewById(R.id.dialogAmountEt);

            dialog.findViewById(R.id.dialogSubmitBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String amount = editText.getText().toString();

                    if (amount.isEmpty())
                        return;

                    Stash.put(Constants.LAST_AMOUNT, amount);

                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.getWindow().setAttributes(layoutParams);
            return false;
        });

    }

    private Bitmap getBitmapFromString(String base64String) {
        byte[] decodedByteArray = Base64.decode(base64String, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

    boolean isFirstTime = true;

    public void clickListener(View view) {
        String tag = view.getTag().toString();

        if (tag.equals("b")) {

            if (isFirstTime)
                return;

            String amount = b.currentCount.getText().toString();

            if (amount.length() == 1) {
                b.currentCount.setText("0");
                isFirstTime = true;
                return;
            }

            amount = amount.substring(0, amount.length() - 1);

            b.currentCount.setText(amount);
            return;
        }

        if (isFirstTime) {
            // FIRST TIME
            isFirstTime = false;

            if (tag.equals(".")) {
                b.currentCount.setText("0.");

            } else
                b.currentCount.setText(tag);
        } else {
            // NOT FIRST TIME
            b.currentCount.setText(b.currentCount.getText().toString() + tag);
        }

    }

    private String getStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return base64String;
    }

    Uri imageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 9999) {
                imageUri = data.getData();

                b.profileImage.setImageURI(imageUri);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.profile);
                }

                Stash.put(Constants.PROFILE_IMAGE, getStringFromBitmap(bitmap));

            }
        }
    }
}








