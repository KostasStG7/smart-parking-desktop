package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import model.ParkingSpot;
import model.User;

import service.ParkingSpotService;
import service.ProfileService;

public class MainFrame extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JPanel sidebarPanel;
    private JPanel topBar;

    private JLabel pageTitleLabel;
    private JLabel userBadge;

    private JTextField globalSearchField;

    private final Map<String, JButton> navButtons =
            new LinkedHashMap<>();

    private final Map<String, String> navButtonTexts =
            new LinkedHashMap<>();

    private final Map<String, String> navButtonShortTexts =
            new LinkedHashMap<>();

    private JPanel brandPanel;
    private JLabel logoLabel;
    private JPanel brandTextPanel;

    private JButton sidebarToggleButton;
    private JButton logoutButton;

    private boolean sidebarCollapsed =
            false;

    private static final int SIDEBAR_EXPANDED_WIDTH =
            248;

    private static final int SIDEBAR_COLLAPSED_WIDTH =
            88;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    private DashboardPanel dashboardPanel;

    private AddParkingSpotPanel addSpotPanel;
    private MyParkingSpotsPanel myParkingSpotsPanel;

    private AddAvailabilityPanel addAvailabilityPanel;
    private MyAvailabilitiesPanel myAvailabilitiesPanel;

    private SearchParkingSpotPanel searchSpotsPanel;

    private ReservationPanel reservationPanel;
    private MyReservationsPanel myReservationsPanel;
    private ReservationsOnMySpotsPanel reservationsOnMySpotsPanel;

    private NotificationsPanel notificationsPanel;

    private ProfilePanel profilePanel;

    private final ProfileService profileService =
            new ProfileService();

    private int currentUserId =
            -1;

    private boolean guestMode =
            false;

    public MainFrame() {

        UITheme.applyGlobalStyle();

        setTitle(
                "Smart Parking"
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                1380,
                820
        );

        setMinimumSize(
                new Dimension(
                        1080,
                        680
                )
        );

        setLocationRelativeTo(
                null
        );

        getContentPane()
                .setBackground(
                        UITheme.BACKGROUND
                );

        setLayout(
                new BorderLayout()
        );

        buildSidebar();

        buildTopBar();

        cardLayout =
                new CardLayout();

        cardPanel =
                new JPanel(
                        cardLayout
                );

        cardPanel.setBackground(
                UITheme.BACKGROUND
        );

        loginPanel =
                new LoginPanel(
                        cardLayout,
                        cardPanel
                );

        registerPanel =
                new RegisterPanel(
                        cardLayout,
                        cardPanel
                );

        searchSpotsPanel =
                new SearchParkingSpotPanel();

        cardPanel.add(
                loginPanel,
                "login"
        );

        cardPanel.add(
                registerPanel,
                "register"
        );

        cardPanel.add(
                searchSpotsPanel,
                "searchSpots"
        );

        add(
                cardPanel,
                BorderLayout.CENTER
        );

        loginPanel.setLoginListener(
                this::handleLoginSuccess
        );

        loginPanel.setGuestListener(
                this::handleGuestLogin
        );

        cardLayout.show(
                cardPanel,
                "login"
        );
    }

    private void buildSidebar() {

        sidebarPanel =
                new JPanel();

        sidebarPanel.setLayout(
                new BoxLayout(
                        sidebarPanel,
                        BoxLayout.Y_AXIS
                )
        );

        sidebarPanel.setBackground(
                UITheme.SIDEBAR
        );

        sidebarPanel.setPreferredSize(
                new Dimension(
                        SIDEBAR_EXPANDED_WIDTH,
                        0
                )
        );

        sidebarPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        12,
                        18,
                        12
                )
        );

        sidebarPanel.setVisible(
                false
        );

        brandPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        brandPanel.setOpaque(
                false
        );

        brandPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        58
                )
        );

        logoLabel =
                new JLabel(
                        "P",
                        SwingConstants.CENTER
                );

        logoLabel.setOpaque(
                true
        );

        logoLabel.setBackground(
                UITheme.PRIMARY
        );

        logoLabel.setForeground(
                Color.WHITE
        );

        logoLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        logoLabel.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        brandTextPanel =
                new JPanel();

        brandTextPanel.setOpaque(
                false
        );

        brandTextPanel.setLayout(
                new BoxLayout(
                        brandTextPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel name =
                new JLabel(
                        "Smart Parking"
                );

        name.setForeground(
                Color.WHITE
        );

        name.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        JLabel caption =
                new JLabel(
                        "Parking made simple"
                );

        caption.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        caption.setFont(
                UITheme.SMALL
        );

        brandTextPanel.add(
                name
        );

        brandTextPanel.add(
                caption
        );

        sidebarToggleButton =
                new JButton(
                        "‹"
                );

        sidebarToggleButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        sidebarToggleButton.setForeground(
                new Color(
                        203,
                        213,
                        225
                )
        );

        sidebarToggleButton.setBackground(
                UITheme.SIDEBAR
        );

        sidebarToggleButton.setBorderPainted(
                false
        );

        sidebarToggleButton.setFocusPainted(
                false
        );

        sidebarToggleButton.setContentAreaFilled(
                false
        );

        sidebarToggleButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        sidebarToggleButton.setPreferredSize(
                new Dimension(
                        34,
                        42
                )
        );

        sidebarToggleButton.setToolTipText(
                "Collapse sidebar"
        );

        sidebarToggleButton.addActionListener(
                e -> toggleSidebar()
        );

        brandPanel.add(
                logoLabel,
                BorderLayout.WEST
        );

        brandPanel.add(
                brandTextPanel,
                BorderLayout.CENTER
        );

        brandPanel.add(
                sidebarToggleButton,
                BorderLayout.EAST
        );

        sidebarPanel.add(
                brandPanel
        );

        sidebarPanel.add(
                Box.createVerticalStrut(
                        22
                )
        );

        addNavButton(
                "dashboard",
                "Dashboard",
                "D",
                "dashboard"
        );

        addNavButton(
                "searchSpots",
                "Search Spots",
                "S",
                "searchSpots"
        );

        addNavButton(
                "addSpot",
                "Add Parking Spot",
                "+",
                "addSpot"
        );

        addNavButton(
                "myParkingSpots",
                "My Parking Spots",
                "P",
                "myParkingSpots"
        );

        addNavButton(
                "addAvailability",
                "Add Availability",
                "A",
                "addAvailability"
        );

        addNavButton(
                "myAvailabilities",
                "My Availabilities",
                "MA",
                "myAvailabilities"
        );

        addNavButton(
                "reservation",
                "Reservations",
                "R",
                "reservation"
        );

        addNavButton(
                "myReservations",
                "My Reservations",
                "MR",
                "myReservations"
        );

        addNavButton(
                "reservationsOnMySpots",
                "Reservations on My Spots",
                "RS",
                "reservationsOnMySpots"
        );

        addNavButton(
                "notifications",
                "Notifications",
                "N",
                "notifications"
        );

        addNavButton(
                "profile",
                "My Profile",
                "U",
                "profile"
        );

        sidebarPanel.add(
                Box.createVerticalGlue()
        );

        JSeparator separator =
                new JSeparator();

        separator.setForeground(
                new Color(
                        51,
                        65,
                        85
                )
        );

        separator.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        sidebarPanel.add(
                separator
        );

        sidebarPanel.add(
                Box.createVerticalStrut(
                        12
                )
        );

        logoutButton =
                UITheme.sidebarButton(
                        "Sign out"
                );

        logoutButton.setForeground(
                new Color(
                        248,
                        113,
                        113
                )
        );

        logoutButton.setToolTipText(
                "Sign out"
        );

        logoutButton.addActionListener(
                e -> logout()
        );

        sidebarPanel.add(
                logoutButton
        );

        add(
                sidebarPanel,
                BorderLayout.WEST
        );
    }

    private void addNavButton(
            String key,
            String text,
            String shortText,
            String cardName
    ) {

        JButton button =
                UITheme.sidebarButton(
                        text
                );

        button.setToolTipText(
                text
        );

        button.addActionListener(
                e ->
                        showAppCard(
                                cardName,
                                text
                        )
        );

        navButtons.put(
                key,
                button
        );

        navButtonTexts.put(
                key,
                text
        );

        navButtonShortTexts.put(
                key,
                shortText
        );

        sidebarPanel.add(
                button
        );

        sidebarPanel.add(
                Box.createVerticalStrut(
                        4
                )
        );
    }

    private void toggleSidebar() {

        sidebarCollapsed =
                !sidebarCollapsed;

        updateSidebarState();
    }

    private void updateSidebarState() {

        int width =
                sidebarCollapsed
                        ? SIDEBAR_COLLAPSED_WIDTH
                        : SIDEBAR_EXPANDED_WIDTH;

        sidebarPanel.setPreferredSize(
                new Dimension(
                        width,
                        0
                )
        );

        sidebarPanel.setMinimumSize(
                new Dimension(
                        width,
                        0
                )
        );

        brandPanel.removeAll();

        if (
                sidebarCollapsed
        ) {

            sidebarToggleButton.setText(
                    "›"
            );

            sidebarToggleButton.setToolTipText(
                    "Expand sidebar"
            );

            brandPanel.add(
                    sidebarToggleButton,
                    BorderLayout.CENTER
            );

        } else {

            sidebarToggleButton.setText(
                    "‹"
            );

            sidebarToggleButton.setToolTipText(
                    "Collapse sidebar"
            );

            brandPanel.add(
                    logoLabel,
                    BorderLayout.WEST
            );

            brandPanel.add(
                    brandTextPanel,
                    BorderLayout.CENTER
            );

            brandPanel.add(
                    sidebarToggleButton,
                    BorderLayout.EAST
            );
        }

        for (
                Map.Entry<String, JButton> entry :
                navButtons.entrySet()
        ) {

            String key =
                    entry.getKey();

            JButton button =
                    entry.getValue();

            if (
                    sidebarCollapsed
            ) {

                button.setText(
                        navButtonShortTexts.get(
                                key
                        )
                );

                button.setHorizontalAlignment(
                        SwingConstants.CENTER
                );

                button.setBorder(
                        BorderFactory.createEmptyBorder(
                                0,
                                0,
                                0,
                                0
                        )
                );

            } else {

                button.setText(
                        navButtonTexts.get(
                                key
                        )
                );

                button.setHorizontalAlignment(
                        SwingConstants.LEFT
                );

                button.setBorder(
                        BorderFactory.createEmptyBorder(
                                0,
                                24,
                                0,
                                12
                        )
                );
            }
        }

        if (
                sidebarCollapsed
        ) {

            logoutButton.setText(
                    "X"
            );

            logoutButton.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            logoutButton.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            0,
                            0,
                            0
                    )
            );

            logoutButton.setToolTipText(
                    guestMode
                            ? "Leave guest mode"
                            : "Sign out"
            );

        } else {

            logoutButton.setText(
                    guestMode
                            ? "Leave Guest Mode"
                            : "Sign out"
            );

            logoutButton.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            logoutButton.setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            24,
                            0,
                            12
                    )
            );
        }

        brandPanel.revalidate();
        brandPanel.repaint();

        sidebarPanel.revalidate();
        sidebarPanel.repaint();

        revalidate();
        repaint();
    }

    private void buildTopBar() {

        topBar =
                new JPanel();

        topBar.setLayout(
                new BoxLayout(
                        topBar,
                        BoxLayout.X_AXIS
                )
        );

        topBar.setBackground(
                Color.WHITE
        );

        topBar.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                UITheme.BORDER
                        ),

                        BorderFactory.createEmptyBorder(
                                14,
                                24,
                                14,
                                24
                        )
                )
        );

        topBar.setPreferredSize(
                new Dimension(
                        0,
                        72
                )
        );

        topBar.setVisible(
                false
        );

        pageTitleLabel =
                new JLabel(
                        "Dashboard"
                );

        pageTitleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        pageTitleLabel.setForeground(
                UITheme.TEXT
        );

        topBar.add(
                pageTitleLabel
        );

        topBar.add(
                Box.createHorizontalGlue()
        );

        globalSearchField =
                new JTextField(
                        20
                );

        UITheme.styleTextField(
                globalSearchField
        );

        globalSearchField.setToolTipText(
                "Search parking spots by area"
        );

        globalSearchField.setMaximumSize(
                new Dimension(
                        260,
                        42
                )
        );

        topBar.add(
                globalSearchField
        );

        topBar.add(
                Box.createHorizontalStrut(
                        8
                )
        );

        UITheme.ModernButton searchButton =
                UITheme.primaryButton(
                        "Search Area"
                );

        searchButton.addActionListener(
                e -> performGlobalSearch()
        );

        globalSearchField.addActionListener(
                e -> performGlobalSearch()
        );

        topBar.add(
                searchButton
        );

        topBar.add(
                Box.createHorizontalStrut(
                        16
                )
        );

        userBadge =
                new JLabel(
                        "  Signed in  "
                );

        userBadge.setOpaque(
                true
        );

        userBadge.setBackground(
                UITheme.PRIMARY_SOFT
        );

        userBadge.setForeground(
                UITheme.PRIMARY
        );

        userBadge.setFont(
                UITheme.FONT_BOLD
        );

        userBadge.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        12,
                        9,
                        12
                )
        );

        userBadge.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        userBadge.setToolTipText(
                "Open profile"
        );

        userBadge.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (
                                SwingUtilities
                                        .isLeftMouseButton(
                                                e
                                        )
                        ) {

                            if (
                                    guestMode
                            ) {

                                showLoginRequired();

                                return;
                            }

                            if (
                                    currentUserId > 0
                            ) {

                                showAppCard(
                                        "profile",
                                        "My Profile"
                                );
                            }
                        }
                    }

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        if (
                                currentUserId > 0
                                        &&
                                !guestMode
                        ) {

                            userBadge.setBackground(
                                    new Color(
                                            219,
                                            234,
                                            254
                                    )
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        userBadge.setBackground(
                                UITheme.PRIMARY_SOFT
                        );
                    }
                }
        );

        topBar.add(
                userBadge
        );

        add(
                topBar,
                BorderLayout.NORTH
        );
    }

    private void handleLoginSuccess(
            int userId
    ) {

        clearDynamicPanels();

        guestMode =
                false;

        currentUserId =
                userId;

        sidebarPanel.setVisible(
                true
        );

        topBar.setVisible(
                true
        );

        loadUserBadge();

        buildLoggedInPanels();

        updateNavigationVisibility();

        updateSidebarState();

        showAppCard(
                "dashboard",
                "Dashboard"
        );

        revalidate();
        repaint();
    }

    private void handleGuestLogin() {

        clearDynamicPanels();

        guestMode =
                true;

        currentUserId =
                -1;

        sidebarPanel.setVisible(
                true
        );

        topBar.setVisible(
                true
        );

        userBadge.setText(
                "  Guest  "
        );

        userBadge.setToolTipText(
                "Sign in to access your profile"
        );

        reservationPanel =
                new ReservationPanel(
                        -1
                );

        dashboardPanel =
                new DashboardPanel(
                        -1,
                        reservationPanel,
                        this::showLoginRequired
                );

        dashboardPanel
                .loadMarkersFromDatabase(
                        new ParkingSpotService()
                );

        cardPanel.add(
                dashboardPanel,
                "dashboard"
        );

        updateNavigationVisibility();

        updateSidebarState();

        showAppCard(
                "dashboard",
                "Dashboard"
        );

        revalidate();
        repaint();
    }

    private void loadUserBadge() {

        if (
                currentUserId <= 0
        ) {

            userBadge.setText(
                    "  Signed in  "
            );

            return;
        }

        User user =
                profileService
                        .getProfile(
                                currentUserId
                        );

        if (
                user == null
                        ||
                user.getFullName() == null
                        ||
                user.getFullName()
                        .trim()
                        .isEmpty()
        ) {

            userBadge.setText(
                    "  My Profile  "
            );

        } else {

            userBadge.setText(
                    "  "
                            +
                    user.getFullName().trim()
                            +
                    "  "
            );
        }

        userBadge.setToolTipText(
                "Open My Profile"
        );
    }

    private void buildLoggedInPanels() {

        reservationPanel =
                new ReservationPanel(
                        currentUserId
                );

        dashboardPanel =
                new DashboardPanel(
                        currentUserId,
                        reservationPanel
                );

        dashboardPanel
                .loadMarkersFromDatabase(
                        new ParkingSpotService()
                );

        addSpotPanel =
                new AddParkingSpotPanel(
                        currentUserId,
                        dashboardPanel
                );

        myParkingSpotsPanel =
                new MyParkingSpotsPanel(
                        currentUserId,
                        dashboardPanel
                );

        addAvailabilityPanel =
                new AddAvailabilityPanel(
                        currentUserId,
                        dashboardPanel
                );

        myAvailabilitiesPanel =
                new MyAvailabilitiesPanel(
                        currentUserId
                );

        myReservationsPanel =
                new MyReservationsPanel(
                        currentUserId
                );

        reservationsOnMySpotsPanel =
                new ReservationsOnMySpotsPanel(
                        currentUserId
                );

        notificationsPanel =
                new NotificationsPanel(
                        currentUserId
                );

        profilePanel =
                new ProfilePanel(
                        currentUserId
                );

        profilePanel.setProfileUpdatedListener(
                this::loadUserBadge
        );

        cardPanel.add(
                dashboardPanel,
                "dashboard"
        );

        cardPanel.add(
                addSpotPanel,
                "addSpot"
        );

        cardPanel.add(
                myParkingSpotsPanel,
                "myParkingSpots"
        );

        cardPanel.add(
                addAvailabilityPanel,
                "addAvailability"
        );

        cardPanel.add(
                myAvailabilitiesPanel,
                "myAvailabilities"
        );

        cardPanel.add(
                reservationPanel,
                "reservation"
        );

        cardPanel.add(
                myReservationsPanel,
                "myReservations"
        );

        cardPanel.add(
                reservationsOnMySpotsPanel,
                "reservationsOnMySpots"
        );

        cardPanel.add(
                notificationsPanel,
                "notifications"
        );

        cardPanel.add(
                profilePanel,
                "profile"
        );
    }

    private void updateNavigationVisibility() {

        boolean loggedIn =
                currentUserId > 0
                        &&
                !guestMode;

        setNavVisible(
                "dashboard",
                currentUserId > 0
                        ||
                guestMode
        );

        setNavVisible(
                "searchSpots",
                currentUserId > 0
                        ||
                guestMode
        );

        setNavVisible(
                "addSpot",
                loggedIn
        );

        setNavVisible(
                "myParkingSpots",
                loggedIn
        );

        setNavVisible(
                "addAvailability",
                loggedIn
        );

        setNavVisible(
                "myAvailabilities",
                loggedIn
        );

        setNavVisible(
                "reservation",
                loggedIn
        );

        setNavVisible(
                "myReservations",
                loggedIn
        );

        setNavVisible(
                "reservationsOnMySpots",
                loggedIn
        );

        setNavVisible(
                "notifications",
                loggedIn
        );

        setNavVisible(
                "profile",
                loggedIn
        );

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void setNavVisible(
            String key,
            boolean visible
    ) {

        JButton button =
                navButtons.get(
                        key
                );

        if (
                button != null
        ) {

            button.setVisible(
                    visible
            );
        }
    }

    private void showAppCard(
            String cardName,
            String title
    ) {

        if (
                guestMode
                        &&
                !isPublicCard(
                        cardName
                )
        ) {

            showLoginRequired();

            return;
        }

        if (
                !guestMode
                        &&
                currentUserId <= 0
                        &&
                !cardName.equals(
                        "login"
                )
                        &&
                !cardName.equals(
                        "register"
                )
        ) {

            return;
        }

        if (
                cardName.equals(
                        "dashboard"
                )
                        &&
                dashboardPanel != null
        ) {

            dashboardPanel
                    .loadMarkersFromDatabase(
                            new ParkingSpotService()
                    );
        }

        else if (
                cardName.equals(
                        "searchSpots"
                )
        ) {

            searchSpotsPanel
                    .refreshAllSpots();
        }

        else if (
                cardName.equals(
                        "myParkingSpots"
                )
                        &&
                myParkingSpotsPanel != null
        ) {

            myParkingSpotsPanel
                    .loadParkingSpots();
        }

        else if (
                cardName.equals(
                        "myAvailabilities"
                )
                        &&
                myAvailabilitiesPanel != null
        ) {

            myAvailabilitiesPanel
                    .loadAvailabilities();
        }

        else if (
                cardName.equals(
                        "myReservations"
                )
                        &&
                myReservationsPanel != null
        ) {

            myReservationsPanel
                    .loadReservations();
        }

        else if (
                cardName.equals(
                        "reservationsOnMySpots"
                )
                        &&
                reservationsOnMySpotsPanel != null
        ) {

            reservationsOnMySpotsPanel
                    .loadReservations();
        }

        else if (
                cardName.equals(
                        "notifications"
                )
                        &&
                notificationsPanel != null
        ) {

            notificationsPanel
                    .loadNotifications();
        }

        else if (
                cardName.equals(
                        "profile"
                )
                        &&
                profilePanel != null
        ) {

            profilePanel
                    .loadProfile();

            loadUserBadge();
        }

        pageTitleLabel.setText(
                title
        );

        cardLayout.show(
                cardPanel,
                cardName
        );

        setActiveNav(
                cardName
        );
    }

    private boolean isPublicCard(
            String cardName
    ) {

        return cardName.equals(
                "dashboard"
        )
                ||
                cardName.equals(
                        "searchSpots"
                );
    }

    private void showLoginRequired() {

        int result =
                UITheme.showChoiceDialog(
                        this,
                        "Login Required",
                        "You need an account to use this feature.",
                        "Sign in",
                        "Create account",
                        "Cancel"
                );

        if (
                result == 0
        ) {

            leaveGuestMode();

            cardLayout.show(
                    cardPanel,
                    "login"
            );

        } else if (
                result == 1
        ) {

            leaveGuestMode();

            cardLayout.show(
                    cardPanel,
                    "register"
            );
        }
    }

    private void leaveGuestMode() {

        clearDynamicPanels();

        guestMode =
                false;

        currentUserId =
                -1;

        DashboardPanel.currentUserIdGlobal =
                -1;

        sidebarPanel.setVisible(
                false
        );

        topBar.setVisible(
                false
        );

        globalSearchField.setText(
                ""
        );

        updateNavigationVisibility();

        revalidate();
        repaint();
    }

    private void setActiveNav(
            String cardName
    ) {

        for (
                Map.Entry<String, JButton> entry :
                navButtons.entrySet()
        ) {

            boolean active =
                    entry.getKey()
                            .equals(
                                    cardName
                            );

            UITheme.setSidebarActive(
                    entry.getValue(),
                    active
            );
        }
    }

    private void performGlobalSearch() {

        if (
                dashboardPanel == null
        ) {

            return;
        }

        String area =
                globalSearchField
                        .getText()
                        .trim();

        ParkingSpotService service =
                new ParkingSpotService();

        List<ParkingSpot> results =
                area.isEmpty()
                        ? service.getAllActiveSpots()
                        : service.searchByArea(
                                area
                        );

        showAppCard(
                "dashboard",
                "Dashboard"
        );

        dashboardPanel
                .loadMarkersFromList(
                        results
                );

        if (
                !results.isEmpty()
        ) {

            ParkingSpot first =
                    results.get(
                            0
                    );

            if (
                    first.getLatitude() != null
                            &&
                    first.getLongitude() != null
            ) {

                dashboardPanel
                        .getMap()
                        .setDisplayPosition(
                                new Coordinate(
                                        first.getLatitude(),
                                        first.getLongitude()
                                ),
                                14
                        );
            }

        } else {

            UITheme.showInfo(
                    this,
                    "No Results",
                    "No active parking spots found for that area."
            );
        }
    }

    private void logout() {

        String message =
                guestMode
                        ? "Do you want to leave guest mode?"
                        : "Do you want to sign out?";

        boolean confirmed =
                UITheme.showConfirm(
                        this,

                        guestMode
                                ? "Exit Guest Mode"
                                : "Sign out",

                        message,

                        guestMode
                                ? "Leave guest mode"
                                : "Sign out"
                );

        if (
                !confirmed
        ) {

            return;
        }

        clearDynamicPanels();

        guestMode =
                false;

        currentUserId =
                -1;

        DashboardPanel.currentUserIdGlobal =
                -1;

        sidebarPanel.setVisible(
                false
        );

        topBar.setVisible(
                false
        );

        globalSearchField.setText(
                ""
        );

        sidebarCollapsed =
                false;

        updateSidebarState();

        updateNavigationVisibility();

        cardLayout.show(
                cardPanel,
                "login"
        );

        revalidate();
        repaint();
    }

    private void clearDynamicPanels() {

        if (
                dashboardPanel != null
        ) {

            cardPanel.remove(
                    dashboardPanel
            );

            dashboardPanel =
                    null;
        }

        if (
                addSpotPanel != null
        ) {

            cardPanel.remove(
                    addSpotPanel
            );

            addSpotPanel =
                    null;
        }

        if (
                myParkingSpotsPanel != null
        ) {

            cardPanel.remove(
                    myParkingSpotsPanel
            );

            myParkingSpotsPanel =
                    null;
        }

        if (
                addAvailabilityPanel != null
        ) {

            cardPanel.remove(
                    addAvailabilityPanel
            );

            addAvailabilityPanel =
                    null;
        }

        if (
                myAvailabilitiesPanel != null
        ) {

            cardPanel.remove(
                    myAvailabilitiesPanel
            );

            myAvailabilitiesPanel =
                    null;
        }

        if (
                reservationPanel != null
        ) {

            cardPanel.remove(
                    reservationPanel
            );

            reservationPanel =
                    null;
        }

        if (
                myReservationsPanel != null
        ) {

            cardPanel.remove(
                    myReservationsPanel
            );

            myReservationsPanel =
                    null;
        }

        if (
                reservationsOnMySpotsPanel != null
        ) {

            cardPanel.remove(
                    reservationsOnMySpotsPanel
            );

            reservationsOnMySpotsPanel =
                    null;
        }

        if (
                notificationsPanel != null
        ) {

            cardPanel.remove(
                    notificationsPanel
            );

            notificationsPanel =
                    null;
        }

        if (
                profilePanel != null
        ) {

            cardPanel.remove(
                    profilePanel
            );

            profilePanel =
                    null;
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public static void main(
            String[] args
    ) {

        UITheme.applyGlobalStyle();

        SwingUtilities.invokeLater(
                () ->
                        new MainFrame()
                                .setVisible(
                                        true
                                )
        );
    }
}