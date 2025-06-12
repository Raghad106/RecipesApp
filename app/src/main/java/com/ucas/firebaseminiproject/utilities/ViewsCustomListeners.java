package com.ucas.firebaseminiproject.utilities;

import static com.ucas.firebaseminiproject.utilities.Constance.CAMERA_PERMISSION;
import static com.ucas.firebaseminiproject.utilities.Constance.STORAGE_PERMISSION;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class ViewsCustomListeners {
    public static int STUDENT_ID;

    public static boolean hideAndShowPassword(boolean isPasswordShown, EditText et){
        if (isPasswordShown){
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordShown = false;
        }else{
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordShown = true;
        }
        et.setSelection(et.getText().length());
        return isPasswordShown;
    }

    public static Boolean sureAllEditTextsNotEmpty(List<EditText> editTexts, TextView tv){
        int count = 0;
        boolean isEmpty = false;
        for (EditText e : editTexts){
            if (e.getText().toString().trim().isEmpty()){
                count++;
                isEmpty = true;
            }
        }
        if (count == 1)
            tv.setText("There is an empty field");
        else if (count == 0)
            tv.setText("");
        else
            tv.setText("There are empty fields");

        return isEmpty;
    }

    public static void enableOrNotAllEditTextsNotEmpty(List<EditText> editTexts, boolean isEnable){
        if (isEnable){
            for (EditText editText : editTexts) {
                editText.setEnabled(true); //ensure all EditTexts are disabled in non-edit mode
            }
        }
        else{
            for (EditText editText : editTexts) {
                editText.setEnabled(false); //ensure all EditTexts are disabled in non-edit mode
            }
        }
    }

    public static boolean checkPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }
}
