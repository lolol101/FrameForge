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

    opens com.ru.hse.frameforge to javafx.fxml;
    exports com.ru.hse.frameforge;

    opens com.ru.hse.frameforge.view;
    exports com.ru.hse.frameforge.view;
}