package com.example.appforreddit.utils;

import java.util.ArrayList;

public class PaginationUtil {
    private final ArrayList<String> listPage;

    public PaginationUtil() {
        this.listPage = new ArrayList<>();
        setFirstNode();
    }
    public void setNextPage(int targetPage, String nextPage) {
        if(!listPage.contains(nextPage))
            listPage.add(targetPage, nextPage);
    }
    private void setFirstNode(){
        listPage.add("");
    }
    public String getPage(int targetPage){
        if(listPage.size() > 1)
            return listPage.get(targetPage);
        else
            return "";
    }
}
