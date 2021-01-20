package replies;

import messages.Message;

public enum ResultMessageTypes {
	ResultPing, ResultRegister, ResultLogin, ResultChangePassword, ResultCreateTodo, ResultListToDo, ResultGetToDo, ResultDeleteToDo, ResultLogout, ResultError;

	public static ResultMessageTypes parseType(String typeName) {
		ResultMessageTypes type = ResultMessageTypes.ResultError;
		for (ResultMessageTypes value : ResultMessageTypes.values()) {
			if (value.toString().equals(typeName))
				type = value;
		}
		return type;
	}

	public static ResultMessageTypes getType(Message msg) {
		ResultMessageTypes type = ResultMessageTypes.ResultError;
		if (msg instanceof ResultPing)
			type = ResultPing;
		else if (msg instanceof ResultRegister)
			type = ResultRegister;
		else if (msg instanceof ResultLogin)
			type = ResultLogin;
		else if (msg instanceof ResultChangePassword)
			type = ResultChangePassword;
		else if (msg instanceof ResultCreateToDo)
			type = ResultCreateTodo;
		else if (msg instanceof ResultGetToDo)
			type = ResultGetToDo;
		else if (msg instanceof ResultListToDo)
			type = ResultListToDo;
		else if (msg instanceof ResultDeleteToDo)
			type = ResultDeleteToDo;
		else if (msg instanceof ResultLogout)
			type = ResultLogout;
		return type;

	}
}
