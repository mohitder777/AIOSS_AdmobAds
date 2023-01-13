package com;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.vid.allvideodownloader.R;


public class LoadingAds {

        private Activity activity;
        private AlertDialog dialog;

        public  LoadingAds ( Activity myActivity ){

            activity = myActivity;

        }
        public  void startLoadingDialog(){

            AlertDialog.Builder builder = new AlertDialog.Builder (activity);
            LayoutInflater inflater =activity.getLayoutInflater ();
            builder.setView (inflater.inflate(R.layout.loding_dialog_ads,null));
            builder.setCancelable (true);

            dialog=builder.create ();
            dialog.show ();
        }
        public void dismissDialog(){

            dialog.dismiss ();

        }
    }

