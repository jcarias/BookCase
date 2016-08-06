package pt.iscte.daam.bookcase.bo.goodreads;

import java.util.List;

import pt.iscte.daam.bookcase.bo.GRBook;

/**
 * Class to keep the Book search results to enable pagination
 * Created by joaocarias on 14/06/16.
 */
public class BookSearchResult {
    private List<GRBook> books;
    private boolean hasMoreResults;

    public BookSearchResult(List<GRBook> books, boolean hasMoreResults) {
        this.books = books;
        this.hasMoreResults = hasMoreResults;
    }

    public List<GRBook> getBooks() {
        return books;
    }

    public boolean isHasMoreResults() {
        return hasMoreResults;
    }
}
