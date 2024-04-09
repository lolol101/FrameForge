package com.ru.hse.frameforge.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qt.core.QEvent;
import io.qt.core.QObject;

public class RegistrationModel extends QObject {
    private final ObjectMapper jsMapper;

    public RegistrationModel() {
        jsMapper = new ObjectMapper();
    }

    //signals:
    Signal1<ObjectNode> registrationDataReceived = new Signal1<>();

    //slots:
    public void registrationBtnClicked(String userName, String password) {
        ObjectNode json = jsMapper.createObjectNode();
        json.put("Username", userName);
        json.put("Password", password);
        json.put("Subscribers", 0);
        registrationDataReceived.emit(json);
    }
}
