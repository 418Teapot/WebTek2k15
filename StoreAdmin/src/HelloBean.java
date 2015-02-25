
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class HelloBean implements Serializable {
	private String name; 
	
	public HelloBean(){
		System.out.println("eat a dick - Mark!");
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	private int age;
	public int getAge() { return age; }
	public void setAge(int age) {this.age = age;}
	
	
}
