package com.example.bz214proje.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import java.util.function.BiConsumer;
import java.util.Objects;

// ========================================================
// SİMÜLASYON HARİTASI (SIMULATION GRID)
// ========================================================
// Odaların, mobilyaların, kirlerin ve robotun çizildiği ve 
// konumlarının tutulduğu ana ızgara (GridPane) bileşenidir.
public class SimulationGrid extends GridPane {

    private static final int COLS = 26;
    private static final int ROWS = 26;
    private static final int CELL_SIZE = 42;
    private static final int WALL_THICKNESS = 14;
    private static final int WALL_PADDING = 3;

    private Image furnitureSheet;
    private Image floorTexture;
    private Image robotTexture;
    private Image tozTexture;
    private Image siviTexture;
    private Image lekeTexture;
    private Image fayansTexture;
    private Image yatakParkeTexture;
    private Image yatakTexture;
    private Image sehpa1Texture;
    private Image sehpa2Texture;
    private Image mutfak1Texture;
    private Image mutfak3Texture;
    private Image haliTexture;
    private Image yollukTexture;

    private javafx.scene.Node[][] dirtNodes = new javafx.scene.Node[ROWS][COLS];
    private boolean[][] furnitureMap = new boolean[ROWS][COLS];
    private boolean[][] solidMap = new boolean[ROWS][COLS];
    private boolean[][] initialFurnitureMap = new boolean[ROWS][COLS];
    private boolean[][] initialSolidMap = new boolean[ROWS][COLS];
    private boolean[][] carpetMap = new boolean[ROWS][COLS];

    private java.util.List<javafx.scene.Node> pathNodes = new java.util.ArrayList<>();
    private java.util.List<javafx.scene.Node> dynamicFurnitureNodes = new java.util.ArrayList<>();
    private java.util.List<javafx.scene.Node> cleanedTrailNodes = new java.util.ArrayList<>();
    private java.util.List<javafx.scene.Node> allFurnitureNodes = new java.util.ArrayList<>();

    private Pane trailOverlay;
    private javafx.scene.shape.Polyline trailLine;
    private javafx.scene.Group ghostGroup;

    private ImageView robotView;
    private StackPane robotContainer;
    private GridPane roomGrid;

    private ImageView ghostView;
    private String previewType = null;
    private int previewRotation = 0;
    private Runnable onRightClickAction;

