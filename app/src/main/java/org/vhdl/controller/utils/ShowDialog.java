package org.vhdl.controller.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ShowDialog {
    private String title;
    private String msg;
    private Context context;
    private AlertDialog dialog;

    public ShowDialog(Context context){
        this.context = context;
    }

    public void setDialog(String title, String msg){
        this.title = title;
        this.msg = msg;
    }

    private void BuildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
    }

    public void show(){
        BuildDialog();
        dialog.show();
    }
}
