package model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderTest {

	@Test
	private void testOrderObject() {
		Order order = new Order(34, "Timberglen Rd", false);
		System.out.println(order.getAddress());
		Assert.assertEquals(order.getAddress(), "Timberglen Rd");
	}
	
	@Test
	private void testGetInstanceWithDate() {
		Order order = Order.getInstance("03/17/2018");
		System.out.println(order.getHoaMeetingDate());
		System.out.println(order.getDaysToDeadline());
		Assert.assertEquals(order.getDaysToDeadline(), 2);
	}
	
}
