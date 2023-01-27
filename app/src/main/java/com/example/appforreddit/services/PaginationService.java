package com.example.appforreddit.services;

import java.util.ArrayList;

public class PaginationService {
    private final ArrayList<String> listPage;

    public PaginationService() {
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

    public String getPreviousPage(int targetPage){
        if(!listPage.isEmpty())
            return listPage.get(targetPage);
        else
            return "";
    }

    public boolean pagesIsEmpty(){
        return listPage.isEmpty();
    }
}
