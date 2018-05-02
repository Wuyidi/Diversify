package com.techhawk.diversify.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techhawk.diversify.R;
import com.techhawk.diversify.helper.GlideApp;
import com.techhawk.diversify.model.Comment;
import com.techhawk.diversify.model.CommentCredited;
import com.techhawk.diversify.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommentActivity extends BaseActivity {
    private String refKey;
    private DatabaseReference commentRef;
    private StorageReference filePath;
    private FirebaseListAdapter<Comment> adapter;
    private ListView commentView;
    private TextView emptyView;
    private EditText inputComment;
    private Button submitButton;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private String userName;


    public static final String EXTRA_COMMENT_EVENT_KEY = "event_comment_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideKeyboard();
        setTitle("Comments");
        refKey = this.getIntent().getStringExtra(EXTRA_COMMENT_EVENT_KEY);
        commentRef = FirebaseDatabase.getInstance().getReference().child("comments").child(refKey);

        // Require permission
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        // Get reference from activity
        commentView = findViewById(R.id.comment_view);
        emptyView = findViewById(R.id.empty_comment);
        submitButton = findViewById(R.id.btn_submit);
        inputComment = findViewById(R.id.comment_edit);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            submit();
                        }
                    });
                } else {
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CommentActivity.this, AuthMethodPickerActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        auth.addAuthStateListener(authStateListener);
        setUpAdapter(commentRef);
        commentView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadProfileImage(StorageReference ref, ImageView view) {
        RequestOptions options = new RequestOptions().error(R.drawable.ic_profile);
        GlideApp.with(this).load(ref).apply(options).into(view);
    }

    private void createNewComment(final String content) {

        showProgressDialog();

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String uid = getUid();
        Date c = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(c);
        Comment comment = new Comment(uid, userName, date, 0, content);
        commentRef.push().setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    feedback("Comment couldn't be submitted " + databaseError.getMessage());
                } else {
                    feedback("Submit comment successfully");
                }
                hideProgressDialog();
            }
        });

    }

    private void submit() {
        String content = inputComment.getText().toString().trim();
        if (validateForm()) {
            createNewComment(content);
        } else {
            feedback("Error: Comment is empty!");
        }
    }

    private boolean validateForm() {
        boolean canSubmit = true;
        String content = inputComment.getText().toString().trim();
        // Validate user input
        if (TextUtils.isEmpty(content)) {
            canSubmit = false;
        }

        return canSubmit;
    }

    private void thumb(int position, int numOfThumb) {
        DatabaseReference reference = adapter.getRef(position).child("numOfThumb");
        int num = numOfThumb + 1;
        reference.setValue(num);
    }

    private void cancelThumb(int position, int numOfThumb) {
        DatabaseReference reference = adapter.getRef(position).child("numOfThumb");
        int num = numOfThumb - 1;
        reference.setValue(num);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void setUpAdapter(Query query) {
        FirebaseListOptions<Comment> options = new FirebaseListOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .setLayout(R.layout.list_comment_item)
                .setLifecycleOwner(this).build();
        adapter = new FirebaseListAdapter<Comment>(options) {
            @Override
            protected void populateView(View v, final Comment model, final int position) {
                ((TextView) v.findViewById(R.id.user_name)).setText(model.getUsername());
                ((TextView) v.findViewById(R.id.comment_content)).setText(model.getContent());
                if (model.getNumOfThumb() != 0) {
                    ((TextView) v.findViewById(R.id.num_thumbs)).setText(String.valueOf(model.getNumOfThumb()));
                } else {
                    ((TextView) v.findViewById(R.id.num_thumbs)).setText("credit");
                }
                ((TextView) v.findViewById(R.id.comment_date)).setText(model.getDate());
                StorageReference ref = FirebaseStorage.getInstance().getReference().child("photos").child(model.getUid());
                ImageView profile = v.findViewById(R.id.profile);
                loadProfileImage(ref, profile);
                if (getUid().equals(model.getUid())) {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buildDeleteDialog(model, position);
                        }
                    });
                }
                final ToggleButton thumb = v.findViewById(R.id.btn_thumb);
                // validate credit
                String key = this.getRef(position).getKey();
                DatabaseReference reference = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("users")
                        .child(getUid())
                        .child("comment_credit")
                        .child(key);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CommentCredited commentCredited = dataSnapshot.getValue(CommentCredited.class);
                        if (commentCredited == null || !commentCredited.isCredited()) {
                            thumb.setChecked(false);
                        } else {
                            thumb.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (model.getUid().equals(getUid())) {
                    thumb.setChecked(false);
                    thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            thumb.setChecked(false);
                            feedback("Couldn't credit your comment!");
                        }
                    });
                } else {
                    thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (thumb.isChecked()) {
                                thumb(position, model.getNumOfThumb());
                                setCredited(position, true);
                            } else {
                                cancelThumb(position, model.getNumOfThumb());
                                setCredited(position, false);
                            }
                        }
                    });
                }

            }
        };

    }

    private void deleteData(int position) {
        adapter.getRef(position).removeValue();
    }

    private void buildDeleteDialog(Comment comment, final int position) {
        // Build a dialog to delete item
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Comment?");
        builder.setMessage("Are you sure you wish to delete this comment?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }


    private void setCredited(int position, boolean isCredited) {
        String key = adapter.getRef(position).getKey();
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(getUid())
                .child("comment_credit")
                .child(key);
        CommentCredited commentCredited = new CommentCredited(isCredited);
        reference.setValue(commentCredited);
    }


}
