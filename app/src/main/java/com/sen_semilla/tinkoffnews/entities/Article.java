package com.sen_semilla.tinkoffnews.entities;

/**
 * Created by sen_semilla on 13.06.2017.
 */

public class Article{
    private MiniArticle title;
    private String content;
    private TimeWrapper creationDate, lastModificationDate;
    private int bankInfoTypeId;
    private String typeId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public TimeWrapper getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(TimeWrapper creationDate) {
        this.creationDate = creationDate;
    }

    public TimeWrapper getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(TimeWrapper lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public MiniArticle getTitle() {
        return title;
    }

    public void setTitle(MiniArticle title) {
        this.title = title;
    }

    public int getBankInfoTypeId() {
        return bankInfoTypeId;
    }

    public void setBankInfoTypeId(int bankInfoTypeId) {
        this.bankInfoTypeId = bankInfoTypeId;
    }
}
