import java.io.File;
import java.lang.annotation.Annotation;
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
		classNames.remove("ClassFinder");
		
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
			Annotation[] clazzAnnotations = clazz.getDeclaredAnnotations();
			
			int i = 0;
			while (i < clazzAnnotations.length && clazzAnnotations[i].annotationType().equals(SimpleTest.class)) { i++; }
			
			if (i < clazzAnnotations.length) {
				for (Method m : clazz.getDeclaredMethods()) {
					methods.add(m);
				}
			}
			else {
				for (Method m : clazz.getDeclaredMethods()) {
					boolean hasAnnotation = false;
					System.out.println(m.toGenericString());
					for (Annotation a : m.getAnnotations()) {
						if (a.annotationType().equals(SimpleTest.class)) {
							hasAnnotation = true;
						}
					}
					if (hasAnnotation)
						methods.add(m);
				}
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
		System.out.println(methods.length);
		for (Method m : methods) {
			System.out.println(m.getName());
		}
	}
	
}
