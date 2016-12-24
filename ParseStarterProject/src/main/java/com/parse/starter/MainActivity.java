/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity {

    Switch riderDriverSwitch;
    EditText userNameEditText;

    public void getStarted(View view){

        String username = userNameEditText.getText().toString();


    String riderOrDriver = "rider";
        if(riderDriverSwitch.isChecked()){

            riderOrDriver = "driver";

        }
        ParseUser.getCurrentUser().put("riderOrDriver", riderOrDriver);
        ParseUser.getCurrentUser().put("username", username);

       redirect();


    }

    public void redirect(){
        if(ParseUser.getCurrentUser().getString("riderOrDriver").equals("rider")){
            Intent i = new Intent(getApplicationContext(),YourLocation.class);
            startActivity(i);
            Log.i("aaa","logged in as a rider:" + ParseUser.getCurrentUser().getUsername());
        } else {
            Intent i = new Intent(getApplicationContext(),DriverActivity.class);
            startActivity(i);
            Log.i("aaa", "logged in as a rider:" + ParseUser.getCurrentUser().getUsername());
        }
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    getSupportActionBar().hide();

   riderDriverSwitch = (Switch)findViewById(R.id.switch1);
   userNameEditText = (EditText)findViewById(R.id.userName);



      if (ParseUser.getCurrentUser() == null) {
          ParseAnonymousUtils.logIn(new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {
                  if(e == null){
                      Log.i("aaa", "login successful");
                  } else {
                      Log.i("aaa", "login failed");
                  }
              }
          });

      }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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

    return super.onOptionsItemSelected(item);
  }
}
