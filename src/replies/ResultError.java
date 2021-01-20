package replies;

import java.util.ArrayList;

public class ResultError extends ResultMsg {
	private static final String ERROR = "error";
	
	private String error;
	
	public ResultError() {
		super();
	}

	@Override
	protected void receiveAttributes(ArrayList<NameValue> pairs) {
		this.error = findAttributes(pairs, ERROR);
	}

	@Override
	protected void sendAttributes(ArrayList<NameValue> pairs) {
		pairs.add(new NameValue(ERROR, this.error));
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
