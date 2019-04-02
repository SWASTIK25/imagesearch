package com.assignment.presentation.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.assignment.R;

import java.util.List;


public class DialogUtils {

    public interface IDialogItemClickListener
    {
        void onItemClick(int pos, String content);
    }


    public static AlertDialog doAlert(Context context, String content, String yesBtnText, final Runnable yesRun)
    {
        return doAlert(context,content,yesBtnText,yesRun,null,null);
    }

    public static AlertDialog doAlert(Context context, String content, String yesBtnText, final Runnable yesRun, String noBtnText, final Runnable noRun)
    {
        return doAlert(context,content,yesBtnText,yesRun,noBtnText,noRun,null,null);
    }

    public static AlertDialog doAlert(Context context, String content, String yesBtnText, final Runnable yesRun, String noBtnText, final Runnable noRun, String neutralBtnText, final Runnable neutralRun)
    {
        return doAlert(context,null,content,yesBtnText,yesRun,noBtnText,noRun,neutralBtnText,neutralRun);
    }

    public static AlertDialog doAlert(Context context, String title, String content, String yesBtnText, final Runnable yesRun)
    {
        return doAlert(context,title,content,yesBtnText,yesRun,null,null);
    }

    public static AlertDialog doAlert(Context context, String title, String content, String yesBtnText, final Runnable yesRun, String noBtnText, final Runnable noRun)
    {
        return   doAlert(context,title,content,yesBtnText,yesRun,noBtnText,noRun,null,null);
    }

    public static AlertDialog doAlert(Context context, String title, String content, String yesBtnText, final Runnable yesRun, String noBtnText, final Runnable noRun, String neutralBtnText, final Runnable neutralRun)
    {
        return  doAlert(context,title,0,content,null,yesBtnText,yesRun,noBtnText,noRun,neutralBtnText,neutralRun);
    }


    public static AlertDialog doAlert(Context context, String title, int titleIcon, String content, View view,
                                      String yesBtnText, final Runnable yesRun, String noBtnText,
                                      final Runnable noRun, String neutralBtnText, final Runnable neutralRun)
    {
      return  doAlert(context, R.style.AssignmentAppDialog,title,titleIcon,content,view,yesBtnText,yesRun,noBtnText,noRun,neutralBtnText ,neutralRun);
    }

    public static AlertDialog doAlert(Context context, int style, String title, int titleIcon, String content, View view,
                                      String yesBtnText, final Runnable yesRun, String noBtnText,
                                      final Runnable noRun, String neutralBtnText, final Runnable neutralRun)
    {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(content);
        if(title!=null)
            builder.setTitle(title);
        if(titleIcon!=0)
            builder.setIcon(ContextCompat.getDrawable(context,titleIcon));

        if(yesBtnText!=null) {
            builder.setPositiveButton(yesBtnText, yesRun == null ? null : new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    yesRun.run();
                }
            });
        }

        if(noBtnText!=null)
        {
            builder.setNegativeButton(noBtnText,noRun == null? null:new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    noRun.run();
                }
            });
        }

        if(neutralBtnText!=null)
        {
            builder.setNeutralButton(neutralBtnText,neutralRun == null?null:new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    neutralRun.run();
                }
            });
        }

        if(view!=null)
            builder.setView(view);
        alertDialog = builder.create();
        try {
            alertDialog.show();
            return  alertDialog;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AlertDialog doAlert(Context context, View view)
    {
        return  doAlert(context, R.style.AssignmentAppDialog,null,0,null,view, null,null,null,null,null,null);

    }

    public static AlertDialog doAlert(Context context, View view, String title)
    {
        return  doAlert(context, R.style.AssignmentAppDialog,title,0,null,view, null,null,null,null,null,null);

    }


    public static AlertDialog doListAlert(Context context, String title, final List<String> list, final IDialogItemClickListener listener)
    {
        return doListAlert(context, R.style.AssignmentAppDialog,title,0,list,listener);
    }
    public static AlertDialog doListAlert(Context context, int style, String title, int titleIcon, final List<String> list, final IDialogItemClickListener listener)
    {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onItemClick(which,list.get(which));
            }
        });

        if(title!=null)
            builder.setTitle(title);
        if(titleIcon!=0)
            builder.setIcon(ContextCompat.getDrawable(context,titleIcon));
        alertDialog = builder.create();
        try {
            alertDialog.show();
            return  alertDialog;
        } catch (IllegalStateException e) {
            return null;
        }
    }


    public static AlertDialog showCustomViewDialog(Context context, String title, int titleIcon, View view) {
        return showCustomViewDialog(context,title,titleIcon,view,null,null,null,null,null,null);

    }

    public static abstract class StateRunnable implements Runnable {
        @Override
        public void run() {
        }
        public abstract boolean doRun();
    }

    public static AlertDialog showCustomViewDialog(Context context, String title, int titleIcon, View view,
                                                   String yesText, final Runnable yesRun, String noText, final Runnable noRun,
                                                   final String neutralText, final Runnable neutralRun)
    {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        if(title!=null)
            builder.setTitle(title);
        if(titleIcon!=0)
            builder.setIcon(ContextCompat.getDrawable(context,titleIcon));
        if(yesText!=null)
            builder.setPositiveButton(yesText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    yesRun.run();
                }
            });
        if(noText!=null)
            builder.setNegativeButton(noText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    noRun.run();
                }
            });
        if(neutralText!=null)
            builder.setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    neutralRun.run();
                }
            });
        alertDialog = builder.create();
        try {
            alertDialog.show();
            return  alertDialog;
        } catch (IllegalStateException e) {
            return null;
        }
    }


    public static AlertDialog showCustomViewDialog(Context context, String title, int titleIcon, View view,
                                                   String yesText, final StateRunnable yesRun, String noText, final StateRunnable noRun,
                                                   final String neutralText, final StateRunnable neutralRun)
    {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        if(title!=null)
            builder.setTitle(title);
        if(titleIcon!=0)
            builder.setIcon(ContextCompat.getDrawable(context,titleIcon));
        if(yesText!=null)
            builder.setPositiveButton(yesText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        if(noText!=null)
            builder.setNegativeButton(noText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!noRun.doRun()){
                        return;
                    }
                    dialog.dismiss();
                }
            });
        if(neutralText!=null)
            builder.setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!neutralRun.doRun()){
                        return;
                    }
                    dialog.dismiss();
                }
            });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            if(dialog instanceof AlertDialog){
                if(yesText!=null) {
                    Button yesButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    yesButton.setText(yesText);
                    yesButton.setOnClickListener(v -> {
                        if (!yesRun.doRun()) {
                            return;
                        }
                        dialog.dismiss();
                    });
                }

                if(noText!=null) {
                    Button noButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    noButton.setText(noText);
                    noButton.setOnClickListener(v -> {
                        if (!noRun.doRun()) {
                            return;
                        }
                        dialog.dismiss();
                    });
                }

                if(neutralText!=null) {
                    Button neutralButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                    neutralButton.setText(noText);
                    neutralButton.setOnClickListener(v -> {
                        if (!neutralRun.doRun()) {
                            return;
                        }
                        dialog.dismiss();
                    });
                }
            }
        });
        try {
            alertDialog.show();
            return  alertDialog;
        } catch (IllegalStateException e) {
            return null;
        }
    }




}
