package com.nurujjamanpollob.jsonparsing;


import android.os.Bundle;

import android.os.StrictMode;

import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //ListView Object
    ListView listView;

    //For URL


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		//Set Thread Policy
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

        listView = (ListView) findViewById(R.id.list);

        // Calling async task to get json
        // URL to get contacts JSON
        String url = "http://deeplearningsolution.com/api/";
        new GetStudents(this, url,listView).execute();

		



    }

}
