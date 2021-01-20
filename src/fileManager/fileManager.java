package fileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.Todo;
import client.Client_View;

public abstract class fileManager {
	
	protected Client_View view;
	protected Logger logger = Logger.getLogger("");
	
	protected String filePath;
	protected static int id;
	
	public fileManager(String filePath) {
		this.filePath = filePath;
	}
	
	public String getPropertySeparator() {
		return "\\|";
	}
	
	public List<Todo> getAll(){
		List<Todo> list = new ArrayList<Todo>();
		
		try {
			Stream<String> lines = loadFileContent();
			lines.forEach(l -> {
				Todo task = fromProperties(l.split(getPropertySeparator()));
				if (task != null) {
					list.add(task);
				}
			});
		} catch (IOException e) {
			logger.warning(e.toString());
		}
		
		logger.info("Tasks loaded: " + list.size());
		
		return list;
	}
	
	protected abstract Todo fromProperties(String[] properties);
	
	public void save(Todo task) {
	
		
		try {
			Stream<String> lines = loadFileContent();
			List<String> list;
			
				list = lines.collect(Collectors.toList());
				list.add(task.toString());
			
			write(list);
		} catch (IOException e) {
			logger.warning("could not save " + task.getClass().getSimpleName() + ": " + task.toString());
		}
	}

	private Stream<String> loadFileContent()  throws IOException{

		return Files.lines(getFile().toPath()).filter(l -> !l.isEmpty());
	}
	
	private File getFile() throws IOException {
		File file = new File(this.filePath);
		file.createNewFile();
		return file;
	}
	
	private void write(List<String> lines) throws IOException {
		Files.write(getFile().toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public void delete(Todo task) {
		String id = task.getId();
		
		try {
			write(loadFileContent()
					.filter(l -> !lineBeginsWithID(l, id))
					.collect(Collectors.toList()));
		} catch (IOException e) {
			logger.warning(e.toString());
		}
	}
	
	private boolean lineBeginsWithID(String line, String id) {
		return line.split(getPropertySeparator())[0].equals(id);
	}
	
}

