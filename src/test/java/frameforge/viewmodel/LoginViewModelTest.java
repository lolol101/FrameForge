package frameforge.viewmodel;

import frameforge.model.LoginModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginViewModelTest {
    LoginModel model;
    LoginViewModel viewModel;

    @BeforeEach
    void initialize() {
        model = new LoginModel();
        viewModel = new LoginViewModel(model);
    }

    @Test
    void sendLoginRequest() {
        viewModel.nicknameProperty.set("nickname");
        viewModel.passwordProperty.set("password");

        viewModel.sendLoginRequest();

        Assertions.assertEquals("nickname", model.username);
        Assertions.assertEquals("password", model.password);
        Assertions.assertEquals(LoginModel.ViewActions.authBtnClicked, model.viewAction.getValue());
    }

    @Test
    void switchToRegistration() {
        viewModel.switchToRegistration();
        Assertions.assertEquals(LoginModel.ViewActions.switchToRegistrationBtnClicked, model.viewAction.getValue());
    }
}