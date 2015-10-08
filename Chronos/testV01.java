import static org.junit.Assert.*;

import org.junit.Test;


public class testV01 {

	@Test
	public void test() {
		Storage store = new Storage();
		ByteArrayInputStream in = new ByteArrayInputStream("a.txt".getBytes());
		System.setIn(in);
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		assertEquals("Your agenda stored in \"a.txt\" is loaded", outContent.toString());
	}
}
