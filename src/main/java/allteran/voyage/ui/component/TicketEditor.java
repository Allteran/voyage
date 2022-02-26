package allteran.voyage.ui.component;

import allteran.voyage.domain.*;
import allteran.voyage.security.SecurityService;
import allteran.voyage.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.prefs.Preferences;

@SpringComponent
@UIScope
public class TicketEditor extends Dialog {
    private static final long ID_DEFAULT_POS = 0;
    private static final String ERROR_NOT_BLANK = "Поле не может быть пустым";

    private final TicketService ticketService;
    private final TicketTypeService typeService;
    private final TicketStatusService statusService;
    private final PayTypeService payTypeService;
    private final POSService posService;
    private final SecurityService securityService;

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
    private NumberField tariffPrice;
    private NumberField taxYQPrice;
    private NumberField taxRUYRPrice;
    private NumberField totalPrice;

    private TextField comment;

    private DatePicker issueDate;
    private DatePicker departureDate;
    private DatePicker dateOfBirth;

    private Select<TicketType> type;
    private Select<TicketStatus> status;
    private Select<PayType> payType;
    private TextField pointOfSales;

    private H3 authorTitle;

    private User currentUser;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public TicketEditor(TicketService ticketService, TicketTypeService typeService, TicketStatusService statusService, PayTypeService payTypeService, POSService posService, SecurityService securityService) {
        this.ticketService = ticketService;
        this.typeService = typeService;
        this.statusService = statusService;
        this.payTypeService = payTypeService;
        this.posService = posService;
        this.securityService = securityService;

        createDialogLayout();

        binder.forField(tariffPrice).withValidator(Objects::nonNull, "Необходимо ввести число").bind(Ticket::getTariffPrice, Ticket::setTariffPrice);
        binder.forField(taxYQPrice).withValidator(Objects::nonNull, "Необходимо ввести число").bind(Ticket::getTaxYQPrice, Ticket::setTaxYQPrice);
        binder.forField(taxRUYRPrice).withValidator(Objects::nonNull, "Необходимо ввести число").bind(Ticket::getTaxRUYRPrice, Ticket::setTaxRUYRPrice);

        binder.bindInstanceFields(this);

        setMaxWidth("66%");
        setMaxHeight("100%");
        setDraggable(true);
        setResizable(true);
    }


    private void createDialogLayout() {
        H2 headline = new H2(headerTitle);
        headline.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

        currentUser = (User) securityService.getAuthenticatedUser();

        authorTitle = new H3();
        authorTitle.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

        HorizontalLayout titleBar = new HorizontalLayout(headline, authorTitle);
        titleBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        customer = new TextField("ФИО Пассажира");
        customerPhone = new TextField("Контактный телефон");
        passport = new TextField("Пасспортные данные");
        reservationNumber = new TextField("Номер брони");
        ticketNumber = new TextField("Номер билета");
        flightRoute = new TextField("Полетный маршрут");
        comment = new TextField("Комментарий");

        pointOfSales = new TextField("Точка продаж");
        pointOfSales.setReadOnly(true);

        customer.setErrorMessage(ERROR_NOT_BLANK);
        customerPhone.setErrorMessage("79XXXXXXXXX");
        passport.setErrorMessage(ERROR_NOT_BLANK);
        reservationNumber.setErrorMessage(ERROR_NOT_BLANK);
        ticketNumber.setErrorMessage(ERROR_NOT_BLANK);
        flightRoute.setErrorMessage(ERROR_NOT_BLANK);
        comment.setErrorMessage(ERROR_NOT_BLANK);

        tariffPrice = new NumberField("Цена тарифа");
        taxYQPrice = new NumberField("Такса YQ");
        taxRUYRPrice = new NumberField("Такса RU/YR");
        totalPrice = new NumberField("Итоговая цена");
        totalPrice.setReadOnly(true);

        tariffPrice.addValueChangeListener(e -> calculateTotalPrice());
        taxYQPrice.addValueChangeListener(e -> calculateTotalPrice());
        taxRUYRPrice.addValueChangeListener(e -> calculateTotalPrice());

        issueDate = new DatePicker("Дата выписки");
        departureDate = new DatePicker("Дата вылета");
        dateOfBirth = new DatePicker("Дата рождения");

        DatePicker.DatePickerI18n datePickerFormatter = new DatePicker.DatePickerI18n();
        datePickerFormatter.setDateFormat("dd.MM.yyyy");

        issueDate.setI18n(datePickerFormatter);
        departureDate.setI18n(datePickerFormatter);
        dateOfBirth.setI18n(datePickerFormatter);

        issueDate.setPlaceholder("дд.мм.гггг");
        departureDate.setPlaceholder("дд.мм.гггг");
        dateOfBirth.setPlaceholder("дд.мм.гггг");

        issueDate.setErrorMessage(ERROR_NOT_BLANK);
        departureDate.setErrorMessage(ERROR_NOT_BLANK);
        dateOfBirth.setErrorMessage(ERROR_NOT_BLANK);


        type = new Select<>();
        type.setLabel("Тип выписки");
        type.setItemLabelGenerator(TicketType::getName);
        type.setItems(typeService.findAll());

        status = new Select<>();
        status.setLabel("Статус купона");
        status.setItemLabelGenerator(TicketStatus::getName);
        status.setItems(statusService.findAll());
        status.setEmptySelectionAllowed(false);

        payType = new Select<>();
        payType.setLabel("Тип оплаты");
        payType.setItemLabelGenerator(PayType::getName);
        payType.setItems(payTypeService.findAll());
        payType.setEmptySelectionAllowed(false);

        type.setErrorMessage(ERROR_NOT_BLANK);
        status.setErrorMessage(ERROR_NOT_BLANK);
        payType.setErrorMessage(ERROR_NOT_BLANK);

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                customer,dateOfBirth,
                passport,customerPhone,
                issueDate,departureDate,reservationNumber,ticketNumber,
                flightRoute,type, status,
                tariffPrice, taxYQPrice, taxRUYRPrice, totalPrice,
                payType, pointOfSales, comment

        );

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        formLayout.setColspan(customer,3);
        formLayout.setColspan(dateOfBirth, 1);
        formLayout.setColspan(passport, 2);
        formLayout.setColspan(customerPhone, 2);

