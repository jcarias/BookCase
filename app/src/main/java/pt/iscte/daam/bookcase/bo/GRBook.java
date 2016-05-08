package pt.iscte.daam.bookcase.bo;

import android.media.Image;

import java.util.List;

/**
 * Created by joaocarias on 26/03/16.
 */
public class GRBook extends DefaultBook {

    private String workId;
    private String averageRating;
    private String bookId;
    private String imageUrl;
    private String smallImageUrl;

    private String lentTo;
    private String lentToDate;

    public String getLentTo() {
        return this.lentTo;
    }
    public void setLentTo(String lentTo) {
        this.lentTo = lentTo;
    }

    public String getLentToDate() {
        return this.lentToDate;
    }
    public void setLentToDate(String lentToDate) {
        this.lentToDate = lentToDate;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

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

    public String getApplicationID() {
        return applicationID;
    }

    @Override
    public Image getCoverImage() {
        return null;
    }


}
