package com.thanhle.englishvocabulary.resource;

/**
 * Created by LaiXuanTam on 10/7/2015.
 */
public class LibraryResource {

    public String price;
    public String name;
    public String description;
    public String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
