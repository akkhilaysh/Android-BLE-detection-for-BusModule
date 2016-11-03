package org.altbeacon.probbc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
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
import java.util.Collection;

public class StudentListActivity extends Activity implements BeaconConsumer {


    String MAC;
    int list_size;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    protected static final String TAG = "StudentListActivity";
    ArrayList<String> MACIDS=new ArrayList<String>();
    String batchID_SL;
    ListView studentList;
    SLAdapter studentListAdapter;
    SwipeRefreshLayout studentListSwipe;
    TextView schoolName,batchName,status_BLE;
    ArrayList<StudentListProvider> student_List_array;
    String[] macs=new String[30];
    String[] students=new String[30];
    int[] counter=new int[30];
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        student_List_array=new ArrayList<StudentListProvider>();
        schoolName=(TextView)findViewById(R.id.student_list_school_name);
        batchName=(TextView)findViewById(R.id.student_list_batch);
        studentList=(ListView)findViewById(R.id.student_list);
        studentListSwipe=(SwipeRefreshLayout)findViewById(R.id.student_list_swipe);
       // studentListAdapter=new SLAdapter(getApplicationContext(),R.layout.row_layout2);
        status_BLE=(TextView)findViewById(R.id.s_childStatus);
        for(int i=0;i<20;i++)
            counter[i]=0;

        Bundle bundle=getIntent().getExtras();

        String school=bundle.getString("School");
        String batch=bundle.getString("Batch");
        batchID_SL = bundle.getString("BatchID");
        schoolName.setText(school);
        batchName.setText(batch + " Batch");

        getActionBar().setHomeButtonEnabled(true);
        studentListSwipe.setColorSchemeColors(Color.BLUE, Color.RED, Color.DKGRAY, Color.GREEN);
        verifyBluetooth();
        beaconManager.bind(this);
        Log.d("MACCR",MAC+" ");
        new StudentListTask().execute();

        studentListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onBeaconServiceConnect();
                //Toast.makeText(getApplication(),"MAC is:"+MAC,Toast.LENGTH_SHORT).show();
                myOperation();
                MAC=null;
                studentListSwipe.setRefreshing(false);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        studentList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int positions[]=new int[10],i=0;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount=studentList.getCheckedItemCount();
                mode.setTitle(checkedCount+" Selected");
                positions[i]=position;
                i++;
                studentListAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_main, menu);
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.onboard:
                     for(int j=0;j<positions.length;j++)
                     {
                         student_List_array.get(positions[i]).setStatus("ONBOARD");
                     }
                        mode.finish();
                        return true;
                        /*// Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listviewadapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                WorldPopulation selecteditem = listviewadapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listviewadapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;*/
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                studentListAdapter.removeSelection();
            }
        });
    }
    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("ProBBCBus Application")
                        .setContentText("An child is nearby.")
                        .setSmallIcon(R.drawable.logobbc);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, StudentListActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
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
    public class ScanCompleteTask extends AsyncTask<String,String,String>
{
    @Override
    protected String doInBackground(String... params) {
        for (int i=0;i<list_size;i++)
        {
            final String status=student_List_array.get(i).getStatus();

            Log.d("EXP1", status);
            }




        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
    public class StudentListTask extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                students=null;
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    macs[i]=c.getString("MACAdd");

                    String st = c.getString("StudentStatusDetail");
                    for(String m:MACIDS)
                    {
                        if(m.equals(macs[i])) {
                        st="ONBOARD";
                            counter[i]++;
                        }else if(counter[i]==0){
                            st="ABSENT";
                        }

                    }

                    StudentListProvider slp = new StudentListProvider(c.getString("StudentName"),st,c.getString("MACAdd"));
                    student_List_array.add(slp);
                    list_size=student_List_array.size();
                }

