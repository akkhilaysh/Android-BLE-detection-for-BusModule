package org.altbeacon.probbc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SelectBatchActivity extends Activity {

    ListView  selectionList;
    SBAdapter adapter;
    ArrayList<SelectBatchModel> batchList;
    String[] batchNames=new String[10];
    String[] schoolNames=new String[10];
    String[] batchId=new String[20];
    SwipeRefreshLayout swipe;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_batch);
        selectionList=(ListView)findViewById(R.id.listView);
        batchList=new ArrayList<SelectBatchModel>();
        swipe=(SwipeRefreshLayout)findViewById(R.id.select_batch_swipe);
      //  adapter=new SBAdapter(getApplicationContext(),R.layout.row_layout);

       new SelectBatchTask().execute();
        swipe.setColorSchemeColors(Color.BLUE, Color.RED, Color.LTGRAY, Color.CYAN);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myOperation();
                swipe.setRefreshing(false);
            }
        });
        selectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent SelectBatchListIntent=new Intent(SelectBatchActivity.this,StudentListActivity.class);

                //Passing values to next activity
                    SelectBatchListIntent.putExtra("Batch",batchNames[position]);
                    SelectBatchListIntent.putExtra("School",schoolNames[position]);
                    SelectBatchListIntent.putExtra("BatchID", batchId[position]);


                //starting StudentListActivty
                    startActivity(SelectBatchListIntent);
                finish();



            }

        });

    }
    public void myOperation(){
        if (batchList!=null)
        {batchList.clear();}
        new SelectBatchTask().execute();
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
    public class SelectBatchTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {


            String result = "";

            try {
                SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, 0);
                String bbcloginid=sharedPreferences.getString(LoginActivity.BBCLoginID,"4");

                URL url = new URL("http://ws.eighty20technologies.com/StudentService/Service1.svc/GetLoginBatch/" +
                        "?BBCLoginID="+bbcloginid);
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
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NullPointerException e) {

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IllegalStateException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                JSONArray jsonArray = new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    batchNames[i]=c.getString("BatchName");
                    schoolNames[i]=c.getString("SchoolName");
                    batchId[i]=c.getString("BatchID");
                    SelectBatchModel sbm = new SelectBatchModel(c.getString("BatchName")+" Batch", c.getString("BatchID"),c.getString("BatchAlias"),c.getString("SchoolName"));
                  //  Log.d("Adpter",c.getString("SchoolName"));
                    batchList.add(sbm);
                }
               // Log.d("Adpter","DemoAdpter");
                adapter=new SBAdapter(SelectBatchActivity.this,batchList);
                selectionList.setAdapter(adapter);

               // Log.d("Adpter","DemoAdpter");

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }



    }
}
