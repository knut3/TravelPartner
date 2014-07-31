package models.view;

import models.User;

public class UserViewModel {

	public Long id;
	public String firstName;
	public String gender;
	public String profilePictureId;
	public float latitude;
	public float longitude;
	public boolean isFriend;
	
	public UserViewModel(User user) {
		this.id = user.id;
		this.firstName = user.firstName;
		this.gender = user.gender;
		this.profilePictureId = user.profilePicture.id;
		this.latitude = user.currentLocation.latitude;
		this.longitude = user.currentLocation.longitude;
		this.isFriend = false;
	}
	
	
}
