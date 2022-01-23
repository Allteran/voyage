package allteran.voyage.ui.component;

import allteran.voyage.domain.Ticket;
import allteran.voyage.service.TicketService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class TicketEditor extends VerticalLayout implements KeyNotifier {
    private final TicketService ticketService;

    private Ticket ticket;

    private TextField reservationNumber = new TextField("Номер брони");
    private TextField ticketNumber = new TextField("Номер билета");
    private TextField customer = new TextField("Customer Name");
    private TextField flightRoute = new TextField("Полетный маршрут");
    private TextField passportSeries = new TextField("Серия паспорта");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Ticket> binder = new Binder<>(Ticket.class);
    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }


    @Autowired
    public TicketEditor(TicketService ticketService) {
        this.ticketService = ticketService;

        add(reservationNumber, ticketNumber, customer, flightRoute, passportSeries, actions);

        binder.bindInstanceFields(this);
        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editTicket(ticket));
        setVisible(false);

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
            setVisible(false);
            return;
        }
        if(t.getId() != null) {
            this.ticket = ticketService.findById(t.getId(), t);
        } else {
            this.ticket = t;
        }

        binder.setBean(ticket);
        setVisible(true);


    }


}
