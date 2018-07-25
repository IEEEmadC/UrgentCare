package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import static com.example.peterbrayo.urgentcare.VolunteerHomeActivity.decodeFromFirebaseBase64;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {
    private Context c;
    private ArrayList<RecyclerviewUser> arrayList;

    RecyclerAdapter(Context c, ArrayList<RecyclerviewUser> arrayList){
        Log.i("adapter", "constructor");
        this.c = c;
        this.arrayList = arrayList;
    }


    /*
    INITIALIZE VIEWHOLDER
     */

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("oncreateview",parent.toString());
        View v= LayoutInflater.from(c).inflate(R.layout.recylerview_row,parent,false);
        return new MyHolder(v);
    }

    /*
    BIND
     */
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Log.i("onBind",arrayList.get(position).getContact());
        RecyclerviewUser user = arrayList.get(position);

        //decode image-url in firebase from string to bitmap
        Bitmap imageBitmap = decodeFromFirebaseBase64(user.getImage());
        final String phoneNum = user.getContact();
        holder.nameTxt.setText(user.getName());
        holder.phoneTxt.setText(phoneNum);

        holder.phoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNum));
                if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                view.getContext().startActivity(callIntent);
            }
        });
        holder.img.setImageBitmap(getResizedBitmap(imageBitmap, 200,220));
        holder.ratingBar.setRating(user.getRating());
    }

    @Override
    public int getItemCount() {
        if(arrayList != null)
        return arrayList.size();
        else
            return 0;
    }

    /*
    VIEW HOLDER CLASS
     */
    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView nameTxt;
        TextView phoneTxt;
        ImageView img;
        SimpleRatingBar ratingBar;

        MyHolder(View userDetails) {
            super(userDetails);
            nameTxt =  userDetails.findViewById(R.id.nameTxt);
            phoneTxt = userDetails.findViewById(R.id.phoneTxt);
            img =  userDetails.findViewById(R.id.profile_picture);
            ratingBar =  userDetails.findViewById(R.id.ratingBarID);
        }
    }

    //method to scale image on ImageView
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
