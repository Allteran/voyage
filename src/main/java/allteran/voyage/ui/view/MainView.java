package allteran.voyage.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.security.PermitAll;

@PermitAll
@PageTitle("Voyage")
@Route
public class MainView extends AppLayout {
    public MainView() {
        DrawerToggle drawerToggle = new DrawerToggle();

        H1 title = new H1("Voyage");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        Tabs tabs = getTabs();

        addToDrawer(tabs);
        addToNavbar(false, createHeaderContent(drawerToggle,title));
    }

    private Component createHeaderContent(DrawerToggle drawerToggle, H1 title) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        //add Drawer to navbar
        layout.add(drawerToggle);

        //add Title to navbar
        layout.add(title);

        HorizontalLayout buttonsGroup = new HorizontalLayout();
        buttonsGroup.getStyle().set("margin-inline-start", "auto");

        Button profile = new Button("User profile");
        profile.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        profile.getElement().setAttribute("aria-label", "Profile");
        profile.getStyle().set("margin-inline-start", "auto");
        buttonsGroup.add(profile);


        Button logout = new Button(new Icon(VaadinIcon.SIGN_OUT));
        logout.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        logout.getElement().setAttribute("aria-label", "Logout");
        logout.getStyle().set("margin-inline-start", "auto");
        buttonsGroup.add(logout);

        layout.add(buttonsGroup);

        return layout;
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.LIST, "Список билетов", TicketsView.class),
                createTab(VaadinIcon.ARCHIVE, "Отчеты", TicketsView.class),
                createTab(VaadinIcon.CHART, "Аналитика", TicketsView.class)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;

    }

    private Tab createTab(VaadinIcon viewIcon, String label, Class<? extends Component> navigationRoute) {
        Icon icon = viewIcon.create();
        icon.getStyle()
                .set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(label));
        link.setRoute(navigationRoute);
        link.setTabIndex(-1);

        return new Tab(link);
    }
}
