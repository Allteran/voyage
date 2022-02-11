package allteran.voyage.ui.component;

import allteran.voyage.domain.TicketType;
import allteran.voyage.service.TicketTypeService;
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
public class TicketTypeEditor extends Dialog {
    private final TicketTypeService typeService;
    private TicketType type;

    private Binder<TicketType> binder = new Binder<>(TicketType.class);

    private TextField name;

    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public TicketTypeEditor(TicketTypeService typeService) {
        this.typeService = typeService;
        createDialog();

        binder.bindInstanceFields(this);
        setMaxWidth("66%");
        setMaxHeight("100%");
        setDraggable(true);
        setResizable(true);
    }

    private void createDialog() {
        H2  headline = new H2("Тип билета");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");
        name = new TextField("Наименование");

        FormLayout form = new FormLayout(name);

        Button saveButton = new Button("Сохранить");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener( e -> save());

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener( e-> discardChanges());

        Button deleteButton = new Button("Удалить");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-inline-end", "auto");
        deleteButton.addClickListener( e -> delete());

        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div dialogLayout = new Div(headline, form, buttonLayout);

        add(dialogLayout);
    }

    private void save() {
        if(!name.isEmpty()) {
            typeService.save(type);
            changeHandler.onChange();
            Notification.show("Тип билета был успешно сохранен").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();
        } else {
            name.setErrorMessage("Поле не может быть пустым");
            name.setInvalid(true);
        }
    }

    private void discardChanges() {
        name.clear();
        close();
    }

    private void delete() {
        if(type.getId() == null) {
            name.clear();
            Notification.show("Нечего удалять :)");
            close();
            return;
        }
        typeService.delete(type);
        changeHandler.onChange();
        Notification.show("Выбраный тип был удален").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        name.clear();
        close();
    }

    public void editType(TicketType t) {
        if(t == null) {
            close();
            return;
        }
        open();
        if(t.getId() != null) {
            this.type = typeService.findById(t.getId(), t);
        } else {
            this.type = t;
        }

        binder.setBean(type);
    }
}
