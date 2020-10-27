package com.epicture.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.epicture.Epicture_global;
import com.epicture.R;

public class settings extends AppCompatActivity {
    ImageView imageView;
    Switch mature;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences mPrefs = getSharedPreferences("mature", 0);
        String mString = mPrefs.getString("mature", "false");
        final SharedPreferences.Editor mEditor = mPrefs.edit();

        mature = findViewById(R.id.mature);
        if (Boolean.parseBoolean(mString)) {
            mature.setChecked(true);
            Epicture_global.mature = true;
            mEditor.clear();
            mEditor.putString("mature", "true").commit();
        } else {
            mature.setChecked(false);
            Epicture_global.mature = false;
            mEditor.clear();
            mEditor.putString("mature", "false").commit();
        }

        mature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditor.clear();
                    mEditor.putString("mature", "true").commit();
                    Epicture_global.mature = true;
                } else {
                    mEditor.clear();
                    mEditor.putString("mature", "false").commit();
                    Epicture_global.mature = false;
                }
            }
        });

        imageView = (ImageView) findViewById(R.id.returnSetting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(settings.this, dashboard.class);
                startActivity(i);
            }
        });
    }
}
