package com.example.bz214proje.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

// ========================================================
// ANA ARAYÜZ (MAIN VIEW) BİLEŞENİ
// ========================================================
// Sol kontrol menüsü, sağ harita paneli ve alt istatistikleri
// tek bir ekranda birleştiren ana konteyner sınıfıdır.
public class MainView extends BorderPane {

    private ControlPanel controlPanel;
    private SimulationGrid simulationGrid;
    private StatsPanel statsPanel;
    private SoundManager soundManager;

    public MainView() {
        // 1. ALT BİLEŞENLERİN OLUŞTURULMASI
        controlPanel = new ControlPanel();
        simulationGrid = new SimulationGrid();
        statsPanel = new StatsPanel();
        soundManager = new SoundManager();

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(5, 0, 20, 0));

        SVGPath robotHead = new SVGPath();
        robotHead.setContent("M5 6 h14 v12 h-14 z M8 9 h2 v2 h-2 z M14 9 h2 v2 h-2 z M9 15 h6 v1 h-6 z M11 2 h2 v4 h-2 z M2 9 h3 v4 h-3 z M19 9 h3 v4 h-3 z");
        robotHead.setFill(Color.web("#93c5fd"));
        robotHead.setScaleX(1.4); robotHead.setScaleY(1.4);

        Label lblTitle = new Label("Robot Süpürge Simülasyonu");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        headerBox.getChildren().addAll(lblTitle);
        this.setTop(headerBox);

        // ========================================================
        // 2. SOL MENÜ KAYDIRMA ALANI
        javafx.scene.control.ScrollPane leftScroll = new javafx.scene.control.ScrollPane(controlPanel);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER); // Yatay çubuk yok
        leftScroll.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED); // Butonlar sığmazsa dikey çubuk çıksın
        leftScroll.setStyle("-fx-background: #0b1320; -fx-background-color: transparent; -fx-border-color: transparent;");
        this.setLeft(leftScroll);

        // ========================================================
        // 3. SAĞ HARİTA VE İSTATİSTİK ALANI (KAYDIRILABİLİR)
        // ========================================================
        VBox rightSide = new VBox(15);
        rightSide.setPadding(new Insets(0, 0, 0, 20));

        // Harita ve istatistik panelinin VBox içerisine eklenmesi
        rightSide.getChildren().addAll(simulationGrid, statsPanel);

        // Tüm sağ tarafı kapsayan ana kaydırma alanı
        javafx.scene.control.ScrollPane rightScroll = new javafx.scene.control.ScrollPane(rightSide);
        rightScroll.setPannable(true); // Farenin sol tıkıyla tutup kaydırma özelliği AKTİF
        rightScroll.setFitToWidth(true);

        // --------------------------------------------------------
        // Kaydırma Çubukları Gizleme
        // --------------------------------------------------------
        rightScroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setStyle("-fx-background: #0b1320; -fx-background-color: transparent; -fx-border-color: transparent;");

        this.setCenter(rightScroll);
        this.setStyle("-fx-background-color: #0b1320;");
        this.setPadding(new Insets(20, 25, 20, 20));

        // ========================================================
        // 4. OLAY DİNLEYİCİLERİ (EVENT LISTENERS)
        // ========================================================

        // Mobilya ekleme modu iptal edildiğinde önizlemeyi gizle
        controlPanel.getBtnAddMobilya().selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                simulationGrid.setPreviewMode(null, 0);
            }
        });

        // Mobilya seçimi onaylandığında yeni mobilya önizlemesini güncelle
        controlPanel.getBtnAddMobilya().textProperty().addListener((obs, oldVal, newVal) -> {
            if (controlPanel.getBtnAddMobilya().isSelected() && !newVal.contains("KAPALI")) {
                simulationGrid.setPreviewMode(controlPanel.getSeciliMobilyaTuru(), controlPanel.getSeciliMobilyaYonu());
            }
        });

        // Sağ Tık
        simulationGrid.setOnRightClick(() -> {
            if (controlPanel.getBtnAddMobilya().isSelected()) {
                controlPanel.rotateMobilya();
                simulationGrid.setPreviewMode(controlPanel.getSeciliMobilyaTuru(), controlPanel.getSeciliMobilyaYonu());
            }
        });

        // Sol Tık (Haritaya Ekleme)
        simulationGrid.setOnGridClicked((col, row) -> {
            if (controlPanel.getBtnAddMobilya().isSelected()) {
                simulationGrid.addFurnitureVisually(col, row, controlPanel.getSeciliMobilyaTuru(), controlPanel.getSeciliMobilyaYonu());
            } else if (controlPanel.getBtnAddDirt().isSelected()) {
                javafx.scene.control.ToggleButton seciliButon = (javafx.scene.control.ToggleButton) controlPanel.getDirtGroup().getSelectedToggle();
                if (seciliButon != null) {
                    simulationGrid.addDirtVisually(col, row, seciliButon.getText());
                }
            }
        });

        // ========================================================
        // 5. SES EFEKTİ DİNLEYİCİLERİ
        // ========================================================
        controlPanel.getBtnStart().addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            soundManager.playSesBasla();
        });

        controlPanel.getBtnPause().addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            soundManager.playSesDuraklat();
            soundManager.stopClean(); // Süpürge sesini kes
        });

        controlPanel.getBtnReturnStation().addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            soundManager.playSesIstasyon();
        });

        controlPanel.getBtnReset().addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            soundManager.stopClean(); // Süpürge sesini kes
        });
    }

    public ControlPanel getControlPanel() { return controlPanel; }
    public SimulationGrid getSimulationGrid() { return simulationGrid; }
    public StatsPanel getStatsPanel() { return statsPanel; }
    public SoundManager getSoundManager() { return soundManager; }
}