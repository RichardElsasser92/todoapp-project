package replies;

public class ResultLogin extends ResultMsg {
	private String reply;

	public void setRep() {
		result = reply + "|" + token;

	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

}
