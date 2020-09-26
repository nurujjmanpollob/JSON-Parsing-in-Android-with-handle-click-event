package com.nurujjamanpollob.jsonparsing;
import android.annotation.SuppressLint;
import android.os.*;
import android.graphics.*;
import android.widget.*;
import java.net.*;
import java.io.*;
import android.content.*;
import android.app.*;


public class LoadData extends AsyncTask<Void, Void, Bitmap>
{
	
	String url;
	@SuppressLint("StaticFieldLeak")
	ImageView imageView;
	@SuppressLint("StaticFieldLeak")
	Context context;
	ProgressDialog pDialog;
	int imageWidth;
	int imageHeight;

	//Deprecated this function
	@Deprecated
	public LoadData(String url, ImageView imageView, Context context){
		
		this.url = url;
		this.imageView = imageView;
		this.context = context;
		
	}
	
	public LoadData(String url, ImageView imageView, Context context, int imageWidth, int imageHeight){
		
		this.url = url;
		this.imageView = imageView;
		this.context = context;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		
	}

	@Override
	protected void onPreExecute()
	{
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Fetching Image from server...");
		pDialog.setCancelable(false);
		pDialog.show();
		super.onPreExecute();
	}
	
	

	@Override
	protected Bitmap doInBackground(Void[] p1)
	{
		
		try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
				.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
			return BitmapFactory.decodeStream(input);
			
			
			
			
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap result)
	{
		if(pDialog.isShowing())pDialog.dismiss();
		
		if(imageWidth >= 1 && imageHeight >= 1){
			imageView.setImageBitmap(getResizeBitmap(result, imageWidth, imageHeight));
			
		}else{
			
			imageView.setImageBitmap(result);
			
		}
		
		
		
		super.onPostExecute(result);
	}
	

	
	public Bitmap getResizeBitmap(Bitmap bm, int newWidth, int newHeight) {
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
