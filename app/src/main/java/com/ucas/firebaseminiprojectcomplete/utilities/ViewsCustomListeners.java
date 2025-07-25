package com.ucas.firebaseminiprojectcomplete.utilities;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewsCustomListeners {

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

    public static void declareRecyclerView(Context context, RecyclerView.Adapter adapter, RecyclerView recyclerView, boolean isLinear){
        recyclerView.setAdapter(adapter);
        if (isLinear)
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        else
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}
