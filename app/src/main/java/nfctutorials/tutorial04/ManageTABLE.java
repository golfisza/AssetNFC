package nfctutorials.tutorial04;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by songkran on 10/11/2558.
 */
public class ManageTABLE {
    //Explicit ประกาศตัวแปร
    private MyOpenHelper objMyOpenHelper;
    private SQLiteDatabase writeSqLiteDatabase, readSqLiteDatabase;


    public static final String TABLE_USER = "userTABLE";
    public static final String TABLE_DEVICE = "deviceTABLE";
    public static final String TABLE_ASSIGNED = "assignTABLE";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRSTNAME = "Firstname";
    public static final String COLUMN_LASTNAME = "Lastname";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_USERTYPE = "UserType";
    public static final String COLUMN_TAGNFC = "tagNFC";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_LOCATION = "Location";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_USERID = "userTABLE_Golf_user_id";
    public static final String COLUMN_DEVICEID = "deviceTABLE_Golf_device_id";
    public static final String COLUMN_ASSIGNEDDATE = "Assigned_date";
    public static final String COLUMN_CHECKSTATUS = "checkStatus";
    public static final String COLUMN_COMMENT = "Comment";


    public ManageTABLE(Context context) {
        //create and Connected
        objMyOpenHelper = new MyOpenHelper(context);
        writeSqLiteDatabase = objMyOpenHelper.getWritableDatabase();
        readSqLiteDatabase = objMyOpenHelper.getReadableDatabase();

    }//Constructor

    //serch tag nfc
    public String[] searchTagNFC(String strNFC) {
        try {
            String[] strResult = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_DEVICE,
                    new String[]{COLUMN_ID, COLUMN_TAGNFC, COLUMN_NAME, COLUMN_LOCATION, COLUMN_STATUS},
                    COLUMN_TAGNFC + "=?",
                    new String[]{String.valueOf(strNFC)},
                    null, null, null, null);
            if (objCursor != null) {
                if (objCursor.moveToFirst()) {
                    strResult = new String[5];
                    strResult[0] = objCursor.getString(0);
                    strResult[1] = objCursor.getString(1);
                    strResult[2] = objCursor.getString(2);
                    strResult[3] = objCursor.getString(3);
                    strResult[4] = objCursor.getString(4);


                }//if
            }//if
            objCursor.close();
            return strResult;


        } catch (Exception e) {
            return null;
        }


        //return new String[0];
    }


    //Add New Value to SQLite
    public long addUser(String strFirstName, String strLastName, String strUsername, String strPassword, String strUserType) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_FIRSTNAME, strFirstName);
        objContentValues.put(COLUMN_LASTNAME, strLastName);
        objContentValues.put(COLUMN_USERNAME, strUsername);
        objContentValues.put(COLUMN_PASSWORD, strPassword);
        objContentValues.put(COLUMN_USERTYPE, strUserType);

        return writeSqLiteDatabase.insert(TABLE_USER, null, objContentValues);
    }

    public long addDevice(String strTAGNFC, String strName, String strLocation, String strStatus) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_TAGNFC, strTAGNFC);
        objContentValues.put(COLUMN_NAME, strName);
        objContentValues.put(COLUMN_LOCATION, strLocation);
        objContentValues.put(COLUMN_STATUS, strStatus);

        return writeSqLiteDatabase.insert(TABLE_DEVICE, null, objContentValues);
    }

    public long addAssign(String strUserID, String strDeviceID, String strAssignDate, String strCheckStatus, String strComment) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_USERID, strUserID);
        objContentValues.put(COLUMN_DEVICEID, strDeviceID);
        objContentValues.put(COLUMN_ASSIGNEDDATE, strAssignDate);
        objContentValues.put(COLUMN_CHECKSTATUS, strCheckStatus);
        objContentValues.put(COLUMN_COMMENT, strComment);

        return writeSqLiteDatabase.insert(TABLE_ASSIGNED, null, objContentValues);
    }

    public String[] AuthenUser(String strUser) {

        //ตรวจสอบ user
        try {

            String arrayDATA[] = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_USER, new String[]{COLUMN_ID, COLUMN_FIRSTNAME, COLUMN_LASTNAME, COLUMN_USERNAME,COLUMN_PASSWORD,COLUMN_USERTYPE }, COLUMN_USERNAME + "=?", new String[]{String.valueOf(strUser)}, null, null, null, null);
            if (objCursor != null) {

                if (objCursor.moveToFirst()) {

                    arrayDATA = new String[objCursor.getColumnCount()];
                    arrayDATA[0] = objCursor.getString(0);
                    arrayDATA[1] = objCursor.getString(1);
                    arrayDATA[2] = objCursor.getString(2);
                    arrayDATA[3] = objCursor.getString(3);
                    arrayDATA[4] = objCursor.getString(4);

                }

            }

            objCursor.close();
            return arrayDATA;
        } catch (Exception e) {

            return null;

        }
    }
}// Main Class
