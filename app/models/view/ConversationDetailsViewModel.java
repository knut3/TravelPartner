package models.view;

import java.util.ArrayList;
import java.util.List;

public class ConversationDetailsViewModel {

	public long userId;
	public String userName;
	public String userPictureId;
	public int unreadMessageCount;
	public List<MessageViewModel> messages = new ArrayList<MessageViewModel>();
	
	public ConversationDetailsViewModel() {
		unreadMessageCount = 0;
	}
	
}
