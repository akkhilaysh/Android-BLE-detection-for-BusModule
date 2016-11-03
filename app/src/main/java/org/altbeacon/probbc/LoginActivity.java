package org.altbeacon.probbc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {

    EditText user,pass;
    Button btnLogin;
    ProgressDialog dialog;
    String userid,passw;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String BBCLoginID = "bbcLoginid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user=(EditText)findViewById(R.id.login_email);
        pass=(EditText)findViewById(R.id.login_password);
        btnLogin=(Button)findViewById(R.id.btnLogin);

        user.setText("ashwaths99@gmail.com");
        pass.setText("development");

        if (!isConnected()) {

            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                    .create();

            // Setting Dialog Title
            alertDialog.setTitle("Error");

            // Setting Dialog Message
            alertDialog
                    .setMessage("OOPS! It seems Internet connection is lost or too slow. Please try again.");

            // Setting Icon to Dialog
           alertDialog.setIcon(android.R.drawable.stat_notify_error);


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    // Toast.makeText(getApplicationContext(),
                    // "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid=user.getText().toString();
                passw=pass.getText().toString();

                if (userid.equals("") || passw.equals("")) {
                    Toast.makeText(LoginActivity.this, "None of the fields should be empty", Toast.LENGTH_LONG).show();
                } else if(!(isValidEmail(userid))){
                    Toast.makeText(LoginActivity.this, "Invalid Email ID!", Toast.LENGTH_LONG).show();

                }else if (isConnected()) {

                    dialog=new ProgressDialog(LoginActivity.this,R.style.AppTheme);
                    dialog.setCancelable(false);
                    dialog.setProgressStyle(android.R.style.Widget_ProgressBar);
                    dialog.setMessage("Logging in....");
                    dialog.show();

                    new AuthenticateUser().execute();


                }

            }
        });

    }
    public boolean isConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())

            return true;
        else
            return false;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public class AuthenticateUser extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            try {
                URL url = new URL("http://ws.eighty20technologies.com/StudentService/Service1.svc/GetAuthenticate/?Email="
                        + userid + "&Password=" + passw+"&LoginType=1");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                result = convertStreamToString(stream);


            } catch (UnknownHostException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SocketTimeoutException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (NullPointerException e) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IllegalStateException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",

                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                }
                runOnUiThread(new Runnable() {
                    public void run() {

                        /*Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again5.",
                                Toast.LENGTH_SHORT).show();*/
                    }
                });

            }

            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            try {


                if (!(result.equals("0"))) {

                    SharedPreferences sharedPreferences=getSharedPreferences(MyPREFERENCES,0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(BBCLoginID,result);
                    editor.commit();
                    Toast.makeText(LoginActivity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, SelectBatchActivity.class));
                    finish();

                } else if (result.equals("0")) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(
                            LoginActivity.this,
                            "Invalid credentials!  User name or password does not match",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(
                            LoginActivity.this,
                            "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    public void run() {


                    }
                });

            }
        }

    }

}
