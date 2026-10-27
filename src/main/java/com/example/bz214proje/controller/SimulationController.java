package com.example.bz214proje.controller;

import com.example.bz214proje.model.*;
import com.example.bz214proje.view.MainView;
import javafx.animation.AnimationTimer;
import java.util.List;

public class SimulationController
{
    // REFERANSLAR
    private MainView view;
    private RoomGrid grid;
    private Robot robot; 
    private AnimationTimer gameLoop;
    private PathFinder pathFinder = new PathFinder();

    // SABİTLER
    private static final double CRITICAL_BATTERY_LEVEL = 20.0; // Robotun şarja dönmesi için belirlenen kritik batarya yüzdesi
    private static final double MAX_BATTERY_LEVEL = 100.0; // Maksimum batarya kapasitesi
    private static final double BATTERY_CONSUMPTION_PER_STEP = 0.25; // Robotun her adımda harcadığı batarya miktarı

    // DEĞİŞKENLER
    private boolean isRunning = false; // Simülasyonun o an çalışıp çalışmadığını belirten bayrak
    private long lastUpdate = 0; // Son güncellemenin yapıldığı zamanı tutan değişken
    private int totalArea = 0; // Haritadaki toplam temizlenebilir alan miktarı
    private int elapsedSimulationSeconds = 0; // Simülasyon başladığından beri geçen toplam saniye
    private int totalCollectedDirt = 0; // Simülasyon boyunca temizlenen toplam kir adedi
    private boolean isManualReturn = false; // Robotun kullanıcı tarafından zorla şarja gönderilip gönderilmediğini tutan bayrak
    private boolean isNavigatingToRoom = false; // Robotun hedef odaya gitmekte olduğunu belirten bayrak
    private String currentCleaningRoom = "";

    // Şarj istasyonuna dönüş sırasında adım adım takip edilecek hedefleri hafızada tutmak için Liste referansı oluşturulmuştur.
    private List<Position> returnPath = null;
    // Hedef odaya gidiş sırasında adım adım takip edilecek hedefleri hafızada tutmak için Liste referansı oluşturulmuştur.
    private List<Position> targetRoomPath = null; 

    // YAPICI METOD
    public SimulationController(MainView view, RoomGrid grid, Robot robot)
    {
        // MainAppta üretilen nesneleri parametre olarak alarak globalde ürettiğimiz refereraslarımıza bağladık.
        this.view = view;
        this.grid = grid;
        this.robot = robot;

        grid.loadDefaultMap();  // Model katmanında veri olarak haritayı kurar.
        
        totalArea = grid.calculateWalkableArea();
        
        setupEventHandlers();  // Kullanıcı girdilerinin tetiklenmesi sağlar.
        setupGameLoop();  // Oyun Döngüsünü kurar.

        updateUI();  // Kullanıcı arayüzünü günceller.
        updateStatsPanel(); // AltPaneli günceller
    }

    private void setupEventHandlers()
    {
        view.getControlPanel().getBtnStart().setOnAction(e -> handleStartClick());
        view.getControlPanel().getBtnPause().setOnAction(e -> pauseSimulation());
        view.getControlPanel().getBtnReturnStation().setOnAction(e -> handleReturnStationClick());
        view.getControlPanel().getBtnReset().setOnAction(e -> resetSimulation());

        view.getControlPanel().getCmbRooms().valueProperty().addListener((obs, oldVal, newVal) -> handleRoomChange(newVal));
        view.getControlPanel().getBtnUpdateBattery().setOnAction(e -> handleBatteryUpdate());
        view.getControlPanel().getAlgoGroup().selectedToggleProperty().addListener((obs, oldV, newV) -> robot.resetAIMemory());
        view.getSimulationGrid().setOnGridClicked((col, row) -> handleGridClick(col, row));
    }


    private void handleStartClick()
    {
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
        pause.setOnFinished(event -> startSimulation());
        pause.play();
    }

    private void handleReturnStationClick()
    {
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
        pause.setOnFinished(event -> {
            isManualReturn = true;
            calculateReturnPath();
            robot.setisCleaning(false);
            view.getSoundManager().setCleanSoundQuiet();
            if (!isRunning) {
                startSimulation();
            }
        });
        pause.play();
    }