    public SimulationGrid() {
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: transparent;");

        try {
            furnitureSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/mobilya.png")));
            floorTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/parke.png")));
            robotTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/robot.png")));
            tozTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/toz.png")));
            siviTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/sivi.png")));
            yatakParkeTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/yatak_parke.png")));
            fayansTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/fayans.png")));
            yollukTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/yolluk.png")));
            haliTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/hali.png")));
            lekeTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/leke.png")));

            yatakTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/yatak.png")));
            sehpa1Texture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/sehpa1.png")));
            sehpa2Texture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/sehpa2.png")));
            mutfak1Texture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/mutfak1.png")));
            mutfak3Texture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bz214proje/images/mutfak3.png")));
        } catch (NullPointerException e) {
            System.err.println("HATA: resources/images klasöründe eksik veya uzantısı yanlış resim var!");
        }

        koordinatlariVeHaritayiCiz();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                initialFurnitureMap[r][c] = furnitureMap[r][c];
                initialSolidMap[r][c] = solidMap[r][c];
            }
        }
    }

    private void koordinatlariVeHaritayiCiz() {
        Region woodFloor = new Region();
        if (floorTexture != null && !floorTexture.isError()) {
            BackgroundImage bgImage = new BackgroundImage(
                    floorTexture, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                    BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
            );
            woodFloor.setBackground(new Background(bgImage));
        } else {
            woodFloor.setStyle("-fx-background-color: #e6d5b8;");
        }

        roomGrid = new GridPane();
        roomGrid.setHgap(0); roomGrid.setVgap(0);

        for (int col = 0; col < COLS; col++) {
            roomGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE, CELL_SIZE, CELL_SIZE));
        }
        for (int row = 0; row < ROWS; row++) {
            roomGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE, CELL_SIZE, CELL_SIZE));
        }

        int fayansBaslangicSatiri = 15;
        int fayansSatirSayisi = ROWS - fayansBaslangicSatiri;



        Rectangle yatakParkesi = new Rectangle(10 * CELL_SIZE, fayansSatirSayisi * CELL_SIZE);
        if (yatakParkeTexture != null && !yatakParkeTexture.isError()) {
            yatakParkesi.setFill(new javafx.scene.paint.ImagePattern(yatakParkeTexture, 0, 0, CELL_SIZE * 3, CELL_SIZE * 3, false));
        } else {
            yatakParkesi.setFill(Color.web("#78350f"));
        }
        roomGrid.add(yatakParkesi, 0, fayansBaslangicSatiri, 10, fayansSatirSayisi);

        Rectangle mutfakFayansi = new Rectangle(11 * CELL_SIZE, fayansSatirSayisi * CELL_SIZE);
        if (fayansTexture != null && !fayansTexture.isError()) {
            mutfakFayansi.setFill(new javafx.scene.paint.ImagePattern(fayansTexture, 0, 0, CELL_SIZE, CELL_SIZE, false));
        } else {
            mutfakFayansi.setFill(Color.web("#9ca3af"));
        }
        roomGrid.add(mutfakFayansi, 15, fayansBaslangicSatiri, 11, fayansSatirSayisi);

        Rectangle salonHalisi = new Rectangle(10 * CELL_SIZE, 7 * CELL_SIZE);
        if (haliTexture != null && !haliTexture.isError()) {
            salonHalisi.setFill(new javafx.scene.paint.ImagePattern(haliTexture, 0, 0, 1, 1, true));
        } else {
            salonHalisi.setFill(Color.web("#cbd5e1"));
        }
        roomGrid.add(salonHalisi, 8, 4, 10, 7);
        for(int r = 4; r < 4 + 7; r++) {
            for(int c = 8; c < 8 + 10; c++) {
                carpetMap[r][c] = true;
            }
        }

        Rectangle koridorHalisi = new Rectangle(3 * CELL_SIZE, 8 * CELL_SIZE);

        // Koridor yolluk zemini
        if (yollukTexture != null && !yollukTexture.isError()) {
            koridorHalisi.setFill(new javafx.scene.paint.ImagePattern(yollukTexture, 0, 0, 1, 1, true));
        } else {
            koridorHalisi.setFill(Color.web("#cbd5e1")); // Resim yüklenemezse gri renk atar
        }
        roomGrid.add(koridorHalisi, 11, 16, 3, 8);
        for(int r = 16; r < 16 + 8; r++) {
            for(int c = 11; c < 11 + 3; c++) {
                carpetMap[r][c] = true;
            }
        }


        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.web("#94a3b8", 0.4));
                cell.setStrokeWidth(0.5);
                roomGrid.add(new StackPane(cell), col, row);
            }
        }

        if (furnitureSheet != null && !furnitureSheet.isError()) {
            placeFurniture("Saksı / Çiçek", roomGrid, 2, 1, 2, 2, 3211, 151, 745, 682, 0, 1.0);
            placeFurniture("Yuvarlak Sehpa", roomGrid, 12, 2, 2, 2, 191, 611, 635, 575, 0, 1.2);
            placeFurniture("Tekli Koltuk", roomGrid, 7, 1, 3, 3, 118, 2979, 1150, 855, 180, 1.0);
            placeFurniture("Tekli Koltuk", roomGrid, 16, 1, 3, 3, 118, 2979, 1150, 855, 180, 1.0);
            placeFurniture("Orta Sehpa", roomGrid, 10, 6, 4, 3, 1500, 1550, 1068, 1153, 0, 1.35);
            placeFurniture("Büyük Koltuk", roomGrid, 3, 4, 3, 7, 2989, 1326, 904, 2474, 180, 1.0);
            placeFurniture("Büyük Koltuk", roomGrid, 20, 4, 3, 7, 2989, 1326, 904, 2474, 0, 1.0);

            if (sehpa1Texture != null) placeFurniture("Sehpa 1", roomGrid, 0, 17, 1, 1, 0, 0, 0, 0, 0, 1.3);
            if (yatakTexture != null) placeFurniture("Yatak", roomGrid, 0, 18, 5, 7, 0, 0, 0, 0, 270, 1.0);
            if (sehpa2Texture != null) placeFurniture("Sehpa 2", roomGrid, 0, 23, 1, 1, 0, 0, 0, 0, 180, 1.3);

            if (mutfak1Texture != null) placeFurniture("Mutfak 1", roomGrid, 23, 13, 9, 3, 0, 0, 0, 0, 90, 1.0);
            if (mutfak3Texture != null) placeFurniture("Mutfak 3", roomGrid, 15, 23, 3, 8, 0, 0, 0, 0, 90, 1.0);
        }

        trailOverlay = new Pane();
        trailOverlay.setPrefSize(COLS * CELL_SIZE, ROWS * CELL_SIZE);
        trailOverlay.setMouseTransparent(true);
        trailLine = new javafx.scene.shape.Polyline();
        trailLine.setStroke(Color.web("#3b82f6"));
        trailLine.setStrokeWidth(2.5);
        trailLine.getStrokeDashArray().addAll(10d, 8d);
        trailOverlay.getChildren().add(trailLine);

        GridPane.setColumnSpan(trailOverlay, COLS);
        GridPane.setRowSpan(trailOverlay, ROWS);
        roomGrid.add(trailOverlay, 0, 0);

        StackPane wallContainer = new StackPane(woodFloor, roomGrid);
        wallContainer.setPadding(new Insets(0));
        wallContainer.setStyle("");

        Pane housePlan = new Pane();
        int wallThick = 14;
        double gridW = COLS * CELL_SIZE;

        double visualGridH = 26 * CELL_SIZE;

        housePlan.setPrefSize(gridW + (wallThick * 2), visualGridH + (wallThick * 2));
        housePlan.setMinSize(gridW + (wallThick * 2), visualGridH + (wallThick * 2));

        wallContainer.setLayoutX(wallThick);
        wallContainer.setLayoutY(wallThick);
        housePlan.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 5, 5);");

        javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle(gridW + (wallThick * 2), visualGridH + (wallThick * 2));
        housePlan.setClip(clipRect);

        Color wallColor = Color.web("#1e293b");

        Rectangle outerTop = new Rectangle(gridW + (wallThick * 2), wallThick, wallColor);
        outerTop.setLayoutX(0); outerTop.setLayoutY(0);

        // Dış alt duvar sınır koordinatları
        Rectangle outerBottom = new Rectangle(gridW + (wallThick * 2), wallThick, wallColor);
        outerBottom.setLayoutX(0);
        outerBottom.setLayoutY(visualGridH + wallThick);

        Rectangle outerLeft = new Rectangle(wallThick, visualGridH + (wallThick * 2), wallColor);
        outerLeft.setLayoutX(0); outerLeft.setLayoutY(0);

        Rectangle outerRight = new Rectangle(wallThick, visualGridH + (wallThick * 2), wallColor);
        outerRight.setLayoutX(gridW + wallThick); outerRight.setLayoutY(0);

        double yRow15 = wallThick + (15 * CELL_SIZE);

        Rectangle inHorizLeft = new Rectangle(10 * CELL_SIZE, wallThick, wallColor);
        inHorizLeft.setLayoutX(wallThick); inHorizLeft.setLayoutY(yRow15);

        Rectangle inHorizRight = new Rectangle(11 * CELL_SIZE, wallThick, wallColor);
        inHorizRight.setLayoutX(wallThick + (15 * CELL_SIZE)); inHorizRight.setLayoutY(yRow15);

        double xCol10 = wallThick + (10 * CELL_SIZE);
        Rectangle yatakSagUst = new Rectangle(wallThick, 1.5 * CELL_SIZE, wallColor);
        yatakSagUst.setLayoutX(xCol10); yatakSagUst.setLayoutY(yRow15 + wallThick);

        double altDuvarStart = yRow15 + wallThick + (4.5 * CELL_SIZE);
        double altDuvarHeight = visualGridH + wallThick - altDuvarStart;

        Rectangle yatakSagAlt = new Rectangle(wallThick, altDuvarHeight, wallColor);
        yatakSagAlt.setLayoutX(xCol10);
        yatakSagAlt.setLayoutY(altDuvarStart);

        double xCol15 = wallThick + (15 * CELL_SIZE);
        Rectangle mutfakSolUst = new Rectangle(wallThick, 1.5 * CELL_SIZE, wallColor);
        mutfakSolUst.setLayoutX(xCol15);
        mutfakSolUst.setLayoutY(yRow15 + wallThick);

        Rectangle mutfakSolAlt = new Rectangle(wallThick, altDuvarHeight, wallColor);
        mutfakSolAlt.setLayoutX(xCol15);
        mutfakSolAlt.setLayoutY(altDuvarStart);

        housePlan.getChildren().addAll(
                wallContainer,
                outerTop, outerBottom, outerLeft, outerRight,
                inHorizLeft, inHorizRight, yatakSagUst, yatakSagAlt, mutfakSolUst, mutfakSolAlt
        );

        int offset = wallThick + WALL_PADDING;
        String fontStyle = "-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-weight: bold;";

        HBox topCoords = new HBox(0);
        topCoords.setPadding(new Insets(0, 0, 5, offset));
        for (int col = 0; col < COLS; col++) {
            Label lblCol = new Label(String.valueOf(col));
            lblCol.setPrefWidth(CELL_SIZE);
            lblCol.setAlignment(Pos.CENTER);
            lblCol.setStyle(fontStyle);
            topCoords.getChildren().add(lblCol);
        }

        VBox leftCoords = new VBox(0);
        leftCoords.setPadding(new Insets(offset, 5, 0, 0));

        for (int row = 0; row < 26; row++) {
            Label lblRow = new Label(String.valueOf(row));
            lblRow.setPrefHeight(CELL_SIZE);
            lblRow.setAlignment(Pos.CENTER_RIGHT);
            lblRow.setStyle(fontStyle);
            leftCoords.getChildren().add(lblRow);
        }

        StackPane dockNode = new StackPane();
        Rectangle dockBase = new Rectangle(CELL_SIZE, CELL_SIZE, Color.web("#0f172a"));
        dockBase.setArcWidth(8); dockBase.setArcHeight(8);
        Circle dockPad = new Circle(CELL_SIZE * 0.35, Color.web("#1e293b"));
        SVGPath bolt = new SVGPath();
        bolt.setContent("M10 2 L3 11 H9 L7 19 L16 9 H10 Z");
        bolt.setFill(Color.web("#22c55e"));
        bolt.setScaleX(0.7);
        bolt.setScaleY(0.7);
        dockNode.getChildren().addAll(dockBase, dockPad, bolt);

        roomGrid.add(dockNode, 0, 12);

        if (robotTexture != null && !robotTexture.isError()) {
            robotView = new ImageView(robotTexture);
            robotView.setViewport(new Rectangle2D(102, 107, 419, 410));
            robotView.setFitWidth(CELL_SIZE * 1.0);
            robotView.setFitHeight(CELL_SIZE * 1.0);
            robotView.setPreserveRatio(true);
            robotView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 6, 0, 0, 3);");
            robotView.setRotate(90);

            robotContainer = new StackPane(robotView);
            roomGrid.add(robotContainer, 0, 12);
        }

        this.add(topCoords, 1, 0);
        this.add(leftCoords, 0, 1);
        this.add(housePlan, 1, 1);

        for(int c=0; c<10; c++) { solidMap[15][c] = true; furnitureMap[15][c] = true; }
        for(int c=15; c<COLS; c++) { solidMap[15][c] = true; furnitureMap[15][c] = true; }

        for(int r=16; r<ROWS; r++) {
            boolean isDoor = (r >= 17 && r <= 19);
            if (!isDoor) {
                solidMap[r][10] = true; furnitureMap[r][10] = true;
                solidMap[r][15] = true; furnitureMap[r][15] = true;
            }
        }


        ghostView = new ImageView(furnitureSheet);
        ghostView.setViewport(new Rectangle2D(0, 0, 1, 1));
        ghostView.setOpacity(0.6);

        ghostGroup = new javafx.scene.Group(ghostView);
        ghostGroup.setMouseTransparent(true);
        ghostGroup.setVisible(false);
        roomGrid.add(ghostGroup, 0, 0);

        roomGrid.setOnMouseMoved(event -> {
            if (previewType != null) {
                ghostGroup.setVisible(true);
                javafx.geometry.Point2D localPoint = roomGrid.sceneToLocal(event.getSceneX(), event.getSceneY());
                int col = (int) (localPoint.getX() / CELL_SIZE);
                int row = (int) (localPoint.getY() / CELL_SIZE);

                if (previewType.equals("Yatak") && yatakTexture != null) { ghostView.setImage(yatakTexture); ghostView.setViewport(null); }
                else if (previewType.equals("Sehpa 1") && sehpa1Texture != null) { ghostView.setImage(sehpa1Texture); ghostView.setViewport(null); }
                else if (previewType.equals("Sehpa 2") && sehpa2Texture != null) { ghostView.setImage(sehpa2Texture); ghostView.setViewport(null); }
                else if (previewType.equals("Mutfak 1") && mutfak1Texture != null) { ghostView.setImage(mutfak1Texture); ghostView.setViewport(null); }
                else if (previewType.equals("Mutfak 3") && mutfak3Texture != null) { ghostView.setImage(mutfak3Texture); ghostView.setViewport(null); }
                else {
                    ghostView.setImage(furnitureSheet);
                    double viewX = 0, viewY = 0, viewW = 0, viewH = 0;
                    int baseColSpan = 1, baseRowSpan = 1;
                    switch (previewType) {
                        case "Saksı / Çiçek": viewX = 3211; viewY = 151; viewW = 745; viewH = 682; baseColSpan = 2; baseRowSpan = 2; break;
                        case  "Yuvarlak Sehpa": viewX = 191; viewY = 611; viewW = 635; viewH = 575; baseColSpan = 2; baseRowSpan = 2; break;
                        case "Tekli Koltuk": viewX = 118; viewY = 2979; viewW = 1150; viewH = 855; break;
                        case "Orta Sehpa": viewX = 1500; viewY = 1550; viewW = 1068; viewH = 1153; break;
                        case "Büyük Koltuk": viewX = 2989; viewY = 1326; viewW = 904; viewH = 2474; break;
                    }
                    ghostView.setViewport(new Rectangle2D(viewX, viewY, viewW, viewH));
                }

                int baseColSpan = 1, baseRowSpan = 1;
                switch (previewType) {
                    case "Saksı / Çiçek":
                    case "Yuvarlak Sehpa": baseColSpan=2; baseRowSpan=2; break;
                    case "Tekli Koltuk": baseColSpan=3; baseRowSpan=3; break;
                    case "Orta Sehpa": baseColSpan=4; baseRowSpan=3; break;
                    case "Büyük Koltuk": baseColSpan=3; baseRowSpan=7; break;
                    case "Yatak": baseColSpan=5; baseRowSpan=7; break;
                    case "Sehpa 1": case "Sehpa 2": baseColSpan=1; baseRowSpan=1; break;
                    case "Mutfak 1": baseColSpan=9; baseRowSpan=3; break;
                    case "Mutfak 3": baseColSpan=3; baseRowSpan=8; break;
                }

                ghostView.setFitWidth(CELL_SIZE * baseColSpan);
                ghostView.setFitHeight(CELL_SIZE * baseRowSpan);
                ghostGroup.setRotate(previewRotation);

                int actualColSpan = (previewRotation % 180 != 0) ? baseRowSpan : baseColSpan;
                int actualRowSpan = (previewRotation % 180 != 0) ? baseColSpan : baseRowSpan;

                if (previewType != null && previewType.equals("Yatak")) {
                    ghostGroup.setTranslateX(CELL_SIZE * 0.9);
                    ghostGroup.setTranslateY(0);
                } else {
                    ghostGroup.setTranslateX((actualColSpan - baseColSpan) * CELL_SIZE / 2.0);
                    ghostGroup.setTranslateY((actualRowSpan - baseRowSpan) * CELL_SIZE / 2.0);
                }

                if (col >= 0 && col + actualColSpan <= COLS && row >= 0 && row + actualRowSpan <= ROWS) {
                    GridPane.setColumnIndex(ghostGroup, col);
                    GridPane.setRowIndex(ghostGroup, row);
                    GridPane.setColumnSpan(ghostGroup, actualColSpan);
                    GridPane.setRowSpan(ghostGroup, actualRowSpan);

                    boolean collision = false;
                    for (int r = row; r < row + actualRowSpan; r++) {
                        for (int c = col; c < col + actualColSpan; c++) {
                            if (furnitureMap[r][c]) collision = true;
                        }
                    }
                    if (collision) ghostView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(239,68,68,0.9), 15, 0, 0, 0);");
                    else ghostView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(34,197,94,0.9), 15, 0, 0, 0);");
                }
            } else {
                if (ghostGroup != null) ghostGroup.setVisible(false);
            }
        });
    }

    private void placeFurniture(String tur, GridPane grid, int col, int row, int baseColSpan, int baseRowSpan, double viewX, double viewY, double viewW, double viewH, int rotation, double scale) {
        ImageView imgView;

        if (tur != null && tur.equals("Yatak") && yatakTexture != null) imgView = new ImageView(yatakTexture);
        else if (tur != null && tur.equals("Sehpa 1") && sehpa1Texture != null) imgView = new ImageView(sehpa1Texture);
        else if (tur != null && tur.equals("Sehpa 2") && sehpa2Texture != null) imgView = new ImageView(sehpa2Texture);
        else if (tur != null && tur.equals("Mutfak 1") && mutfak1Texture != null) imgView = new ImageView(mutfak1Texture);
        else if (tur != null && tur.equals("Mutfak 3") && mutfak3Texture != null) imgView = new ImageView(mutfak3Texture);
        else {
            imgView = new ImageView(furnitureSheet);
            imgView.setViewport(new Rectangle2D(viewX, viewY, viewW, viewH));
        }

        imgView.setFitWidth(baseColSpan * CELL_SIZE);
        imgView.setFitHeight(baseRowSpan * CELL_SIZE);
        imgView.setPreserveRatio(false);
        imgView.setScaleX(scale);
        imgView.setScaleY(scale);
        imgView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 2, 2);");

        javafx.scene.Group furnitureGroup = new javafx.scene.Group(imgView);
        furnitureGroup.setUserData(tur);
        furnitureGroup.setRotate(rotation);

        int actualColSpan = (rotation % 180 != 0) ? baseRowSpan : baseColSpan;
        int actualRowSpan = (rotation % 180 != 0) ? baseColSpan : baseRowSpan;

        if (tur != null && tur.equals("Yatak")) {
            furnitureGroup.setTranslateX(CELL_SIZE * 0.9);
            furnitureGroup.setTranslateY(0);
        }
        else if (tur != null && tur.equals("Mutfak 3")) {
            furnitureGroup.setTranslateX((actualColSpan - baseColSpan) * CELL_SIZE / 2.0);
            furnitureGroup.setTranslateY(-CELL_SIZE * 0.09);
        }
        else if (tur != null && tur.equals("Mutfak 1")) {
            furnitureGroup.setTranslateX((actualColSpan - baseColSpan) * CELL_SIZE / 2.0);
            furnitureGroup.setTranslateY(((actualRowSpan - baseRowSpan) * CELL_SIZE / 2.0) - (CELL_SIZE * -0.36));
        }
        else {
            furnitureGroup.setTranslateX((actualColSpan - baseColSpan) * CELL_SIZE / 2.0);
            furnitureGroup.setTranslateY((actualRowSpan - baseRowSpan) * CELL_SIZE / 2.0);
        }

        grid.add(furnitureGroup, col, row, actualColSpan, actualRowSpan);
        allFurnitureNodes.add(furnitureGroup);

        for (int r = row; r < row + actualRowSpan; r++) {
            for (int c = col; c < col + actualColSpan; c++) {
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {

                    if (tur != null && (tur.equalsIgnoreCase("Halı") || tur.equalsIgnoreCase("Hali") || tur.equalsIgnoreCase("Yolluk"))) {
                        carpetMap[r][c] = true;
                        continue;
                    }

                    furnitureMap[r][c] = true;
                    solidMap[r][c] = true;

                    if (tur != null && (tur.equalsIgnoreCase("Orta Sehpa") || tur.equalsIgnoreCase("Yatak"))) {
                        boolean isCorner = (r == row || r == row + actualRowSpan - 1) &&
                                (c == col || c == col + actualColSpan - 1);
                        if (!isCorner) {
                            solidMap[r][c] = false;
                            furnitureMap[r][c] = false;
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // YARDIMCI METOTLAR: Görsel Güncellemeler (Robot Hareketi, Temizlik vb.)
    // ----------------------------------------------------------------------

    public void updateRobotPosition(int col, int row, double angle) {
        if (robotContainer != null && robotView != null) {
            GridPane.setColumnIndex(robotContainer, col);
            GridPane.setRowIndex(robotContainer, row);
            robotView.setRotate(angle);
        }
        bringFurnitureToFront();
    }

    public int getCols() { return COLS; }
    public int getRows() { return ROWS; }

    public void setPreviewMode(String type, int rotation) {
        this.previewType = type;
        this.previewRotation = rotation;
        if (type == null && ghostGroup != null) ghostGroup.setVisible(false);
    }

    public void setOnRightClick(Runnable onRightClick) { this.onRightClickAction = onRightClick; }

    public void setOnGridClicked(BiConsumer<Integer, Integer> onGridClick) {
        roomGrid.setOnMouseClicked(event -> {
            javafx.geometry.Point2D localPoint = roomGrid.sceneToLocal(event.getSceneX(), event.getSceneY());
            int col = (int) (localPoint.getX() / CELL_SIZE);
            int row = (int) (localPoint.getY() / CELL_SIZE);

            if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                if (onRightClickAction != null) {
                    onRightClickAction.run();
                }
            }else if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
                    onGridClick.accept(col, row);
                }
            }
        });
    }

    public void addDirtVisually(int col, int row, String type) {
        if (dirtNodes[row][col] != null) {
            roomGrid.getChildren().remove(dirtNodes[row][col]);
            dirtNodes[row][col] = null;
        }

        Image selectedTexture = null;
        switch (type.trim().toLowerCase()) {
            case "toz": selectedTexture = tozTexture; break;
            case "sıvı":
            case "sivi": selectedTexture = siviTexture; break;
            case "leke": selectedTexture = lekeTexture; break;
            default: return;
        }

        if (selectedTexture != null && !selectedTexture.isError()) {
            ImageView dirtView = new ImageView(selectedTexture);
            dirtView.setFitWidth(CELL_SIZE * 0.8);
            dirtView.setFitHeight(CELL_SIZE * 0.8);
            dirtView.setPreserveRatio(true);

            StackPane dirtContainer = new StackPane(dirtView);
            roomGrid.add(dirtContainer, col, row);
            dirtNodes[row][col] = dirtContainer;

            dirtContainer.toFront();
        }
        bringFurnitureToFront();
    }

    public void addFurnitureVisually(int col, int row, String mobilyaTuru, int rotation) {
        double viewX = 0, viewY = 0, viewW = 0, viewH = 0;
        int baseColSpan = 1, baseRowSpan = 1;
        double scale = 1.0;

        switch (mobilyaTuru) {
            case "Saksı / Çiçek": viewX = 3211; viewY = 151; viewW = 745; viewH = 682; baseColSpan = 2; baseRowSpan = 2; break;
            case  "Yuvarlak Sehpa": viewX = 191; viewY = 611; viewW = 635; viewH = 575; baseColSpan = 2; baseRowSpan = 2; break;
            case "Tekli Koltuk": viewX = 118; viewY = 2979; viewW = 1150; viewH = 855; baseColSpan = 3; baseRowSpan = 3; break;
            case "Orta Sehpa": viewX = 1500; viewY = 1550; viewW = 1068; viewH = 1153; baseColSpan = 4; baseRowSpan = 3; break;
            case "Büyük Koltuk": viewX = 2989; viewY = 1326; viewW = 904; viewH = 2474; baseColSpan = 3; baseRowSpan = 7; break;
            case "Yatak": baseColSpan = 5; baseRowSpan = 7; break;
            case "Sehpa 1":
            case "Sehpa 2": baseColSpan = 1; baseRowSpan = 1; break;
            case "Mutfak 1": baseColSpan = 9; baseRowSpan = 3; break;
            case "Mutfak 3": baseColSpan = 3; baseRowSpan = 8; break;
            default: return;
        }

        int actualColSpan = (rotation % 180 != 0) ? baseRowSpan : baseColSpan;
        int actualRowSpan = (rotation % 180 != 0) ? baseColSpan : baseRowSpan;

        if (col + actualColSpan > COLS || row + actualRowSpan > ROWS) return;

        for (int r = row; r < row + actualRowSpan; r++) {
            for (int c = col; c < col + actualColSpan; c++) {
                if (furnitureMap[r][c]) return;
            }
        }

        placeFurniture(mobilyaTuru, roomGrid, col, row, baseColSpan, baseRowSpan, viewX, viewY, viewW, viewH, rotation, scale);

        int lastIndex = roomGrid.getChildren().size() - 1;
        dynamicFurnitureNodes.add(roomGrid.getChildren().get(lastIndex));
    }

    public void removeDirtVisually(int col, int row) {
        if (dirtNodes[row][col] != null) {
            javafx.scene.Node dirtNode = dirtNodes[row][col];
            // Animasyon sırasında nesnenin görünürlüğünü korumak için referans tutulur

            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(800), dirtNode);
            st.setToX(0); st.setToY(0);

            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), dirtNode);
            ft.setToValue(0);

            // Vakumlanma efekti (Dönüş ve Küçülme)
            javafx.animation.RotateTransition rt = new javafx.animation.RotateTransition(javafx.util.Duration.millis(800), dirtNode);
            rt.setByAngle(360); // 1 tam tur dönecek

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(st, ft, rt);
            pt.setOnFinished(e -> {
                roomGrid.getChildren().remove(dirtNode);
                // Animasyon bitiminde veri yapısından temizlenir
                if (dirtNodes[row][col] == dirtNode) {
                    dirtNodes[row][col] = null;
                }
            });
            pt.play();
        }
    }

    public void markAsCleaned(int col, int row, double angle) {
        double centerX = col * CELL_SIZE + (CELL_SIZE / 2.0);
        double centerY = row * CELL_SIZE + (CELL_SIZE / 2.0);

        trailLine.getPoints().addAll(centerX, centerY);

        javafx.scene.shape.Polygon arrow = new javafx.scene.shape.Polygon(-5, 5, 5, 5, 0, -6);
        arrow.setFill(Color.web("#3b82f6"));
        arrow.setLayoutX(centerX);
        arrow.setLayoutY(centerY);
        arrow.setRotate(angle);

        trailOverlay.getChildren().add(arrow);
        cleanedTrailNodes.add(arrow);
    }

    public void drawPath(java.util.List<com.example.bz214proje.model.Position> path) {
        clearPath();
        for (com.example.bz214proje.model.Position pos : path) {
            Circle dot = new Circle(CELL_SIZE * 0.15, Color.web("#3b82f6"));
            dot.setOpacity(0.6);
            StackPane dotContainer = new StackPane(dot);
            roomGrid.add(dotContainer, pos.getX(), pos.getY());
            pathNodes.add(dotContainer);
        }
    }

    public void clearPath() {
        roomGrid.getChildren().removeAll(pathNodes);
        pathNodes.clear();
    }

    public void clearAllVisuals() {
        this.getChildren().clear();
    }

    public boolean isRobotOnHali(int col, int row) {
        // Robot harita dışındaysa false dön
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return false;

        // Robotun bulunduğu hücrede "Halı" veya "Yolluk" verisi var mı?
        return carpetMap[row][col];
    }

    public void resetView() {
        try {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (dirtNodes[r][c] != null) {
                        roomGrid.getChildren().remove(dirtNodes[r][c]);
                        dirtNodes[r][c] = null;
                    }
                }
            }

            clearPath();
            if (trailLine != null) {
                trailLine.getPoints().clear();
            }
            if (trailOverlay != null && cleanedTrailNodes != null) {
                trailOverlay.getChildren().removeAll(cleanedTrailNodes);
                cleanedTrailNodes.clear();
            }

            if (dynamicFurnitureNodes != null) {
                for (javafx.scene.Node node : dynamicFurnitureNodes) {
                    roomGrid.getChildren().remove(node);
                    allFurnitureNodes.remove(node);
                }
                dynamicFurnitureNodes.clear();
            }

            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    furnitureMap[r][c] = initialFurnitureMap[r][c];
                    solidMap[r][c] = initialSolidMap[r][c];
                }
            }
        } catch (Exception e) {
            System.err.println("Sıfırlama sırasında kritik bir hata engellendi: " + e.getMessage());
        }
    }

    private void bringFurnitureToFront() {
        for (javafx.scene.Node f : allFurnitureNodes) {
            Object data = f.getUserData();
            String tur = data != null ? data.toString() : "";
            if (tur.equalsIgnoreCase("Halı") || tur.equalsIgnoreCase("Hali")) {
                f.toBack();
            }
        }

        if (roomGrid.getChildren().contains(trailOverlay)) {
            trailOverlay.toFront();
        }

        for (javafx.scene.Node f : allFurnitureNodes) {
            Object data = f.getUserData();
            String tur = data != null ? data.toString() : "";
            if (!tur.equalsIgnoreCase("Halı") && !tur.equalsIgnoreCase("Hali") &&
                    !tur.equalsIgnoreCase("Orta Sehpa") && !tur.equalsIgnoreCase("Yatak")) {
                f.toFront();
            }
        }

        if (robotContainer != null) {
            robotContainer.toFront();
        }

        // --------------------------------------------------------
        // Z Katman (Z-Index) Sıralaması
        // --------------------------------------------------------
        // Kir düğümleri robotun üzerinde render edilir
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (dirtNodes[r][c] != null) {
                    dirtNodes[r][c].toFront();
                }
            }
        }

        for (javafx.scene.Node f : allFurnitureNodes) {
            Object data = f.getUserData();
            String tur = data != null ? data.toString() : "";
            if (tur.equalsIgnoreCase("Orta Sehpa") || tur.equalsIgnoreCase("Yatak")) {
                f.toFront();
            }
        }
    }

    public boolean isSolid(int col, int row) {
        return solidMap[row][col];
    }

}