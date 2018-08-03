package com.example.xyzreader.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import com.example.xyzreader.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class NetworkUtils {

    private static boolean mNetworkIsOnline;

    public static URL getUrlFromString(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean isNetworkOnline(final Context context) {

        setNetworkIsOnline(true);

        // check if Network is enabled on device
        if (!isNetworkEnabled(context)) {
            displayNetworkUnavailableAlert(context);
            setNetworkIsOnline(false);
        } else {

            // check Internet connection
            new CheckInternet(new CheckInternet.IsNetworkOnline() {
                @Override
                public void checkNetwork(boolean isOnline) {
                    if (!isOnline) {
                        NetworkUtils.displayNetworkNotRespondingAlert(context);
                        setNetworkIsOnline(false);
                    }
                }
            }).execute();
        }

        return mNetworkIsOnline;
    }

    public static void displayNetworkUnavailableAlert(Context context) {
        displayAlert(context, R.string.network_unavailable, R.string.check_network_settings);
    }

    public static void displayNetworkNotRespondingAlert(Context context) {
        displayAlert(context, R.string.not_connected, R.string.check_connection);
    }


    private static Boolean isNetworkEnabled(Context context) {
        // code used from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private static void displayAlert(Context context, int title, int message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(context.getString(title))
                .setMessage(context.getString(message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private static void setNetworkIsOnline(boolean bool) {
        mNetworkIsOnline = bool;
    }

    private static class CheckInternet extends AsyncTask<Void, Void, Boolean> {

        private final IsNetworkOnline mListener;

        interface IsNetworkOnline {
            void checkNetwork(boolean isOnline);
        }

        CheckInternet(IsNetworkOnline listener) {
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            // code used from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }

        @Override
        protected void onPostExecute(Boolean isOnline) {
            super.onPostExecute(isOnline);

            if (mListener != null) {
                mListener.checkNetwork(isOnline);
            }
        }
    }
}
