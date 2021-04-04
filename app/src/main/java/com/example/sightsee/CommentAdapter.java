package com.example.sightsee;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sightsee.Models.Comment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    Context _context;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;

        Comment comment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_list, parent, false);
        }

        // Get the views
        TextView tv_username = convertView.findViewById(R.id.tvCommentUsername);
        TextView tv_rating = convertView.findViewById(R.id.tvCommentRating);
        TextView tv_date = convertView.findViewById(R.id.tvCommentDate);
        TextView tv_message = convertView.findViewById(R.id.tvCommentMessage);

        // Inflate views with data
        // TODO: Fill with Firebase Comment Data
        tv_username.setText("CommentAdapter - userID: " + Integer.valueOf(comment.getUserId()));
        tv_rating.setText(String.valueOf(comment.getRating()));
        tv_date.setText(comment.getDate().toString());
        tv_message.setText(comment.getMessage());

        return convertView;
    }
}