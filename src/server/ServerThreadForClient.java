package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import application.Todo;
import application.Task_dueDate;
import application.User;
import fileManager.fileManager;
import fileManager.Todo_FM;
import fileManager.User_FM;
import messages.Login;
import messages.Message;
import messages.MessageType;
import replies.ResultChangePassword;
import replies.ResultCreateToDo;
import replies.ResultDeleteToDo;
import replies.ResultError;
import replies.ResultGetToDo;
import replies.ResultListToDo;
import replies.ResultLogin;
import replies.ResultLogout;
import replies.ResultPing;
import replies.ResultRegister;

public class ServerThreadForClient extends Thread {
	private final Logger logger = Logger.getLogger("");
	private Socket clientSocket;

	public ServerThreadForClient(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		logger.info(" Request from client " + clientSocket.getInetAddress().toString() + " for server "
				+ clientSocket.getLocalAddress().toString());

		try {
			Message msgIn = Message.receive(clientSocket);
			Message msgOut = processMessage(msgIn);
			logger.info("Message sent: " + msgOut.toString());
			msgOut.send(clientSocket);
		} catch (Exception e) {
			logger.severe(e.toString());
		} finally {
			try {
				if (clientSocket != null)
					clientSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private Message processMessage(Message msgIn) {
		logger.info("Message received from client: " + msgIn.toString());
		// String clientName = msgIn.getClient();

		Message msgOut = null;
		switch (MessageType.getType(msgIn)) {
		case Ping:
			ResultPing repMsg = new ResultPing();
			String messageIn = msgIn.toString();
			if (msgIn.getToken().equals("null")) {
				repMsg.setReply("true");
				repMsg.setRep();
				msgOut = repMsg;
			} else {
				boolean valid = Server_Controller.validToken(msgIn.getToken());
				if (valid) {
					repMsg.setToken(msgIn.getToken());
					repMsg.setReply("true");
					repMsg.setRep();
					msgOut = repMsg;
				} else {
					ResultError errMsg = new ResultError();
					errMsg.setError("Token is not valid!");
					msgOut = errMsg;
				}
			}
			break;
		case Register:
			ArrayList<User> users = new ArrayList<User>();
			users = getAllUsers();
			int numOfUsers = users.size();
			User.setHighestId(numOfUsers);
			ResultRegister regMsg = new ResultRegister();
			regMsg.setResult("true");
			String regMessage = msgIn.toString();
			String[] regSplit = regMessage.split("\\n");
			String[] n = regSplit[2].split("\\|");
			String name = n[1];
			String[] pa = regSplit[3].split("\\|");
			String password = pa[1];
			User user = new User(name, password);

			// check if user already exists
			boolean exists = checkIfUserAlreadyExists(user);
			if (!exists) {
				saveUser(user, true);
				msgOut = regMsg;
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("User already exists! Please chose a new name or password.");
				msgOut = errMsg;
			}
			break;
		case Login:
			ResultLogin logMsg = new ResultLogin();
			regMessage = msgIn.toString();
			regSplit = regMessage.split("\\n");
			n = regSplit[2].split("\\|");
			name = n[1];
			pa = regSplit[3].split("\\|");
			password = pa[1];
			user = new User(name, password);

			exists = checkIfUserAlreadyExists(user);
			String token = null;
			String login = null;
			if (exists) {
				token = Server_Controller.login((Login) msgIn);
				login = "true";
				logMsg.setToken(token);
				logMsg.setReply(login);
				logMsg.setRep();
				msgOut = logMsg;
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("User with username: " + name + " and password: " + password
						+ " does not exist. Please register!");
				msgOut = errMsg;
			}
			break;
		case ChangePassword:
			if (!msgIn.getToken().equals("null")) {
				ResultChangePassword cPMsg = new ResultChangePassword();
				regMessage = msgIn.toString();
				regSplit = regMessage.split("\\n");
				n = regSplit[2].split("\\|");
				name = n[1];
				pa = regSplit[3].split("\\|");
				password = pa[1];
				String[] paN = regSplit[4].split("\\|");
				String passwordN = paN[1];
				String uid = Server_Controller.getUserId(msgIn);
				user = new User(name, passwordN);

				boolean check = changePassword(uid, name, password, passwordN);
				if (check) {
					cPMsg.setResult("true");
					user.setId(uid);
					saveUser(user, false);
					msgOut = cPMsg;
				} else {
					ResultError errMsg = new ResultError();
					errMsg.setError("Password could not be changed. Please check username and password and try again.");
					msgOut = errMsg;
				}
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("Token not valid. Please login again");
				msgOut = errMsg;
			}
			break;
		case CreateToDo:
			if (!msgIn.getToken().equals("null")) {
				ResultCreateToDo tdMsg = new ResultCreateToDo();
				String toDomessage = msgIn.toString();
				String[] split = toDomessage.split("\\n");
				String[] t = split[2].split("\\|");
				String titel = t[1];
				String[] p = split[3].split("\\|");
				String priority = p[1];
				String[] d = split[4].split("\\|");
				String description = d[1];
				String userId = Server_Controller.getUserId(msgIn);
				Todo toDo = new Task_dueDate(userId, titel, priority, description);
				saveTask(toDo);
				String id = toDo.getId();
				tdMsg.setToken(msgIn.getToken());
				tdMsg.setId(id);
				tdMsg.setResult("true" + "|" + id);
				msgOut = tdMsg;
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("Token not valid. Please login again");
				msgOut = errMsg;
			}
			break;
		case GetToDo:
			if (!msgIn.getToken().equals("null")) {
				ResultGetToDo gTdMsg = new ResultGetToDo();
				String[] split = msgIn.toString().split("\\n");
				String[] tdId = split[2].split("\\|");
				String todoId = tdId[1];
				String[] todo = getToDoById(todoId);
				gTdMsg.setResult("true" + "|" + String.join("|", todo));
				msgOut = gTdMsg;
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("Token not valid. Please login again");
				msgOut = errMsg;
			}
			break;
		case DeleteToDo:
			if (!msgIn.getToken().equals("null")) {
				ResultDeleteToDo dMsg = new ResultDeleteToDo();
				String[] split = msgIn.toString().split("\\n");
				String[] tdId = split[2].split("\\|");
				String todoId = tdId[1];
				boolean success = deleteToDoById(todoId);
				if (success) {
					dMsg.setResult("true");
					msgOut = dMsg;
				} else {
					ResultError errMsg = new ResultError();
					errMsg.setError("ToDo could not be deleted");
					msgOut = errMsg;
				}
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("Token not valid. Please login again");
				msgOut = errMsg;
			}
			break;
		case ListToDos:
			if (!msgIn.getToken().equals("null")) {
				ResultListToDo ltMsg = new ResultListToDo();
				String[] split = msgIn.toString().split("\\n");
				String[] ids = getUserToDoIds(msgIn);
				ltMsg.setResult("true" + "|" + String.join("|", ids));
				msgOut = ltMsg;
			} else {
				ResultError errMsg = new ResultError();
				errMsg.setError("Token not valid. Please login again");
				msgOut = errMsg;
			}
			break;
		case Logout:
			ResultLogout loMsg = new ResultLogout();
			Server_Controller.removeToken(msgIn.getToken());
			loMsg.setResult("true");
			msgOut = loMsg;
			break;
		default:
			ResultError err = new ResultError();
			err.setError("We're sorry something went wrong...");
			msgOut = err;
			break;

		}
		return msgOut;

	}

	private boolean changePassword(String id, String name, String password, String passwordN) {
		boolean changed = false;
		List<User> users = getAllUsers();

		for (User u : users) {
			if (u.getId().equals(id) && u.getUsername().equals(name) && u.getPassword().equals(password)) {
				changed = true;
				break;
			}
		}
		return changed;

	}

	private boolean checkIfUserAlreadyExists(User user) {
		boolean exists = false;
		List<User> users = getAllUsers();

		for (User u : users) {
			if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())) {
				exists = true;
			}
		}
		return exists;
	}

	// get all ToDos assigned to the user ID of user
	private String[] getUserToDoIds(Message msg) {
		List<Todo> todos = getAllToDos();
		List<String> userTodoIds = new ArrayList<>();

		String uid = Server_Controller.getUserId(msg);

		for (Todo todo : todos) {
			if (todo.getUserId().equals(uid)) {
				userTodoIds.add(todo.getId());
			}
		}

		return userTodoIds.toArray(new String[0]);
	}

	public String[] getToDoById(String id) {
		List<Todo> todos = getAllToDos();
		List<String> attributes = new ArrayList<>();

		for (Todo todo : todos) {
			if (todo.getId().equals(id)) {
				attributes.add(todo.getId());
				attributes.add(todo.getTitel());
				attributes.add(todo.getPriority());
				attributes.add(todo.getDescription());
				break;
			}
		}
		return attributes.toArray(new String[0]);
	}

	private boolean deleteToDoById(String todoId) {
		boolean deleted = false;
		Todo_FM fileManager = new Todo_FM();
		List<Todo> todos = getAllToDos();
		Todo todo = null;

		for (Todo t : todos) {
			if (t.getId().equals(todoId)) {
				todo = t;
				deleted = true;
				break;
			}
		}

		fileManager.delete(todo);
		return deleted;
	}

	public ArrayList<User> getAllUsers() {
		User_FM fileManager = new User_FM();
		return (ArrayList<User>) fileManager.getAll();
	}

	public ArrayList<Todo> getAllToDos() {
		Todo_FM fileManager = new Todo_FM();
		return (ArrayList<Todo>) fileManager.getAll();
	}

	public void saveUser(User user, boolean isNew) {
		User_FM fileManager = new User_FM();
		fileManager.save(user, isNew);
	}

	public void saveTask(Todo task) {
		fileManager fileManager = new Todo_FM();

		fileManager.save(task);
	}

	public void deleteTask(Todo task) {
		fileManager fileManager = new Todo_FM();
		fileManager.delete(task);
	}

}

