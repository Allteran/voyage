package allteran.voyage.ui.view.admin;

import allteran.voyage.domain.TicketStatus;
import allteran.voyage.service.TicketStatusService;
import allteran.voyage.ui.component.TicketStatusEditor;
import allteran.voyage.ui.view.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;

@Route(value = "adm/status-list", layout = MainView.class)
@RolesAllowed("ADMIN")
@PageTitle("Статусы билетов | Администрирование VOYAGE")
public class StatusListView extends Div {
    private final TicketStatusService statusService;
    private final TicketStatusEditor statusEditor;

    private Grid<TicketStatus> grid = new Grid<>(TicketStatus.class, false);

    @Autowired
    public StatusListView(TicketStatusService statusService, TicketStatusEditor statusEditor) {
        this.statusService = statusService;
        this.statusEditor = statusEditor;
        getStyle().set("padding", "10px");

        add(statusEditor);
        statusEditor.setChangeHandler(() -> {
            grid.setItems(statusService.findAll());
        });

        Button addNewButton = new Button("Добавить статус", VaadinIcon.PLUS.create());
        addNewButton.getElement().setAttribute("aria-label", "Profile");
        addNewButton.getStyle().set("margin-inline-start", "auto").set("padding", "5px");
        addNewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewButton.addClickListener(e -> statusEditor.editStatus(new TicketStatus()));

        add(createToolbar(addNewButton));
        add(createGrid());

        grid.addItemDoubleClickListener(e -> statusEditor.editStatus(e.getItem()));
    }

    private Component createGrid() {
        grid.setItems(statusService.findAll());
        grid.setAllRowsVisible(true);

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);

        grid.addColumn(TicketStatus::getName).setHeader("Наименование").setSortable(true);

        return grid;
    }

    private Component createToolbar(Button button) {
        H2 title = new H2("Типы выписываемых билетов");
        title.getStyle().set("margin", "0 auto 0 0");

        return new HorizontalLayout(title, button);
    }
}
