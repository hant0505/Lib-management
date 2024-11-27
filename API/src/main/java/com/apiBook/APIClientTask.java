package com.apiBook;
import javafx.concurrent.Task;

import java.util.List;

public class APIClientTask extends Task<List<API_Book>> {
    private String query;

    public APIClientTask(String query) {
        this.query = query;
    }

    @Override
    protected List<API_Book> call() throws Exception {
        APIClient_ggbook apiClient = new APIClient_ggbook();
        return apiClient.getBooks(query);
    }
}
