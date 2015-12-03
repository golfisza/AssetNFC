package nfctutorials.tutorial04;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    //Explicit
    private NfcAdapter nfcAdapter;
    private String tagNFCString,userIdString;
    private static final String TAG = "Suthep";
    private String nameString,locationString, statusString,selected,comment;
    private String[] select = {"good", "bad", "Etc"};
    SQLiteDatabase write;
    MyOpenHelper myOpenHelper;
    ManageTABLE mngTable;
    private int num;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(nfctutorials.tutorial04.R.layout.activity_main);

        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Create ListView
        createListView();


        myOpenHelper = new MyOpenHelper(this);
        write = myOpenHelper.getWritableDatabase();

    }   // Main Method

    private void createListView() {             //สร้าง list view

        String[] deviceStrings = null;
        userIdString = getIntent().getStringExtra("userID"); //รับค่า userid จากหน้า Login


        try {
            String[] strResult = null;

            SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("golf.db", MODE_PRIVATE, null);
            Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM assignTABLE WHERE checkStatus = " + "'UNCHECK' AND userTABLE_Golf_user_id = " +userIdString+ "  ",
                    null);
            objCursor.moveToFirst();


            strResult = new String[objCursor.getCount()];
            for (int i=0;i<objCursor.getCount();i++) {

                strResult[i] = objCursor.getString(objCursor.getColumnIndex("deviceTABLE_Golf_device_id"));

                Log.d("Suthep", "indexDevicer ==> " + strResult[i]);

                objCursor.moveToNext();
            }   // for

            Cursor obj2Cursor = objSqLiteDatabase.rawQuery("SELECT * FROM deviceTABLE", null);
            obj2Cursor.moveToFirst();
            deviceStrings = new String[strResult.length];
            for (int i=0;i<strResult.length;i++) {

                obj2Cursor.moveToPosition(Integer.parseInt(strResult[i]) - 1);
                deviceStrings[i] = obj2Cursor.getString(obj2Cursor.getColumnIndex("Name"));

                Log.d("Suthep", "nameDevice ==> " + deviceStrings[i]); // ตรวจสอบว่า device name มีอะไรบ้าง ด้วย logcat
                Log.d("Suthep", "userId==>" + userIdString); //ตรวจสอบว่าค่า userid ที่ส่งมามีค่าเท่าไร ด้วย logcat

            }   // for


        } catch (Exception e) {
            e.printStackTrace();
        }


        MyAdapter objMyAdapter = new MyAdapter(MainActivity.this, deviceStrings);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(objMyAdapter);


    }   // createListView

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }


    @Override //toast ว่าเจอ NFC
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NFC Intent!", Toast.LENGTH_SHORT).show();

            if (true) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                } else {
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText()+"");
                NdefMessage ndefMessage = createNdefMessage("");
                writeNdefMessage(tag, ndefMessage);
            }

        }
    }


    private void readTextFromMessage(NdefMessage ndefMessage) {    //อ่าน text จาก NFC

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];
            //สิ่งที่อ่านได้จาก tagNFC

            tagNFCString = getTextFromNdefRecord(ndefRecord);

            // txtTagContent.setText(tagNFCString);

            //Log.d(TAG, "NFC read ==> " + tagNFCString);
            Log.d(TAG, tagNFCString);
            searchMyNFC(tagNFCString);

        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }

    }//readTextFromMessage

    private void searchMyNFC(String tagNFCString) {
        try {
            ManageTABLE objManageTABLE = new ManageTABLE(this);
            String[] strMyResult = objManageTABLE.searchTagNFC(tagNFCString);
            nameString = strMyResult[2];
            locationString = strMyResult[3];
            statusString = strMyResult[4];
            Log.d("Suthep", "Name"  + nameString);  //แสดงค่าที่ได้ logcat
            Log.d("Suthep", "location"  + locationString);//แสดงค่าที่ได้ logcat
            Log.d("Suthep", "status" + statusString);//แสดงค่าที่ได้ logcat

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(nameString);
            builder.setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selected = select[i];
                    num = i;

                }
            });
            builder.setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Toast.makeText(getApplicationContext(), "สภาวะทรัพย์สิน" + selected, Toast.LENGTH_LONG);
                    Log.d("Suthep", "select===>" + selected);
                    Log.d("Suthep", "num ===" + num);
                    dialogInterface.dismiss();

                    //update SQLite จาก uncheck เป็น check
                    //   write.execSQL("UPDATE" + mngTable.TABLE_ASSIGNED +
                    //          "WHERE" +mngTable.);



                    if (num == (0)) {   //กำหนดเงื่อนไขเพื่อการกรอก comment

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle(nameString);
                        builder1.setMessage("สภาพทรัพย์สินปกติ");
                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder1.show();


                    } else {
                        AlertDialog.Builder commentBuilder1 = new AlertDialog.Builder(MainActivity.this);
                        commentBuilder1.setTitle(nameString);
                        commentBuilder1.setMessage("Comment");
                        final EditText input = new EditText(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        input.setLayoutParams(layoutParams);
                        commentBuilder1.setView(input);
                        commentBuilder1.setIcon(R.drawable.question);
                        commentBuilder1.setPositiveButton("OK !", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                comment = input.getText().toString();
                                Log.d("Suthep", "comment==>" + comment);


                            }
                        });
                        commentBuilder1.show();

                    }


                }
            });
            builder.show();





        } catch (Exception e) {
            Toast.makeText(MainActivity.this,"No This NFC in Database",Toast.LENGTH_LONG).show();
        }


    }


    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }


    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }


    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

}//Main Class
