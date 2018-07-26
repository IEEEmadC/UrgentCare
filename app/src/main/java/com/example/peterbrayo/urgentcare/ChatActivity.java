package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity{

    private static final String TAG = "ChatActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_IMAGE_CAPTURE = 111;

    Button mSendButton;
    EditText mMessageEditText;
    ImageView mAddMessageImageView;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        MessageViewHolder(View v) {
            super(v);
            messageTextView =  itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messengerTextView = itemView.findViewById(R.id.messengerTextView);
            messengerImageView = itemView.findViewById(R.id.messengerImageView);
        }
    }

    private FirebaseRecyclerAdapter<ChatMessages, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;
    private ProgressBar mProgressBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_chat);

        mSendButton = findViewById(R.id.sendButton);
        mMessageRecyclerView = findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(false);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgressBar = findViewById(R.id.chatProgressBar);

        String LOCATION = "location";
        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // New child entries
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child("volunteers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //get current user's profile pic and store in shared preferences. To be saved later to Firebase
                String profilePic = dataSnapshot.child(userId).child("imageUrl").getValue().toString();
                editor.putString("userDp", profilePic);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SnapshotParser<ChatMessages> parser = new SnapshotParser<ChatMessages>() {
            @Override
            public ChatMessages parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessages message = dataSnapshot.getValue(ChatMessages.class);
                return message;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);

        FirebaseRecyclerOptions<ChatMessages> options = new FirebaseRecyclerOptions.Builder<ChatMessages>().setQuery(messagesRef, parser).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessages, MessageViewHolder>(options) {

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int position, ChatMessages message) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                Log.i("onBindView", message.getImage());
                if (message.getText() != null) {
                    Log.i("onBindStart", message.getText());
                    viewHolder.messageTextView.setText(message.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                }
                else if (message.getImage() != null) {

                    viewHolder.messageImageView.setImageBitmap(UtilityFunctions.getResizedBitmap(UtilityFunctions.decodeFromFirebaseBase64(message.getImage()),150,150));
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }

               viewHolder.messengerTextView.setText(message.getSender());
                String dp = message.getProfilePic();
                if(!dp.equals("")){
                    Bitmap userPic = UtilityFunctions.decodeFromFirebaseBase64(dp);
                    Bitmap circularDp = UtilityFunctions.getCircleBitmap(UtilityFunctions.getResizedBitmap(userPic, 400,400));
                    viewHolder.messengerImageView.setImageBitmap(circularDp);
                }

            }
        }; //end adapter

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("volunteers");
                ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String sender = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();
                    String text = mMessageEditText.getText().toString().trim();
                    String dp = sharedPreferences.getString("userDp","");
                    ChatMessages chatMessages = new ChatMessages(text, sender, null, dp);
                    FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(chatMessages);
                    mMessageEditText.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }});

        //add listener to add button
        mAddMessageImageView = findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onLaunchCamera();
            }
        });

    } // end onCreate

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == VolunteerHomeActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void onLaunchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("volunteers");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dp = sharedPreferences.getString("userDp","");
                String sender = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();
                ChatMessages chatMessages = new ChatMessages(null, sender, imageEncoded, dp);
                FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(chatMessages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
