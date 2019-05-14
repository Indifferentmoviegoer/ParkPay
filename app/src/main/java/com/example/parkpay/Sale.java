package com.example.parkpay;

class Sale {
    final String name;
    final String text;
    final String dateStart;
    final String dateEnd;
    private String photo;

    Sale(
            String name,
            String text,
            String dateStart,
            String dateEnd,
            String photo
    ) {
        this.name = name;
        this.text = text;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.photo = photo;
    }
    public String getImageUrl() {
        return photo;
    }

    public void setImageUrl(String imageUrl) {
        this.photo = imageUrl;
    }
}