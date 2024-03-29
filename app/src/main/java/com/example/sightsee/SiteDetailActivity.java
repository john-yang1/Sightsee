package com.example.sightsee;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sightsee.Models.Comment;
import com.example.sightsee.Models.CommentUpload;
import com.example.sightsee.Models.Promotion;
import com.example.sightsee.Models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SiteDetailActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabaseRef;
    private DatabaseReference databaseCases;
    private Button moreCommentsBtn;
    private GoogleMap mMap;
    private ArrayList<User> userList;
    private NavigationView navigationView;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_site_details);

        // GOOGLE MAPS
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //
        userList = new ArrayList<User>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference test = mDatabase.child("users");
        test.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    User user = item_snapshot.getValue(User.class);
                    userList.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String name = (String) getIntent().getExtras().get("name");
        String address = (String) getIntent().getExtras().get("address");
        String imageUrl = (String) getIntent().getExtras().get("imageUrl");
        position = (Integer) getIntent().getExtras().get("position");
        String type = (String) getIntent().getExtras().get("site_type");
        String price = (String) getIntent().getExtras().get("price");

        ScrollView expanded_site_detail = findViewById(R.id.expanded_site_background);
        if (position % 2 == 0) {
            expanded_site_detail.setBackgroundColor(Color.parseColor("#BF008BF8"));
        }
        else {
            expanded_site_detail.setBackgroundColor(Color.parseColor("#68B684"));
        }

        ImageView photo = findViewById(R.id.siteImage);
        //Glide.with(getApplicationContext()).load(imageUrl).into(photo);

        Picasso.get()
                .load(imageUrl)
                .into(photo);

        //photo.setImageResource(R.drawable.capilano);
//        photo.setContentDescription(food.toString());

        // Display product information
        TextView nametv = findViewById(R.id.siteName);
        nametv.setText(name);

        TextView addresstv = findViewById(R.id.siteAddress);
        TextView typetv = findViewById(R.id.siteType);
        TextView pricetv = findViewById(R.id.sitePrice);

        addresstv.setText(getString(R.string.address_header, address));
        typetv.setText(getString(R.string.type_header, type));
        pricetv.setText(getString(R.string.price_header, price));

        loadComments();
        loadPromotions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut(); // logout
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                else if (id == R.id.add_location) {
                    String user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    for (User user: userList) {
                        if (user.user_email.equals(user_email)) {
                            String user_type = user.user_type;
                            if (user_type.equals(getString(R.string.admin_type))) {
                                startActivity(new Intent(getApplicationContext(), AddSiteActivity.class));
                            }
                            else {
                                Toast.makeText(SiteDetailActivity.this,
                                        getString(R.string.insufficient_privileges),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    drawer.closeDrawer(GravityCompat.START);
                }
                else if (id == R.id.profile) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                }
                else if (id == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ScrollView expanded_site_detail = findViewById(R.id.expanded_site_background);
        mMap = googleMap;
        String queried_address = (String) getIntent().getExtras().get("address");
        // Add a marker in Sydney and move the camera
        LatLng selected_site = new LatLng(49.246292, -123.116226);
        mMap.addMarker(new MarkerOptions()
                .position(selected_site)
                .title("Site"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selected_site, 10.0f));
        expanded_site_detail.smoothScrollTo(0, 0);
    }

    public void loadComments() {
        ArrayList<Comment> first_comment = new ArrayList<Comment>();
        String site_id = (String) getIntent().getExtras().get("site_id");
        ListView lvCommentList = findViewById(R.id.lv_singleComment);
        databaseCases = FirebaseDatabase.getInstance().getReference();
        databaseCases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                first_comment.clear();
                ArrayList<Comment> last_comment = new ArrayList<Comment>();
                for (DataSnapshot caseSnapshot: dataSnapshot.child("comments").getChildren()) {
                    Comment comment = caseSnapshot.getValue(Comment.class);
                    if (comment.getSite_id().equals(site_id)) {
                        first_comment.add(comment);
                    }
                }
                if (first_comment.size() >= 1) {
                    last_comment.add(first_comment.get(first_comment.size() - 1));
                }
                CommentAdapter adapter = new CommentAdapter(SiteDetailActivity.this, last_comment);
                lvCommentList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        CommentAdapter adapter = new CommentAdapter(SiteDetailActivity.this, first_comment);
        lvCommentList.setEmptyView(findViewById(android.R.id.empty));
        lvCommentList.setAdapter(adapter);
    }


    public void loadPromotions() {
        ArrayList<Promotion> first = new ArrayList<Promotion>();
        String site_id = (String) getIntent().getExtras().get("site_id");
        ListView lvPromoList = findViewById(R.id.lv_singlePromotion);
        databaseCases = FirebaseDatabase.getInstance().getReference();
        databaseCases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Promotion> last_promotion = new ArrayList<Promotion>();
                first.clear();
                for (DataSnapshot caseSnapshot: dataSnapshot.child("promotions").getChildren()) {
                    Promotion promo = caseSnapshot.getValue(Promotion.class);
                    if (promo.getSiteId().equals(site_id)) {
                        first.add(promo);
                    }
                }
                if (first.size() >= 1) {
                    last_promotion.add(first.get(first.size() - 1));
                }
                PromotionAdapter adapter = new PromotionAdapter(SiteDetailActivity.this, last_promotion);
                lvPromoList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        PromotionAdapter adapter = new PromotionAdapter(SiteDetailActivity.this, first);
        lvPromoList.setEmptyView(findViewById(android.R.id.empty));
        lvPromoList.setAdapter(adapter);
    }


    public void moreComments(View view) {
        String site_id = (String) getIntent().getExtras().get("site_id");
        Intent intent = new Intent(SiteDetailActivity.this, CommentsActivity.class);
        intent.putExtra("site_id", site_id);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void morePromotions(View view) {
        String site_id = (String) getIntent().getExtras().get("site_id");
        Intent intent = new Intent(SiteDetailActivity.this, PromotionActivity.class);
        intent.putExtra("site_id", String.valueOf(site_id));
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void addPromotion(View view) {
        String user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        for (User user: userList) {
            if (user.user_email.equals(user_email)) {
                String user_type = user.user_type;
                if (user_type.equals("admin")) {
                    String siteId = (String) getIntent().getExtras().get("site_id");
                    Intent intent = new Intent(SiteDetailActivity.this, AddPromotionActivity.class);
                    intent.putExtra("siteId", String.valueOf(siteId));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SiteDetailActivity.this,
                            getString(R.string.insufficient_privileges),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void submitComment(View view) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("comments");
        EditText comment_edit_text = findViewById(R.id.comment_edit_text);
        String user_id = (String) getIntent().getExtras().get("user_id");
        String site_id = (String) getIntent().getExtras().get("site_id");
        String comment_text = comment_edit_text.getText().toString().trim();
        String capitalized_user_id = user_id.substring(0, 1).toUpperCase() + user_id.substring(1);

        if (comment_text.trim().equals("")) {
            Toast.makeText(SiteDetailActivity.this, getString(R.string.no_comment),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            CommentUpload comment_upload = new CommentUpload(capitalized_user_id.trim(),
                    site_id.trim(),
                    comment_text.trim());
            String uploadId = mDatabaseRef.push().getKey();
            mDatabaseRef.child(uploadId).setValue(comment_upload);
            Toast.makeText(SiteDetailActivity.this,
                    getString(R.string.comment_added), Toast.LENGTH_SHORT).show();
            comment_edit_text.setText("");
        }
    }
}
