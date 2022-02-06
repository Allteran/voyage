package allteran.voyage.ui.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;

@Route("login")
@PageTitle("Авторизация | VOYAGE")
public class LoginView extends VerticalLayout implements BeforeEnterObserver{
    private final LoginForm login = new LoginForm();

    public LoginView() {
        login.setAction("login");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Авторизация");
        i18nForm.setUsername("Номер телефона");
        i18nForm.setPassword("Пароль");
        i18nForm.setSubmit("Войти");
        i18nForm.setForgotPassword("Забыли пароль?");
        i18n.setForm(i18nForm);

        i18n.setAdditionalInformation("Нет учетной записи? Обратитесь к Вашему системному администратору");

        login.setI18n(i18n);
        login.addForgotPasswordListener(e -> {
            Notification.show("Функционал пока только в разработке. Для восстановления пароля обратитесь к своему системному администратору");
        });
        add(new H1("VOYAGE"),login);
        // Prevent the example from stealing focus when browsing the documentation
        login.getElement().setAttribute("no-autofocus", "");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }

}
