package com.example.parkpay;

class Transaction {


    final String name;
    final String date;
    final String value;
    final int photo;

    Transaction(

               String name,
               String date,
               String value,
               int photo
    ) {
        this.name = name;
        this.date = date;
        this.value = value;
        this.photo = photo;

    }

}