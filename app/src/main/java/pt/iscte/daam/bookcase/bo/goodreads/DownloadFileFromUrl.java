package pt.iscte.daam.bookcase.bo.goodreads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import pt.iscte.daam.bookcase.bo.UserProfile;

/**
 * Created by User on 19-05-2016.
 */
public class DownloadFileFromUrl extends AsyncTask<String, Void, byte[]> {
        protected byte[] doInBackground(String... url) {
            try {
                if(url.length > 1)
                    return null;

                URL pictureURL = new URL(url[0]);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = pictureURL.openConnection().getInputStream();

                byte[] byteChunk = new byte[4096];
                int n;

                while ( (n = is.read(byteChunk)) > 0 ) {
                    baos.write(byteChunk, 0, n);
                }

                return baos.toByteArray();

            } catch (Exception e) {
                Log.e("UTILS", "Error getting picture with url: " + url[0] + "\nError:" + e.getMessage());
                return null;
            }
        }
}
