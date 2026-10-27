package com.example.bz214proje.model;

public enum RoomType {
    OTURMA_ODASI, YATAK_ODASI, MUTFAK, KORIDOR, NONE;

    public static RoomType fromDisplayName(String name) { //Odalara sınıfın isminden direkt ulaşabilmek için static tanımladık.
        if (name == null) return NONE; //başta eğer tanımsız bir değer gelip, kod hata verir diye bu if bloğunu ekledik
        if (name.equals("Yatak Odası")) return YATAK_ODASI;
        if (name.equals("Mutfak")) return MUTFAK;
        if (name.equals("Oturma Odası")) return OTURMA_ODASI;
        return NONE; //eğer arayüzden gelen değer null değil fakat aynı zamanda bizim de istediğimiz bir değer değilse
                     //(mesela banyo) o zaman "hiçbiri" şey döndürüyoruz.
    }
}
