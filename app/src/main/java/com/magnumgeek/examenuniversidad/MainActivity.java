package com.magnumgeek.examenuniversidad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authListener;

    private TextView tvUserName, tvUserEmail;
    private ImageView ivUserPhoto;
    private String userName, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu();

        CardView cardViewMultiplayer = (CardView)findViewById(R.id.cardviewMultiplayer);
        cardViewMultiplayer.setOnClickListener(this);

    }

    public void menu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvUserName = (TextView)header.findViewById(R.id.userName);
        tvUserEmail = (TextView)header.findViewById(R.id.userEmail);
        ivUserPhoto = (ImageView)header.findViewById(R.id.userPhoto);

        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(Constants.TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(Constants.TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.userSignIn) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
            Log.d(Constants.TAG, "iniciando sesion");
        } else if (id == R.id.userSignOut) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUserData();
                            Log.d(Constants.TAG, "cerrar sesion");
                        }
                    });
        } else if (id == R.id.userDelete) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUserData();
                            Log.d(Constants.TAG, "eliminar cuenta");
                        }
                    });
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                firebaseUser = firebaseAuth.getCurrentUser();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
                usersRef.child(firebaseUser.getUid()).child("userName").setValue(firebaseUser.getDisplayName());
                Log.d(Constants.TAG, "inicio sesion");
                updateUserData();
            } else {
                Log.d(Constants.TAG, "error inicio sesion");
            }
        }
    }

    public void updateUserData(){
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            for (UserInfo profile : firebaseUser.getProviderData()) {
                Uri photoUrl = profile.getPhotoUrl();
                Log.d(Constants.TAG, photoUrl.toString());
                if(photoUrl != null){
                    Picasso.with(this).load(photoUrl).transform(new CircleTransform()).into(ivUserPhoto);
                } else {
                    Log.d(Constants.TAG, "urlUserPhoto == null");
                }
            }
            userName = firebaseUser.getDisplayName();
            userId = firebaseUser.getUid();
            tvUserName.setText(userName);
            tvUserEmail.setText(firebaseUser.getEmail());
            //urlUserPhoto = firebaseUser.getPhotoUrl();

        } else {
            tvUserName.setText(null);
            tvUserEmail.setText(null);
            ivUserPhoto.setImageURI(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
        updateUserData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardviewMultiplayer:
                Intent intent = new Intent(this, Multiplayer.class);
                intent.putExtra(Constants.EXTRA_USER_NAME, userName);
                intent.putExtra(Constants.EXTRA_USER_ID, userId);
                startActivity(intent);
        }
    }
}
