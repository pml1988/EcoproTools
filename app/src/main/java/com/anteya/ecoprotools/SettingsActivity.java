package com.anteya.ecoprotools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.object.DataControl;

import java.io.Serializable;
import java.util.List;
import java.util.zip.Inflater;

public class SettingsActivity extends Activity  {

    private TextView text1, text2, text3;

    private DataControl dataControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Settings");
        dataControl = (DataControl) getApplicationContext();

        text1 = (TextView) findViewById(R.id.activitySettings_textView1);
        text3 = (TextView) findViewById(R.id.activitySettings_textView2);
        text2 = (TextView) findViewById(R.id.activitySettings_password);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, IpSettingsActivity.class);
                startActivity(intent);
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater inflater = LayoutInflater.from(SettingsActivity.this);
                View view = inflater.inflate(R.layout.activity_main_first_passwrod, null);
                final EditText ed = (EditText) view.findViewById(R.id.activity_main_password_editText);
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle("INput Password").setView(view).setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        System.out.println( ed.getText().toString());

                        saveData(ed.getText().toString());

                        String temp = ed.getText().toString();

                        if(ed.getText().toString().length()!=4)
                        {
                            temp = "3259";

                        }
                            dataControl.setPd_one(Integer.parseInt(temp.substring(0, 1)));
                            dataControl.setPd_two(Integer.parseInt(temp.substring(1, 2)));
                            dataControl.setPd_three(Integer.parseInt(temp.substring(2, 3)));
                            dataControl.setPd_four(Integer.parseInt(temp.substring(3, 4)));



                        Toast.makeText(SettingsActivity.this, "設定完成", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();


            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, WifiSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_main_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        if (actionBar == null) {

            Log.d("1", "actionBar == null");
        }

        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String password = "PASSWORD";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBar Menu OnclickListener
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveData(String number) {

        settings = getSharedPreferences(data, 0);
        settings.edit()
                .putString(password, number)
                .commit();

    }
}
