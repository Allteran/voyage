package allteran.voyage.ui.view;

import allteran.voyage.domain.User;
import allteran.voyage.security.SecurityService;
import allteran.voyage.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "profile", layout = MainView.class)
@PermitAll
@PageTitle("Профиль | VOYAGE")
public class ProfileView extends Div {
    private static final String AFTER_EDIT_REDIRECT_URL = "/";
    private static final String NOT_BLANK_MESSAGE = "Поле не может быть пустым";

    private final SecurityService securityService;
    private final UserService userService;


    private Binder<User> binder = new Binder<>(User.class);

    private TextField firstName = new TextField("Имя");
    private TextField lastName = new TextField("Фамилия");
    private TextField phone = new TextField("Номер телефона");
    private Button changePasswordButton = new Button("Сменить пароль");

    private Button saveButton = new Button("Сохранить");
    private Button cancelButton = new Button("Отменить");

    private Dialog changePasswordDialog;

    private User currentUser;

    @Autowired
    public ProfileView(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
        changePasswordDialog = createChangePasswordDialog();

        getStyle()
                .set("display", "block")
                .set("margin", "0 auto")
                .set("max-width", "1024px")
                .set("padding", "0 var(--lumo-space-l)");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);

        currentUser = (User) securityService.getAuthenticatedUser();
        binder.setBean(currentUser);

        cancelButton.addClickListener(e -> {
            binder.setBean(new User());
            UI.getCurrent().getPage().setLocation(AFTER_EDIT_REDIRECT_URL);
        });
        saveButton.addClickListener(e -> updateProfile());
        changePasswordButton.addClickListener(e-> changePasswordDialog.open());
    }

    private void updateProfile() {
        User userFromDb = userService.findById(currentUser.getId());
        userService.updateUser(userFromDb, currentUser);
        Notification.show("Профиль успешно изменен. Перезайдите в систему, чтобы изменения вступили в силу").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }


    private Component createTitle() {
        return new H3("Профиль пользователя");
    }

    private Component createFormLayout() {
        binder.forField(phone).withValidator(ph -> ph.matches("\\^?(79)\\d{9}"), "79XXXXXXXXX").bind(User::getPhone, User::setPhone);
        binder.forField(firstName).withValidator(fn -> fn.length()>0, "Поле не может быть пустым").bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName).withValidator(ln -> ln.length()>0, "Поле не может быть пустым").bind(User::getLastName, User::setLastName);

        FormLayout form = new FormLayout();
        form.add(firstName, lastName, phone, changePasswordButton);

        form.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );

        return form;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(saveButton);
        buttonLayout.add(cancelButton);
        return buttonLayout;
    }

    private Dialog createChangePasswordDialog() {
        Dialog dialog = new Dialog();

        H2 headline = new H2("Смена пароля");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        PasswordField currentPassword = new PasswordField("Текущий пароль");
        PasswordField newPassword = new PasswordField("Новый пароль");
        PasswordField newPasswordConfirm = new PasswordField("Повторите новый пароль");

        FormLayout dialogForm = new FormLayout();

        dialogForm.add(
                currentPassword,
                newPassword, newPasswordConfirm);

        dialogForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        dialogForm.setColspan(currentPassword, 2);

        Button save = new Button("Сохранить");
        Button cancel = new Button("Отмена");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(e -> {
                    User fromDb = userService.findById(currentUser.getId());

                    if(!newPassword.getValue().equals(newPasswordConfirm.getValue())) {
                        newPassword.setErrorMessage("Пароли должны совпадать");
                        newPasswordConfirm.setErrorMessage("Пароли должны совпадать");
                        newPassword.setInvalid(true);
                        newPasswordConfirm.setInvalid(true);
                        return;
                    }
                    if(userService.isPasswordsMatches(currentPassword.getValue(), fromDb.getPassword())) {
                        currentUser.setPassword(currentPassword.getValue());
                        currentUser.setNewPassword(newPassword.getValue());
                        userService.updateUser(fromDb, currentUser);
                        Notification.show("Пароль был изменен успешно").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        clearPasswordForm(currentPassword, newPassword, newPasswordConfirm);
                        dialog.close();
                    } else {
                        currentPassword.setErrorMessage("Текущий пароль введен неверно");
                        currentPassword.setInvalid(true);
                    }
                }
        );
        cancel.addClickListener(e -> {
            clearPasswordForm(currentPassword, newPassword, newPasswordConfirm);
            dialog.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        newPasswordConfirm.addValueChangeListener(e -> {
            if(!e.getValue().equals(newPassword.getValue())) {
                newPassword.setErrorMessage("Пароли должны совпадать");
                newPasswordConfirm.setErrorMessage("Пароли должны совпадать");
                newPassword.setInvalid(true);
            }
        });

        dialog.add(new Div(headline, dialogForm, buttonLayout));
        return dialog;
    }

    private void clearPasswordForm(PasswordField currentPassword, PasswordField newPassword, PasswordField passwordConfirm ) {
        currentPassword.clear();
        newPassword.clear();
        passwordConfirm.clear();
    }

}
