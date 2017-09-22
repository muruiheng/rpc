package junit;


import org.junit.Test;

public class BinaryTest {

	@Test
	public void test() {
		
		Integer data = new Integer(2);
		
		System.out.println(Integer.toBinaryString(data));
	}
}
