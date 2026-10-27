package com.example.bz214proje.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

// Kullanıcının simülasyonla etkileşime girdiği sol taraftaki menü paneli.
// Burada araçlar (kir/mobilya ekleme), hız ayarı, temizlik algoritması ve istasyon butonları yer alıyor.
public class ControlPanel extends VBox {

    private ToggleButton btnAddDirt;
    private ToggleGroup dirtGroup;
    private ToggleButton tbToz;
    private ToggleButton tbSivi;
    private ToggleButton tbLeke;
    private Button btnAddObstacle;
    private ComboBox<String> cmbRooms;

    private Slider speedSlider;
    private Label lblSpeedVal;

    private ToggleGroup algoGroup;
    private RadioButton rbRastgele;
    private RadioButton rbSpiral;
    private RadioButton rbDuvar;

    private Label lblKonumVal;
    private Label lblYonVal;
    private Label lblBataryaVal;

    private Rectangle chargeRect;
    private Button btnUpdateBattery;

    private ToggleButton btnAddMobilya;
    private String seciliMobilyaTuru = "Tekli Koltuk";
    private int seciliMobilyaYonu = 0;

    private Button btnStart;
    private Button btnPause;
    private Button btnReset;
    private Button btnReturnStation;

    public void rotateMobilya() { seciliMobilyaYonu = (seciliMobilyaYonu + 90) % 360; }

