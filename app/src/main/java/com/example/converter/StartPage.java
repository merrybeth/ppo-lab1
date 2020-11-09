package com.example.converter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartPage extends AppCompatActivity {

    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (view.getId()) {
            case R.id.currency:
                message = "currency";
                intent.putExtra("param", message);
                startActivity(intent);
                break;
            case R.id.lenth:
                message = "length";
                intent.putExtra("param", message);
                startActivity(intent);
                break;
            case R.id.weight:
                message = "weight";
                intent.putExtra("param", message);
                startActivity(intent);
                break;
        }
    }
}