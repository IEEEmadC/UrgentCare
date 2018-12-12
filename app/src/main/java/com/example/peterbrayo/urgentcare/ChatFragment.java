package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private FirebaseRecyclerAdapter<ChatMessages, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;
    private ProgressBar mProgressBar;
    SharedPreferences sharedPreferences;
    DatabaseReference mFirebaseDatabaseReference;

    Button mSendButton;
    EditText mMessageEditText;
    ImageView mAddMessageImageView;
    SnapshotParser<ChatMessages> parser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        mLinearLayoutManager = new LinearLayoutManager(getContext());



        String LOCATION = "location";
        sharedPreferences = getActivity().getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child("volunteers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //get current user's profile pic and store in shared preferences. To be saved later to Firebase
                String profilePic;
                if(dataSnapshot.child(userId).child("imageUrl").exists()) {
                    profilePic = dataSnapshot.child(userId).child("imageUrl").getValue().toString();
                    editor.putString("userDp", profilePic);
                }

                String sender = dataSnapshot.child(userId).child("name").getValue().toString();
                editor.putString("sender", sender);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        parser = new SnapshotParser<ChatMessages>() {
            @Override
            public ChatMessages parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessages message = dataSnapshot.getValue(ChatMessages.class);
                return message;
            }
        };

  //end adapter

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessageEditText = rootView.findViewById(R.id.messageEditText);
        mSendButton = rootView.findViewById(R.id.sendButton);
        mAddMessageImageView = rootView.findViewById(R.id.addMessageImageView);
        mSendButton = rootView.findViewById(R.id.sendButton);
        mMessageRecyclerView = rootView.findViewById(R.id.messageRecyclerView);
        mProgressBar = rootView.findViewById(R.id.chatProgressBar);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(false);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

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
        };

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

        // this will send the text messages
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sender = sharedPreferences.getString("sender", "Anonymous");
                String text = mMessageEditText.getText().toString().trim();
                String dp = sharedPreferences.getString("userDp","");
                ChatMessages chatMessages = new ChatMessages(text, sender, null, dp);
                FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(chatMessages);
                mMessageEditText.setText("");

            }});

        //add listener to add button

        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });

    }


//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == VolunteerActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void onLaunchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        String sender = sharedPreferences.getString("sender", "Anonymous");
        String dp = sharedPreferences.getString("userDp","");
        ChatMessages chatMessages = new ChatMessages(null, sender, imageEncoded, dp);
        FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(chatMessages);

    }

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

}
