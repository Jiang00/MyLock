package com.security.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superjoy on 2014/9/23.
 */
public class SearchThread extends Thread {
    public interface OnSearchResult{
        void onResult(ArrayList<SearchData> list);
    }

    public static class SearchData {
        public String label;
        public String pkg;
        public boolean system;
        public boolean predefined;
    }

    boolean running = true;
    boolean wait;
    String key;
    List<SearchData> list;
    OnSearchResult listener;
    public void waittingForSearch(String key, List<SearchData> list, OnSearchResult listener)
    {
        wait = true;
        this.key = key.toLowerCase();
        this.list = list;
        this.listener = listener;
        interrupt();
    }

    @Override
    public void run() {
        while (running)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {

            }
            if (wait)
            {
                wait = false;
                continue;
            }
            if (list != null)
            {
                ArrayList<SearchData> lst = new ArrayList<>();
                final String k = key;
                for(SearchData s : list)
                {
                    if (s.label.toLowerCase().contains(k))
                    {
                        lst.add(s);
                    }
                    if (wait) {
                        lst.clear();
                        break;
                    }
                }
                if (wait)
                {
                    wait = false;
                }
                else
                {
                    listener.onResult(lst);
                }
                list = null;
            }
        }
    }
}
