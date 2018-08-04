package com.example.xyzreader.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class NetworkUtils {

    /**
     * Returns a URL object from a given url String
     * * @param stringUrl the string url
     * @return the URL object
     */
    public static URL getUrlFromString(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Checks if a network is available on the device
     * @param context the current activity context
     * @return a boolean that is true if a network is available, and false otherwise
     */
    public static boolean isNetworkEnabled(Context context) {
        // code used from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Runs a background Task to check that a connection to the Internet exits by attempting to
     * contact 8.8.8.8 which is a public google DNS server. This will return a boolean by way
     * of a listener callback that is true if the Internet connection is good, and false
     * otherwise.
     */
    public static class CheckInternetConnection extends AsyncTask<Void, Void, Boolean> {

        private final TaskCompleted mListener;

        public interface TaskCompleted {
            void onInternetCheckCompleted(boolean result);
        }

        public CheckInternetConnection(TaskCompleted listener) {
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
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (mListener != null) {
                mListener.onInternetCheckCompleted(result);
            }
        }
    }
}
