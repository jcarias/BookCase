package pt.iscte.daam.bookcase.bo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * Created by DVF on 06-04-2016.
 */
public class UserProfile
{
    private static UserProfile profile = null;
    private static String filename = "USERPROFILE";
    private static String profilePicture = "PROFILEPICTURE";

    public String name;
    public String facebookId;
    public String email;

    private UserProfile() { }

    private UserProfile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            StringBuffer fileContent = new StringBuffer("");
            byte[] buffer = new byte[1024];
            int n;
            while (( n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }

            JSONObject jsonObj = new JSONObject(fileContent.toString());

            this.setEmail(jsonObj.getString("email"));
            this.setName(jsonObj.getString("name"));
            this.setFacebookId(jsonObj.getString("facebookId"));

            return;

        } catch (Exception e) {
            Log.e("USERPROFILE", "Error loading user profile. Error:" + e.getMessage());
        }

        return;
    }

    public static UserProfile getUserProfile(Context context){
        if(profile == null)
            profile = new UserProfile(context);

        return profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {

        return facebookId;
    }

    public void setFacebookId(String facebookId) {

        this.facebookId = facebookId;
    }

    public Bitmap getPicture(Context context) {
        try {
            FileInputStream fis = context.openFileInput(profilePicture);
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            fis.close();

            return  bmp;
        } catch (Exception e) {
            Log.e("USERPROFILE", "Error getting profile picture. Error:" + e.getMessage());
            return null;
        }
    }

    public void setPicture(Bitmap picture, Context context) {
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);

            FileOutputStream oos = context.openFileOutput(profilePicture, Context.MODE_PRIVATE);
            oos.write(stream.toByteArray());
            oos.close();

        } catch (Exception e) {
            Log.e("USERPROFILE", "Error saving profile picture. Error:" + e.getMessage());
        }
    }

    public static void saveProfile(Context context){
        try {

            if(profile != null) {
                JSONObject manJson = new JSONObject();
                manJson.put("email", profile.getEmail());
                manJson.put("name", profile.getName());
                manJson.put("facebookId", profile.getFacebookId());

                OutputStreamWriter oos = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
                oos.write(manJson.toString());
                oos.close();
            }

        } catch (Exception e) {
            Log.e("USERPROFILE", "Error saving user profile. Error:" + e.getMessage());
        }
    }

    public static void destroyProfile(Context context){
        try {
            new File(context.getFilesDir(), filename).delete();
            new File(context.getFilesDir(), profilePicture).delete();

            profile = null;

        } catch (Exception e) {
            Log.e("USERPROFILE", "Error deleting user profile. Error:" + e.getMessage());
        }
    }
}
