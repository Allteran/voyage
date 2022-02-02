package allteran.voyage.ui.component;

import allteran.voyage.domain.Ticket;
import allteran.voyage.domain.TicketStatus;
import allteran.voyage.domain.TicketType;
import allteran.voyage.service.TicketService;
import allteran.voyage.service.TicketStatusService;
import allteran.voyage.service.TicketTypeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class TicketEditor extends Dialog {
    private final TicketService ticketService;
    private final TicketTypeService typeService;
    private final TicketStatusService statusService;

    private Ticket ticket;

    private Binder<Ticket> binder = new Binder<>(Ticket.class);

    @Setter
    private String headerTitle = "Детали билета";
    @Setter
    private ChangeHandler changeHandler;

    private TextField customer;
    private TextField customerPhone;
    private TextField passport;
    private TextField reservationNumber;
    private TextField ticketNumber;
    private TextField flightRoute;
    private TextField price;
    private TextField comment;
    private DatePicker issueDate;
    private DatePicker departureDate;
    private DatePicker dateOfBirth;
    private Select<TicketType> type;
    private Select<TicketStatus> status;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public TicketEditor(TicketService ticketService, TicketTypeService typeService, TicketStatusService statusService) {
        this.ticketService = ticketService;
        this.typeService = typeService;
        this.statusService = statusService;
        createDialogLayout();

        binder.forField(price)
                .withConverter(new StringToIntegerConverter(0, "Необходимо ввести число"))
                .bind("price");
        binder.bindInstanceFields(this);

        setMaxWidth("66%");
        setMaxHeight("100%");
        setDraggable(true);
        setResizable(true);
    }

    private void createDialogLayout() {
        H2 headline = new H2(headerTitle);
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        customer = new TextField("ФИО Пассажира");
        customerPhone = new TextField("Контактный телефон");
        passport = new TextField("Пасспортные данные");
        reservationNumber = new TextField("Номер брони");
        ticketNumber = new TextField("Номер билета");
        flightRoute = new TextField("Полетный маршрут");
        price = new TextField("Цена");
        comment = new TextField("Комментарий");

        issueDate = new DatePicker("Дата выписки");
        departureDate = new DatePicker("Дата вылета");
        dateOfBirth = new DatePicker("Дата рождения");

        type = new Select<>();
        type.setLabel("Тип выписки");
        type.setItemLabelGenerator(TicketType::getName);
        type.setItems(typeService.findAll());

        status = new Select<>();
        status.setLabel("Статус купона");
        status.setItemLabelGenerator(TicketStatus::getName);
        status.setItems(statusService.findAll());

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                customer,
                dateOfBirth,
                passport,
                customerPhone,
                issueDate,
                departureDate,
                reservationNumber,
                ticketNumber,
                flightRoute,
                price,
                type,
                status,
                comment

        );

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        formLayout.setColspan(customer,3);
        formLayout.setColspan(dateOfBirth, 1);
        formLayout.setColspan(passport, 2);
        formLayout.setColspan(customerPhone, 2);

        formLayout.setColspan(flightRoute, 2);
        formLayout.setColspan(price, 2);

        formLayout.setColspan(comment, 2);

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

    private void discardChanges() {
        close();
    }

    private void save() {
        ticketService.save(ticket);
        changeHandler.onChange();
        Notification.show("Билет был успешно сохранен");
        close();
    }

    private void delete() {
        ticketService.delete(ticket);
        changeHandler.onChange();
        Notification.show("Выбранный билет был удален");
        close();
    }

    public void editTicket(Ticket t) {
        if(t == null) {
            close();
            return;
        }
        open();
        if(t.getId() != null) {
            setHeaderTitle("Редактирование билета");
            this.ticket = ticketService.findById(t.getId(), t);
        } else {
            setHeaderTitle("Новый билет");
            this.ticket = t;
        }

        binder.setBean(ticket);
    }


}
