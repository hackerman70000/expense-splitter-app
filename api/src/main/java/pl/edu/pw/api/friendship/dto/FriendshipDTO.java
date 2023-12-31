package pl.edu.pw.api.friendship.dto;

import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.User;

public class FriendshipDTO {
	private User from;
	private User to;
	private Friendship.Status status;

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}
	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}
}
