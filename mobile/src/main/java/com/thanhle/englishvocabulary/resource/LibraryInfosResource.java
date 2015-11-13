package com.thanhle.englishvocabulary.resource;

import java.io.Serializable;

/**
 * Created by LaiXuanTam on 10/15/2015.
 */
public class LibraryInfosResource implements Serializable{
    private static final long serialVersionUID = 1L;
    public String name;
    public String price;
    public String image;
    public String downloaded;
    public String _id;
    public String card_total;

    public LibraryInfoResource[] cards;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCard_total() {
        return card_total;
    }

    public void setCard_total(String card_total) {
        this.card_total = card_total;
    }
}