        formLayout.setColspan(flightRoute, 2);

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

        Div dialogLayout = new Div(titleBar, formLayout, buttonLayout);

        add(dialogLayout);
    }

    private void calculateTotalPrice() {
        double tp = 0;
        if(tariffPrice.getValue() != null) {
            tp = tp + tariffPrice.getValue();
        }
        if(taxYQPrice.getValue() != null) {
            tp = tp + taxYQPrice.getValue();
        }
        if(taxRUYRPrice.getValue() != null) {
            tp = tp + taxRUYRPrice.getValue();
        }
        totalPrice.setValue(tp);
    }

    private void discardChanges() {
        close();
    }

    private void save() {
        if(validate()) {
            if(ticket != null) {
                if (ticket.getAuthor() == null) {
                    ticket.setAuthor(currentUser);
                }
            }
            ticketService.save(ticket);
            changeHandler.onChange();
            Notification.show("Билет был успешно сохранен").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();
        }
    }

    private void delete() {
        ticketService.delete(ticket);
        changeHandler.onChange();
        Notification.show("Выбранный билет был удален").addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        close();
    }

    public void editTicket(Ticket t) {
        if(t == null) {
            close();
            return;
        }
        open();

        PointOfSales activePos;
        if(t.getId() != null) {
            //edit Ticket
            this.ticket = ticketService.findById(t.getId(), t);
            String authorName = "Автор: " + ticket.getAuthor().getFirstName() + " " + ticket.getAuthor().getLastName().charAt(0) + ".";
            authorTitle.setText(authorName);

            activePos = this.ticket.getPos();
        } else {
            //new Ticket
            String authorName = "Автор: " + currentUser.getFirstName() +" " + currentUser.getLastName().charAt(0) + ".";
            authorTitle.setText(authorName);

            Preferences pref = Preferences.userRoot();
            activePos = posService.findById(pref.getLong("pos", ID_DEFAULT_POS), new PointOfSales());

            t.setPos(activePos);
            this.ticket = t;
        }
        pointOfSales.setValue(activePos.getNickname());

        binder.setBean(ticket);
    }

    private boolean validate() {
        customer.setInvalid(customer.isEmpty());
        customerPhone.setInvalid(!customerPhone.getValue().matches("\\^?(79)\\d{9}"));
        ticketNumber.setInvalid(ticketNumber.isEmpty());
        flightRoute.setInvalid(flightRoute.isEmpty());

        issueDate.setInvalid(issueDate.isEmpty());
        departureDate.setInvalid(departureDate.isEmpty());
        dateOfBirth.setInvalid(dateOfBirth.isEmpty());

        type.setInvalid(type.isEmpty());
        status.setInvalid(status.isEmpty());
        payType.setInvalid(payType.isEmpty());

        boolean invalid =  customer.isEmpty()
                || !customerPhone.getValue().matches("\\^?(79)\\d{9}") || ticketNumber.isEmpty() || flightRoute.isEmpty()
                || issueDate.isEmpty() || departureDate.isEmpty() || dateOfBirth.isEmpty() || type.isEmpty() || status.isEmpty()
                || payType.isEmpty();

        return !invalid;
    }


}
