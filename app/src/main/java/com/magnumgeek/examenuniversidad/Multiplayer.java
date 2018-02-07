package com.magnumgeek.examenuniversidad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Multiplayer extends AppCompatActivity {

    private DatabaseReference multiplayerRef;
    private String userName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        userName = intent.getStringExtra(Constants.EXTRA_USER_NAME);
        userId = intent.getStringExtra(Constants.EXTRA_USER_ID);

        multiplayerRef = database.getReference("multiplayer");

        Query listName = multiplayerRef;


        listName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String listaNombre;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Log.d(Constants.TAG, "toString()");
                    Log.d(Constants.TAG, postSnapshot.toString());
                    Log.d(Constants.TAG, "hasChildren()");
                    Log.d(Constants.TAG, String.valueOf(postSnapshot.hasChildren()));
                    Log.d(Constants.TAG, "getChildrenCount()");
                    Log.d(Constants.TAG, String.valueOf(postSnapshot.getChildrenCount()));
                    int i = 1;
                    for (DataSnapshot hijo : postSnapshot.getChildren()){
                        Log.d(Constants.TAG, "hijo " + i);
                        Log.d(Constants.TAG, hijo.toString());
                        Log.d(Constants.TAG, hijo.getKey());
                        i ++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(Constants.TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        multiplayerRef.child(userId).child(Constants.USER_NAME).setValue(userName);
    }

    @Override
    public void onStop() {
        super.onStop();
        //multiplayerRef.child(userId).removeValue();
    }
}