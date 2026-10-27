module com.example.bz214proje {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.bz214proje to javafx.fxml;
    exports com.example.bz214proje;
}