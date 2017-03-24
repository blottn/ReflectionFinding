import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClassFinder {

	public Class<?>[] findLocalClasses() {		
		
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		
		Path path = Paths.get("/workspace", "ClassFinder", "bin");
		File root = path.toFile();
		
		ArrayList<String> classNames = new ArrayList<String>();
		
		
		//find all classNames in the current protected domains directory
		
		Queue<File> toRead = new ConcurrentLinkedQueue<File>();
		toRead.add(root);
		while (!toRead.isEmpty()) {
			File current = toRead.poll();
			if (current.isFile() && isClassFile(current)) {
				String classRemoved = current.getPath().substring(0, current.getPath().length() - 6);
				classNames.add(classRemoved.substring(path.toString().length() + 1).replace("\\", "."));
			}
			else if (current.isDirectory()){
				File[] subs = current.listFiles();
				for (File f : subs) {
					toRead.add(f);
				}
			}
		}
		
		//don't need to load ourselves
		classNames.remove("ClassFinder.class");
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		
		for (String str : classNames) {
			try {
				classes.add(loader.loadClass(str));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return classes.toArray(new Class<?>[classes.size()]);
	}
	
	
	public boolean isClassFile(File f) {
		return f != null && f.getName().toString().split("\\.")[f.getName().toString().split("\\.").length - 1].equals("class");
	}
	
	public static void main(String[] args) {
		Class<?>[] classes = new ClassFinder().findLocalClasses();
		for (Class<?> clazz : classes) {
			System.out.println(clazz.getName());
		}
	}
	
}
