package com.nelsoft.redroid;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by barry on 11/16/15.
 */
public class RedditBleach {
    
    private static final String TAG = "RedditBleach";

    private static RedditBleach instance = null;

    HashMap<String, String> dirtyClean = new HashMap<String, String>() {{
        put("fuck", "f*&#");
        put("shit", "s*&#");
        put("motherfucker", "m*&#^%$#%&!@");
        put("fucker", "f*&#^%");
        put("piss", "p*&^");
        put("pissoff", "p*&^*(&");
        put("pissed", "drunk");
        put("pecker", "p@$#$%");
        put("cunt", "^&%$");
        put("asshole", "a(*&*%!");
    }};

    public static RedditBleach getInstance() {
        if (instance == null) {
            instance = new RedditBleach();
        }
        return instance;
    }

    public String cleanString(String input) {
        StringBuffer output = new StringBuffer(input);
        Iterator<String> keys = dirtyClean.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (input.contains(key)) {
                int p = input.indexOf(key);
                output.replace(p, p + key.length(), dirtyClean.get(key));
                Log.i(TAG, key + ":" + p);
            }
        }
        return output.toString();
    }


    /**
     * Massage link to point directly to image
     *
     * @param url
     * @return
     */
    public String targetImage(String url) {
        System.out.println(url);
        if (url.startsWith("http://imgur.com/")) {
            StringBuffer temp = new StringBuffer(url);
            temp.append(".jpg");
            temp.insert(7, "i.");
            return temp.toString();
        } else {
            return url;
        }
    }

    public String findGraphicObject(String url) {

        class FileCheck extends Thread {
            boolean busy = true;
            String url = null;
            String theUrl = null;

            public FileCheck(String url) {
                this.url = url;
                this.theUrl = url;
            }

            public void run() {
                String[] extensions = {"", ".jpg", ".gif", ".mov", ".mp4"};
                String testUrl = this.url;// + ".";

                for (String ext : extensions) {

                    try {
                        URL url = new URL(testUrl + ext);
                        URLConnection resp = url.openConnection();
                        String ff = url.getFile();
                        String type = resp.getContentType();
                        Log.i(TAG, "file :" + ff + ", type :" + type);
                        if ("image/gif".equals(type)
                                || "image/jpeg".equals(type)
                                ) {
                            theUrl = url.toString();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "file :" + theUrl);
                busy = false;
            }

            public String getUrl() {
                return theUrl;
            }
        }

        FileCheck fc = new FileCheck(url);
        fc.start();
        while (fc.busy) {
        }
        return fc.getUrl();
    }


    public String buFindGraphicObject(String url) {
        if (url.endsWith(".jpg")
                || url.endsWith(".gif")
                ){
            return url;
        }
        class FileCheck extends AsyncTask<String, String, String> {

            private boolean busy = true;

            @Override
            protected String doInBackground(String... params) {
                String theUrl = params[0];
                String[] extensions = {"jpg", "gif", "mov", "mp4"};
                String testUrl = params[0] + ".";

                for (String ext : extensions) {

                    try {
                        URL url = new URL(testUrl + ext);
                        URLConnection resp = url.openConnection();
                        String ff = url.getFile();
                        String type = resp.getContentType();
                        Log.i(TAG, "file :" + ff + ", type :" + type);
                        if ("image/gif".equals(type)
                                || "image/jpeg".equals(type)
                                ) {
                            theUrl = url.toString();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return theUrl;
            }

            @Override
            protected void onPostExecute(String fullPath) {
                super.onPostExecute(fullPath);
                Log.i(TAG, "found :" + fullPath);
                busy = false;
            }

            public boolean busy() {
                return this.busy;
            }
        }

        FileCheck fc = new FileCheck();
        fc.execute(url);
        while(fc.busy()){}
        return url;
    }

}
