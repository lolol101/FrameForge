module com.ru.hse.frameforge {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires jdk.jdi;

    opens frameForge to javafx.fxml;
    exports frameForge;

    opens frameForge.view;
    exports frameForge.view;
}