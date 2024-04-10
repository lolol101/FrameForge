package com.ru.hse.frameforge.viewmodel;

import com.ru.hse.frameforge.model.RegistrationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class RegistrationViewModel {
    // ViewModel class for view-model interactions; contains model as a member; is a member of Controller class
    RegistrationModel model;
    public StringProperty nicknameProperty;
    public StringProperty passwordProperty;

    public RegistrationViewModel(RegistrationModel model) {
        this.model = model;
        nicknameProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
    }

    public void addUser() {

    }
}
