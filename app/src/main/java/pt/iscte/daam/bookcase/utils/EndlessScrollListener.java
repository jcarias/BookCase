package pt.iscte.daam.bookcase.utils;

import android.widget.AbsListView;

import pt.iscte.daam.bookcase.SearchBooksActivity;
import pt.iscte.daam.bookcase.bo.goodreads.SearchBooksAsyncTask;

/**
 * Created by joaocarias on 05/08/16.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private SearchBooksActivity senderActivity;

    public EndlessScrollListener(int visibleThreshold, SearchBooksActivity senderActivity) {
        this.visibleThreshold = visibleThreshold;
        this.senderActivity = senderActivity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            new SearchBooksAsyncTask(senderActivity).execute(senderActivity.getSearchTerm(), currentPage + 1 + "");
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}