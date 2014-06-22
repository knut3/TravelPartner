package models.view;

import java.util.ArrayList;
import java.util.List;

public class ConversationDetailsViewModel {

	public long userId;
	public String userName;
	public String userPictureId;
	public List<MessageViewModel> messages = new ArrayList<MessageViewModel>();
	
}
