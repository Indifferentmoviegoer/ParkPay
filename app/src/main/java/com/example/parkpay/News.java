package com.example.parkpay;

class News {
    final String name;
    private String photo;
    final String date;
    final String text;
    final String link;

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