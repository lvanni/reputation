package edu.lognet.reputation.view.gui;

import org.eclipse.swt.SWT;

import edu.lognet.reputation.model.user.User;



public class UserGUI {
	
	private User user;
	private int x;
	private int y;
	private int color;
	
	
	// clone
	public UserGUI(UserGUI userGUIStatus) {
		this.user = userGUIStatus.user;
		this.x = userGUIStatus.x;
		this.y = userGUIStatus.y;
		this.color = userGUIStatus.color;
	}

	public UserGUI(User user, int x, int y) {
		super();
		this.user = user;
		this.x = x;
		this.y = y;
		switch (user.getMyProviderType()) {
		case GOOD:
			color = SWT.COLOR_GREEN;
			break;
		case GOODTURNSBAD:
			color = SWT.COLOR_DARK_GREEN;
			break;
		case BAD:
			color = SWT.COLOR_RED;
			break;
		case BADTURNSGOOD:
			color = SWT.COLOR_DARK_RED;
			break;
		case FLUCTUATE:
			color = SWT.COLOR_BLACK;
			break;
		case NORMAL:
			color = SWT.COLOR_WHITE;
			break;
		default:
			break;
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	
	
}
