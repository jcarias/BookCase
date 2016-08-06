package pt.iscte.daam.bookcase.goodreads.xml.parsers;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pt.iscte.daam.bookcase.bo.GRBook;

/**
 * XML Parser for the search results
 * Created by joaocarias on 26/03/16.
 */
public class GRBooksSearchResultsParser extends AbstractXMLParser {

    private List<GRBook> books;
    private String resultsEnd;
    private String totalResults;

    public GRBooksSearchResultsParser() {
        books = new ArrayList<>();
    }

    public List<GRBook> getBooks() {
        return books;
    }

    public boolean hasMoreBooks() {
        Integer resultsEnd, totalResults;

        try {
            resultsEnd = Integer.valueOf(this.resultsEnd);
        } catch (NumberFormatException e) {
            resultsEnd = 0;
        }
        try {
            totalResults = Integer.valueOf(this.totalResults);
        } catch (NumberFormatException e) {
            totalResults = 0;
        }

        return resultsEnd < totalResults;
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);

        } finally {
            in.close();
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "GoodreadsResponse");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("search")) {
                readSearch(parser);
            } else {
                skip(parser);
            }


        }
    }

    private void readSearch(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "search");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("results-end".equals(name)) {
                this.resultsEnd = readTagText(parser, name);
            } else if ("total-results".equals(name)) {
                this.totalResults = readTagText(parser, name);
            } else if (name.equals("results")) {
                readResults(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readResults(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "results");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if ("work".equals(name)) {
                readWork(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readWork(XmlPullParser parser) throws XmlPullParserException, IOException {
        GRBook book = new GRBook();

        parser.require(XmlPullParser.START_TAG, ns, "work");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "id":
                    book.setWorkId(readTagText(parser, name));
                    break;
                case "original_publication_year":
                    book.setReleaseYear(readTagText(parser, name));
                    break;
                case "original_publication_month":
                    book.setReleaseMonth(readTagText(parser, name));
                    break;
                case "original_publication_day":
                    book.setReleaseDay(readTagText(parser, name));
                    break;
                case "average_rating":
                    book.setAverageRating(readTagText(parser, name));
                    break;
                case "best_book":
                    readBestBook(parser, book);
                    break;
                default:
                    skip(parser);
            }

        }

        books.add(book);
    }

    private void readBestBook(XmlPullParser parser, GRBook book) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "best_book");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "id":
                    book.setBookId(readTagText(parser, name));
                    break;
                case "title":
                    book.setTitle(readTagText(parser, name));
                    break;
                case "image_url":
                    book.setImageUrl(readTagText(parser, name));
                    break;
                case "small_image_url":
                    book.setSmallImageUrl(readTagText(parser, name));
                    break;
                case "author":
                    readAuthor(parser, book);
                    break;
                default:
                    skip(parser);
            }
        }
    }

    private void readAuthor(XmlPullParser parser, GRBook book) throws XmlPullParserException, IOException {
        List<String> authorsList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "author");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            switch (name) {
                case "name":
                    authorsList.add(readTagText(parser, name));
                    break;
                default:
                    skip(parser);
            }
        }

        String listString = "";

        for (String s : authorsList) {
            listString += s + "\t";
        }

        book.setAuthors(listString);
    }


}

