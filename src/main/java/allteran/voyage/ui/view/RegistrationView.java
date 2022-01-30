package allteran.voyage.ui.view;

import allteran.voyage.domain.User;
import allteran.voyage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.fusion.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

@Route("registration")
@Endpoint
@PageTitle("Регистрация | VOYAGE")
@AnonymousAllowed
public class RegistrationView extends Div {
    private final UserService userService;

    private final TextField firstName;
    private final TextField lastName;
    private final TextField phone;
    private final PasswordField password;
    private final PasswordField confirmPassword;
    private final Button signUpButton;
    private Binder<User> binder = new Binder<>(User.class);

    @Autowired
    public RegistrationView(UserService userService) {
        this.userService = userService;
        getStyle()
                .set("display", "-ms-flexbox")
                .set("display", "-webkit-box")
                .set("display", "flex")
                .set("-ms-flex-align", "center")
                .set("-ms-flex-pack", "center")
                .set("-webkit-box-align", "center")
                .set("-align-items", "center")
                .set("-webkit-box-pack", "center")
                .set("justify-content", "center")
                .set("padding-top", "40px")
                .set("padding-bottom", "40px")
        ;
        firstName = new TextField("Имя");
        lastName = new TextField("Фамилия");
        phone = new TextField("Номер телефона");
        password = new PasswordField("Пароль");
        confirmPassword = new PasswordField("Подтверждение пароля");
        signUpButton = new Button("Зарегистрироваться");
        signUpButton.addClickListener(e -> {
            if(binder.validate().isOk()) {
                User user = new User();
                user.setFirstName(firstName.getValue());
                user.setLastName(lastName.getValue());
                user.setPhone(phone.getValue());
                user.setPassword(password.getValue());

                userService.addUser(user);

            }
        });

        H2 title = new H2("VOYAGE | Регистрация");
        title.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold").set("text-align", "center");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle()
                .set("padding", "5px");
        formLayout.add(
                title,
                firstName, lastName,
                phone,
                password, confirmPassword,
                signUpButton
        );
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(title, 2);
        formLayout.setColspan(phone, 2);
        formLayout.setColspan(signUpButton, 2);

        binder.forField(phone).withValidator(ph -> ph.matches("\\^?(79)\\d{9}"), "79XXXXXXXXX").bind(User::getPhone, User::setPhone);
        binder.forField(firstName).withValidator(fn -> fn.length()>0, "Поле не может быть пустым").bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName).withValidator(ln -> ln.length()>0, "Поле не может быть пустым").bind(User::getLastName, User::setLastName);
        binder.forField(password).withValidator(p -> p.length() >= 8 && p.length() <= 32, "Длинна пароля должна быть от 8 до 32 символов").bind(User::getPassword, User::setPassword);
        binder.forField(confirmPassword).withValidator(pc -> pc.length() >= 8 && pc.length() <= 32, "Длинна пароля должна быть от 8 до 32 символов").bind(User::getPasswordConfirm, User::setPasswordConfirm);

        add(formLayout);
    }
}
