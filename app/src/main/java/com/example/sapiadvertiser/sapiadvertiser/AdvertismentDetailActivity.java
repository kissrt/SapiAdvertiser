package com.example.sapiadvertiser.sapiadvertiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdvertismentDetailActivity extends AppCompatActivity {

    private   String mAd_key;
    private DatabaseReference mDatabase;

    private ImageView mAd_image;
    private TextView mAd_title;
    private TextView mAd_desc;

    private Button mRemoveBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment_detail);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Advertisment");

        mAuth = FirebaseAuth.getInstance();
        mRemoveBtn = (Button) findViewById(R.id.removeBtn);
        mAd_image = (ImageView) findViewById(R.id.singlead_image);
        mAd_title = (TextView) findViewById(R.id.singlead_title);
        mAd_desc = (TextView) findViewById(R.id.singlead_desc);

        mAd_key = getIntent().getExtras().getString("ad_id");
        mDatabase.child(mAd_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ad_title = (String) dataSnapshot.child("title").getValue();
                String ad_desc = (String) dataSnapshot.child("desc").getValue();
                String ad_image = (String) dataSnapshot.child("image").getValue();
                String ad_uid = (String) dataSnapshot.child("uid").getValue();

                mAd_title.setText(ad_title);
                mAd_desc.setText(ad_desc);
                Glide.with(AdvertismentDetailActivity.this).load(ad_image).into(mAd_image);

                if(mAuth.getCurrentUser().getUid().equals(ad_uid)){
                    mRemoveBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mAd_key).removeValue();
                Intent mainIntent = new Intent(AdvertismentDetailActivity.this,MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }
}
