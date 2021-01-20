package application;

import java.time.LocalDate;


public class Task_dueDate extends Todo {
	private LocalDate date;

	public Task_dueDate(String userId, String titel, String priority, String description) {
		super(userId, titel, priority, description);
		//this.date = date;
	}

}
