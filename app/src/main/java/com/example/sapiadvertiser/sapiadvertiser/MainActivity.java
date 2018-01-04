package com.example.sapiadvertiser.sapiadvertiser;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mAdList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                   resetMenu();
                }else{
                    checkUserExist();
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Advertisment");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        mAdList = (RecyclerView) findViewById(R.id.ad_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mAdList.setHasFixedSize(true);
        mAdList.setLayoutManager(layoutManager);
    }

    private void resetMenu() {
        if(mMenu!= null && mAuth!=null){
            if(mAuth.getCurrentUser() != null){
                mMenu.findItem(R.id.action_settings).setVisible(true);
                mMenu.findItem(R.id.action_logout).setVisible(true);
                mMenu.findItem(R.id.action_add).setVisible(true);
                mMenu.findItem(R.id.action_login).setVisible(false);
            }
            else{
                mMenu.findItem(R.id.action_settings).setVisible(false);
                mMenu.findItem(R.id.action_logout).setVisible(false);
                mMenu.findItem(R.id.action_add).setVisible(false);
                mMenu.findItem(R.id.action_login).setVisible(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Advertisment,AdViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Advertisment, AdViewHolder>(Advertisment.class,R.layout.ad_row,AdViewHolder.class,mDatabase) {//mQuery
            @Override
            protected void populateViewHolder(AdViewHolder viewHolder, Advertisment model, int position) {
                final String ad_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailIntent = new Intent(MainActivity.this,AdvertismentDetailActivity.class);
                        detailIntent.putExtra("ad_id",ad_key);
                        startActivity(detailIntent);
                    }
                });
            }
        };
        mAdList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {
        if(mAuth.getCurrentUser()!= null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                    else{
                       /* Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);*/
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public static class AdViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public AdViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;

        }

        public void setTitle(String title){
            TextView ad_title = (TextView) mView.findViewById(R.id.singlead_title);
            ad_title.setText(title);
        }
        public void setDesc (String desc){
            TextView ad_desc = (TextView) mView.findViewById(R.id.singlead_desc);
            ad_desc.setText(desc);
        }
        public void setImage (Context ctx, String image){
            ImageView ad_image = (ImageView) mView.findViewById(R.id.singlead_image);
            Glide.with(ctx).load(image).into(ad_image);
        }
        public void setUsername(String username){
            TextView ad_username = (TextView) mView.findViewById(R.id.ad_user);
            ad_username.setText(username);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        mMenu = menu;
        resetMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this, AddAdActivity.class));
        }

        if(item.getItemId() == R.id.action_logout){
            logout();
        }
        if(item.getItemId() == R.id.action_login){
            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
