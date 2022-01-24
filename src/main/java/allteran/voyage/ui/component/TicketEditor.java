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
    private ChangeHandler changeHandler;
    @Setter
    private String headerTitle;

    private TextField customer;
    private TextField phone;
    private TextField passport;
    private TextField reservationNumber;
    private TextField ticketNumber;
    private TextField route;
    private TextField price;
    private TextField comment;
    private DatePicker issueDate;
    private DatePicker departureDate;
    private DatePicker dateOfBirth;
    private Select<TicketType> typeSelector;
    private Select<TicketStatus> statusSelector;

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
        phone = new TextField("Контактный телефон");
        passport = new TextField("Пасспортные данные");
        reservationNumber = new TextField("Номер брони");
        ticketNumber = new TextField("Номер билета");
        route = new TextField("Полетный маршрут");
        price = new TextField("Цена");
        comment = new TextField("Комментарий");

        issueDate = new DatePicker("Дата выписки");
        departureDate = new DatePicker("Дата вылета");
        dateOfBirth = new DatePicker("Дата рождения");

        typeSelector = new Select<>();
        typeSelector.setLabel("Тип выписки");
        typeSelector.setItemLabelGenerator(TicketType::getName);
        typeSelector.setItems(typeService.findAll());

        statusSelector = new Select<>();
        statusSelector.setLabel("Статус купона");
        statusSelector.setItemLabelGenerator(TicketStatus::getName);
        statusSelector.setItems(statusService.findAll());

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                customer,
                dateOfBirth,
                passport,
                phone,
                issueDate,
                departureDate,
                reservationNumber,
                ticketNumber,
                route,
                price,
                typeSelector,
                statusSelector,
                comment

        );

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        formLayout.setColspan(customer,3);
        formLayout.setColspan(dateOfBirth, 1);
        formLayout.setColspan(passport, 2);
        formLayout.setColspan(phone, 2);

        formLayout.setColspan(route, 2);
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

    }

    private void delete() {
        ticketService.delete(ticket);
        changeHandler.onChange();
    }

    public void editTicket(Ticket t) {
        if(t == null) {
            return;
        }
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
