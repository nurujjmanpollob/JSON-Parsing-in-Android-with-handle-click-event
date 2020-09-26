package com.nurujjamanpollob.jsonparsing;



//Import Strings from Strings.Java

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.nurujjamanpollob.jsonparsing.Strings.KEY_NAME;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_BIO;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_PROFILE_LINK;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_SHOW_CODE;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_SOCIAL_LINK;







public class StudentDetails extends AppCompatActivity
{
	
	ImageView profileImg;
	TextView name;
	TextView bio;
	ImageView socialLink;
	ImageView showCode;

	//private Context context;


	int isShown = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		
		//set content view
		
		setContentView(R.layout.student_details);
		
		profileImg = findViewById(R.id.student_detailsProfileImage);
		name = findViewById(R.id.student_detailsName);
		bio = findViewById(R.id.student_detailsBio);
		socialLink = findViewById(R.id.student_detailsSocialLink);
		showCode = findViewById(R.id.student_detailsShowCode);
		
		
		//Lets Scan for data!
		
		final HashMap<String, String> studentData = (HashMap<String, String>) StudentDetails.this.getIntent().getSerializableExtra(KEY_SHOW_CODE);
		
		
		//Get name from HashMap
		assert studentData != null;
		String nameStudent = studentData.get(KEY_NAME);
		//Get Profile Photo Link
		String profilePhotoStudent = studentData.get(KEY_PROFILE_LINK);
		//Get Bio
		String bioStudent = studentData.get(KEY_BIO);
		//Get Social Link
		final String sociallinkStudent = studentData.get(KEY_SOCIAL_LINK);
		
		//SetUP Thread Policy
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Call ImageLoader and set Profile Image from Server
//		profileImg.setImageBitmap(getBitmapFromURL(profilePhotoStudent));

		/*Call ImageLoader and set Profile Image from Server

		@Call AsyncTask<Param> new LoadData(URL, ImageView, Context, imageWidth, imageHeight).execute(); for load a image from Remote place, with Resized size in
		@Call AsyncTask<Param> new LoadData(URL, ImageView, Context).execute(); for load a image from Remote place, no Image Adjustment

		Keep in mind, you can't do Network Operation in same Activity Thread, you need to have it run on another Thread or add this code >>>: StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); ,hence it is bad idea, the activity will not shown untill this network operation is completed.
		SO, we have to set up AsyncTask to do network operation in Background.

		 */

		new LoadData(profilePhotoStudent, profileImg, this, 120, 120).execute();
		
		/*let's set all value and handle click event for elements */
		
		name.setText(nameStudent);
		bio.setText(bioStudent);
		
		socialLink.setOnClickListener(p1 -> {
			CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
			CustomTabsIntent customTabsIntent = builder.build();
			customTabsIntent.launchUrl(this, Uri.parse(sociallinkStudent));
		});
		
		showCode.setOnClickListener(new View.OnClickListener(){

				private String code;
				@Override
				public void onClick(View p1) {



					//Dirty Hack! Try not to laugh :p

					if(isShown == 1){
						for (Map.Entry<String, String> data : studentData.entrySet()) {
							code += data.getKey() + "   " + data.getValue() + "\n";
							isShown = 2;
						}

					}
					else{
							isShown = 2 ;
					}


					//Show Dialog that Contains all data received.

					LayoutInflater inflater = LayoutInflater.from(StudentDetails.this);
					View view = inflater.inflate(R.layout.alart_dialog_layout, null);

					TextView textview = view.findViewById(R.id.textmsg);
					textview.setText(code);
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentDetails.this);
					alertDialog.setTitle("Here's Data that Activity Received");
					alertDialog.setView(view);
					alertDialog.setPositiveButton("Done", (dialogInterface, i) -> dialogInterface.cancel());
					AlertDialog alert = alertDialog.create();
					alert.show();

				}


		});

					profileImg.setOnClickListener(p2 -> Toast.makeText(StudentDetails.this, sociallinkStudent, Toast.LENGTH_SHORT).show());



	}
	
	//Get Image from URL
	
	@Deprecated
	public static Bitmap getBitmapFromURL(String src) {
		try {
			Log.e("src",src);
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			Log.e("Bitmap","returned");
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Exception", Objects.requireNonNull(e.getMessage()));
			return null;
		}
	}
	
	
	
	
}
