package edu.lognet.reputation.view.gui;

import edu.lognet.reputation.model.user.User;

/**
 * Represent a Peer in the simulator with the position (x, y)
 * and the color
 * @author Laurent Vanni, Thao Nguyen
 *
 */
public class UserGUI {
	/* --------------------------------------------------------- */
	/* Attributes */
	/* --------------------------------------------------------- */
	private User user;
	private int x;
	private int y;
	private int r;
	private int g;
	private int b;
	
	
	/* --------------------------------------------------------- */
	/* Constructors */
	/* --------------------------------------------------------- */
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
//			setG(50);
			break;
		case GOODTURNSBAD:
			break;
		case FLUCTUATE:
			break;
		case BAD:
//			setR(50);
			break;
		case BADTURNSGOOD:
			break;
		case NORMAL:
//			setR(50);
//			setG(50);
			break;
		default:
			break;
		}
	}

	/* --------------------------------------------------------- */
	/* GETTER AND SETTER */
	/* --------------------------------------------------------- */
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
