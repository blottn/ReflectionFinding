import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.reflect.AnnotatedElement;
public class ClassFinder {

	public Class<?>[] findLocalClasses() {		
		
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		
		String[] uri = url.toString().substring(9).split("/");	//strip initial stuff such as protocol declaration

		uri[0] = "/" + uri[0];
		
		String pathPrefix = uri[0];
		String[] pathSuffixes = new String[uri.length - 1];
		for (int i = 1 ; i < uri.length; i++) {
			pathSuffixes[i - 1] = uri[i];
		}
		
		Path path = Paths.get(pathPrefix, pathSuffixes);
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
		if (classNames.contains("SimpleTest")) {
			classNames.remove("SimpleTest");
		}
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
	
	public Method[] findTestMethods(Class<?>[] classes) {
		ArrayList<Method> methods = new ArrayList<Method>();
		
		for (Class<?> clazz : classes) {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			
			for (Method m : declaredMethods) {
				System.out.println(clazz.getName() + ": " + m.getName());
			}
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
		for (Method m : methods) {
			System.out.println(m.getName());
		}
	}

	@SimpleTest
	public String toString() {
		return "";
	}
}
