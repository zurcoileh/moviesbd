package main;

public class Espec {

	private Gender gender;
	private String studio;

	public Espec(Gender gender, String studio) {
		this.gender = gender;
		this.studio = studio;
	}

	public Gender getGender() {
		return gender;
	}

	public String getStudio() {
		return studio;
	}

	public boolean match(Espec espMovie){
		if(gender.equals(espMovie.gender) && studio.equals(espMovie.studio)) return true;
		return false;
	}

	public String toString(){
		return gender+" | "+studio;
	}

}
