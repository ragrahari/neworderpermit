package model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Random random = new Random();
	
	private Integer id;
	private String address;
	private Boolean isHOAMember;
	private Boolean isApproved;
	private Date hoaMeetingDate;
	private int daysToDeadline;
	
	Order(){
	}
	
	Order(Integer id, String address, Boolean isHOAMember){
		this.id = id;
		this.address = address;
		this.isHOAMember = isHOAMember;
	}
	
	public static Order getInstance() {
		Order order = new Order();
		order.setId(random.nextInt());
		order.setApproved(false);
		return order;
	}
	
	public static Order getInstance(String hoaMeetingDate) {
		Order order = new Order();
		order.setId(random.nextInt());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date parsedDate = sdf.parse(hoaMeetingDate);
			order.setHoaMeetingDate(parsedDate);
		}catch(ParseException e) {
			System.out.println("Exception occurred while parsing hoa meeting date");
		}
		// set daysToDeadlin
		Date currentDate = new Date();
		Date salesDeadline = order.getHoaMeetingDate();
		long daysInMillis = salesDeadline.getTime() - currentDate.getTime();
		int days = (int) TimeUnit.DAYS.convert(daysInMillis, TimeUnit.MILLISECONDS);
		if(days>7) {
			order.setDaysToDeadline(days-7);
		}else {
			order.setDaysToDeadline(0);
		}
		order.setApproved(false);
		return order;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public boolean isApproved() {
		return isApproved;
	}
	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	public Boolean getIsHOAMember() {
		return isHOAMember;
	}
	public void setIsHOAMember(Boolean isHOAMember) {
		this.isHOAMember = isHOAMember;
	}
	public Date getHoaMeetingDate() {
		return hoaMeetingDate;
	}
	public void setHoaMeetingDate(Date hoaMeetingDate) {
		this.hoaMeetingDate = hoaMeetingDate;
	}
	public int getDaysToDeadline() {
		return daysToDeadline;
	}
	private void setDaysToDeadline(int daysToDeadline) {
		this.daysToDeadline = daysToDeadline;
	}
}
