package com.example.bz214proje.model;

public enum DirtType { //Bu enum şunu söyler: Eğer bir şey toz ise temizlenmesi 1 saniye sürer ve 2 birim batarya yer.
                       //genel kuralı söyler.
    DUST(1,2.0),LIQUID(3,3.0),STAIN(4,4.0);
    private final int cleaningTime; //OOP'nin kapsülleme ilkesine göre değişkenlerimizi "private" yaptık ve "get..." metotları
                                    //ekledik. Çünkü değişken değerlerimizin dışarıdan değiştirilmesini isteyiz ve eğer birisi
                                    // temizlenme süresini ve pil tüketimini merak ederse "get..." metodu ile öğrenebilir fakat on-
                                    //ların değerini değiştiremez.
    private final double batteryConsumption;

    DirtType(int cleaningTime,double batteryConsumption){ //enum'larda constructor metotlar privatedir.
        this.cleaningTime=cleaningTime;
        this.batteryConsumption=batteryConsumption;
    }

    public int getCleaningTime() {
        return cleaningTime;
    }

    public double getBatteryConsumption() {
        return batteryConsumption;
    }
}
