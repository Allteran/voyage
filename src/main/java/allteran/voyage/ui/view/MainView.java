package allteran.voyage.ui.view;

import allteran.voyage.domain.Role;
import allteran.voyage.domain.User;
import allteran.voyage.security.SecurityService;
import allteran.voyage.ui.view.admin.TypeListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PermitAll
@PageTitle("Voyage")
@Route
public class MainView extends AppLayout {
    private static final String PROFILE_PAGE = "/profile";
    private final SecurityService securityService;
    private User user;

    public MainView(@Autowired SecurityService securityService) {
        this.securityService = securityService;
        user = (User) securityService.getAuthenticatedUser();

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

        User user = (User) securityService.getAuthenticatedUser();
        String userName = "profile";
        if(user != null) {
            userName =  user.getFirstName() + " " + user.getLastName();
        }
        Button profile = new Button(userName);
        profile.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        profile.getElement().setAttribute("aria-label", "Profile");
        profile.getStyle().set("margin-inline-start", "auto");
        profile.addClickListener(e ->UI.getCurrent().getPage().setLocation(PROFILE_PAGE));
        buttonsGroup.add(profile);


        Button logout = new Button(new Icon(VaadinIcon.SIGN_OUT));
        logout.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        logout.getElement().setAttribute("aria-label", "Logout");
        logout.getStyle().set("margin-inline-start", "auto");
        logout.addClickListener(e -> securityService.logout());
        buttonsGroup.add(logout);

        layout.add(buttonsGroup);

        return layout;
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();

        tabs.add(createTab(VaadinIcon.AIRPLANE, "Список билетов", TicketsView.class));
        tabs.add(createTab(VaadinIcon.ARCHIVE, "Отчеты", TicketsView.class));
        tabs.add(createTab(VaadinIcon.CHART, "Аналитика", TicketsView.class));

        // Now display items that available only for admin
        for (Role r : user.getRoles()) {
            if(r.equals(Role.ADMIN)) {
                tabs.add(createTab(VaadinIcon.AUTOMATION, "Типы билетов" ,TypeListView.class));
            }
        }

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
