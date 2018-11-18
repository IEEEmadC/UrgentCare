package com.example.peterbrayo.urgentcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    RadioGroup userType;
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userType = findViewById(R.id.userType);
        done = findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                if(userType.getCheckedRadioButtonId()==R.id.subscriber){
                    startActivity(new Intent(MainActivity.this, VictimActivity.class));

                }
                else if(userType.getCheckedRadioButtonId()==R.id.volunteer)
                {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this,"Please select an option",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
