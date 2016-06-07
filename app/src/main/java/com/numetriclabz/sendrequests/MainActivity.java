package com.numetriclabz.sendrequests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    public static Activity activity;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        String Api = "GetAll";

        new GetClass(this).execute("GetAll");
    }

    /*This handler defines the onClick behaviour for each added button*/
    View.OnClickListener handleOnClick(final String ID) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                new GetClass(MainActivity.activity).execute("InfoGet", ID);
            }
        };
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public GetClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (params.length < 1) {
                    return null;
                }
                final TextView outputView = (TextView) findViewById(R.id.showOutput);
                String url_string = "http://people-count.azurewebsites.net/api/";
                if (params.length > 0) {
                    for (String component : params) {
                        url_string = url_string + component + "/";
                    }
                }
                URL url = new URL(url_string);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'Get' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator") + "Type " + "GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                Log.d("Resopnse", "" + br);
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                final JSONObject[] jsonObj = new JSONObject[1];
                if(params[0].equals("GetAll")) {
                    jsonObj[0] = new JSONObject(responseOutput.toString());
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // This is where you do your work in the UI thread.
                        // Your worker tells you in the message what to do.
                        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if(jsonObj[0]==null){
                            Toast toast = Toast.makeText(getApplicationContext(), responseOutput.toString(), Toast.LENGTH_LONG);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setTextSize(25);
                            toast.show();
                            return;
                        }
                        for (int i = 0; i < jsonObj[0].length(); i++) {
                            try {
                                Button b = new Button(MainActivity.activity);
                                b.setText(jsonObj[0].get(jsonObj[0].names().getString(i)).toString());
                                ll.gravity = Gravity.CENTER;
                                b.setLayoutParams(ll);
                                b.setOnClickListener(handleOnClick(jsonObj[0].names().getString(i).toString()));
                                ((LinearLayout) findViewById(R.id.myLayout)).addView(b);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        outputView.setText(output);
                        progress.dismiss();

                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}