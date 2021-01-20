package replies;

import java.util.ArrayList;

public class ResultPing extends ResultMsg {
	private String reply;

	
	public void setRep() {
		if(token == null) {
			result = reply;
		}
		else if (token != null) {
			result = reply + "|" + token;
		}
		
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
	
	
}
