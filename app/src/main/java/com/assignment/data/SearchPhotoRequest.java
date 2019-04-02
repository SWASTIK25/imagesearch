package com.assignment.data;

import javax.inject.Inject;
import javax.inject.Named;

public class SearchPhotoRequest {
    private String method;
    private String apiKey;
    private String text;
    private int pageSize;
    private int page;
    private String format;
    private int noJsonCallback;


    @Inject
    public SearchPhotoRequest(@Named("flickerApiKey") String flickerApiKey, @Named("flickerSearchPhotoMethod") String flickerSearchMethod) {
        apiKey = flickerApiKey;
        method = flickerSearchMethod;
        format = "json";
        noJsonCallback = 1;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getNoJsonCallback() {
        return noJsonCallback;
    }

    public void setNoJsonCallback(int noJsonCallback) {
        this.noJsonCallback = noJsonCallback;
    }
}
