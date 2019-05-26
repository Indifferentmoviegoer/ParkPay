package com.example.parkpay;

class Card {


    final String name;
    final String code;
    final String money;
    final String bonus;
    final String id;

    Card(

               String name,
               String code,
               String money,
               String bonus,
               String id
    ) {
        this.name = name;
        this.code = code;
        this.money = money;
        this.bonus = bonus;
        this.id = id;

    }

}