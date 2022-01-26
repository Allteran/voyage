package allteran.voyage.ui.view;

import allteran.voyage.domain.Ticket;
import allteran.voyage.service.TicketService;
import allteran.voyage.service.TicketStatusService;
import allteran.voyage.service.TicketTypeService;
import allteran.voyage.ui.component.TicketEditor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "tickets", layout = MainView.class)
@PageTitle("Билеты | VOYAGE")
public class TicketsView extends Div {
    private final TicketEditor ticketEditor;
    private ListDataProvider<Ticket> dataProvider;

    private final TicketService ticketService;
    private final TicketTypeService typeService;
    private final TicketStatusService statusService;

    private Grid<Ticket> grid = new Grid<>(Ticket.class,false);
    private Button addNewButton = new Button("Добавить билет", VaadinIcon.PLUS.create());

    @Autowired
    public TicketsView(TicketEditor ticketEditor, TicketService ticketService, TicketTypeService typeService, TicketStatusService statusService) {
        this.ticketEditor = ticketEditor;
        this.ticketService = ticketService;
        this.typeService = typeService;
        this.statusService = statusService;
        this.dataProvider = new ListDataProvider<>(ticketService.findAllSortedByIssueDate());

        add(ticketEditor);

        ticketEditor.setChangeHandler(() -> {
            dataProvider = new ListDataProvider<>(ticketService.findAllSortedByIssueDate());
            grid.setDataProvider(dataProvider);
        });

        addNewButton.getElement().setAttribute("aria-label", "Profile");
        addNewButton.getStyle().set("margin-inline-start", "auto").set("padding", "5px");
        addNewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewButton.addClickListener(e -> ticketEditor.editTicket(new Ticket()));

        add(createToolbar(createFilter(), addNewButton));
        add(createGridLayout());

        createGridColumns();
        createFilter();
        grid.addItemDoubleClickListener(l -> ticketEditor.editTicket(l.getItem()));
    }


    private Component createGridLayout() {
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setDataProvider(dataProvider);
        grid.setAllRowsVisible(true);

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);
        return grid;
    }

    private void createGridColumns() {
        grid.addColumn(Ticket::getCustomer).setHeader("Пассажир").setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(Ticket::getIssueDate, "dd.MM.yyyy"))
                .setHeader("Дата выписки")
                .setComparator((SerializableComparator<Ticket>) (o1, o2) -> {
                    if(o1.getIssueDate().isAfter(o2.getIssueDate())) {
                        return 1;
                    } else if(o1.getIssueDate().isBefore(o2.getIssueDate())) {
                        return -1;
                    } else {
                        return 0;
                    }
                }).setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(Ticket::getDepartureDate, "dd.MM.yyyy"))
                .setHeader("Дата вылета")
                .setComparator((SerializableComparator<Ticket>) (o1, o2) -> {
                    if(o1.getDepartureDate().isAfter(o2.getDepartureDate())) {
                        return 1;
                    } else if(o1.getDepartureDate().isBefore(o2.getDepartureDate())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .setSortable(true);

        grid.addColumn(Ticket::getReservationNumber).setHeader("№ брони").setSortable(true);
        grid.addColumn(Ticket::getTicketNumber).setHeader("№ билета").setSortable(true);

        grid.addColumn(Ticket::getFlightRoute).setHeader("Маршрут").setSortable(true);
        grid.addColumn(Ticket::getPrice).setHeader("Цена").setSortable(true);

        TemplateRenderer<Ticket> typeRenderer = TemplateRenderer.<Ticket>of("[[item.type.name]]")
                .withProperty("type", Ticket::getType);
        grid.addColumn(typeRenderer).setHeader("Тип выписки").setSortable(true);

        grid.addColumn(Ticket::getPassport).setHeader("Документ");

        grid.addColumn(new LocalDateRenderer<>(Ticket::getDateOfBirth, "dd.MM.yyyy"))
                .setHeader("Дата рождения")
                .setComparator((SerializableComparator<Ticket>) (o1, o2) -> {
                    if(o1.getDateOfBirth().isAfter(o2.getDateOfBirth())) {
                        return 1;
                    } else if(o1.getDateOfBirth().isBefore(o2.getDateOfBirth())) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .setSortable(true);
        grid.addColumn(Ticket::getCustomerPhone).setHeader("Телефон").setSortable(true);

        TemplateRenderer<Ticket> statusRender = TemplateRenderer.<Ticket>of("[[item.status.name]]")
                .withProperty("status", Ticket::getStatus)        ;
        grid.addColumn(statusRender).setHeader("Статус купона").setSortable(true);

    }

    private Component createFilter() {
        TextField search = new TextField();
        search.setWidth("50%");
        search.setPlaceholder("Поиск");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataProvider.refreshAll());

        dataProvider.addFilter(ticket -> {
            String searchTerm = search.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesCustomer = matchesTerm(ticket.getCustomer(),searchTerm);
            boolean matchesReservationNumber = matchesTerm(ticket.getReservationNumber(), searchTerm);
            boolean matchesTicketNumber = matchesTerm(ticket.getTicketNumber(),searchTerm);
            boolean matchesFlightRoute = matchesTerm(ticket.getFlightRoute(),searchTerm);
            boolean matchesCustomerPhone = matchesTerm(ticket.getCustomerPhone(),searchTerm);

            return matchesCustomer || matchesReservationNumber || matchesTicketNumber || matchesFlightRoute
                    || matchesCustomerPhone;

        });

        return search;
    }

    private boolean matchesTerm(String value, String searchTerm) {
        if(value != null) {
            return value.toLowerCase().contains(searchTerm.toLowerCase());
        }
        return false;
    }

    private Component createToolbar(Component filter, Component button) {
        H2 title = new H2("Список билетов в работе");
        title.getStyle().set("margin", "0 auto 0 0");

        HorizontalLayout horizontalLayout = new HorizontalLayout(filter, button);
        horizontalLayout.setWidthFull();
        return new VerticalLayout(title, horizontalLayout);
    }

}
