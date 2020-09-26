package com.nurujjamanpollob.jsonparsing;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


import static com.nurujjamanpollob.jsonparsing.Strings.KEY_NAME;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_BIO;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_PROFILE_LINK;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_SHOW_CODE;
import static com.nurujjamanpollob.jsonparsing.Strings.KEY_SOCIAL_LINK;



public class GetStudents extends AsyncTask<Void, Void, Void> {

    // JSON Node names
    private static final String TAG_STUDENTINFO = "studentsinfo";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_STUDENT_SOCIAL_LINK = "sociallink";
    private static final String TAG_BIO = "bio";
    private static final String TAG_PROFILE_LINK = "profilephoto";


    // Hashmap for ListView
    ArrayList<HashMap<String, String>> studentList;
    ProgressDialog pDialog;


    @SuppressLint("StaticFieldLeak")
    Context context;

    String url;

    @SuppressLint("StaticFieldLeak")
    ListView listView;

    public GetStudents(Context context, String url, ListView listView) {

        this.context = context;
        this.url = url;
        this.listView = listView;



    }

    @Override
    protected void onPreExecute() {


        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        super.onPreExecute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {


        // Creating service handler class instance
        WebRequest webreq = new WebRequest();


        // Making a request to url and getting response
        String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GET);


        Log.d("Response: ", "> " + jsonStr);

        studentList = ParseJSON(jsonStr);

        return null;


    }



    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);



        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();

        //Updating parsed JSON data into ListView



            ListAdapter adapter = new SimpleAdapter(
                    context, studentList,
                    R.layout.list_item, new String[]{TAG_NAME, TAG_EMAIL,
                    TAG_PHONE_MOBILE}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            listView.setAdapter(adapter);

            //Handling list item click event

			listView.setOnItemClickListener((p1, p2, positions, p4) -> {


                //Check for positions what listview item is selected then copy its key and value

                HashMap<String, String> selectedItem = studentList.get(positions);

                //Let's Extract value from selection!
                String socialLink = selectedItem.get(TAG_STUDENT_SOCIAL_LINK);
                String bio = selectedItem.get(TAG_BIO);
                String name = selectedItem.get(TAG_NAME);
                String profilelink = selectedItem.get(TAG_PROFILE_LINK);

                //Let's put all data to HashMap and send VIA String

                HashMap<String, String> studentDetail = new HashMap<>();
                studentDetail.put(KEY_SOCIAL_LINK, socialLink);
                studentDetail.put(KEY_NAME, name);
                studentDetail.put(KEY_BIO, bio);
                studentDetail.put(KEY_PROFILE_LINK, profilelink);

                //Send all data to StudentDetails.Java

                Intent intent = new Intent(context, StudentDetails.class);
                intent.putExtra(KEY_SHOW_CODE, studentDetail);
                context.startActivity(intent);




            });

    }








    private ArrayList<HashMap<String, String>> ParseJSON(String json) {

        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> studentList = new ArrayList<>();

                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray students = jsonObj.getJSONArray(TAG_STUDENTINFO);

                // looping through All Students
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String email = c.getString(TAG_EMAIL);
                    String address = c.getString(TAG_ADDRESS);
                    String gender = c.getString(TAG_GENDER);
                    String social = c.getString(TAG_STUDENT_SOCIAL_LINK);
                    String bio = c.getString(TAG_BIO);
                    String profilelnk = c.getString(TAG_PROFILE_LINK);

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject(TAG_PHONE);
                    String mobile = phone.getString(TAG_PHONE_MOBILE);
                    String home = phone.getString(TAG_PHONE_HOME);

                    // tmp hashmap for single student
                    HashMap<String, String> student = new HashMap<>();

                    // adding each child node to HashMap key => value
                    student.put(TAG_ID, id);
                    student.put(TAG_NAME, name);
                    student.put(TAG_EMAIL, email);
                    student.put(TAG_PHONE_MOBILE, mobile);
                    student.put(TAG_STUDENT_SOCIAL_LINK, social);
                    student.put(TAG_BIO, bio);
                    student.put(TAG_PROFILE_LINK, profilelnk);

                    // adding student to students list
                    studentList.add(student);


                }
                return studentList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;

        }
    }

}
