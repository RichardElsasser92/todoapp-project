package messages;

import java.util.ArrayList;
import java.util.Random;

import application.Todo;
import messages.Message.NameValue;

public class ListToDos extends Message {

	@Override
	protected void receiveAttributes(ArrayList<NameValue> pairs) {
		this.token = findAttributes(pairs, CLIENT_TOKEN);
	}

	@Override
	protected void sendAttributes(ArrayList<NameValue> pairs) {
		// TODO Auto-generated method stub
		
	}
	
}

