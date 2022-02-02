package allteran.voyage.ui.view.admin;

import allteran.voyage.domain.TicketType;
import allteran.voyage.service.TicketTypeService;
import allteran.voyage.ui.component.TicketTypeEditor;
import allteran.voyage.ui.view.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

@Route(value = "adm/ticket-type", layout = MainView.class)
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@PageTitle("Типы билетов | Администрирование VOYAGE")
public class TypeListView extends Div {
    private final TicketTypeService typeService;
    private final TicketTypeEditor typeEditor;
    private ListDataProvider<TicketType> dataProvider;

    private Grid<TicketType> grid = new Grid<>(TicketType.class, false);
    private Button addNewButton = new Button("Добавить тип", VaadinIcon.PLUS.create());

    @Autowired
    public TypeListView(TicketTypeService typeService, TicketTypeEditor typeEditor) {
        this.typeService = typeService;
        this.dataProvider = new ListDataProvider<>(typeService.findAll());
        this.typeEditor = typeEditor;

        add(typeEditor);
        typeEditor.setChangeHandler(() -> {
            dataProvider = new ListDataProvider<>(typeService.findAll());
            grid.setDataProvider(dataProvider);
        });

        addNewButton.getElement().setAttribute("aria-label", "Profile");
        addNewButton.getStyle().set("margin-inline-start", "auto").set("padding", "5px");
        addNewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewButton.addClickListener(e -> typeEditor.editType(new TicketType()));

        add(createToolbar(addNewButton));
        add(createGrid());

        grid.addItemDoubleClickListener(e -> typeEditor.editType(e.getItem()));
    }

    private Component createGrid() {
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setDataProvider(dataProvider);
        grid.setAllRowsVisible(true);

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);

        grid.addColumn(TicketType::getName).setHeader("Наименование").setSortable(true);

        return grid;
    }

    private Component createToolbar(Component button) {
        H2 title = new H2("Типы выписываемых билетов");
        title.getStyle().set("margin", "0 auto 0 0");

        HorizontalLayout horizontalLayout = new HorizontalLayout(button);
        horizontalLayout.setWidthFull();
        return new VerticalLayout(title, horizontalLayout);
    }
}
