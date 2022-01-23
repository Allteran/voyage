package allteran.voyage.ui.view;

import allteran.voyage.domain.Ticket;
import allteran.voyage.service.TicketService;
import allteran.voyage.ui.component.TicketEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "tickets", layout = MainView.class)
public class TicketsView extends Div {
    private final TicketService ticketService;
    private final TicketEditor editor;

    private Grid<Ticket> grid = new Grid<>(Ticket.class,false);

    @Autowired
    public TicketsView(TicketService ticketService, TicketEditor editor) {
        this.ticketService = ticketService;
        this.editor = editor;

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        splitLayout.addToSecondary(editor);

        grid.addColumn(Ticket::getCustomer).setHeader("Пассажир").setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(Ticket::getIssueDate, "dd.MM.yyyy"))
                .setHeader("Дата выписки").setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(Ticket::getDepartureDate, "dd.MM.yyyy"))
                .setHeader("Дата вылета").setSortable(true);
        grid.addColumn(Ticket::getReservationNumber).setHeader("№ брони").setSortable(true);
        grid.addColumn(Ticket::getTicketNumber).setHeader("№ билета").setSortable(true);

        grid.addColumn(Ticket::getFlightRoute).setHeader("Маршрут").setSortable(true);
        grid.addColumn(Ticket::getPrice).setHeader("Цена").setSortable(true);
        grid.addColumn(Ticket::getType).setHeader("Тип выписки").setSortable(true);

        grid.addColumn(Ticket::getPassport).setHeader("Документ");

        grid.addColumn(new LocalDateRenderer<>(Ticket::getDateOfBirth, "dd.MM.yyyy"))
                .setHeader("Дата рождения").setSortable(true);
        grid.addColumn(Ticket::getCustomerPhone).setHeader("Телефон").setSortable(true);

        grid.addColumn(Ticket::getStatus).setHeader("Статус купона").setSortable(true);

        add(splitLayout);


        showTickets("");
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void showTickets(String customerName) {
        if(customerName.isEmpty()) {
            grid.setItems(ticketService.findAll());
        } else {
            grid.setItems(ticketService.findByCustomer(customerName));
        }
    }

    private static class TicketFilter {
    }
}
