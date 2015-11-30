package nfctutorials.tutorial04;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by songkran on 10/11/2558.
 */
public class MyAlertDialog {

    private AlertDialog.Builder objAlert;
    public void HaveSpace(Context context) {

        objAlert = new AlertDialog.Builder(context);
        objAlert.setIcon(R.drawable.question);
        objAlert.setTitle("มีช่องว่าง");
        objAlert.setMessage("กรุณากรอก User และ Password");
        objAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        });
        objAlert.show();


    }//end Of have space หน้า alert จากการไม่พิมพ์ user / password

    public void UserPasswordNotTrue(Context context) {

        objAlert = new AlertDialog.Builder(context);
        objAlert.setIcon(R.drawable.danger);
        objAlert.setTitle("User หรือ Password ผิดพลาด");
        objAlert.setMessage("User หรือ Password ไม่ถูกต้อง" + "\n" + "โปรดลองอีกครั้ง");
        objAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        objAlert.show();

    }//end of userPasswordNotTrue

}

