package hello;

public class Main {
	public static void main(String[] args) {
		// System.out.println("Hello Docker from Java!");

		
		String name = System.getenv("HELLO_NAME");
		if (name == null || name.trim().isEmpty()) {
			name = "Docker";
		}
		System.out.println("Hello " + name + " from Java!");
		
	}
}
