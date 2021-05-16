package com.kernelkid.playfab;

import android.app.Activity;
import android.content.Intent;
import android.net.http.HttpsConnection;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.playfab.PlayFabAdminModels;
import com.playfab.PlayFabAuthenticationAPI;
import com.playfab.PlayFabClientAPI;
import com.playfab.PlayFabClientModels;
import com.playfab.PlayFabErrors;
import com.playfab.PlayFabErrors.PlayFabResult;
import com.playfab.PlayFabServerModels;
import com.playfab.PlayFabSettings;
import com.playfab.internal.PlayFabHTTP;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.*;
import com.google.gson.reflect.*;

import java.util.concurrent.FutureTask;

import static com.playfab.PlayFabClientAPI.*;

public class PlayFabAuthentication extends AsyncTask<String, Void, String> {

   static HttpPost postRequest=null;
   Activity activity=null;
   String sessionTicket=null;
   String error=null;
    @Override
    protected String doInBackground(String... params) {

        String name=params[0];
        String pass=params[1];
        String id=params[2];

        //if you need you can increase the number of times it is contacting the playfab api to login and getting the friends list
        for(int i=0;i<1;i++) {
                //Toast.makeText(g,"test",Toast.LENGTH_LONG).show();
                //login();
                loginApi(name,pass,id);
         /*   try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
       // TextView txt = (TextView) findViewById(R.id.output);
       // txt.setText("Executed");
        return null;
    }

    private String  loginApi(String name, String pass, String id) {
        ArrayList<String> friendsName=new ArrayList<>();
        PlayFabClientModels.LoginWithEmailAddressRequest request= new PlayFabClientModels.LoginWithEmailAddressRequest();
        request.Email=name;
        request.Password=pass;
        request.TitleId=id;

        //login starts

        PlayFabResult<PlayFabClientModels.LoginResult> result  = LoginWithEmailAddress(request);
       // System.out.println(result.Result);
        if(result.Result==null){
            if(result.Error!=null){
                error="Error: "+result.Error.errorMessage;
               // Toast.makeText(activity,"Error "+result.Error.errorMessage,Toast.LENGTH_LONG).show();
            }
           return "";
        }
        sessionTicket=result.Result.SessionTicket;
        System.out.println("Session Ticket:  " +sessionTicket);

        //login ends
        if(sessionTicket==null){
            error="Error: Session Ticket is empty ";
          //  Toast.makeText(activity,"Error :Session Ticket is empty ",Toast.LENGTH_LONG).show();
        }


        //getting the friends list

        PlayFabClientModels.GetFriendsListRequest request1=new PlayFabClientModels.GetFriendsListRequest();
        request1.IncludeFacebookFriends=true;
        request1.IncludeSteamFriends=true;

        try {

           // PlayFabResult<PlayFabClientModels.GetFriendsListResult> list = GetFriendsList(request1);
           // System.out.println("firne lsit88 "+list.Error.errorMessage);


            FutureTask<Object> task = PlayFabHTTP.doPost("https://"+id+".playfabapi.com/Client/GetFriendsList", request, "X-Authorization", PlayFabSettings.ClientSessionTicket);
            task.run();
            Object httpResult = task.get();
            if (httpResult instanceof PlayFabErrors.PlayFabError) {
                PlayFabErrors.PlayFabError errorF = (PlayFabErrors.PlayFabError)httpResult;
                PlayFabResult errorMessage = new PlayFabResult<PlayFabClientModels.GetFriendsListResult>();
                errorMessage.Error = errorF;
                error="Error: Getting friends list "+errorMessage.Error.errorMessage;
                //Toast.makeText(activity,"Error getting friends list "+errorMessage.Error.errorMessage,Toast.LENGTH_LONG).show();
                //System.out.println("fail "+errorMessage.Error.errorMessage );
            }
            else{
                String resultRawJson = (String) httpResult;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                PlayFabErrors.PlayFabJsonSuccess<PlayFabClientModels.GetFriendsListResult> resultData = gson.fromJson(resultRawJson, new TypeToken<PlayFabErrors.PlayFabJsonSuccess<PlayFabClientModels.GetFriendsListResult>>(){}.getType());
                PlayFabClientModels.GetFriendsListResult successResult = resultData.data;

              //  PlayFabResult<PlayFabClientModels.GetFriendsListResult> pfResult = new PlayFabResult<PlayFabClientModels.GetFriendsListResult>();
                //pfResult.Result = successResult;
                for(int i=0;i<successResult.Friends.size();i++){
                    friendsName.add(successResult.Friends.get(i).TitleDisplayName);
                   // System.out.println("friend  "+i+": Name:"+successResult.Friends.get(i).TitleDisplayName );

                    if(activity!=null){
                        Intent intent=new Intent(activity,FriendList.class);
                        intent.putExtra("FriendsList",friendsName);
                        activity.startActivity(intent);

                        //to finish the main activity . comment below line to avoid it.
                        activity.finish();

                    }else{
                        error="Error: Activity is null ";
                        //Toast.makeText(activity,"Error :Activity is null ",Toast.LENGTH_LONG).show();
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if(error!=null) {
            Log.e("permission", "error  "+error);
            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }


    private static  void login() {
        HttpClient httpClient = new DefaultHttpClient();
      //  CloseableHttpClient closeableHttpClient=
       // HttpsConnection connection=new HttpsConnection();
        try
        {
          //  LoginWithEmailAddress();
            Log.v("permission", "testing 1");
            //Define a postRequest request
            postRequest = new HttpPost("https://titleId.playfabapi.com/Client/LoginWithEmailAddress");

            //postRequest.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
            //postRequest.setHeader("Content-Length", "0");
            postRequest.setHeader("Accept", "*/*");
            postRequest.setHeader("Connection", "keep-alive");
            postRequest.setHeader("Accept-Encoding", "gzip, deflate, br");
            postRequest.setHeader("Content-Type", "application/json; utf-8");
            postRequest.setHeader("User-Agent", "PostmanRuntime/7.28.0");
            postRequest.setHeader("Host", "59c0f.playfabapi.com/");



          //  postRequest.setHeader("Content-Type", "application/json; utf-8");
           // HttpPost postRequest = new HttpPost("http://www.http2demo.io/");


           // PlayFabAuthenticationAPI.GetEntityTokenAsync(postRequest);
            //Set the API media type in http content-type header
           // postRequest.addHeader("content-type", "application/xml");

            //Set the request post body
            // StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
            // postRequest.setEntity(userEntity);;
           // postRequest.setParams(new BasicHttpParams().setParameter("Email","bharani19797@gmail.com").setParameter("Password", "Yogi@19797").setParameter("TitleId", "59C0F"));
            postRequest.getParams().setParameter("Email","bharfddani19797@gmail.com");
            postRequest.getParams().setParameter("Password", "Yogi@19797");
            postRequest.getParams().setParameter("TitleId", "59C0F");

            httpClient.getParams().setParameter("Email","bharani1dd9797@gmail.com");
            httpClient.getParams().setParameter("Password", "Yogi@19797");
            httpClient.getParams().setParameter("TitleId", "59C0F");


            //Send the request; It will immediately return the response in HttpResponse object if any
           HttpResponse response = httpClient.execute(postRequest);

            //verify the valid error code first
            int statusCode = response.getStatusLine().getStatusCode();
           if (statusCode != 200)
            {
              //  Log.e("permission", response.setStatusCode());
                Log.e("permission", response.getParams().toString());
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
            //Important: Close the connect
            httpClient.getConnectionManager().shutdown();
        }
    }

    public void setActivity(MainActivity mainActivity) {
        activity=mainActivity;
    }
}

