package com.example.bz214proje.model;

public enum Direction {
    NORTH,SOUTH,EAST,WEST;

    public Direction getRightDir(){ //Robotun yönünün sağ tarafının hangi yön olduğunu bulan metot
                                    //Robot sağa dön komutunu aldığında sağ tarafının hangi yön olduğunu bu metot ile öğrenir.

                                    //bu metotun Direction içinde olmasının sebebi OOP'nn encapsulation (kapsülleme) kuralı.
                                    //OOP'de her nesne kendisinden sorumludur. O yüzden Bir yönün sağı neresidir sorusunun cevabını
                                    //Direction enum'u vermelidir.

        switch (this) { //örneğin robot sağa dönmek istediğinde this ile o anki konumunu alır.
            case NORTH: return EAST;
            case EAST:  return SOUTH;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            default:    return NORTH;
        }
    }
}
