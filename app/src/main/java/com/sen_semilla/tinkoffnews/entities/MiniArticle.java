package com.sen_semilla.tinkoffnews.entities;

import java.io.Serializable;

/**
 * Created by sen_semilla on 13.06.2017.
 */

public class MiniArticle implements Serializable{
    private int id;
    private String name;
    private String text;
    private TimeWrapper publicationDate;
    private int bankInfoTypeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBankInfoTypeId() {
        return bankInfoTypeId;
    }

    public void setBankInfoTypeId(int bankInfoTypeId) {
        this.bankInfoTypeId = bankInfoTypeId;
    }

    public TimeWrapper getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(TimeWrapper publicationDate) {
        this.publicationDate = publicationDate;
    }
}
