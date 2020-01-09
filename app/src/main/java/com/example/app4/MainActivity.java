package com.example.app4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bttn = findViewById(R.id.button);
        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,
                        GuessByGuessActivity.class);
                startActivity(intent);
            }
        });

        Button bttn2 = findViewById(R.id.button2);
        bttn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,
                        ContinuousActivity.class);
                startActivity(intent);
            }
        });
    }
}