                studentListAdapter=new SLAdapter(StudentListActivity.this,student_List_array);
                studentList.setAdapter(studentListAdapter);
                studentList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);



            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==1",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            try {

                URL url = new URL("http://ws.eighty20technologies.com/StudentService/Service1.svc/" +
                        "GetStatusByBatchID/?BatchID="+batchID_SL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());



                result = convertStreamToString(stream);

            } catch (UnknownHostException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==2",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SocketTimeoutException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==3",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NullPointerException e) {

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==4",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IllegalStateException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==5",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(
                                getBaseContext(),
                                "OOPS! It seems Internet connection is lost or too slow. Please try again.==6",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            return result;
        }
    }

    public void myOperation(){
        if (student_List_array!=null)
        { student_List_array.clear();}

        new StudentListTask().execute();
    }

    //Myoperation 2 :Go action
    public void myOperation2(){
        new ScanCompleteTask().execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Beacon firstBeacon = beacons.iterator().next();
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    if (firstBeacon.getDistance() < 5) {
                        // logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away and is having " + firstBeacon.getRssi() + "   " + firstBeacon.getBluetoothAddress() + "\n");

                        MAC = firstBeacon.getBluetoothAddress();
                        MACIDS.add(MAC);
                        Log.d("MAC", MAC);
                    }

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
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
        if (id == R.id.action_change_child) {
            startActivity(new Intent(StudentListActivity.this,SelectBatchActivity.class));
            finish();
            return true;
        }else if(id==R.id.action_logout)
        {
            startActivity(new Intent(StudentListActivity.this,LoginActivity.class));
            finish();
            return true;
        }else if(id==R.id.action_contact)
        {

            return true;
        }else if (id==R.id.action_completescan)
        {
            myOperation2();
        return true;
        }else if (id==R.id.action_reachedschool)
        {
            return true;

        }else if (id==R.id.action_exit)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}


/*
package org.altbeacon.probbc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class StudentListActivity extends Activity implements BeaconConsumer {

    String MAC,batchID_SL;
    ListView studentList;
    SLAdapter studentListAdapter;
    SwipeRefreshLayout studentListSwipe;
    TextView schoolName,batchName,status_BLE;
    StudentListProvider slp1;
    ArrayList<StudentListProvider> student_List_array;
    StudentListProvider slp2;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        student_List_array=new ArrayList<>();
        schoolName=(TextView)findViewById(R.id.student_list_school_name);
        batchName=(TextView)findViewById(R.id.student_list_batch);
        studentList=(ListView)findViewById(R.id.student_list);
        studentListSwipe=(SwipeRefreshLayout)findViewById(R.id.student_list_swipe);
       // studentListAdapter=new SLAdapter(getApplicationContext(),R.layout.row_layout2);
        status_BLE=(TextView)findViewById(R.id.s_childStatus);

        Bundle bundle=getIntent().getExtras();

        String school=bundle.getString("School");
        String batch=bundle.getString("Batch");
        batchID_SL=bundle.getString("BatchID");
        schoolName.setText(school);
        batchName.setText(batch + " Batch");

        getActionBar().setHomeButtonEnabled(true);
        studentListSwipe.setColorSchemeColors(Color.BLUE, Color.RED, Color.DKGRAY, Color.GREEN);



        slp1=new StudentListProvider("Harshad Shinde","Reached","");
        slp2=new StudentListProvider("Ankit Verma","Home","");

        student_List_array.add(slp1);
        student_List_array.add(slp2);
        studentListAdapter=new SLAdapter(StudentListActivity.this,student_List_array);
        studentList.setAdapter(studentListAdapter);

        studentListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onBeaconServiceConnect();
                Toast.makeText(getApplication(),"MAC is:"+MAC,Toast.LENGTH_SHORT).show();
                myOperation();
                MAC=null;
                studentListSwipe.setRefreshing(false);
            }
        });
    }

    public void myOperation(){
        if(MAC != null) {
            slp1.setStatus("IN");
            slp2.setStatus("OUT");
            studentList.setAdapter(studentListAdapter);
        }
        else{
            slp1.setStatus("OUT");
            slp2.setStatus("IN");
            studentList.setAdapter(studentListAdapter);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Beacon firstBeacon = beacons.iterator().next();
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    if (firstBeacon.getDistance() < 3) {
                        // logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away and is having " + firstBeacon.getRssi() + "   " + firstBeacon.getBluetoothAddress() + "\n");
                        Log.d("MAC",firstBeacon.getBluetoothAddress());
                        MAC=firstBeacon.getBluetoothAddress();
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
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
        if (id == R.id.action_change_child) {
            startActivity(new Intent(StudentListActivity.this,SelectBatchActivity.class));
            finish();
            return true;
        }else if(id==R.id.action_logout)
        {
            startActivity(new Intent(StudentListActivity.this,LoginActivity.class));
            finish();
            return true;
        }else if(id==R.id.action_contact)
        {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
*/

