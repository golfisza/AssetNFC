package nfctutorials.tutorial04;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends ActionBarActivity {

    //Explicit
    private ManageTABLE objManageTABLE;
    private EditText userEditText, passwordEditText;
    private String userString, passwordString, truePasswordString, firstNameString, userIDString;
    private MyAlertDialog objMyAlertDialog;
    private AlertDialog.Builder objAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bind Widget
        bindWidget();


        //Connect Database
        objManageTABLE = new ManageTABLE(this);

        //tester Add Value
        //testerAddValue();

        //delete All data
        deleteAllData();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();

    }//Main Method

    public void clickLogin(View view) {
        try {
            userString = userEditText.getText().toString().trim();
            passwordString = passwordEditText.getText().toString().trim();

            //check zero
            if (userString.equals("") || passwordString.equals("")) {

                //Have space
                objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.HaveSpace(LoginActivity.this);


            } else {

                //no space
                checkUserpassword();


            }
        } catch (Exception e) {

        }


    }

    private void checkUserpassword() {
        try {

            String[] arrayDATA = objManageTABLE.AuthenUser(userString);
            truePasswordString = arrayDATA[4];
            firstNameString = arrayDATA[1];
            userIDString = arrayDATA[0];


            if (passwordString.equals(truePasswordString)) {

                //พาสเวิร์ดใช่ไปไหนต่อ ????
                AlertAndIntent();


            } else {

                objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.UserPasswordNotTrue(LoginActivity.this);
            }

        } catch (Exception e) {
            objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.UserPasswordNotTrue(LoginActivity.this);
        }
    }

    private void AlertAndIntent() {

        objAlert = new AlertDialog.Builder(this);
        objAlert.setIcon(R.drawable.world);
        objAlert.setTitle("Successful");
        objAlert.setMessage("ยินดีต้อนรับ คุณ " + firstNameString);
        objAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);

            }
        });
        objAlert.show();
    }


    private void bindWidget() {
        userEditText = (EditText) findViewById(R.id.edtLoginUser);
        passwordEditText = (EditText) findViewById(R.id.edtLoginPassword);
    }

    private void synJSONtoSQLite() {

        //Setup Policy
        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);
        int intTimes = 1;
        while (intTimes <= 3) {
            //วน 2 รอบ จากตาราง
            //1.การ create inputStream
            InputStream objInputStream = null;
            String strJSON = null;
            HttpPost objHttpPost = null;
            String strUrlUserTABLE = "http://swiftcodingthai.com/golf/php_get_data_golf.php";
            String strUrldeviceTABLE = "http://swiftcodingthai.com/golf/php_get_data_device.php";
            String strUrlassignTABLE = "http://swiftcodingthai.com/golf/php_get_assign2_golf.php";

            try {
                //ป้องกัน fc
                HttpClient objHttpClient = new DefaultHttpClient();

                switch (intTimes) {
                    case 1:
                        objHttpPost = new HttpPost(strUrlUserTABLE);
                        break;
                    case 2:
                        objHttpPost = new HttpPost(strUrldeviceTABLE);
                        break;
                    case 3:

                        objHttpPost = new HttpPost(strUrlassignTABLE);
                }

                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();

            } catch (Exception e) {
                Log.d("Suthep", "InputSteram ==>" + e.toString());
            }
            //2.crate JSON string
            try {

                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
                String strLine = null;
                StringBuilder objStringBuilder = new StringBuilder();
                while ((strLine = objBufferedReader.readLine()) != null) {
                    objStringBuilder.append(strLine); //ตัดเชือกแต่ล่ะช่วง
                }

                objInputStream.close();
                strJSON = objStringBuilder.toString(); // ต่อเชื่อกแต่ล่ะช่วง

            } catch (Exception e) {
                Log.d("Suthep", "JSON String ==>" + e.toString());
            }

            //3. Update to SQLite
            try {

                JSONArray objJsonArray = new JSONArray(strJSON);
                for (int i = 0; i < objJsonArray.length(); i++) {

                    JSONObject object = objJsonArray.getJSONObject(i);

                    switch (intTimes) {
                        case 1:

                            //for userTABLE
                            String strFirstname = object.getString("Firstname");
                            String strLastname = object.getString("Lastname");
                            String strUsername = object.getString("Username");
                            String strPassword = object.getString("Password");
                            String strUserType = object.getString("UserType");
                            objManageTABLE.addUser(strFirstname, strLastname, strUsername, strPassword, strUserType);

                            break;
                        case 2:

                            //for deviceTABLE
                            String strTagNFC = object.getString("tagNFC");
                            String strName = object.getString("Name");
                            String strLocation = object.getString("Location");
                            String strStatus = object.getString("Status");
                            objManageTABLE.addDevice(strTagNFC, strName, strLocation, strStatus);

                            break;
                        case 3:
                            //for AssignTABLE
                            String strUserID = object.getString("userTABLE_Golf_user_id");
                            String strDeviceID = object.getString("deviceTABLE_Golf_device_id");
                            String strAssignDate = object.getString("Assigned_date");
                            String strCheckStatus = object.getString("checkStatus");
                            String strComment = object.getString("comment");
                            objManageTABLE.addAssign(strUserID, strDeviceID, strAssignDate, strCheckStatus, strComment);
                            break;

                    }

                }

            } catch (Exception e) {
                Log.d("Suthep", "Update ==>" + e.toString());
            }


            intTimes += 1;
        }//while


    }

    private void deleteAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("golf.db", MODE_PRIVATE, null);
        objSqLiteDatabase.delete("userTABLE", null, null);
        objSqLiteDatabase.delete("deviceTABLE", null, null);
        objSqLiteDatabase.delete("assignTABLE", null, null);
    }

    private void testerAddValue() {

        objManageTABLE.addUser("testFirstname", "testLastname", "testUser", "testPassword", "testUserType");
        objManageTABLE.addDevice("testTag", "testName", "testLocation", "testStatus");
        objManageTABLE.addAssign("testUserID", "testDeviceID", "testAssignedDate", "testCheckStatus", "testComment");


    }
}//Main Class
