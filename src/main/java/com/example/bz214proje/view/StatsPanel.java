package com.example.bz214proje.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

// ========================================================
// İSTATİSTİK PANELİ (STATS PANEL)
// ========================================================
// Simülasyon sırasında anlık olarak değişen verilerin 
// (temizlenen alan, kalan alan, geçen süre vb.) gösterildiği alt paneldir.
public class StatsPanel extends HBox {

    private Label lblTotalVal;
    private Label lblCleanedVal;
    private Label lblRemainingVal;
    private Label lblTimeVal;
    private Label lblDustVal;

    public StatsPanel() {
        this.setPadding(new Insets(15, 20, 15, 20));
        this.setAlignment(Pos.CENTER_LEFT);

        this.setStyle("-fx-background-color: #172130; -fx-background-radius: 8; -fx-border-color: #243042; -fx-border-radius: 8;");

        Circle dotTotal = new Circle(5, Color.web("#3b82f6"));
        Circle dotCleaned = new Circle(5, Color.web("#22c55e"));
        Circle dotRemaining = new Circle(5, Color.web("#94a3b8"));

        SVGPath clockIcon = new SVGPath();
        clockIcon.setContent("M8 0a8 8 0 1 0 8 8 8 8 0 0 0-8-8zm0 14A6 6 0 1 1 14 8a6 6 0 0 1-6 6zm.5-11H7v5.2l3.8 3.8 1-1.1-3.3-3.3z");
        clockIcon.setFill(Color.web("#a855f7"));
        clockIcon.setScaleX(1.1); clockIcon.setScaleY(1.1);

        SVGPath broomIcon = new SVGPath();
        broomIcon.setContent("M19.36 2.72 L20.78 4.14 L15.06 9.85 C16.13 11.39 16.28 13.24 15.38 14.44 L9.06 8.12 C10.26 7.22 12.11 7.37 13.65 8.44 L19.36 2.72 M5.93 17.57 C3.92 15.56 2.69 13.16 2.35 10.92 L7.23 8.83 L14.67 16.27 L12.58 21.15 C10.34 20.81 7.94 19.58 5.93 17.57 Z");
        broomIcon.setFill(Color.web("#eab308"));
        broomIcon.setScaleX(0.8); broomIcon.setScaleY(0.8);

        // --------------------------------------------------------
        // İstatistik Değerleri İçin Ortak Stil Tanımlaması
        // --------------------------------------------------------
        String valStyle = "-fx-text-fill: #f8fafc; -fx-font-size: 14px; -fx-font-weight: bold;";

        lblTotalVal = new Label("260 m²");
        lblTotalVal.setStyle(valStyle);

        lblCleanedVal = new Label("0.00 m² (%0)");
        lblCleanedVal.setStyle(valStyle);

        lblRemainingVal = new Label("260.00 m² (%100)");
        lblRemainingVal.setStyle(valStyle);

        lblTimeVal = new Label("00:00");
        lblTimeVal.setStyle(valStyle);

        lblDustVal = new Label("%0");
        lblDustVal.setStyle(valStyle);

        HBox boxTotal = createStatBox(dotTotal, "Toplam Alan", lblTotalVal);
        HBox boxCleaned = createStatBox(dotCleaned, "Temizlenen Alan", lblCleanedVal);
        HBox boxRemaining = createStatBox(dotRemaining, "Kalan Alan", lblRemainingVal);
        HBox boxTime = createStatBox(clockIcon, "Geçen Süre", lblTimeVal);
        HBox boxDust = createStatBox(broomIcon, "Toplanan Toz", lblDustVal);

        HBox.setHgrow(boxTotal, Priority.ALWAYS);
        HBox.setHgrow(boxCleaned, Priority.ALWAYS);
        HBox.setHgrow(boxRemaining, Priority.ALWAYS);
        HBox.setHgrow(boxTime, Priority.ALWAYS);
        HBox.setHgrow(boxDust, Priority.ALWAYS);

        this.getChildren().addAll(
                boxTotal, createSeparator(),
                boxCleaned, createSeparator(),
                boxRemaining, createSeparator(),
                boxTime, createSeparator(),
                boxDust
        );
    }

    private Rectangle createSeparator() {
        Rectangle sep = new Rectangle(1, 35, Color.web("#334155"));
        HBox.setMargin(sep, new Insets(0, 20, 0, 10));
        return sep;
    }

    private HBox createStatBox(Node icon, String title, Label lblValue) {
        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(2);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        textBox.getChildren().addAll(lblTitle, lblValue);
        container.getChildren().addAll(icon, textBox);

        return container;
    }

    public Label getLblTotalVal() { return lblTotalVal; }
    public Label getLblCleanedVal() { return lblCleanedVal; }
    public Label getLblRemainingVal() { return lblRemainingVal; }
    public Label getLblTimeVal() { return lblTimeVal; }
    public Label getLblDustVal() { return lblDustVal; }
}