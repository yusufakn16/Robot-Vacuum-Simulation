package com.example.bz214proje.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class RoomGrid { //robotun temizlik yapacağı alanı yazdığımız sınıf
    private int width; //haritanın genişliği
    private int height; //haritanın yüksekliği
    private CellType[][] grid; //her bir karede ne olduğunu (duvar,boşluk,şarj istasyonu) tutan matris
    private Map<Position, Dirt> dirtMap;
    private Position chargingStationPosition;
    private Set<Position> visitedCells;

    public RoomGrid(int width,int height){
        this.width=width;
        this.height=height;
        this.grid=new CellType[width][height];
        this.dirtMap=new HashMap<>();
        this.visitedCells = new HashSet<>();
        initializeMap(); //haritayı hata almamak için başlangıçta boş dolduruyoruz.
    }

    public int getWidth(){ //Haritanın genişlik ve yükseklik bilgilerini dışarı veren get metotları
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Map<Position,Dirt> getDirtMap(){
        return dirtMap;
    }

    private void initializeMap(){ //Haritayı hata almamak için başlangıçta boş hücrelerle dolduran metot.
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                grid[x][y]=CellType.EMPTY;
            }
        }
    }

    public boolean isValidPosition(int x,int y){ //arayüzden gelen (x,y) koordinatlarının haritanın içerisinde
                                                 //olup olmadığını denetleyen metot.Bu sayede program ArrayIndexOutOfBoundException
                                                 //hatasıyla çökmez.

        return (x>=0 && x<width && y>=0 && y<height); //x ve y sıfırdan küçük olamaz ve harita boyutlarından büyük olamaz.
    }

    public void addDirt(int x,int y,DirtType type){ //haritada istenilen bölgeye kir ekleyen metot
        if(isValidPosition(x,y)){
            Position pos=new Position(x,y);
            Dirt newDirt=new Dirt(type);
            this.dirtMap.put(pos,newDirt);
        }
    }

    public boolean isWalkable(int x,int y){
        if(!isValidPosition(x,y)){ //Eğer arayüzden gelen koordinatlar haritanın içinde değilse başlangıçta
                                   //if blogu ile kontrol yapıp false döndürüyoruz.
            return false;
        }
        CellType type=grid[x][y];
        return type!=CellType.WALL && type!=CellType.FURNITURE; //Eğer gelen koordinatlarda duvar veya mobilya yoksa
                                                                //isWalkable true döner.
    }

    public void cleanDirtAt(Position position){ //Kiri temizleyen metot
        if(position!=null){
            this.dirtMap.remove(position);
        }
    }

    public Position getChargingStationPosition(){
        return chargingStationPosition;
     }

    public void setCellType(int x,int y,CellType type){
        if(isValidPosition(x,y)){
            this.grid[x][y]=type;
        }
    }

    public void setChargingStation(int x,int y){
        if(isValidPosition(x,y)){
            this.grid[x][y]=CellType.CHARGING_STATION;
            this.chargingStationPosition=new Position(x,y);
        }
    }

    public void addFurnitureObstacle(int startX, int startY,int width,int height){ //Haritaya mobilya ekleyen metot
        for(int x=startX;x<startX+width;x++){
            for(int y=startY;y<startY+height;y++){
                if(isValidPosition(x,y)) { //Mobilya eklemeden önce koordinatların haritayı kapsayıp kapsamadığına
                                           //bakıyoruz.
                    grid[x][y]=CellType.FURNITURE;
                }
            }
        }
    }

    public void markAsVisited(Position pos) { //ziyaret edilen koorinatları alır.
        if (pos != null) {
            visitedCells.add(new Position(pos.getX(), pos.getY()));
        }
    }

    public int getCleanedAreaCount() { // Temizlenen toplam kare sayısını verir
        return visitedCells.size();
    }

    public void clearVisitedCells() { //ziyaret edilen hücreleri sıfırlayan metot
        visitedCells.clear();
    }

    public void addWallLine(int startX, int startY, int length, boolean isHorizontal){
        for(int i=0;i<length;i++){
            if(isHorizontal){
                if(isValidPosition(startX+i,startY)){
                    this.grid[startX+i][startY]=CellType.WALL;
                }
            }else{
                if(isValidPosition(startX,startY+i)){
                    this.grid[startX][startY+i]=CellType.WALL;
                }
            }
        }
    }

    public void loadDefaultMap() { //Haritaya mobilya vs ekleyen metot

        this.addWallLine(0, 15, 10, true);
        this.addWallLine(15, 15, 11, true);
        this.addWallLine(10, 16, 1, false);
        this.addWallLine(15, 16, 1, false);
        this.addWallLine(10, 20, 6, false);
        this.addWallLine(15, 20, 6, false);
        this.addFurnitureObstacle(2, 1, 2, 2);
        this.addFurnitureObstacle(12, 2, 2, 2);
        this.addFurnitureObstacle(7, 1, 3, 3);
        this.addFurnitureObstacle(16, 1, 3, 3);
        this.addFurnitureObstacle(10, 6, 1, 1);
        this.addFurnitureObstacle(13, 6, 1, 1);
        this.addFurnitureObstacle(10, 8, 1, 1);
        this.addFurnitureObstacle(13, 8, 1, 1);
        this.addFurnitureObstacle(3, 4, 3, 7);
        this.addFurnitureObstacle(20, 4, 3, 7);
        this.addFurnitureObstacle(0, 17, 1, 1);
        this.addFurnitureObstacle(0, 18, 1, 1);
        this.addFurnitureObstacle(6, 18, 1, 1);
        this.addFurnitureObstacle(0, 22, 1, 1);
        this.addFurnitureObstacle(6, 22, 1, 1);
        this.addFurnitureObstacle(0, 23, 1, 1);
        this.addFurnitureObstacle(23, 16, 3, 9);
        this.addFurnitureObstacle(15, 23, 8, 3);
        this.setChargingStation(0, 12);
    }

    public int calculateWalkableArea(){
        int count=0;
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                if(isWalkable(x,y)){
                    count++;
                }
            }
        }
        return count;
    }

    public RoomType getRoomType(int x, int y) {
        if (!isValidPosition(x, y))
            return RoomType.NONE;
        if (y <= 14)
            return RoomType.OTURMA_ODASI;
        if (x <= 9 && y >= 16)
            return RoomType.YATAK_ODASI;
        if (x >= 16 && y >= 16)
            return RoomType.MUTFAK;
        return RoomType.KORIDOR;
    }

    public Position getRoomCenter(RoomType room) { //Merkez noktaları döner Çünkü robot BFS ile baseye gideceği zaman
                                                   //odaların orta noktasını kullanıyor.
        switch (room) {
            case OTURMA_ODASI: return new Position(12, 7);
            case YATAK_ODASI:  return new Position(5, 20);
            case MUTFAK:       return new Position(20, 20);
            default:           return getChargingStationPosition();
        }
    }

    public void openAllDoors() {
        for (int y = 17; y <= 19; y++) {
            setCellType(10, y, CellType.EMPTY); //mutfak ve yatak odası kapısı olmadığı için o bölgeleri boş tanımladık.
            setCellType(15, y, CellType.EMPTY);
        }
    }

    public void closeDoor(RoomType room) {
        if (room == RoomType.YATAK_ODASI || room == RoomType.MUTFAK) {
            int doorX = (room == RoomType.YATAK_ODASI) ? 10 : 15;
            for (int y = 17; y <= 19; y++) {
                setCellType(doorX, y, CellType.WALL);
            }
        } else if (room == RoomType.OTURMA_ODASI) {
            for (int y = 17; y <= 19; y++) {
                setCellType(10, y, CellType.WALL);
                setCellType(15, y, CellType.WALL);
            }
        }
    }
}
