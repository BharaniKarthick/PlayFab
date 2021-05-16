package com.kernelkid.playfab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendList extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        textView=findViewById(R.id.friend);

        Bundle extras = getIntent().getExtras();
        ArrayList mDetails=(ArrayList) extras.get("FriendsList");
        System.out.println("friends list "+mDetails);
        textView.setText(mDetails.toString());

    }
}