import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.reflect.AnnotatedElement;
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
		classNames.remove("ClassFinder");
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		
		Queue<Class<?>> toScan = new LinkedList<Class<?>>();
		for (String str : classNames) {
			try {
				toScan.add(loader.loadClass(str));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
		// get all classes that are subclasses of the already loaded classes.
		
		while (!toScan.isEmpty()) {
			Class<?> current = toScan.poll();
			classes.add(current);
			
			Class<?>[] subs = current.getDeclaredClasses();
			for (Class<?> c : subs ) {
				toScan.add(c);
			}
		}
		
		return classes.toArray(new Class<?>[classes.size()]);
	}
	
	public Method[] findTestMethods(Class<?>[] classes) {
		ArrayList<Method> methods = new ArrayList<Method>();
		
		//check for nested classes
		ConcurrentLinkedQueue<Class<?>> clazzQueue = new ConcurrentLinkedQueue<Class<?>>();
		ArrayList<Class<?>> toRead = new ArrayList<Class<?>>();
		
		for (Class<?> clazz : classes) {
			clazzQueue.add(clazz);
		}
		
		while(!clazzQueue.isEmpty()) {
			Class<?> clazz = clazzQueue.poll();
			// get subclasses
		}
		
		
		
		return methods.toArray(new Method[methods.size()]);
	}
	
	public boolean isClassFile(File f) {
		return f != null && f.getName().toString().split("\\.")[f.getName().toString().split("\\.").length - 1].equals("class");
	}
	

	
	public static void main(String[] args) {
		ClassFinder classFinder = new ClassFinder();
		Class<?>[] classes = classFinder.findLocalClasses();
		Method[] methods = classFinder.findTestMethods(classes);
		System.out.println(methods.length);
		for (Method m : methods) {
			System.out.println(m.getName());
		}		
	}

	@SimpleTest
	public String toString() {
		return "";
	}
}
