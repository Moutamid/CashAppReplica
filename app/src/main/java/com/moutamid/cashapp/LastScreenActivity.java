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

import java.text.NumberFormat;
import java.util.Locale;

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
                "The funds have been sent but due to security reason the funds will not be available until " +
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
                        "'s account. Funds will be held for 7 days until transaction minimum is met. " +
                        "Funds will be released after 7 days back into your account if minimum is not met. "
        );

        b.amountTv.setText("$" + getFormattedAmount(Integer.parseInt(amount)) + ".00");

    }

    private String getFormattedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    private Bitmap getBitmapFromString(String base64String) {
        byte[] decodedByteArray = Base64.decode(base64String, Base64.NO_WRAP);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedBitmap;
    }

}