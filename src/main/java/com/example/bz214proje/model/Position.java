package com.example.bz214proje.model;

import java.util.Objects;

public class Position { //Harita üzerindeki araçların, eşyaların konumunu tutan sınıf
    private int x; //yatay x eksenlerini tutar. OOP'nin kapsülleme kuralları gereği dışarıdan değiştirilmesini istemediğimiz için
                   //private tanımladık.
    private int y; //dikey y eksenlerini tutar.

    public Position(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) { //x ve y yi private tanımlayıp set metodu koymamızın sebebi: set burada güvenlik kapısı gibidir.
                              //Dışarıdan başkası direkt x ve y yi değiştiremez. x ve y nin kontrollü bir şekilde değişmesi için
                              //set metotlarını koyduk çünkü move() metodunda robat hareket ettikçe koordinat bilgisi değişecek.
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position getAdjacent(Direction dir) { //Robot komşu kareye giderken o karenin konumunu döndürür.
        int nx = this.x;
        int ny = this.y;
        switch (dir) {
            case NORTH: ny -= 1; break;
            case SOUTH: ny += 1; break;
            case EAST:  nx += 1; break;
            case WEST:  nx -= 1; break;
        }
        return new Position(nx, ny); //mevcut koordinatları değiştirmek yerine yeni koordinatları tutan bir position nesnesi
                                     //üretir.
    }

    @Override
    public boolean equals(Object o) { //iki koordinat noktasının birbiriyle aynı olup olmadığına bakar.
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
