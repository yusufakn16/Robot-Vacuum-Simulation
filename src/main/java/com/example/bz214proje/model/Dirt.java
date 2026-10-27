package com.example.bz214proje.model;

public class Dirt { //Bu sınıfın DirtType enumundan farkı fiziksel iş yapmasıdır. Robotun (x,y) koordinatlarında
                    //üzerinde geçip temizleyeceği gerçek kirdir.
    private DirtType type;
    private int cleaningTimeRequired;

    public Dirt(DirtType type){
        this.type=type;
        this.cleaningTimeRequired=type.getCleaningTime();
    }

    public DirtType getType(){
        return type;
    }

    public int getCleaningTimeRequired(){
        return this.cleaningTimeRequired;
    }

}
