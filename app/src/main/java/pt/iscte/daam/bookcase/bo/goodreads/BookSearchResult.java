package pt.iscte.daam.bookcase.bo.goodreads;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.daam.bookcase.bo.Book;

/**
 * Class to keep the Book search results to enable pagination
 * Created by joaocarias on 14/06/16.
 */
public class BookSearchResult {
    private int resultsStart;
    private int resultsEnd;
    private int totalResults;
    private int currentPage;

    private List<Book> books;

    public BookSearchResult() {
        this.resultsStart = 0;
        this.resultsEnd = 0;
        this.totalResults = 0;
        this.currentPage = 0;
        this.books = new ArrayList<>(0);
    }

    public void parseSearchResult(List<Book> books) {
        this.books = books;
    }
}
