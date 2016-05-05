package pt.iscte.daam.bookcase.bo;

import android.media.Image;

import java.util.List;

/**
 * Created by joaocarias on 23/03/16.
 */
public class TestBook extends DefaultBook {

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public String getReleaseYear() {
        return releaseYear;
    }

    @Override
    public String getReleaseMonth() {
        return releaseMonth;
    }

    @Override
    public String getReleaseDay() {
        return releaseDay;
    }

    @Override
    public String getCodeISBN() {
        return codeISBN;
    }

    @Override
    public Image getCoverImage() {
        return null;
    }



}
