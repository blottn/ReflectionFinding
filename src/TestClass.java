
public class TestClass {
	
	public class SubClass {
		
		public SubClass() {
			
		}
		
		public boolean method() {
			System.out.println("Called the sub class method");
			return true;
		}
	}
	
	
	public TestClass() {}
	
	@SimpleTest
	public boolean theAnnotatedMethod() {
		System.out.println("Called me!");
		return true;
	}
	
	
	@SimpleTest
	public static boolean anotherTest() {
		System.out.println("The second test was called");
		return false;
	}
	
	@SimpleTest
	public boolean somethingElse() {
		System.out.println("Third one");
		return true;
	}
	
	public void notAnnotated() {
		
	}
}
