package com.ru.hse.frameforge.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

enum States {
    Open,
    Closed
}

enum Commands {
    sendRegistrationRequest
}

public class RegistrationModel {
    StringProperty menuState;
    Property<Commands> command;

}
