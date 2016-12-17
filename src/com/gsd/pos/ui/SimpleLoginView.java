package com.gsd.pos.ui;

import org.apache.log4j.Logger;

import com.gsd.pos.dao.UserDao;
import com.gsd.pos.dao.impl.UserDaoImpl;
import com.gsd.pos.model.User;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class SimpleLoginView extends CustomComponent implements View,
		Button.ClickListener {
	public static final String NAME = "";
	private final TextField user;
	private final PasswordField password;
	private final Button loginButton;
	private UserDao userDao = new UserDaoImpl();
	private static final Logger logger = Logger.getLogger(SimpleLoginView.class
			.getName());
	private VerticalLayout fields;

	public SimpleLoginView() {
		setSizeFull();
		// Create the user input field
		user = new TextField("User:");
		user.setWidth("300px");
		user.setRequired(true);
		user.setInputPrompt("Your username");
		password = new PasswordField("Password:");
		password.setWidth("300px");
		password.addValidator(new PasswordValidator());
		password.setRequired(true);
		password.setValue("");
		// password.setNullRepresentation("");
		// Create login button
		loginButton = new Button("Login", this);
		loginButton.setClickShortcut(KeyCode.ENTER);

		// Add both to a panel
		fields = new VerticalLayout(user, password, loginButton);
		fields.setComponentAlignment(loginButton, Alignment.MIDDLE_LEFT);
		fields.setCaption("Please login to access the application.");
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();
		// The view root layout
		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Runo.LAYOUT_DARKER);
		setCompositionRoot(viewLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		logger.debug("In Simple login view enter ...");
		// focus the username field when user arrives to the login view
		user.focus();
	}

	//
	// Validator for validating the passwords
	//
	private static final class PasswordValidator extends
			AbstractValidator<String> {
		public PasswordValidator() {
			super("The password provided is not valid");
		}

		@Override
		protected boolean isValidValue(String value) {
			return true;
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		//
		// Validate the fields using the navigator. By using validors for the
		// fields we reduce the amount of queries we have to use to the database
		// for wrongly entered passwords
		//
		if (!user.isValid() || !password.isValid()
				|| (password.getValue() == null)
				|| (password.getValue().isEmpty())) {
			return;
		}
		String username = user.getValue();
		String password = this.password.getValue();
		User u = userDao.getUser(username, password);
		boolean isValid = (u != null);
		if (isValid) {
			logger.debug("Got user [" + u.getUserId() + "/"
					+ u.getUsername() + "]");
			// Store the current user in the service session
			getSession().setAttribute("user", u);
			// Navigate to main view
			logger.debug("Navigating to SitesView");
			getUI().getNavigator().navigateTo(SitesView.NAME);
		} else {
			fields.setCaption("Invalid username or password!!");
			// Wrong password clear the password field and refocuses it
			this.password.setValue("");
			this.password.focus();
		}
	}

}