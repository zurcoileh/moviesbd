package main;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Constraint;
import com.db4o.query.Query;


public class DbInterface {	

	//ObjectContainer movies = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "../movies.db4o");	

	ObjectContainer movies;	

	public DbInterface(){	

		EmbeddedConfiguration conf = Db4oEmbedded.newConfiguration();			
		conf.common().objectClass(Movie.class).cascadeOnUpdate(true);
		conf.common().objectClass(Movie.class).cascadeOnDelete(true);
		movies = Db4oEmbedded.openFile(conf, "../moviesbd/database/movies.db4o");	
	}

	//adiciona o filme no banco
	public boolean addMovie(Movie mv){

		ObjectSet<Movie> result = searchId(mv.getIdMovie());		
		if (result.isEmpty()){
			movies.store(mv);
			movies.commit();	
			return true;
		}	
		return false;
	}
	//list movies
	public  ObjectSet<Movie> listMovies() {		

		Query  query = movies.query();
		query.constrain(Movie.class);
		query.descend("title").orderAscending();
		ObjectSet<Movie> result = query.execute();		
		return result;
	}

	//busca pela especificacao
	public ObjectSet<Movie> searchMovieByEsp(Espec esp){

		Query query=movies.query();
		query.descend("esp").constrain(esp);
		ObjectSet<Movie> result = query.execute();
		return result;
	}

	//busca generica
	public ObjectSet<Movie> searchMovie(String type, Object obj){

		Query query=movies.query();
		query.descend(type).constrain(obj);
		ObjectSet<Movie> result = query.execute();
		return result;
	}

	//busca por gender
	public ObjectSet<Movie> searchGender(Gender gender){

		Query query=movies.query();
		query.descend("esp").descend("gender").constrain(gender);
		ObjectSet<Movie> result = query.execute();
		return result;		
	}
	
	//busca por studio
	public ObjectSet<Movie> searchStudio(String studio){

		Query query=movies.query();
		query.descend("esp").descend("studio").constrain(studio);
		ObjectSet<Movie> result = query.execute();
		return result;			
	}	

	//busca pelo id do filme	
	public ObjectSet<Movie> searchId(int id){	

		Query query=movies.query();
		query.descend("idMovie").constrain(id);
		ObjectSet<Movie> result = query.execute();
		return result;	
	}

	//metodo deletar do banco
	public boolean delMovie(int id){

		ObjectSet<Movie> result = searchId(id);

		if(!result.isEmpty()){
			Movie mv = result.next();
			movies.delete(mv);
			movies.commit();
			return true;
		}

		return false;
	}

	//busca por palavra chave
	public  ObjectSet<Movie> searchKeyword(String keyword) {		   

		Query query=movies.query();
		query.constrain(Movie.class);
		//Atributos da busca
		Constraint constr=query.descend("rating").descend("rate").constrain(keyword);
		Constraint constr1=query.descend("esp").descend("studio").constrain(keyword);
		Constraint constr2=query.descend("esp").descend("gender").constrain(Gender.getValueByString(keyword));
		Constraint constr3=query.descend("year").constrain(integerValue(keyword));
		Constraint constr4=query.descend("duration").constrain(integerValue(keyword));

		query.descend("title").constrain(keyword.replaceAll("%20"," ")).like().or(constr).or(constr1).or(constr2).or(constr3).or(constr4);		

		ObjectSet<Movie> result=query.execute();			

		return result;
	}
	//adiciona nota ao filme e atualiza o rating (percentual)
	public boolean addGrade(int id, double grade){

		ObjectSet<Movie> result = searchId(id);

		if(!result.isEmpty()){
			Movie found = result.next();
			double newgrade;
			found.getRating().setQtVotes(found.getRating().getQtVotes() + 1);
			found.getRating().setGrade((found.getRating().getGrade() + grade ));
			newgrade =  found.getRating().getGrade()/found.getRating().getQtVotes();						
			found.getRating().setRate(Math.round(newgrade) + "");
			movies.store(found);
			movies.commit();
			return true;
		}
		return false;
	}	
	
	//atualiza os campos	
	public boolean editMovie(Movie mv){

		ObjectSet<Movie> result = searchId(mv.getIdMovie());

		String title = mv.getTitle().replaceAll("%20"," ");

		if(!result.isEmpty()){
			Movie found = result.next();
			found.setDuration(mv.getDuration());
			found.setTitle(title);
			found.setYear(mv.getYear());
			found.setEsp(mv.getEsp());
			found.setRating(mv.getRating());	
			movies.store(found);
			movies.commit();	
			return true;
		}
		return false;
	}	
	
	//busca por palavra usando metodo contains da classe Movie
	public ObjectSet<Movie> searchKey(String keyword) {
		
		Query query=movies.query();
		query.constrain(Movie.class);		
		query.descend("idMovie");	

		ObjectSet<Movie> result=query.execute();		

		for (Movie mv : result){
			if (!mv.contains(keyword)) result.remove(mv);
		}

		return result;
	}
	
	//retorna ultimo index da tabela
	public int getLastIndex(){
		int last = 0;
		Query  query = movies.query();
		query.constrain(Movie.class);
		query.descend("idMovie").orderDescending();
		ObjectSet<Movie> result = query.execute();			
		if (!result.isEmpty()){
			Movie mv = result.next();
			last = mv.getIdMovie();
		}
		return last;
	}

	//metodo para listar os filmes
	public String toString(){

		ObjectSet<Movie> search = listMovies();
		String result = "";
		for (Movie m: search){			
			result += "\n"+ m.toString();
		}

		if(result.isEmpty())
			result = "listagem vazia!";
		return result;
	}

	public int integerValue(String s){		
		int num;
		try{
			num = Integer.parseInt(s);
			// is an integer!
		} catch (NumberFormatException e) {
			// not an integer!
			num = 0;
		}
		return num;
	}

}
