module frameforge {
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
    requires javafx.swing;
    requires com.google.gson;

    opens frameforge to javafx.fxml;
    exports frameforge;

    opens frameforge.view;
    exports frameforge.view;
    exports frameforge.model;
//    requires org.hamcrest;
//    requires org.testfx;
}