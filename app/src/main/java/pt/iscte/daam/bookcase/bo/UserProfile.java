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
    private static String profilePicture = "PROFILEPICTURE";

    public String name;
    public String facebookId;
    public String email;

    public UserProfile() { }

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

    public static void setPicture(Bitmap picture, Context context) {
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

    public void saveProfile(Context context){
        BookCaseDbHelper db = new BookCaseDbHelper(context);

        db.deleteUsers(); //only one user allowed
        db.InsertUser(this);
    }

    public static UserProfile getProfile(Context context){
        return (new BookCaseDbHelper(context)).GetUser();
    }

    public static void logoutProfile(Context context){
        try {
            new File(context.getFilesDir(), profilePicture).delete();

            (new BookCaseDbHelper(context)).deleteUsers();

        } catch (Exception e) {
            Log.e("USERPROFILE", "Error deleting user profile. Error:" + e.getMessage());
        }
    }
}
