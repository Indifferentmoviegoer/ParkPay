package com.example.parkpay;

class Park {
    String parkId;
    String name;
    Float latTop;
    Float lngTop;
    Float latBottom;
    Float lngBottom;
    Float latCenter;
    Float lngCenter;
    String photo;

    Park(
            String parkId,
            String name,
            Float latTop,
            Float lngTop,
            Float latBottom,
            Float lngBottom,
            Float latCenter,
            Float lngCenter,
            String photo
    ) {
        this.parkId = parkId;
        this.name = name;
        this.latTop = latTop;
        this.lngTop = lngTop;
        this.latBottom = latBottom;
        this.lngBottom = lngBottom;
        this.latCenter = latCenter;
        this.lngCenter = lngCenter;
        this.photo = photo;
    }

    public String getImageUrl() {
        return photo;
    }

    public void setImageUrl(String imageUrl) {
        this.photo = imageUrl;
    }
}