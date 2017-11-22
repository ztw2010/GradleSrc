package com.ebest.frame.compilelib.parama;

import javax.lang.model.element.Element;

public class ParcelException extends RuntimeException {
    private Element ele;

    public ParcelException(String message, Throwable cause, Element ele) {
        super(message, cause);
        this.ele = ele;
    }

    public ParcelException(String message, Element ele) {
        super(message);
        this.ele = ele;
    }

    public Element getEle() {
        return ele;
    }
}
