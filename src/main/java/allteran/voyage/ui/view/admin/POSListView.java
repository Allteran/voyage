package allteran.voyage.ui.view.admin;

import allteran.voyage.domain.PointOfSales;
import allteran.voyage.service.POSService;
import allteran.voyage.ui.component.POSEditor;
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

@Route(value = "adm/pos-list", layout = MainView.class)
@RolesAllowed("ADMIN")
@PageTitle("Точки продаж | Администрирование VOYAGE")
public class POSListView extends Div {
    private final POSService posService;
    private final POSEditor posEditor;

    private Grid<PointOfSales> grid = new Grid<>(PointOfSales.class, false);

    @Autowired
    public POSListView(POSService posService, POSEditor posEditor) {
        this.posService = posService;
        this.posEditor = posEditor;
        getStyle().set("padding", "10px");

        add(posEditor);

        posEditor.setChangeHandler(() -> {
            grid.setItems(posService.findAll());
        });

        Button addNewButton = new Button("Добавить точку", VaadinIcon.PLUS.create());
        addNewButton.getElement().setAttribute("aria-label", "Profile");
        addNewButton.getStyle().set("margin-inline-start", "auto").set("padding", "5px");
        addNewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewButton.addClickListener(e -> posEditor.editPOS(new PointOfSales()));

        add(createToolbar(addNewButton));
        add(createGrid());

        grid.addItemDoubleClickListener(e -> posEditor.editPOS(e.getItem()));
    }

    private Component createGrid() {
        grid.setItems(posService.findAll());
        grid.setAllRowsVisible(true);

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);

        grid.addColumn(PointOfSales::getNickname).setHeader("Наименование").setSortable(true);
        grid.addColumn(PointOfSales::getAddress).setHeader("Адрес").setSortable(true);

        return grid;
    }

    private Component createToolbar(Component button) {
        H2 title = new H2("Точки продаж");
        title.getStyle().set("margin", "0 auto 0 0");
;
        return new HorizontalLayout(title, button);
    }
}
