import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


public class AnnotationExample {

  
  public static void main(String[] args) {
    // generates compile-time error because the param for trim() cannot be null
    System.out.println(trim(null)); 
     
    // generates compile-time error because we have not checked whether the return value is null before calling a method on it
    String name = getNameById(1);
    System.out.println(name.trim()); 
 
    // generates compile-time error because all params for getFullName() cannot be null by default
    System.out.println(getFullName(null, null));
    
    // does not generate an error because the param for greeting is allowed to be null.
    System.out.println(greeting(null));
  }

  private static @Nonnull String trim(@Nonnull String s) {
    return requireNonNull(s).trim();
  }
      
  
  private static @CheckForNull String getNameById (int id) {
    if (id == 0) {
      return "Carlos";
    }
    return null;
  }
  
  @ParametersAreNonnullByDefault
  private static @Nonnull String getFullName(String firstName, String lastName) {
    return requireNonNull(firstName) + " " + requireNonNull(lastName);
  }

  private static @Nonnull String greeting (@Nullable String name) {
    // generates compile-time error because we have not checked whether the param is null before calling a method on it
    return "Hello, " + name.trim() + "!";
  }
}
