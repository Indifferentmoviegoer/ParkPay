package com.example.parkpay;

import java.util.ArrayList;
import java.util.List;

class Person {
    String name;
    String age;
    String photoId;

    Person(String name, String age, String photoId) {
        this.name = name;
        this.age = age;
        this.photoId = photoId;
    }


    private List<Person> persons;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData() {
        persons = new ArrayList<>();
        persons.add(new Person("Emma Wilson", "23 years old", "https://static.tonkosti.ru/images/3/3d/%D0%9F%D0%B5%D1%80%D0%B2%D0%BE%D0%BC%D0%B0%D0%B9%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0_%28%D0%A7%D0%B8%D1%81%D1%82%D1%8F%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0%29%2C_%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE%D0%B4%D0%B0%D1%80.JPG"));
        persons.add(new Person("Lavery Maiss", "25 years old", "https://static.tonkosti.ru/images/3/3d/%D0%9F%D0%B5%D1%80%D0%B2%D0%BE%D0%BC%D0%B0%D0%B9%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0_%28%D0%A7%D0%B8%D1%81%D1%82%D1%8F%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0%29%2C_%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE%D0%B4%D0%B0%D1%80.JPG"));
        persons.add(new Person("Lillie Watts", "35 years old", "https://static.tonkosti.ru/images/3/3d/%D0%9F%D0%B5%D1%80%D0%B2%D0%BE%D0%BC%D0%B0%D0%B9%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0_%28%D0%A7%D0%B8%D1%81%D1%82%D1%8F%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0%29%2C_%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE%D0%B4%D0%B0%D1%80.JPG"));
    }
}