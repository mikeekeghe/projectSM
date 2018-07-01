package com.cchd.talk2me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cchd.talk2me.common.ActivityBase;
import com.cchd.talk2me.model.RobotMessage;
import com.cchd.talk2me.model.RobotMessageDBHelper;
import com.cchd.talk2me.util.NetworkUtils;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainChatActivityNav extends ActivityBase
        implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String myDisplayName, dNameString, myDisplayemail;
    RobotMessageDBHelper.DatabaseHelper dbHandler;
    private RobotMessage myRobotMsg;

    private static final String TAG = "CHAT";
    Bundle extras;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences SP;
    private String strUserEmail;
    private MediaPlayer mp;
    private Context context = this;
    private String globalSearchResult;
    private int myRandomNumber;
    private String chat_partner_name;
    Random rn = new Random();
    private int myRandomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        makeTalk2meUsFetchQuery();
        int Low = 1;
        int High = 12;
        myRandomNum = rn.nextInt(High - Low) + Low;
        Log.d(TAG, "myRandomNum is :" + myRandomNum);

        mp = MediaPlayer.create(this, R.raw.click_button);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dbHandler = new RobotMessageDBHelper.DatabaseHelper(this, null, null, 1);


        layout =  findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);;
        myDisplayName = SP.getString("MY_DISPLAY_NAME","");
        myDisplayemail = SP.getString("email","");
        Log.d(TAG, "CHAT email from prefs is :" + myDisplayemail);
        Log.d(TAG, "CHAT myDisplayName from prefs is :" + myDisplayName);


        extras = getIntent().getExtras();
        if (extras != null) {
            strUserEmail = extras.getString("MY_USER_EMAIL");
            Log.d(TAG, "my personal user_Email_extra is :" + strUserEmail);
            String ExtramyDisplayName = extras.getString("MY_DISPLAY_NAME");
            Log.d(TAG, "MY_DISPLAY_NAME from extra is :" + ExtramyDisplayName);
            dNameString = extras.getString("DISPLAY_NAME");
            Log.d(TAG, "other person dNameString is :" + dNameString);
            myDisplayName = extras.getString("MY_DISPLAY_NAME");
            Log.d(TAG, "CHAT myDisplayName is :" + myDisplayName);
        }
        else{
            return;
        }

//        myRobotMsg = dbHandler.findRobotMessageByEmail(strUserEmail);

        Log.d(TAG, "myDisplayName at chat e is :" + myDisplayName);
        myDisplayName = "Emma";
//        myDisplayName = strUserEmail;
//        Log.d(TAG, "myDisplayName at chat e is :" + myDisplayName);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://talk2me-d88ef.firebaseio.com/messages/" + myDisplayName + "_" + chat_partner_name);
        reference2 = new Firebase("https://talk2me-d88ef.firebaseio.com/messages/" + chat_partner_name + "_" + myDisplayName);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = MediaPlayer.create(context, R.raw.click_button);
                    } mp.start();
                } catch(Exception e) { e.printStackTrace(); }
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", myDisplayName);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(myDisplayName)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(dNameString + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    private void makeTalk2meUsFetchQuery() {
        URL talk2meSearchUrl = NetworkUtils.buildUserList_one_Url(String.valueOf(myRandomNum));
        Log.d( TAG, "talk2meSearchUrl is: " + talk2meSearchUrl.toString());
        // COMPLETED (4) Create a new talk2meQueryTask and call its execute method, passing in the url to query
        new talk2meQueryTask().execute(talk2meSearchUrl);
    }

    public class talk2meQueryTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (2) Override the doInBackground method to perform the query. Return the results. (Hint: You've already written the code to perform the query)
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String talk2meSearchResults = null;
            try {
                talk2meSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                Log.d( TAG, "talk2meSearchResults is : " + talk2meSearchResults.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return talk2meSearchResults;
        }

        // COMPLETED (3) Override onPostExecute to display the results
        @Override
        protected void onPostExecute(String talk2meSearchResults) {
            if (talk2meSearchResults != null && !talk2meSearchResults.equals("")) {
                Log.d( TAG, "talk2meSearchResults is :" + talk2meSearchResults);
                globalSearchResult = talk2meSearchResults;
                loadResultView();
            }
        }
    }

    private void loadResultView() {


        try {

            // get JSONObject from JSON file
            JSONObject obj = new JSONObject(globalSearchResultMethod());
            // fetch JSONArray named users
            JSONArray usersArray = obj.getJSONArray("users");
            // implement for loop for getting users list data
            for (int i = 0; i < usersArray.length(); i++) {
                // create a JSONObject for fetching single user data
                JSONObject srDetail = usersArray.getJSONObject(i);
                // fetch email and name and store it in arraylist

                chat_partner_name = srDetail.getString("display_name");
                Log.d(TAG, "display_name is: " + chat_partner_name);

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String globalSearchResultMethod() {
        return globalSearchResult;
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(MainChatActivityNav.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
            textView.setPadding(10,10,10,10);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
            textView.setPadding(10,10,10,10);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }




    private void prefSetDefaults() {
        SP = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor editor = SP.edit();
        editor.remove("logged_in_status");
        editor.remove("email");
        editor.remove("displayName");
        editor.apply();
    }

    private void launchLoginActivity() {
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(this, R.raw.click_button);
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }

        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_chat_activity_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menuLogout){
            extras = null;
            prefSetDefaults();
            launchLoginActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            // Handle the camera action
        } else if (id == R.id.nav_tell) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
