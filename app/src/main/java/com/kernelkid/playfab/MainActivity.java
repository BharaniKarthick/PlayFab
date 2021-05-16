package com.kernelkid.playfab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.playfab.*;
import com.playfab.internal.PlayFabHTTP;


import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.FutureTask;

import static com.playfab.PlayFabClientAPI.GetFriendsList;
import static com.playfab.PlayFabClientAPI.LoginWithEmailAddress;

public class MainActivity extends AppCompatActivity  {

    EditText userName;
    EditText pass;
    EditText id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName=findViewById(R.id.name);
        pass=findViewById(R.id.pass);
        id=findViewById(R.id.id);

      //  new PlayFabAuthentication().execute();
        //login();

    }

    public boolean  isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.v("permission", "Permission is granted");
                return true;
            } else {
                Log.v("permission", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.INTERNET},
                        1
                );
               return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permission", "Permission is already granted");
            return  true;
        }
    }




   /* private void login() {


        URL url=null;
        HttpURLConnection urlConnection=null;

        try {
            url = new URL("https://developer.android.com/reference/java/net/HttpURLConnection");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }
*/


      /*  private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }*/

    public void login(View view) {
        isStoragePermissionGranted();
        String user=userName.getText().toString();
        String passs=pass.getText().toString();
        String ids=id.getText().toString();
        //  loginApi(user,passs,ids);
        PlayFabAuthentication authentication=new PlayFabAuthentication();
        //setting the activity context
        authentication.setActivity(this);
        //calling the doInBackground method of playfabauthentication class
        authentication.execute(user,passs,ids);

        // new PlayFabAuthentication().execute(user,passs,ids);
    }

}




