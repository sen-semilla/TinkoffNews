package com.sen_semilla.tinkoffnews.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sen_semilla on 6/14/2017.
 */

public class TimeWrapper implements Serializable{
    private long milliseconds;

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public String toString(){
        return new Date(milliseconds).toString();
    }
}
