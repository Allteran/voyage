package allteran.voyage.ui.component;

import allteran.voyage.domain.PayType;
import allteran.voyage.service.PayTypeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class PayTypeEditor extends Dialog {
    private final PayTypeService payTypeService;
    private PayType payType;

    private Binder<PayType> binder = new Binder<>(PayType.class);

    private TextField name;

    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public PayTypeEditor(PayTypeService payTypeService) {
        this.payTypeService = payTypeService;
        createDialog();

        binder.bindInstanceFields(this);
        setMaxWidth("66%");
        setMaxHeight("100%");
        setDraggable(true);
        setResizable(true);
    }

    private void createDialog() {
        H2 headline = new H2("Тип оплаты");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");
        name = new TextField("Наименование");

        FormLayout form = new FormLayout(name);

        Button saveButton = new Button("Сохранить");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener( e -> save());

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener( e-> discardChanges());

        com.vaadin.flow.component.button.Button deleteButton = new Button("Удалить");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-inline-end", "auto");
        deleteButton.addClickListener( e -> delete());

        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div dialogLayout = new Div(headline, form, buttonLayout);

        add(dialogLayout);
    }

    private void delete() {
        if(payType.getId() == null) {
            name.clear();
            Notification.show("Нечего удалять :)");
            close();
            return;
        }
        payTypeService.delete(payType);
        changeHandler.onChange();
        Notification.show("Выбраный тип оплаты был удален").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        name.clear();
        close();
    }

    private void discardChanges() {
        name.clear();
        close();
    }

    private void save() {
        if(!name.isEmpty()) {
            payTypeService.save(payType);
            changeHandler.onChange();
            Notification.show("Статус билета был успешно сохранен").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();
        } else {
            name.setErrorMessage("Поле не может быть пустым");
            name.setInvalid(true);
        }
    }

    public void editPayType(PayType p) {
        if(p == null) {
            close();
            return;
        }
        open();
        if(p.getId() != null) {
            this.payType = payTypeService.findById(p.getId(), p);
        } else {
            this.payType = p;
        }

        binder.setBean(payType);
    }
}
