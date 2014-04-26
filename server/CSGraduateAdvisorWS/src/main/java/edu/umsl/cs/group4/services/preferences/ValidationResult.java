package edu.umsl.cs.group4.services.preferences;

import java.util.ArrayList;
import java.util.List;

class ValidationResult {
	private List<String> notifications = new ArrayList<String>();

	public ValidationResult(List<String> notifications) {
		this.notifications = notifications;
	}

	public List<String> getNotifications() {
	return notifications;
	}

	public void setNotifications(List<String> notifications) {
	this.notifications = notifications;
	}

}
