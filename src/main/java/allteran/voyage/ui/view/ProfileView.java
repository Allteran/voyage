package allteran.voyage.ui.view;

import allteran.voyage.domain.User;
import allteran.voyage.security.SecurityService;
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
    private static final String AFTER_EDIT_REDIRECT_URL = "/tickets";
    private final SecurityService securityService;

    private Binder<User> binder = new Binder<>(User.class);

    private TextField firstName = new TextField("Имя");
    private TextField lastName = new TextField("Фамилия");
    private TextField phone = new TextField("Номер телефона");
    private Button changePasswordButton = new Button("Сменить пароль");

    private Button saveButton = new Button("Сохранить");
    private Button cancelButton = new Button("Отменить");

    private Dialog changePasswordDialog;

    @Autowired
    public ProfileView(SecurityService securityService) {
        this.securityService = securityService;
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
        fillProfileForm();

        cancelButton.addClickListener(e -> {
            binder.setBean(new User());
            UI.getCurrent().getPage().setLocation(AFTER_EDIT_REDIRECT_URL);
        });
        saveButton.addClickListener(e -> Notification.show("UPDATED"));
        changePasswordButton.addClickListener(e-> changePasswordDialog.open());
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

    private void fillProfileForm() {
        User profile = (User) securityService.getAuthenticatedUser();
        binder.setBean(profile);
    }

    private Dialog createChangePasswordDialog() {
        Dialog dialog = new Dialog();

        H2 headline = new H2("Смена пароля");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        PasswordField current = new PasswordField("Текущий пароль");
        PasswordField newPassword = new PasswordField("Новый пароль");
        PasswordField newPasswordConfirm = new PasswordField("Повторите новый пароль");

        FormLayout dialogForm = new FormLayout();

        dialogForm.add(
                current,
                newPassword, newPasswordConfirm);

        dialogForm.setResponsiveSteps(// Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));

        dialogForm.setColspan(current, 2);

        Button save = new Button("Сохранить");
        Button cancel = new Button("Отмена");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(e -> savePassword(current.getValue(), newPassword.getValue(), newPasswordConfirm.getValue()));
        cancel.addClickListener(e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        current.addValueChangeListener(event -> {
            //TODO: custom validation
        });

        dialog.add(new Div(headline, dialogForm, buttonLayout));
        return dialog;
    }

    private void savePassword(String currentPassword, String newPassword, String newPasswordConfirm) {

    }

}