    public ControlPanel() {
        this.setSpacing(12);
        this.setPadding(new Insets(0, 15, 0, 0));
        this.setPrefWidth(260);

        String cardStyle = "-fx-background-color: #172130; -fx-background-radius: 8; -fx-border-color: #243042; -fx-border-radius: 8; -fx-padding: 12;";
        String baslikStili = "-fx-text-fill: #f1f5f9; -fx-font-weight: bold; -fx-font-size: 13px;";
        String yaziStili = "-fx-text-fill: #94a3b8; -fx-font-size: 12px;";

        // --------------------------------------------------------
        // 1. ARAÇLAR KARTI (Kir veya Mobilya Ekleme Alanı)
        // --------------------------------------------------------
        VBox cardTools = new VBox(15);
        cardTools.setPadding(new Insets(15));
        cardTools.setStyle(cardStyle);

        HBox toolHeader = new HBox(8);
        toolHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath toolIcon = createIcon("M14.7 1.3c-.4-.4-1-.4-1.4 0l-2.6 2.6c-.3-.3-.8-.3-1.1 0L2.1 11.4c-.6.6-.6 1.5 0 2.1l1.4 1.4c.6.6 1.5.6 2.1 0l7.5-7.5c.3-.3.3-.8 0-1.1l2.6-2.6c.4-.4.4-1 0-1.4L14.7 1.3z", Color.WHITE);
        Label lblTools = new Label("Araçlar");
        lblTools.setStyle(baslikStili);
        toolHeader.getChildren().addAll(toolIcon, lblTools);

        btnAddDirt = new ToggleButton(" Kir Ekle (KAPALI)");
        btnAddDirt.setGraphic(createIcon("M2 2h2v2H2z M6 2h2v2H6z M10 2h2v2H10z M2 6h2v2H2z M6 6h2v2H6z M10 6h2v2H10z", Color.WHITE));
        btnAddDirt.setMaxWidth(Double.MAX_VALUE);
        btnAddDirt.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        // Kir Ekleme Butonu Dinleyicisi
        btnAddDirt.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btnAddDirt.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                btnAddDirt.setText(" Kir Ekle (AÇIK)");
                btnAddMobilya.setSelected(false); // Diğer mod kapalı duruma getirilir
            } else {
                btnAddDirt.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                btnAddDirt.setText(" Kir Ekle (KAPALI)");
            }
        });

        Label lblDirtType = new Label("Kir Türü");
        lblDirtType.setStyle(yaziStili);

        HBox dirtBox = new HBox(5);
        dirtGroup = new ToggleGroup();
        String tglStyle = "-fx-background-color: #273549; -fx-text-fill: #cbd5e1; -fx-background-radius: 5;";
        String tglSelectedStyle = "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;";

        tbToz = new ToggleButton("Toz");
        tbToz.setGraphic(createIcon("M2 2h2v2H2z M6 2h2v2H6z M10 2h2v2H10z M2 6h2v2H2z M6 6h2v2H6z M10 6h2v2H10z", Color.WHITE));
        tbSivi = new ToggleButton("Sıvı");
        tbSivi.setGraphic(createIcon("M8 0 C8 0 2 6 2 10 C2 13.3 4.7 16 8 16 C11.3 16 14 13.3 14 10 C14 6 8 0 8 0 Z", Color.WHITE));
        tbLeke = new ToggleButton("Leke");
        tbLeke.setGraphic(createIcon("M8 2 C4.7 2 2 4.7 2 8 C2 11.3 4.7 14 8 14 C11.3 14 14 11.3 14 8 C14 4.7 11.3 2 8 2 Z M8 4 C10.2 4 12 5.8 12 8 C12 10.2 10.2 12 8 12 C5.8 12 4 10.2 4 8 C4 5.8 5.8 4 8 4 Z", Color.WHITE));

        tbToz.setToggleGroup(dirtGroup); tbSivi.setToggleGroup(dirtGroup); tbLeke.setToggleGroup(dirtGroup);
        tbToz.setSelected(true);
        tbToz.setMaxWidth(Double.MAX_VALUE); tbSivi.setMaxWidth(Double.MAX_VALUE); tbLeke.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tbToz, Priority.ALWAYS); HBox.setHgrow(tbSivi, Priority.ALWAYS); HBox.setHgrow(tbLeke, Priority.ALWAYS);

        tbToz.setStyle(tglSelectedStyle); tbSivi.setStyle(tglStyle); tbLeke.setStyle(tglStyle);
        dirtGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            tbToz.setStyle(tbToz.isSelected() ? tglSelectedStyle : tglStyle);
            tbSivi.setStyle(tbSivi.isSelected() ? tglSelectedStyle : tglStyle);
            tbLeke.setStyle(tbLeke.isSelected() ? tglSelectedStyle : tglStyle);
        });
        dirtBox.getChildren().addAll(tbToz, tbSivi, tbLeke);

        btnAddMobilya = new ToggleButton(" Mobilya Ekle (KAPALI)");
        btnAddMobilya.setMaxWidth(Double.MAX_VALUE);
        btnAddMobilya.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        btnAddMobilya.setOnAction(e -> {
            if (btnAddMobilya.isSelected()) {
                btnAddDirt.setSelected(false); // Kir eklemeyi kapat

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Mobilya Seçimi");
                dialog.initStyle(javafx.stage.StageStyle.UNDECORATED); // Beyaz pencere çubuğunu gizler
                
                // Dialog penceresine de ikon ekle (Görev çubuğunda vb. görünmesi için)
                try {
                    javafx.stage.Stage stage = (javafx.stage.Stage) dialog.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new javafx.scene.image.Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/robot.png"))));
                } catch (Exception ex) {}

                // Koyu Tema Arka Planı
                DialogPane dialogPane = dialog.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #172130; -fx-border-color: #243042; -fx-border-width: 2;");

                // Dialog İçeriği
                VBox content = new VBox(15);
                content.setPadding(new Insets(10));

                Label lblHeader = new Label("Haritaya eklenecek mobilyayı seçin:\n(Yönünü ayarlamak için haritada SAĞ tıklayın)");
                lblHeader.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 13px; -fx-font-weight: bold;");

                Label lblCmb = new Label("Mobilya Türü:");
                lblCmb.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

                ComboBox<String> cmbType = new ComboBox<>();
                cmbType.getItems().addAll("Saksı / Çiçek", "Yuvarlak Sehpa","Tekli Koltuk", "Orta Sehpa", "Büyük Koltuk", "Yatak", "Sehpa 1", "Sehpa 2", "Mutfak 1", "Mutfak 3");
                cmbType.setValue(seciliMobilyaTuru);
                cmbType.setMaxWidth(Double.MAX_VALUE);


                // --------------------------------------------------------
                // ComboBox Buton Stili (Seçili öğe görünümü)
                // --------------------------------------------------------
                cmbType.setButtonCell(new javafx.scene.control.ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: transparent;");
                        }
                    }
                });

                cmbType.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        // Ortak stil değişkenleri (Border alt tarafta 1px olsun)
                        String normalStyle = "-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-padding: 8 10; -fx-font-size: 13px; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;";
                        String hoverStyle = "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 10; -fx-font-size: 13px; -fx-border-color: #334155; -fx-border-width: 0 0 1 0;";

                        if (empty || item == null) {
                            setText(null);
                            setStyle("-fx-background-color: #1e293b;"); // Boş hücreler için çizgisiz koyu renk
                        } else {
                            setText(item);
                            setStyle(normalStyle);
                        }

                        // Fare üzerine gelince parlama efekti ve çizginin korunması
                        setOnMouseEntered(e -> {
                            if (!empty) setStyle(hoverStyle);
                        });
                        setOnMouseExited(e -> {
                            if (!empty) setStyle(normalStyle);
                        });
                    }
                });

                // ComboBox Koyu Tema
                cmbType.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-border-color: #334155; -fx-border-radius: 4;");

                VBox comboContainer = new VBox(5, lblCmb, cmbType);
                content.getChildren().addAll(lblHeader, comboContainer);

                dialogPane.setContent(content);
                dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                // Buton Renkleri
                javafx.scene.Node okBtn = dialogPane.lookupButton(ButtonType.OK);
                if(okBtn != null) okBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                javafx.scene.Node cancelBtn = dialogPane.lookupButton(ButtonType.CANCEL);
                if(cancelBtn != null) cancelBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

                dialog.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        seciliMobilyaTuru = cmbType.getValue();
                        seciliMobilyaYonu = 0; // Her yeni seçimde yönü sıfırla ki yamuk gelmesin
                        btnAddMobilya.setText(" Mobilya (" + seciliMobilyaTuru + ")");
                        btnAddMobilya.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                    } else {
                        btnAddMobilya.setSelected(false);
                        btnAddMobilya.setText(" Mobilya Ekle (KAPALI)");
                        btnAddMobilya.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                    }
                });
            } else {
                btnAddMobilya.setText(" Mobilya Ekle (KAPALI)");
                btnAddMobilya.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
            }
        });

        cardTools.getChildren().addAll(toolHeader, btnAddDirt, lblDirtType, dirtBox, btnAddMobilya);

        // --------------------------------------------------------
        // 2. ROBOT HIZI KARTI
        // --------------------------------------------------------
        VBox cardSpeed = new VBox(15);
        cardSpeed.setPadding(new Insets(15));
        cardSpeed.setStyle(cardStyle);

        HBox speedHeader = new HBox(8);
        speedHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath speedIcon = createIcon("M8 1 C4.13 1 1 4.13 1 8 C1 9.4 1.41 10.7 2.15 11.77 L3.61 10.32 C3.22 9.62 3 8.83 3 8 C3 5.24 5.24 3 8 3 C10.76 3 13 5.24 13 8 C13 8.83 12.78 9.62 12.39 10.32 L13.85 11.77 C14.59 10.7 15 9.4 15 8 C15 4.13 11.87 1 8 1 Z M7 8 A1 1 0 0 0 9 8 L12 4 L11 3 L7 8 Z", Color.WHITE);
        Label lblSpeedTitle = new Label("Robot Hızı");
        lblSpeedTitle.setStyle(baslikStili);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        lblSpeedVal = new Label("1.5x");
        lblSpeedVal.setStyle(yaziStili);

        speedHeader.getChildren().addAll(speedIcon, lblSpeedTitle, spacer1, lblSpeedVal);

        speedSlider = new Slider(0.5, 3.0, 1.5);
        HBox.setHgrow(speedSlider, Priority.ALWAYS);
        speedSlider.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblSpeedVal.setText(String.format("%.1fx", newVal.doubleValue()));
            updateSliderTrackColor(speedSlider);
        });

        Platform.runLater(() -> updateSliderTrackColor(speedSlider));
        cardSpeed.getChildren().addAll(speedHeader, speedSlider);

        // --------------------------------------------------------
        // 3. TEMİZLİK ALGORİTMASI SEÇİM KARTI
        // --------------------------------------------------------
        VBox cardAlgo = new VBox(15);
        cardAlgo.setPadding(new Insets(15));
        cardAlgo.setStyle(cardStyle + " -fx-accent: #3b82f6; -fx-mark-color: white; -fx-control-inner-background: #1e293b; -fx-color: #475569; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        HBox algoHeader = new HBox(8);
        algoHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath algoIcon = createIcon("M13.85 10.35 C13.9 9.88 13.94 9.4 13.94 8.89 C13.94 8.38 13.9 7.9 13.85 7.43 L15.57 6.09 C15.73 5.96 15.77 5.74 15.68 5.56 L14.05 2.74 C13.95 2.56 13.73 2.49 13.54 2.56 L11.52 3.38 C11.1 3.06 10.65 2.78 10.15 2.57 L9.85 0.42 C9.81 0.23 9.65 0.09 9.45 0.09 L6.21 0.09 C6.02 0.09 5.85 0.23 5.82 0.42 L5.51 2.57 C5.01 2.78 4.56 3.06 4.14 3.38 L2.13 2.56 C1.93 2.49 1.71 2.56 1.62 2.74 L0 5.56 C-0.09 5.74 -0.05 5.96 0.11 6.09 L1.82 7.43 C1.77 7.9 1.73 8.38 1.73 8.89 C1.73 9.4 1.77 9.88 1.82 10.35 L0.11 11.69 C-0.05 11.82 -0.09 12.04 0 12.22 L1.62 15.04 C1.71 15.22 1.93 15.29 2.13 15.22 L4.14 14.4 C4.56 14.72 5.01 15 5.51 15.21 L5.82 17.36 C5.85 17.55 6.02 17.69 6.21 17.69 L9.45 17.69 C9.65 17.69 9.81 17.55 9.85 17.36 L10.15 15.21 C10.65 15 11.1 14.72 11.52 14.4 L13.54 15.22 C13.73 15.29 13.95 15.22 14.05 15.04 L15.68 12.22 C15.77 12.04 15.73 11.82 15.57 11.69 L13.85 10.35 Z M7.83 12.09 C6.07 12.09 4.63 10.65 4.63 8.89 C4.63 7.13 6.07 5.69 7.83 5.69 C9.59 5.69 11.03 7.13 11.03 8.89 C11.03 10.65 9.59 12.09 7.83 12.09 Z", Color.WHITE);
        algoIcon.setScaleX(0.85); algoIcon.setScaleY(0.85);

        Label lblAlgo = new Label("Temizlik Algoritması");
        lblAlgo.setStyle(baslikStili);
        algoHeader.getChildren().addAll(algoIcon, lblAlgo);

        algoGroup = new ToggleGroup();
        rbRastgele = new RadioButton("Rastgele"); rbRastgele.setStyle(yaziStili);
        rbSpiral = new RadioButton("Spiral"); rbSpiral.setStyle(yaziStili);
        rbDuvar = new RadioButton("Duvar Takip"); rbDuvar.setStyle(yaziStili);

        rbRastgele.setToggleGroup(algoGroup); rbSpiral.setToggleGroup(algoGroup); rbDuvar.setToggleGroup(algoGroup);
        rbSpiral.setSelected(true);
        cardAlgo.getChildren().addAll(algoHeader, rbRastgele, rbSpiral, rbDuvar);

        // --------------------------------------------------------
        // 4. HEDEF BÖLGE (ODA) SEÇİM KARTI
        // --------------------------------------------------------
        VBox cardRoom = new VBox(15);
        cardRoom.setPadding(new Insets(15));
        cardRoom.setStyle(cardStyle);

        HBox roomHeader = new HBox(8);
        roomHeader.setAlignment(Pos.CENTER_LEFT);

        SVGPath roomIcon = createIcon("M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z", Color.WHITE);
        roomIcon.setScaleX(0.8); roomIcon.setScaleY(0.8);

        Label lblRoom = new Label("Hedef Bölge");
        lblRoom.setStyle(baslikStili);
        roomHeader.getChildren().addAll(roomIcon, lblRoom);

        cmbRooms = new ComboBox<>();
        cmbRooms.getItems().addAll("Oturma Odası", "Yatak Odası", "Mutfak");
        cmbRooms.setValue("Oturma Odası");
        cmbRooms.setMaxWidth(Double.MAX_VALUE);

        cmbRooms.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-radius: 4; -fx-cursor: hand;");

        cmbRooms.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: transparent;");
                }
            }
        });

        cmbRooms.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1e293b;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-padding: 8 10; -fx-font-size: 13px;");
                }

                setOnMouseEntered(e -> {
                    if (!empty) setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 10; -fx-font-size: 13px;");
                });
                setOnMouseExited(e -> {
                    if (!empty) setStyle("-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-font-weight: normal; -fx-padding: 8 10; -fx-font-size: 13px;");
                });
            }
        });

        cardRoom.getChildren().addAll(roomHeader, cmbRooms);

        // --------------------------------------------------------
        // 5. ROBOT DURUMU (Konum, Yön, Batarya) KARTI
        // --------------------------------------------------------
        VBox cardStatus = new VBox(15);
        cardStatus.setPadding(new Insets(15));
        cardStatus.setStyle(cardStyle);

        HBox statusHeader = new HBox(8);
        statusHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath statusIcon = createIcon("M5 6 h14 v12 h-14 z M8 9 h2 v2 h-2 z M14 9 h2 v2 h-2 z M9 15 h6 v1 h-6 z M11 2 h2 v4 h-2 z M2 9 h3 v4 h-3 z M19 9 h3 v4 h-3 z", Color.WHITE);
        statusIcon.setScaleX(0.8); statusIcon.setScaleY(0.8);

        Label lblStatus = new Label("Robot Durumu");
        lblStatus.setStyle(baslikStili);
        statusHeader.getChildren().add(lblStatus);

        GridPane statusGrid = new GridPane();
        statusGrid.setVgap(5);

        Label l1 = new Label("Konum (x, y)"); l1.setStyle(yaziStili);
        lblKonumVal = new Label("(11, 7)"); lblKonumVal.setStyle(baslikStili);

        Label l2 = new Label("Yön"); l2.setStyle(yaziStili);
        HBox directionBox = new HBox(5);
        directionBox.setAlignment(Pos.CENTER_RIGHT);
        SVGPath arrowIcon = createIcon("M0 3 h6 v-3 l5 4 l-5 4 v-3 h-6 z", Color.web("#22c55e"));
        lblYonVal = new Label("Doğu (+)"); lblYonVal.setStyle(baslikStili);
        directionBox.getChildren().addAll(arrowIcon, lblYonVal);

        Label l3 = new Label("Batarya"); l3.setStyle(yaziStili);
        lblBataryaVal = new Label("100%"); lblBataryaVal.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");

        GridPane.setHgrow(l1, Priority.ALWAYS); GridPane.setHgrow(l2, Priority.ALWAYS); GridPane.setHgrow(l3, Priority.ALWAYS);
        GridPane.setHalignment(lblKonumVal, javafx.geometry.HPos.RIGHT); GridPane.setHalignment(directionBox, javafx.geometry.HPos.RIGHT); GridPane.setHalignment(lblBataryaVal, javafx.geometry.HPos.RIGHT);

        statusGrid.add(l1, 0, 0); statusGrid.add(lblKonumVal, 1, 0);
        statusGrid.add(l2, 0, 1); statusGrid.add(directionBox, 1, 1);
        statusGrid.add(l3, 0, 2); statusGrid.add(lblBataryaVal, 1, 2);

        HBox batteryContainer = new HBox(0);
        batteryContainer.setAlignment(Pos.CENTER_LEFT);

        StackPane batteryShell = new StackPane();
        batteryShell.setAlignment(Pos.CENTER_LEFT);
        batteryShell.setPrefHeight(18);
        batteryShell.setPrefWidth(200);
        batteryShell.setMaxWidth(200);
        batteryShell.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 2; -fx-border-radius: 4; -fx-padding: 2;");

        chargeRect = new Rectangle(192, 10, Color.web("#22c55e"));
        chargeRect.setArcWidth(3); chargeRect.setArcHeight(3);
        batteryShell.getChildren().add(chargeRect);

        Rectangle batteryTip = new Rectangle(4, 8, Color.web("#cbd5e1"));
        batteryTip.setArcWidth(2); batteryTip.setArcHeight(2);
        batteryContainer.getChildren().addAll(batteryShell, batteryTip);

        btnUpdateBattery = new Button(" Pili Güncelle");
        btnUpdateBattery.setGraphic(createIcon("M12.5 6.5 L13.5 7.5 L8.5 12.5 L7.5 12.5 L7.5 11.5 Z M14 6 L15 5 C15.3 4.7 15.3 4.3 15 4 L14 3 C13.7 2.7 13.3 2.7 13 3 L12 4 Z M2 14 L2 2 L12 2 L12 4 L4 4 L4 14 Z", Color.WHITE));
        btnUpdateBattery.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnUpdateBattery.setMaxWidth(Double.MAX_VALUE);

        cardStatus.getChildren().addAll(statusHeader, statusGrid, batteryContainer, btnUpdateBattery);

        // --------------------------------------------------------
        // 6. KONTROLLER KARTI (Başlat, Duraklat, Sıfırla vs.)
        // --------------------------------------------------------
        VBox cardControls = new VBox(15);
        cardControls.setPadding(new Insets(15));
        cardControls.setStyle(cardStyle);

        HBox controlsHeader = new HBox(8);
        controlsHeader.setAlignment(Pos.CENTER_LEFT);

        SVGPath ctrlIcon = createIcon("M4.5 3 C2.5 3 1 4.5 1 6.5 C1 8.5 1.5 10 2.5 12 C2.8 12.6 3.5 13 4.2 13 H5.5 L6.5 11 H9.5 L10.5 13 H11.8 C12.5 13 13.2 12.6 13.5 12 C14.5 10 15 8.5 15 6.5 C15 4.5 13.5 3 11.5 3 H4.5 Z M4 5.5 H5 V4.5 H6 V5.5 H7 V6.5 H6 V7.5 H5 V6.5 H4 V5.5 Z M10.5 5 C11.1 5 11.5 5.4 11.5 6 C11.5 6.6 11.1 7 10.5 7 C9.9 7 9.5 6.6 9.5 6 C9.5 5.4 9.9 5 10.5 5 Z M12.5 6.5 C13.1 6.5 13.5 6.9 13.5 7.5 C13.5 8.1 13.1 8.5 12.5 8.5 C11.9 8.5 11.5 8.1 11.5 7.5 C11.5 6.9 11.9 6.5 12.5 6.5 Z", Color.WHITE);
        ctrlIcon.setScaleX(0.9); ctrlIcon.setScaleY(0.9);

        Label lblControls = new Label("Kontroller");
        lblControls.setStyle(baslikStili);
        controlsHeader.getChildren().addAll(ctrlIcon, lblControls);

        String btnStartCss = "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnStartHover = "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnPauseCss = "-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnPauseHover = "-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnResetCss = "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnResetHover = "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnReturnCss = "-fx-background-color: #0e7490; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String btnReturnHover = "-fx-background-color: #06b6d4; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        HBox playPauseBox = new HBox(10);
        btnStart = new Button(" Başlat");
        btnStart.setGraphic(createIcon("M3 2 L12 8 L3 14 Z", Color.WHITE));
        btnStart.setMaxWidth(Double.MAX_VALUE);
        btnStart.setStyle(btnStartCss);
        btnStart.setOnMouseEntered(e -> btnStart.setStyle(btnStartHover));
        btnStart.setOnMouseExited(e -> btnStart.setStyle(btnStartCss));

        btnPause = new Button(" Duraklat");
        btnPause.setGraphic(createIcon("M3 2 H6 V14 H3 Z M10 2 H13 V14 H10 Z", Color.WHITE));
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnPause.setStyle(btnPauseCss);
        btnPause.setOnMouseEntered(e -> btnPause.setStyle(btnPauseHover));
        btnPause.setOnMouseExited(e -> btnPause.setStyle(btnPauseCss));

        HBox.setHgrow(btnStart, Priority.ALWAYS); HBox.setHgrow(btnPause, Priority.ALWAYS);
        playPauseBox.getChildren().addAll(btnStart, btnPause);

        btnReset = new Button(" Sıfırla");
        btnReset.setGraphic(createIcon("M3 3 H13 V13 H3 Z", Color.WHITE));
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setStyle(btnResetCss);
        btnReset.setOnMouseEntered(e -> btnReset.setStyle(btnResetHover));
        btnReset.setOnMouseExited(e -> btnReset.setStyle(btnResetCss));

        btnReturnStation = new Button(" İstasyona Dön");
        btnReturnStation.setGraphic(createIcon("M8 1 L1 7 H3 V14 H13 V7 H15 Z M7 14 V10 H9 V14 Z", Color.WHITE));
        btnReturnStation.setMaxWidth(Double.MAX_VALUE);
        btnReturnStation.setStyle(btnReturnCss);
        btnReturnStation.setOnMouseEntered(e -> btnReturnStation.setStyle(btnReturnHover));
        btnReturnStation.setOnMouseExited(e -> btnReturnStation.setStyle(btnReturnCss));

        cardControls.getChildren().addAll(controlsHeader, playPauseBox, btnReset, btnReturnStation);

        // Footer (PDF'deki "Simülasyon . JavaFX Projesi" yazısı)
        HBox footerBox = new HBox(5);
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.setPadding(new Insets(10, 0, 0, 5));
        SVGPath infoIcon = createIcon("M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z M7 4h2v2H7V4zm0 3h2v5H7V7z", Color.web("#94a3b8"));
        infoIcon.setScaleX(0.8); infoIcon.setScaleY(0.8);
        Label lblFooter = new Label("Simülasyon • JavaFX Projesi");
        lblFooter.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        footerBox.getChildren().addAll(infoIcon, lblFooter);

        Region spacerBottom = new Region();
        VBox.setVgrow(spacerBottom, Priority.ALWAYS); // Footer'ı en alta itmek için

        this.getChildren().addAll(cardTools, cardSpeed, cardAlgo, cardRoom, cardStatus, cardControls, spacerBottom, footerBox);
        updateBatteryLabel(20);
    }

    private SVGPath createIcon(String pathData, Color color) {
        SVGPath path = new SVGPath();
        path.setContent(pathData);
        path.setFill(color);
        return path;
    }

    private void updateSliderTrackColor(Slider slider) {
        Node track = slider.lookup(".track");
        if (track != null) {
            double percent = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()) * 100;
            track.setStyle(String.format("-fx-background-color: linear-gradient(to right, #1d4ed8 %f%%, #475569 %f%%); -fx-background-radius: 5;", percent, percent));
        }
    }
    // --------------------------------------------------------
    // Koyu Tema Batarya Güncelleme Dialogu
    // --------------------------------------------------------
    public java.util.Optional<String> showBatteryDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Batarya Güncelleme");
        dialog.initStyle(javafx.stage.StageStyle.UNDECORATED); // Beyaz pencere çubuğunu gizler

        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new javafx.scene.image.Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/robot.png"))));
        } catch (Exception ex) {}
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #172130; -fx-border-color: #243042; -fx-border-width: 2;");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 10, 10, 10));

        Label lblHeader = new Label("Yeni batarya seviyesini girin (0-100):");
        lblHeader.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 13px; -fx-font-weight: bold;");

        TextField txtBattery = new TextField();
        txtBattery.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-border-color: #3b82f6; -fx-border-radius: 4; -fx-prompt-text-fill: #64748b;");
        txtBattery.setPromptText("Örn: 85");

        content.getChildren().addAll(lblHeader, txtBattery);
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.Node okBtn = dialogPane.lookupButton(ButtonType.OK);
        if(okBtn != null) okBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        javafx.scene.Node cancelBtn = dialogPane.lookupButton(ButtonType.CANCEL);
        if(cancelBtn != null) cancelBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) return txtBattery.getText();
            return null;
        });

        return dialog.showAndWait();
    }

    public void updateBatteryLabel(int percentage) {
        double newWidth = 192.0 * (percentage / 100.0);
        chargeRect.setWidth(Math.max(0, newWidth));

        lblBataryaVal.setText(percentage + "%");
        String colorHex;
        if (percentage > 50) {
            colorHex = "#22c55e";
        } else if (percentage > 20) {
            colorHex = "#eab308";
        } else {
            colorHex = "#ef4444";
        }
        chargeRect.setFill(Color.web(colorHex));
        lblBataryaVal.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold;");
    }

    public ToggleButton getBtnAddDirt() { return btnAddDirt; }
    public ToggleGroup getDirtGroup() { return dirtGroup; }
    public ToggleButton getTbToz() { return tbToz; }
    public ToggleButton getTbSivi() { return tbSivi; }
    public ToggleButton getTbLeke() { return tbLeke; }
    public Button getBtnAddObstacle() { return btnAddObstacle; }
    public Slider getSpeedSlider() { return speedSlider; }
    public Label getLblSpeedVal() { return lblSpeedVal; }
    public ToggleGroup getAlgoGroup() { return algoGroup; }
    public RadioButton getRbRastgele() { return rbRastgele; }
    public RadioButton getRbSpiral() { return rbSpiral; }
    public RadioButton getRbDuvar() { return rbDuvar; }
    public Label getLblKonumVal() { return lblKonumVal; }
    public Label getLblYonVal() { return lblYonVal; }
    public Label getLblBataryaVal() { return lblBataryaVal; }
    public Rectangle getChargeRect() { return chargeRect; }
    public Button getBtnStart() { return btnStart; }
    public Button getBtnPause() { return btnPause; }
    public Button getBtnReset() { return btnReset; }
    public Button getBtnReturnStation() { return btnReturnStation; }
    public ToggleButton getBtnAddMobilya() { return btnAddMobilya; }
    public String getSeciliMobilyaTuru() { return seciliMobilyaTuru; }
    public int getSeciliMobilyaYonu() { return seciliMobilyaYonu; }
    public Button getBtnUpdateBattery() { return btnUpdateBattery; }
    public ComboBox<String> getCmbRooms() { return cmbRooms; }
}