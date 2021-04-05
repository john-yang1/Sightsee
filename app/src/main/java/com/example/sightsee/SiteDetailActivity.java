package com.example.sightsee;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sightsee.Models.Comment;
import com.example.sightsee.Models.Promotion;
import com.example.sightsee.Models.PromotionActivity;
import com.example.sightsee.Models.Site;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SiteDetailActivity extends AppCompatActivity {

    private Button moreCommentsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_site_details);

        String name = (String) getIntent().getExtras().get("name");
        String address = (String) getIntent().getExtras().get("address");
        String imageUrl = (String) getIntent().getExtras().get("imageUrl");
        int position = (Integer) getIntent().getExtras().get("position");

        ScrollView expanded_site_detail = findViewById(R.id.expanded_site_background);
        if (position % 2 == 0) {
            expanded_site_detail.setBackgroundColor(Color.parseColor("#1485C5"));
        }
        else {
            expanded_site_detail.setBackgroundColor(Color.parseColor("#90CF3F"));
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
        addresstv.setText("Address: " + address);


        // Set up the single comment, sorted by highest rating
        List<Comment> temp = Comment.get_test_comments().stream()
                //.filter(comment -> comment.getSiteId() == siteId) // Had to comment out as i removed siteid
                .sorted(Comparator.comparingInt(Comment::getRating).reversed())
                .collect(Collectors.toList());

        // Inflate and display only the highest rated comment if there is a comment
        if (temp.size() > 0) {
            ListView lvCommentList = findViewById(R.id.lv_singleComment);
            ArrayList<Comment> onlyComment = new ArrayList<Comment>(temp.subList(0, 1));
            CommentAdapter adapter = new CommentAdapter(SiteDetailActivity.this, onlyComment);
            lvCommentList.setAdapter(adapter);

        } else {
            // If no comments for this site (NOTE: No errors thrown if no comments)
        }

        // Filter promos, get a single one and inflate the listview
        List<Promotion> fullPromoList = Promotion.get_test_promotions().stream()
                //.filter(promotion -> promotion.getSiteId() == siteId)
                .collect(Collectors.toList());

        if (fullPromoList.size() > 0) {
            ListView lvPromoList = findViewById(R.id.lv_singlePromotion);
            ArrayList<Promotion> onlyPromotion = new ArrayList<Promotion>(fullPromoList.subList(0, 1));
            PromotionAdapter promotionAdapter = new PromotionAdapter(SiteDetailActivity.this, onlyPromotion);
            lvPromoList.setAdapter(promotionAdapter);

        } else {
            // If no promos
        }
    }

    public void moreComments(View view) {
        Intent intent = new Intent(SiteDetailActivity.this, CommentsActivity.class);
        //intent.putExtra("siteId", String.valueOf(siteId));
        startActivity(intent);
    }

    public void morePromotions(View view) {
        Intent intent = new Intent(SiteDetailActivity.this, PromotionActivity.class);
        //intent.putExtra("siteId", String.valueOf(siteId));
        startActivity(intent);
    }
}
