package pt.iscte.daam.bookcase.bo;

import android.graphics.Bitmap;

/**
 * Created by DVF on 06-04-2016.
 */
public class UserProfile
{
    private String name;
    private String facebookId;
    private String email;
    private Bitmap picture;

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

    public Bitmap getPicture() {

        return picture;
    }

    public void setPicture(Bitmap picture) {

        this.picture = picture;
    }
}
