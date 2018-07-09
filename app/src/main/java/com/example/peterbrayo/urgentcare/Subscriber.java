package com.example.peterbrayo.urgentcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Subscriber extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.subscriber_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.volunteer_map_view){
            startActivity(new Intent(Subscriber.this, VolunteersMapView.class));
        }

        else if(id == R.id.volunteer_list_view){

        }


        return super.onOptionsItemSelected(item);
    }
}
