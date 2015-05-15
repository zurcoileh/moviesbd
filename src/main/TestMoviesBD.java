package main;
import org.json.JSONException;

public class TestMoviesBD {

	public static void main(String[] args) throws JSONException {
		
		final Rest rest = new Rest();
		
		rest.initializeStore();
		
		rest.getMovie();

	}

}
