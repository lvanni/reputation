package edu.lognet.reputation.view.gui;

import edu.lognet.reputation.model.user.User;



public class UserGUI {
	
	private User user;
	private int x;
	private int y;
	private int r;
	private int g;
	private int b;
	
	
	// clone
	public UserGUI(UserGUI userGUIStatus) {
		this.user = userGUIStatus.user;
		this.x = userGUIStatus.x;
		this.y = userGUIStatus.y;
		this.r = 0;
		this.g = 0;
		this.b = 0;
	}

	public UserGUI(User user, int x, int y) {
		super();
		this.user = user;
		this.x = x;
		this.y = y;
		switch (user.getProviderType()) {
		case GOOD:
//			setG(255);
			break;
		case GOODTURNSBAD:
//			color = SWT.COLOR_DARK_GREEN;
			break;
		case FLUCTUATE:
			break;
		case BAD:
//			setR(255);
			break;
		case BADTURNSGOOD:
			break;
		case NORMAL:
//			setR(255);
//			setG(255);
//			setB(255);
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

//	public int getColor() {
//		return new Color(r, g, b).getRGB();
//	}

//	public void setColor(int r, int g, int b) {
//		this.r = r;
//		this.g = g;
//		this.b = b;
//	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}
}
