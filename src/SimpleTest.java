import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SimpleTest {
	
	public boolean enabled() default true;
	
}
