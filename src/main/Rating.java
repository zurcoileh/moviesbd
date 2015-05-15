package main;

public class Rating {
	
	private String rate;
	private double grade;
	private int qtVotes;
	
	public Rating(String rt) {
		this.rate = rt;
		this.grade = 0;
		this.qtVotes = 0;
	}
	
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public double getGrade() {
		return grade;
	}
	public void setGrade(double grade) {
		this.grade = grade;
	}
	public int getQtVotes() {
		return qtVotes;
	}
	public void setQtVotes(int qtVotes) {
		this.qtVotes = qtVotes;
	}
	
	public String toString(){
		return rate;
	}
	

}
