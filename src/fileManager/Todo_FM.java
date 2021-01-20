package fileManager;

import application.Task_dueDate;

public class Todo_FM extends fileManager {


	@Override
	public String getPropertySeparator() {
		return ";";
	}
	
	public Todo_FM() {
		super("ToDo.txt");
	}

	@Override
	protected Task_dueDate fromProperties(String[] properties) {
		Task_dueDate task = null;
		if(properties.length == 5) {
			task = new Task_dueDate(properties[1],
					properties[2],
					properties[3],
					properties[4]);
			task.setId(properties[0]);
		}
		return task;
	}

}
