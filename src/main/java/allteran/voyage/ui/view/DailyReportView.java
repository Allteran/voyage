package allteran.voyage.ui.view;

import allteran.voyage.domain.Ticket;
import allteran.voyage.service.TicketService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "daily-report", layout = MainView.class)
@PageTitle("Ежедневный отчет | VOYAGE")
@PermitAll
public class DailyReportView extends Div {
    private final TicketService ticketService;
    private final Button searchButton;

    private Grid<Ticket> grid = new Grid<>(Ticket.class, false);

    @Autowired
    public DailyReportView(TicketService ticketService) {
        this.ticketService = ticketService;
        searchButton = new Button("Найти", VaadinIcon.SEARCH.create());
        Button generateReportButton = new Button("Экспортировать отчет", VaadinIcon.ARROW_DOWN.create());

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generateReportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(searchButton, generateReportButton);

        buttonsLayout.getElement().setAttribute("aria-label", "Profile");
        buttonsLayout.getStyle().set("margin-inline-start", "auto").set("padding", "5px");

        generateReportButton.addClickListener(e -> Notification.show("ReportButton"));

        add(createToolbar(createFilter(), buttonsLayout));

        add(createGridLayout());
        createGridColumns();
    }

    private Component createGridLayout() {
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setAllRowsVisible(true);
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);
        return grid;
    }

    private void createGridColumns() {
        grid.addColumn(Ticket::getTicketNumber).setHeader("Номер билета").setSortable(true);
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

        LitRenderer<Ticket> typeRenderer = LitRenderer.<Ticket>of("[[item.type.name]]")
                .withProperty("type", Ticket::getType);
        grid.addColumn(typeRenderer).setHeader("Тип выписки").setSortable(true);
        grid.addColumn(Ticket::getFlightRoute).setHeader("Маршрут").setSortable(true);
        grid.addColumn(Ticket::getTariffPrice).setHeader("Тариф").setSortable(true);
        grid.addColumn(Ticket::getTaxYQPrice).setHeader("Такса YQ").setSortable(true);
        grid.addColumn(Ticket::getTaxRUYRPrice).setHeader("Такса RU/YR").setSortable(true);
        grid.addColumn(Ticket::getTotalPrice).setHeader("Сумма").setSortable(true);

        //TODO: RENDERER ISNT WORKING
        LitRenderer<Ticket>payTypeRenderer = LitRenderer.<Ticket>of("[[item.payType.name]]")
                .withProperty("payType", Ticket::getPayType);
        grid.addColumn(payTypeRenderer).setHeader("Тип оплаты").setSortable(true);
        grid.addColumn(Ticket::getComment).setHeader("Комментарий").setSortable(false);
    }

    private Component createFilter() {
        DatePicker dateFilter = new DatePicker("Дата отчета");

        DatePicker.DatePickerI18n datePickerFormatter = new DatePicker.DatePickerI18n();
        datePickerFormatter.setDateFormat("dd.MM.yyyy");

        dateFilter.setI18n(datePickerFormatter);
        dateFilter.setPlaceholder("дд.мм.гггг");

        dateFilter.setWidth("30%");

        searchButton.addClickListener(e -> grid.setItems(ticketService.findAllByIssueDate(dateFilter.getValue())));

        return dateFilter;
    }

    private Component createToolbar(Component filter, Component buttonsLayout) {
        H2 title = new H2("Ежедневный отчет");
        title.getStyle().set("margin", "0 auto 0 0");

        HorizontalLayout horizontalLayout = new HorizontalLayout(filter, buttonsLayout);
        horizontalLayout.setWidthFull();
        return new VerticalLayout(title, horizontalLayout);
    }
}
