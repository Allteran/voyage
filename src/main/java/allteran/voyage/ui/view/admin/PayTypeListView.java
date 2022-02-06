package allteran.voyage.ui.view.admin;

import allteran.voyage.domain.PayType;
import allteran.voyage.domain.TicketType;
import allteran.voyage.service.PayTypeService;
import allteran.voyage.ui.component.PayTypeEditor;
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

@Route(value = "adm/pay-type", layout = MainView.class)
@PageTitle("Типы оплаты | Администрирование VOYAGE")
@RolesAllowed("ADMIN")
public class PayTypeListView extends Div {
    private final PayTypeService payTypeService;
    private final PayTypeEditor payTypeEditor;

    private Grid<PayType> grid = new Grid<>(PayType.class, false);
    private Button addNewButton = new Button("Добавить тип оплаты", VaadinIcon.PLUS.create());

    @Autowired
    public PayTypeListView(PayTypeService payTypeService, PayTypeEditor payTypeEditor) {
        this.payTypeService = payTypeService;
        this.payTypeEditor = payTypeEditor;

        getStyle().set("padding", "10px");

        add(payTypeEditor);
        payTypeEditor.setChangeHandler(() -> {
            grid.setItems(payTypeService.findAll());
        });

        addNewButton.getElement().setAttribute("aria-label", "Profile");
        addNewButton.getStyle().set("margin-inline-start", "auto").set("padding", "5px");
        addNewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewButton.addClickListener(e -> payTypeEditor.editPayType(new PayType()));

        add(createToolbar(addNewButton));
        add(createGrid());

        grid.addItemDoubleClickListener(e -> payTypeEditor.editPayType(e.getItem()));
    }

    private Component createGrid() {
        grid.setItems(payTypeService.findAll());
        grid.setAllRowsVisible(true);

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        wrapper.add(grid);

        grid.addColumn(PayType::getName).setHeader("Наименование").setSortable(true);

        return grid;
    }

    private Component createToolbar(Component button) {
        H2 title = new H2("Типы оплаты");
        title.getStyle().set("margin", "0 auto 0 0");

        return new HorizontalLayout(title, button);
    }

}
