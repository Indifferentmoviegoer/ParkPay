package com.example.parkpay;

import java.util.ArrayList;
import java.util.List;

class News {
    String name;
    String photo;
    String date;
    String text;
    String link;

    News(
            String name,
            String photo,
            String date,
            String text,
            String link
    ) {
        this.name = name;
        this.photo = photo;
        this.date = date;
        this.text = text;
        this.link = link;
    }

    public String getImageUrl() {
        return photo;
    }

    public void setImageUrl(String imageUrl) {
        this.photo = imageUrl;
    }
}