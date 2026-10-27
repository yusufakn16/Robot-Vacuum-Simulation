package com.example.bz214proje.model;

public class Robot {
    private Position position;
    private Direction direction;
    private double batteryLevel;//OOP'nin kapsülleme kuralına göre değişkenleri private yaptık
                                //ve "get...", "set..." metotlarını yazdık.
    private boolean isCleaning;
    private int remainingCleaningTime;
    private int spiralStepsTaken = 0;
    private int spiralTargetSteps = 1;
    private int spiralTurns = 0;
    private boolean foundWall = false;
    private int randomStepsTaken=0;
    private int randomTargetSteps=2;
    private RoomType targetRoomConstraint = null;


    public enum Algorithm { RASTGELE, SPIRAL, DUVAR_TAKIP }

    public Robot(Position position,Direction direction){
        this.position=position;
        this.direction=direction;
        this.batteryLevel=100.0;
    }


    public void resetAIMemory() { //spiral, rastgele, duvar takip algoritmalarının düzgün çalışmasını sağlayan
                                  //kısım
        this.spiralStepsTaken = 0;
        this.spiralTargetSteps = 1;
        this.spiralTurns = 0;
        this.foundWall = false;
        this.randomStepsTaken=0;
        this.randomTargetSteps=2;
    }

    public void setDirectionTowards(Position target) { //robotun adım atmadan önce yönünü doğru kareye çevirmesini sağlayan
                                                       //metot
        if (target == null) return;
        int dx = target.getX() - this.position.getX();
        int dy = target.getY() - this.position.getY();
        Direction targetDir = this.direction;

        if (dx > 0) targetDir = Direction.EAST;
        else if (dx < 0) targetDir = Direction.WEST;
        else if (dy > 0) targetDir = Direction.SOUTH;
        else if (dy < 0) targetDir = Direction.NORTH;

        while (this.direction != targetDir) {
            this.turnRight();
        }
    }

    public boolean performNextMove(RoomGrid grid, Algorithm currentAlgo) {
        switch (currentAlgo) {
            case RASTGELE:     return moveRastgele(grid);
            case SPIRAL:       return moveSpiral(grid);
            case DUVAR_TAKIP:  return moveDuvarTakip(grid);
            default:           return false;
        }
    }

    private boolean moveRastgele(RoomGrid grid) {
        Position nextPos = this.position.getAdjacent(this.direction); //sonraki kareyi hesaplar
        boolean bumped = !canMoveTo(grid, nextPos.getX(), nextPos.getY()); //eğer duvar vs varsa, robot oraya çarpar ve
                                                                           //bumped (çarpıldı) true olur.

        if (randomStepsTaken >= randomTargetSteps || bumped) { //hedef adım bittiğinde de robot yön değiştirir.

            if (Math.random() < 0.5) { //%50 ihtimalle ya sağa ya sola döner
                this.turnRight();
            } else {
                this.turnLeft();
            }

            Position checkPos = this.position.getAdjacent(this.direction);
            if (!canMoveTo(grid, checkPos.getX(), checkPos.getY())) {
                this.turnRight();
            }

            randomStepsTaken = 0;
            randomTargetSteps = 2 + (int)(Math.random() * 3);

            return !bumped; 
        }

        this.move();
        randomStepsTaken++;
        return true;
    }

    private boolean moveSpiral(RoomGrid grid) {
        if (spiralTargetSteps > 15) { //sarmal odadan taşmasın diye 15 adıma ulaştığı zaman sarmalı sıfırlıyor
            spiralTargetSteps = 1;
            spiralTurns = 0;
            spiralStepsTaken = 0;
        }

        Position nextPos = this.position.getAdjacent(this.direction);

        if (canMoveTo(grid, nextPos.getX(), nextPos.getY())) {  //1-1 2-2 3-3 şeklinde sarmal çıkamsını sağlayan metot
            this.move();
            spiralStepsTaken++;
            if (spiralStepsTaken >= spiralTargetSteps) {
                this.turnRight();
                spiralTurns++;
                spiralStepsTaken = 0;
                if (spiralTurns >= 2) {
                    spiralTargetSteps++;
                    spiralTurns = 0;
                }
            }
            return true;
        } else {
            java.util.List<Direction> openDirs = new java.util.ArrayList<>();
            for (Direction dir : Direction.values()) {
                Position adj = this.position.getAdjacent(dir);
                if (canMoveTo(grid, adj.getX(), adj.getY())) {
                    openDirs.add(dir);
                }
            }
            if (!openDirs.isEmpty()) {
                Direction chosenDir = openDirs.get((int) (Math.random() * openDirs.size()));
                while (this.direction != chosenDir) {
                    this.turnRight();
                }
            } else {
                this.turnRight();
                this.turnRight();
            }
            spiralTargetSteps = 3;
            spiralTurns = 0;
            spiralStepsTaken = 0;
            return false;
        }
    }

