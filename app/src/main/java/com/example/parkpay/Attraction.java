package com.example.parkpay;

class Attraction {


    String id;
    String name;
    String photo;
    String price;
    String bonus;
    String text;
    String weight;
    String growth;
    String ageMin;
    String ageMax;
    String levelFear;
    Float lat;
    Float lng;

    Attraction(
               String id,
               String name,
               String photo,
               String price,
               String bonus,
               String text,
               String weight,
               String growth,
               String ageMin,
               String ageMax,
               String levelFear,
               Float lat,
               Float lng
    ) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.bonus = bonus;
        this.text = text;
        this.weight = weight;
        this.growth = growth;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.levelFear = levelFear;
        this.lat = lat;
        this.lng = lng;
    }

    public String getImageUrl() {
        return photo;
    }

    public void setImageUrl(String imageUrl) {
        this.photo = imageUrl;
    }
}