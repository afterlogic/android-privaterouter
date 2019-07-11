package com.PrivateRouter.PrivateMail.model.system;

import java.util.LinkedList;
import java.util.List;

public class Subject<T> {
    private List<Observer> observerList = new LinkedList<>();

    public void registerObserver( Observer observer) {
        observerList.add(observer);
    }

    public void removeObserver( Observer observer) {
        observerList.remove(observer);
    }

    public void notifyObservers(T parameter) {
        for (Observer observer: observerList ) {
            observer.onUpdate(parameter);
        }
    }
}