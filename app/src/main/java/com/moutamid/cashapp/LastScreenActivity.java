package com.moutamid.cashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.fxn.stash.Stash;
import com.moutamid.cashapp.databinding.ActivityLastScreenBinding;
import com.moutamid.cashapp.model.ContactModel;
import com.moutamid.cashapp.utils.Constants;

public class LastScreenActivity extends AppCompatActivity {

    private ActivityLastScreenBinding b;

    ContactModel model = (ContactModel) Stash.getObject(Constants.SELECTED_MODEL, ContactModel.class);
    String amount = Stash.getString(Constants.SENT_AMOUNT, "0");
    String lastAmount = Stash.getString(Constants.LAST_AMOUNT, "0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLastScreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.profileImage.setImageBitmap(getBitmapFromString(model.image));

        b.nameTextview.setText(model.name);

        b.firstTextView.setText(
                "The funds have been sent, but will not be available until " +
                        model.code +
                        " meets the minimum transaction requirements."
        );

        b.secondTextView.setText(
                "You must receive at least " +
                        lastAmount +
                        " in transactions from " +
                        model.code +
                        " to instantly release the $" +
                        amount +
                        " into " +
                        model.name +
                        "'s account."
        );

        b.amountTv.setText("$" + amount + ".00");

    }

    private Bitmap getBitmapFromString(String base64String) {
        byte[] decodedByteArray = Base64.decode(base64String, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

}