    private boolean moveDuvarTakip(RoomGrid grid) {
        Position forwardPos = this.position.getAdjacent(this.direction);

        if (!foundWall) {
            if (canMoveTo(grid, forwardPos.getX(), forwardPos.getY())) {
                this.move();
                return true;
            } else {
                foundWall = true; //duvar bulundu
                this.turnLeft(); //duvar ghep robotun sağında kaldığı için duvarı bulduğu an sola dönecek.
                return false;
            }
        }

        Direction rightDir = this.direction.getRightDir();
        Position rightPos = this.position.getAdjacent(rightDir);

        if (canMoveTo(grid, rightPos.getX(), rightPos.getY())) { //sağ kontrol
            this.turnRight();
            this.move();
            return true;
        }
        else if (canMoveTo(grid, forwardPos.getX(), forwardPos.getY())) { //düz yol kontrolü
            this.move();
            return true;
        }
        else { // sol kontrol ve sola dön
            this.turnLeft();
            return false;
        }
    }

    public boolean getisCleaning(){
        return isCleaning;
    }

    public void setisCleaning(boolean isCleaning){
        this.isCleaning=isCleaning;
    }

    public Position getPosition() {
        return position;
    }


    public void setRemainingCleaningTime(int remainingCleaningTime){
        this.remainingCleaningTime=remainingCleaningTime;
    }

    public int getRemainingCleaningTime(){
        return remainingCleaningTime;
    }

    public void setPosition(Position position) { //kullanıcının cihazı bazen direk baseye götürmek isteyeceği
                                                 //durumlar için "setPosition" metodunu yazdık.
        this.position = position;
    }

    public Direction getDirection() {//Dışarıdan manuel olarak cihazın yönü kontrol edilemediği için
                                     //"setDirection" metodunu yazmadık.
        return direction;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setTargetRoomConstraint(RoomType room) { // Hedef odayı belirlemek veya sıfırlamak (null) için
        this.targetRoomConstraint = room;
    }

    public void decreaseCleaningTime() {
        if (this.remainingCleaningTime > 0) {
            this.remainingCleaningTime--;
        }
    }

    public void consumeBattery(double amount){ //amount harcanak miktarı gösterir.(toz için 3.0 birim)
        this.batteryLevel-=amount;
    }

    public void charge(){ //robot baseye gittiğinde pili yenilenecek
        this.batteryLevel+=5.0;
        if(this.batteryLevel>100.0){
            this.batteryLevel=100.0;
        }
    }

    public void turnRight(){
        switch (this.direction){
            case NORTH: this.direction=Direction.EAST;
                        break;
            case EAST: this.direction=Direction.SOUTH;
                       break;
            case SOUTH: this.direction=Direction.WEST;
                        break;
            case WEST: this.direction=Direction.NORTH;
                       break;
        }
    }

    public void turnLeft(){
        switch (this.direction){
            case NORTH: this.direction=Direction.WEST;
                        break;
            case WEST: this.direction=Direction.SOUTH;
                       break;
            case SOUTH: this.direction=Direction.EAST;
                        break;
            case EAST: this.direction=Direction.NORTH;
                       break;
        }
    }

    public void move(){ //robotun koordinatlarını günceller
        switch (this.direction){
            case NORTH: this.position.setY(this.position.getY()-1);
                        break;
            case EAST: this.position.setX(this.position.getX()+1);
                       break;
            case SOUTH: this.position.setY(this.position.getY()+1);
                        break;
            case WEST: this.position.setX(this.position.getX()-1);
                       break;
        }
        consumeBattery(0.25); //Her adımda 0.25 birim pili azaltır.
    }

    private boolean canMoveTo(RoomGrid grid, int x, int y) {
        if (!grid.isWalkable(x, y)) {
            return false;
        }
        if (this.targetRoomConstraint != null) {
            RoomType nextRoom = grid.getRoomType(x, y);

            if (nextRoom != this.targetRoomConstraint) {
                return false;
            }
        }
        return true;
    }
}
