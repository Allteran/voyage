//package allteran.voyage.ui.view;
//
//import allteran.voyage.domain.Ticket;
//import allteran.voyage.service.TicketService;
//import allteran.voyage.ui.component.TicketEditor;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.value.ValueChangeMode;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@PageTitle("")
//@Route
//public class MainView extends Div {
//    private final TicketService ticketService;
//
//    private final TextField filter = new TextField("","Отфильтровать");
//    private final Button addNewButton = new Button("Новая запись");
//    private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton);
//
//    private final TicketEditor editor;
//
//    private Grid<Ticket> grid = new Grid<>(Ticket.class);
//
//    @Autowired
//    public MainView(TicketService ticketService, TicketEditor editor) {
//        this.ticketService = ticketService;
//        this.editor = editor;
//
//        add(toolbar, grid, editor);
//
//        // Replace listing with filtered content when user changes filter
//        filter.setValueChangeMode(ValueChangeMode.EAGER);
//        filter.addValueChangeListener(e -> showTickets(e.getValue()));
//
//        // Connect selected Customer to editor or hide if none is selected
//        grid.asSingleSelect().addValueChangeListener(e -> {
//            editor.editTicket(e.getValue());
//        });
//
//        // Instantiate and edit new Customer the new button is clicked
//        addNewButton.addClickListener(e -> editor.editTicket(new Ticket()));
//
//        // Listen changes made by the editor, refresh data from backend
//        editor.setChangeHandler(() -> {
//            editor.setVisible(false);
//            showTickets(filter.getValue());
//        });
//
//
//        showTickets("");
//    }
//
//    private void showTickets(String customer) {
//        if(customer.isEmpty()) {
//            grid.setItems(ticketService.findAll());
//        } else {
//            grid.setItems(ticketService.findByCustomer(customer));
//        }
//    }
//}
