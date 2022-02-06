package allteran.voyage.ui.component;

import allteran.voyage.domain.PointOfSales;
import allteran.voyage.service.POSService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
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
public class POSEditor extends Dialog {
    private final POSService posService;
    private PointOfSales pos;

    private Binder<PointOfSales> binder = new Binder<>(PointOfSales.class);

    private TextField nickname;
    private TextField address;

    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public POSEditor(POSService posService) {
        this.posService = posService;
        createDialog();

        binder.bindInstanceFields(this);
        setMaxWidth("66%");
        setMaxHeight("100%");
        setDraggable(true);
        setResizable(true);
    }

    private void createDialog() {
        H2 headline = new H2("Точка продаж");
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        nickname = new TextField("Наименование");
        address = new TextField("Адрес точки");

        FormLayout formLayout = new FormLayout(nickname, address);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        formLayout.setColspan(nickname, 1);
        formLayout.setColspan(address, 1);

        Button saveButton = new com.vaadin.flow.component.button.Button("Сохранить");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener( e -> save());

        Button cancelButton = new com.vaadin.flow.component.button.Button("Отмена");
        cancelButton.addClickListener( e-> discardChanges());

        Button deleteButton = new Button("Удалить");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-inline-end", "auto");
        deleteButton.addClickListener( e -> delete());

        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, cancelButton, saveButton);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div dialogLayout = new Div(headline, formLayout, buttonLayout);

        add(dialogLayout);
    }

    private void delete() {
        nickname.clear();
        address.clear();
        if(pos.getId() == null) {
            Notification.show("Нечего удалять :)");
            close();
            return;
        }
        posService.delete(pos);
        changeHandler.onChange();
        Notification.show("Выбраная точка продаж была удалена");
        close();
    }

    private void discardChanges() {
        nickname.clear();
        address.clear();
        close();
    }

    private void save() {
        if(nickname.isEmpty() || address.isEmpty()) {
            nickname.setErrorMessage("Необходимо заполнить");
            address.setErrorMessage("Необходимо заполнить");
            nickname.setInvalid(true);
            address.setInvalid(true);
        } else {
            posService.save(pos);
            Notification.show("Точка продаж была успешно сохранена");
            changeHandler.onChange();
            close();
        }
    }

    public void editPOS(PointOfSales p) {
        if (p == null) {
            close();
            return;
        }
        open();
        if(p.getId() != null) {
            this.pos = posService.findById(p.getId(), p);
        } else {
            this.pos = p;
        }

        binder.setBean(pos);
    }
}
