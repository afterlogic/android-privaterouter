package com.PrivateRouter.PrivateMail.model;

import java.util.ArrayList;
import java.util.LinkedList;

public class InternalLists<T> extends LinkedList<ArrayList<T>> {
    int maxInternalSize;

    public InternalLists(int maxInternalSize) {
        this.maxInternalSize = maxInternalSize;
    }

    public void addElement(T element) {
        ArrayList<T> currentList = null;
        if (!isEmpty())
            currentList = getFirst();

        if (currentList==null || currentList.size() == maxInternalSize) {
            currentList = new ArrayList<>(maxInternalSize);
            addFirst(currentList);
        }

        currentList.add( 0,  element );
    }
}
