package allteran.voyage.ui.view;

import allteran.voyage.domain.PointOfSales;
import allteran.voyage.domain.User;
import allteran.voyage.security.SecurityService;
import allteran.voyage.service.POSService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.util.prefs.Preferences;

@PageTitle("Выбор торговой точки | VOYAGE")
@Route("pos-select")
@PermitAll
public class PosSelectView extends Div {
    private final POSService posService;
    private final SecurityService securityService;
    private final Select<PointOfSales> selector;

    @Autowired
    public PosSelectView(POSService posService, SecurityService securityService) {
        this.posService = posService;
        this.securityService = securityService;

        getStyle()
                .set("display", "block")
                .set("margin", "0 auto")
                .set("max-width", "1024px")
                .set("padding", "0 var(--lumo-space-l)");


        H2 title = new H2("Выберите точку продаж");
        title.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold").set("text-align", "center");

        selector = new Select<>();
        selector.setLabel("Точка продаж");
        selector.setItemLabelGenerator(PointOfSales::getNickname);
        selector.setItems(posService.findAll());

        Button selectButton = new Button("Выбрать");
        selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        selectButton.addClickListener(e -> savePos());

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(e -> discardChanges());

        FormLayout formLayout = new FormLayout(selector);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        formLayout.setColspan(selector, 1);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, selectButton);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div posSelectorLayout = new Div(title, formLayout, buttonLayout);

        add(posSelectorLayout);
    }

    private void discardChanges() {
        securityService.logout();
    }

    private void savePos() {
        User user = (User) securityService.getAuthenticatedUser();
        if(user != null && selector.getValue() != null) {
            Preferences userPref = Preferences.userRoot();
            userPref.putLong("pos", selector.getValue().getId());
            UI.getCurrent().getPage().setLocation("/");
        } else {
            selector.setErrorMessage("Нужно выбрать точку продаж");
            selector.setInvalid(true);
        }
    }
}