    private void handleRoomChange(String newVal)
    {
        if (newVal != null && !newVal.equals(currentCleaningRoom)) {
            currentCleaningRoom = newVal;
            if (returnPath != null || robot.getBatteryLevel() < CRITICAL_BATTERY_LEVEL) return;

            RoomType targetRoom = RoomType.fromDisplayName(currentCleaningRoom);
            Position targetPos = grid.getRoomCenter(targetRoom);

            if (targetPos != null) {
                grid.openAllDoors(); 
                robot.setTargetRoomConstraint(null);
                targetRoomPath = pathFinder.findPath(robot.getPosition(), targetPos, grid);
                if (targetRoomPath != null && !targetRoomPath.isEmpty()) {
                    isNavigatingToRoom = true;
                    robot.setisCleaning(false); 
                    robot.resetAIMemory();
                } else {
                    isNavigatingToRoom = false;
                    grid.closeDoor(targetRoom);
                    robot.setTargetRoomConstraint(targetRoom);
                    robot.resetAIMemory();
                }
            }
        }
    }

    private void handleBatteryUpdate()
    {
        view.getControlPanel().showBatteryDialog().ifPresent(input -> {
            try {
                int newBat = Integer.parseInt(input.trim());
                if(newBat >= 0 && newBat <= 100) {
                    robot.setBatteryLevel(newBat);
                    view.getControlPanel().updateBatteryLabel(newBat);
                    updateUI();
                } else {
                    System.out.println("Hata: 0-100 arası değer girilmeli!");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Hata: Sadece sayı giriniz!");
            }
        });
    }

    private void handleGridClick(int col, int row)
    {
        if (view.getControlPanel().getBtnAddMobilya().isSelected()) {
            handleFurnitureAddition(col, row);
        } else if (view.getControlPanel().getBtnAddDirt().isSelected()) {
            handleDirtAddition(col, row);
        }
    }


    private void handleFurnitureAddition(int col, int row)
    {
        String tur = view.getControlPanel().getSeciliMobilyaTuru();
        int yon = view.getControlPanel().getSeciliMobilyaYonu();

        view.getSimulationGrid().addFurnitureVisually(col, row, tur, yon);

        int w = 1, h = 1;
        if (tur.equals("Tekli Koltuk")) { w = 3; h = 3; }
        else if (tur.equals("Orta Sehpa")) { w = 4; h = 3; }
        else if (tur.equals("Büyük Koltuk")) { w = 3; h = 7; }
        else if (tur.equals("Yatak")) { w = 5; h = 7; }
        else if (tur.equals("Sehpa 1") || tur.equals("Sehpa 2")) { w = 1; h = 1; }
        else if (tur.equals("Mutfak 1")) { w = 9; h = 3; }
        else if (tur.equals("Mutfak 2")) { w = 3; h = 7; }
        else if (tur.equals("Mutfak 3")) { w = 3; h = 8; }
        else if (tur.equals("Saksı / Çiçek")) { w = 1; h = 1; }
        else if (tur.equals("Yuvarlak Sehpa")) { w = 2; h = 2; }

        int actualW = (yon % 180 != 0) ? h : w;
        int actualH = (yon % 180 != 0) ? w : h;

        if (tur.equals("Orta Sehpa") || tur.equals("Yatak")) {
            grid.addFurnitureObstacle(col, row, 1, 1);
            grid.addFurnitureObstacle(col + actualW - 1, row, 1, 1);
            grid.addFurnitureObstacle(col, row + actualH - 1, 1, 1);
            grid.addFurnitureObstacle(col + actualW - 1, row + actualH - 1, 1, 1);
        } else {
            grid.addFurnitureObstacle(col, row, actualW, actualH);
        }
        
        totalArea = grid.calculateWalkableArea();
        updateStatsPanel();
    }


    private void handleDirtAddition(int col, int row)
    {
        if (view.getSimulationGrid().isSolid(col, row)) return;
        javafx.scene.control.ToggleButton seciliButon = (javafx.scene.control.ToggleButton) view.getControlPanel().getDirtGroup().getSelectedToggle();
        if (seciliButon != null) {
            String kirIsmi = seciliButon.getText();
            view.getSimulationGrid().addDirtVisually(col, row, kirIsmi);

            DirtType type = DirtType.DUST;
            if (kirIsmi.equalsIgnoreCase("Sıvı") || kirIsmi.equalsIgnoreCase("Sivi")) type = DirtType.LIQUID;
            else if (kirIsmi.equalsIgnoreCase("Leke")) type = DirtType.STAIN;

            grid.addDirt(col, row, type);
        }
    }

    private void startSimulation()
    {
        if (returnPath != null && returnPath.isEmpty()) {
            Position current = robot.getPosition();
            Position station = grid.getChargingStationPosition();
            if (current != null && station != null && 
                current.getX() == station.getX() && current.getY() == station.getY()) {
                
                if (robot.getBatteryLevel() >= CRITICAL_BATTERY_LEVEL) {
                    returnPath = null;
                    isManualReturn = false;
                    
                    robot.resetAIMemory();
                } else {
                    System.out.println("Uyarı: Batarya %20'nin altında olduğu için şarjdan çıkılamıyor!");
                }
            }
        }

        Position rPos = robot.getPosition();
        Position sPos = grid.getChargingStationPosition();
        if (rPos != null && sPos != null && rPos.getX() == sPos.getX() && rPos.getY() == sPos.getY() && returnPath == null && !isNavigatingToRoom) {
            currentCleaningRoom = view.getControlPanel().getCmbRooms().getValue();
            RoomType targetRoom = RoomType.fromDisplayName(currentCleaningRoom);
            Position targetPos = grid.getRoomCenter(targetRoom);

            if (targetPos != null) {
                grid.openAllDoors(); 
                robot.setTargetRoomConstraint(null);
                targetRoomPath = pathFinder.findPath(robot.getPosition(), targetPos, grid);
                if (targetRoomPath != null && !targetRoomPath.isEmpty()) {
                    isNavigatingToRoom = true;
                } else if (targetRoomPath != null && targetRoomPath.isEmpty()) {
                    isNavigatingToRoom = false;
                    grid.closeDoor(targetRoom);
                    robot.setTargetRoomConstraint(targetRoom);
                }
            }
        }

        if (gameLoop != null)
        {
            isRunning = true;
            updateMotorSound();
            gameLoop.start();
        }else
        {
            System.out.println("gameLoop nesnesi oluşturulmadı.");
        }
    }

    private void pauseSimulation()
    {
        if (gameLoop != null)
        {
            isRunning = false;
            gameLoop.stop();
        }
    }

    private void resetSimulation() 
    {
        pauseSimulation(); 
        
        Position startPos = grid.getChargingStationPosition();
        if (startPos != null) {
            robot.setPosition(new Position(startPos.getX(), startPos.getY()));
        }
        
        robot.setBatteryLevel(MAX_BATTERY_LEVEL);
        robot.setisCleaning(false);
        robot.setRemainingCleaningTime(0);
        
        while (robot.getDirection() != Direction.NORTH) {
            robot.turnRight();
        }
        
        grid.clearVisitedCells();
        elapsedSimulationSeconds = 0;
        totalCollectedDirt = 0;
        
        robot.resetAIMemory();
        returnPath = null;
        isManualReturn = false;
        
        isNavigatingToRoom = false;
        targetRoomPath = null;
        grid.openAllDoors(); 
        robot.setTargetRoomConstraint(null);

        grid.getDirtMap().clear();
        
        updateUI();
        updateStatsPanel();
        
        view.getSimulationGrid().resetView();
    }

    private void setupGameLoop()
    {
        gameLoop = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                double speedMultiplier = view.getControlPanel().getSpeedSlider().getValue();
                long delay = (long) (1_000_000_000 / speedMultiplier);

                if (now - lastUpdate >= delay)
                {
                    if (isRunning)
                    {
                        performSimulationStep();
                        updateUI();

                        elapsedSimulationSeconds++;
                        updateStatsPanel();
                    }
                    lastUpdate = now;
                }
            }
        };
    }

    private void performSimulationStep()
    {
        if (checkCriticalBattery()) return;

        grid.markAsVisited(robot.getPosition());

        if (handleBatteryReturn()) return;
        if (handleRoomNavigation()) return;
        if (handleDirtCleaning()) return;
        
        handleNormalSweeping();
    }

    private boolean checkCriticalBattery()
    {
        Position rPos = robot.getPosition();
        Position sPos = grid.getChargingStationPosition();
        boolean atStation = (rPos != null && sPos != null && 
                             rPos.getX() == sPos.getX() && rPos.getY() == sPos.getY());

        if (robot.getBatteryLevel() <= 0 && !atStation) {
            System.out.println("Kritik Uyarı: Batarya tamamen bitti! Robot yolda kaldı. Lütfen 'Sıfırla' (Reset) butonunu kullanın.");
            pauseSimulation();
            return true;
        }
        return false;
    }

    private boolean handleBatteryReturn()
    {
        if (robot.getBatteryLevel() < CRITICAL_BATTERY_LEVEL || returnPath != null) {
            if (returnPath == null) {
                calculateReturnPath();
                isManualReturn = false;
                view.getSoundManager().setCleanSoundQuiet();
            }
            
            if (returnPath != null && !returnPath.isEmpty()) {
                Position nextPos = returnPath.get(0);
                
                if (!grid.isWalkable(nextPos.getX(), nextPos.getY())) {
                    calculateReturnPath();
                    if (returnPath.isEmpty()) {
                        System.out.println("Hata: Şarj istasyonuna giden yol sonradan kapandı!");
                        pauseSimulation();
                        returnPath = null;
                        isManualReturn = false;
                        return true;
                    }
                    nextPos = returnPath.get(0);
                }
                
                returnPath.remove(0);
                moveRobotTo(nextPos);

                if (robot.getBatteryLevel() > 0) {
                    robot.consumeBattery(BATTERY_CONSUMPTION_PER_STEP);
                }
            } else {
                Position current = robot.getPosition();
                Position station = grid.getChargingStationPosition();
                
                if (current.getX() == station.getX() && current.getY() == station.getY()) {
                    view.getSoundManager().stopClean();
                    view.getSoundManager().setCleanSoundNormal();
                    robot.charge();
                    if (robot.getBatteryLevel() >= MAX_BATTERY_LEVEL) {
                        returnPath = null;
                        robot.resetAIMemory();
                        if (isManualReturn) {
                            pauseSimulation();
                            isManualReturn = false;
                        } else {
                            if (currentCleaningRoom != null && !currentCleaningRoom.isEmpty() && !currentCleaningRoom.equals("Tüm Ev")) {
                                RoomType targetRoom = RoomType.fromDisplayName(currentCleaningRoom);
                                Position targetPos = grid.getRoomCenter(targetRoom);
                                if (targetPos != null) {
                                    grid.openAllDoors();
                                    robot.setTargetRoomConstraint(null);
                                    targetRoomPath = pathFinder.findPath(robot.getPosition(), targetPos, grid);
                                    if (targetRoomPath != null && !targetRoomPath.isEmpty()) {
                                        isNavigatingToRoom = true;
                                    } else if (targetRoomPath != null && targetRoomPath.isEmpty()) {
                                        isNavigatingToRoom = false;
                                        grid.closeDoor(targetRoom);
                                        robot.setTargetRoomConstraint(targetRoom);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Hata: Şarj istasyonuna giden yol mobilyalarla kapalı!");
                    pauseSimulation();
                    returnPath = null;
                    isManualReturn = false;
                }
            }
            return true;
        }
        return false;
    }


    private boolean handleRoomNavigation()
    {
        if (isNavigatingToRoom && targetRoomPath != null) {
            RoomType targetRoom = RoomType.fromDisplayName(currentCleaningRoom);

            if (view.getControlPanel().getRbDuvar().isSelected()) {
                if (grid.getRoomType(robot.getPosition().getX(), robot.getPosition().getY()) == targetRoom) {
                    targetRoomPath.clear();
                    
                    if (targetRoom == RoomType.OTURMA_ODASI) {
                        robot.setDirectionTowards(new Position(robot.getPosition().getX(), 25)); 
                    } else if (targetRoom == RoomType.MUTFAK) {
                        robot.setDirectionTowards(new Position(robot.getPosition().getX(), 0)); 
                    } else if (targetRoom == RoomType.YATAK_ODASI) {
                        robot.setDirectionTowards(new Position(robot.getPosition().getX(), 0)); 
                    }
                }
            }

            if (!targetRoomPath.isEmpty()) {
                Position nextPos = targetRoomPath.get(0);
                
                if (!grid.isWalkable(nextPos.getX(), nextPos.getY())) {
                    grid.openAllDoors();
                    Position tgt = grid.getRoomCenter(targetRoom);
                    targetRoomPath = pathFinder.findPath(robot.getPosition(), tgt, grid);
                    if (targetRoomPath == null || targetRoomPath.isEmpty()) {
                        System.out.println("Hata: Hedef odaya giden yol kapalı!");
                        isNavigatingToRoom = false;
                        pauseSimulation();
                        return true;
                    }
                    nextPos = targetRoomPath.get(0);
                }
                
                targetRoomPath.remove(0);
                moveRobotTo(nextPos);
                if (robot.getBatteryLevel() > 0) robot.consumeBattery(BATTERY_CONSUMPTION_PER_STEP);
            }
            
            if (targetRoomPath.isEmpty()) {
                isNavigatingToRoom = false;
                targetRoomPath = null;
                robot.resetAIMemory();
                grid.closeDoor(targetRoom);
                robot.setTargetRoomConstraint(targetRoom);
            }
            return true;
        }
        return false;
    }

    private boolean handleDirtCleaning()
    {
        if (robot.getisCleaning()) {
            int timeLeft = robot.getRemainingCleaningTime();
            if (timeLeft > 0) {
                robot.decreaseCleaningTime();
                return true;
            } else {
                robot.setisCleaning(false);
                Position currentPos = robot.getPosition();
                grid.cleanDirtAt(currentPos);
                view.getSimulationGrid().removeDirtVisually(currentPos.getX(), currentPos.getY());
                totalCollectedDirt++;
                updateMotorSound();
                return true;
            }
        }
        return false;
    }

    private void handleNormalSweeping()
    {
        Robot.Algorithm seciliAlgo = Robot.Algorithm.SPIRAL;
        if (view.getControlPanel().getRbRastgele().isSelected()) seciliAlgo = Robot.Algorithm.RASTGELE;
        else if (view.getControlPanel().getRbDuvar().isSelected()) seciliAlgo = Robot.Algorithm.DUVAR_TAKIP;

        boolean hasMoved = robot.performNextMove(grid, seciliAlgo);

        if (hasMoved) {
            Position newPos = robot.getPosition();
            double angle = getRobotAngle();

            view.getSimulationGrid().markAsCleaned(newPos.getX(), newPos.getY(), angle);

            if (grid.getDirtMap().containsKey(newPos)) {
                Dirt dirt = grid.getDirtMap().get(newPos);
                robot.setisCleaning(true);
                robot.setRemainingCleaningTime(dirt.getCleaningTimeRequired());
                robot.consumeBattery(dirt.getType().getBatteryConsumption());

                String kirTuru = "toz";
                if (dirt.getType().name().contains("LIQUID")) kirTuru = "sıvı";
                else if (dirt.getType().name().contains("STAIN")) kirTuru = "leke";

                view.getSoundManager().playDirtSound(kirTuru);
            }
        } else {
            view.getSoundManager().playBump();
        }

        updateMotorSound();
    }

    private double getRobotAngle()
    {
        switch (robot.getDirection()) {
            case EAST:  return 90;
            case SOUTH: return 180;
            case WEST:  return 270;
            case NORTH:
            default:    return 0;
        }
    }

    private void updateUI()
    {
        int x = robot.getPosition().getX();
        int y = robot.getPosition().getY();
        int batteryPercent = (int) Math.ceil(robot.getBatteryLevel());

        double angle = getRobotAngle();

        view.getSimulationGrid().updateRobotPosition(x, y, angle);
        view.getControlPanel().getLblKonumVal().setText("(" + x + ", " + y + ")");
        view.getControlPanel().updateBatteryLabel(batteryPercent);

        String yonYazisi = "";
        switch (robot.getDirection()) {
            case NORTH: yonYazisi = "Kuzey"; break;
            case EAST:  yonYazisi = "Doğu"; break;
            case SOUTH: yonYazisi = "Güney"; break;
            case WEST:  yonYazisi = "Batı"; break;
        }
        view.getControlPanel().getLblYonVal().setText(yonYazisi);
    }

    private void updateStatsPanel()
    {
        int cleanedArea = grid.getCleanedAreaCount();
        int remainingArea = totalArea - cleanedArea;

        int cleanedPercent = (int) Math.round((double) cleanedArea / totalArea * 100);
        int remainingPercent = 100 - cleanedPercent;

        int minutes = elapsedSimulationSeconds / 60;
        int seconds = elapsedSimulationSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        view.getStatsPanel().getLblTotalVal().setText(totalArea + " m²");
        view.getStatsPanel().getLblCleanedVal().setText(cleanedArea + " m² (%" + cleanedPercent + ")");
        view.getStatsPanel().getLblRemainingVal().setText(remainingArea + " m² (%" + remainingPercent + ")");
        view.getStatsPanel().getLblTimeVal().setText(timeString);
        view.getStatsPanel().getLblDustVal().setText(totalCollectedDirt + " Adet");
    }

    private void moveRobotTo(Position nextPos)
    {
        robot.setDirectionTowards(nextPos);
        robot.setPosition(new Position(nextPos.getX(), nextPos.getY()));
        updateMotorSound();
        double angle = getRobotAngle();
        view.getSimulationGrid().markAsCleaned(robot.getPosition().getX(), robot.getPosition().getY(), angle);
    }

    private void calculateReturnPath()
    {
        grid.openAllDoors();
        robot.setTargetRoomConstraint(null);
        returnPath = pathFinder.findPath(robot.getPosition(), grid.getChargingStationPosition(), grid);
    }

    private void updateMotorSound() {
        if (robot != null && robot.getPosition() != null) {
            boolean onCarpet = view.getSimulationGrid().isRobotOnHali(robot.getPosition().getX(), robot.getPosition().getY());
            view.getSoundManager().playClean(onCarpet);
        }
    }
}