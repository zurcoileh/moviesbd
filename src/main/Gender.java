package main;

public enum Gender {
	
	action, adventure, fiction, horror, drama, romance, animation;
	
	public static Gender getValueByString(String value){
		for(Gender brand: Gender.values() ){
			if(value.equalsIgnoreCase(brand.toString())){
				return brand;
			}
		}
		return null;
	}

}
