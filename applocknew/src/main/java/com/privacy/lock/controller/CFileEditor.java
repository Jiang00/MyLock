package com.privacy.lock.controller;

import java.util.Arrays;

/**
 * Created by SongHualin on 6/1/2015.
 */
public class CFileEditor {
    boolean selects[];
    int selectCount;

    public void reset(int size){
        selects = new boolean[size];
        clear();
    }

    public final void reset(){
        clear();
    }

    public void selectAll(){
        if (selectCount == selects.length){
            clear();
        } else {
            select_all();
        }
    }

    public final boolean[] selects(){
        return selects;
    }

    private void select_all() {
        Arrays.fill(selects, true);
        selectCount = selects.length;
    }

    private void clear() {
        Arrays.fill(selects, false);
        selectCount = 0;
    }

    public final int getCount(){
        return selectCount;
    }

    public void select(int which){
        selects[which] = !selects[which];
        if (selects[which]){
            ++selectCount;
        } else {
            --selectCount;
        }
    }

    public final boolean isSelected(int which){
        return selects[which];
    }

    public void destroy(){
        selects = null;
    }
}
