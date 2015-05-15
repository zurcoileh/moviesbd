package main;
/*
* Copyright 2014 Giuliano Bertoti 
* Released under the MIT license 
* github.com/giulianobertoti
*/

import static spark.Spark.get;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spark.Request;
import spark.Response;
import spark.Route;

import com.db4o.ObjectSet;

public class Rest {
	
	private DbInterface db;
	
	public Rest(){
		
		db = new DbInterface();		
	}
	// metodo q retorna JSON com lista de atributos do objeto Movie
	private JSONArray jsonListMovie(ObjectSet<Movie> result, Response response){
		
		//allows everyone to access the resource
        response.header("Access-Control-Allow-Origin", "*");
		
		JSONArray jsonResult = new JSONArray();
		
		for(Movie mv: result){ 	    
 	    	JSONObject jsonObj = new JSONObject();
 	    	try {
				jsonObj.put("id", mv.getIdMovie());
				jsonObj.put("title", mv.getTitle());
				jsonObj.put("duration", mv.getDuration());
     	    	jsonObj.put("year", mv.getYear());
     	    	jsonObj.put("gender", mv.getEsp().getGender());
     	    	jsonObj.put("studio", mv.getEsp().getStudio());
     	    	jsonObj.put("rating", mv.getRating().getRate());
     	    	jsonResult.put(jsonObj);
			} catch (JSONException e) {				
				e.printStackTrace();
			} 	    	
 	    } 	    
 	    return jsonResult;		
	}
	//metodo que retorna um JSON verificando resultado de tentativa de delete ou insercao no banco
	private JSONArray jsonResult(boolean res, Response response){
		
		//allows everyone to access the resource
        response.header("Access-Control-Allow-Origin", "*");
        
		JSONObject jsonObj = new JSONObject();		
		JSONArray jsonResult = new JSONArray();	
    	try {    		
    		if (res)    			
    			    jsonObj.put("result", "sucess");
    			else
    				jsonObj.put("result", "fail");    		  		
 	    	jsonResult.put(jsonObj);
		} catch (JSONException e) {					
			e.printStackTrace();
		}	     
 	    return jsonResult;		
	}
	
	public void getMovie() throws JSONException{
		
		//busca por palavra chave
		get(new Route("/movies/search/key/:keyword") {
	         @Override
	         public Object handle(Request request, Response response) {   
	        	 
	     	      return jsonListMovie(db.searchKeyword((request.params(":keyword"))),response);	     	    
	         }
	      });
		
		//listas todos elementos
		get(new Route("/movies/list") {
	         @Override
	         public Object handle(Request request, Response response) {		        	
	     	    
	        	 return jsonListMovie(db.listMovies(),response);	     	    
	         }
	      });		
		
		//busca pelo ID		
		get(new Route("/movies/search/id/:id") {
			  @Override
			  public Object handle(Request request, Response response) {
			
				  return jsonListMovie(db.searchId(Integer.valueOf(request.params(":id"))),response);			     	    
	        }
	    });			
		
		//busca pela especificacao
		get(new Route("/movies/search/spec/:gender/:studio") {
	         @Override
	         public Object handle(Request request, Response response) {	 	     		 
	        		        	 
	        	 return jsonListMovie(db.searchMovieByEsp(new Espec(Gender.valueOf(Gender.class, request.params(":gender")), request.params(":studio"))),response);	     	 
	     	 }
	      });
		//busca pelo genero		
		get(new Route("/movies/search/gender/:gender") {
			 @Override
			  public Object handle(Request request, Response response) {	 	     		 
						        	 
				 return jsonListMovie(db.searchGender(Gender.valueOf(Gender.class, request.params(":gender"))),response);	     	 
			     		     	    
			   }
	    });		
		//busca pelo studio		
		get(new Route("/movies/search/studio/:studio") {
			 @Override
			 public Object handle(Request request, Response response) {	 					  
							        	 
				 return jsonListMovie(db.searchStudio(request.params(":studio")),response);	     	 
					     		     	    
		   }
	  });			
		
		//adiciona filme ao banco
		get(new Route("/movies/save/:id/:title/:duration/:year/:gender/:studio/:rating") {
	         @Override
	         public Object handle(Request request, Response response) {	     	   
		     	        	
	        	//ex movies/add/121/insurgent/160/2014/adventure/warnner  	             	     
	     	    return jsonResult(db.addMovie(new Movie(Integer.valueOf(request.params(":id")), Double.valueOf(request.params(":duration")),
						  request.params(":title"), Integer.valueOf(request.params(":year")),
						  new Espec(Gender.getValueByString(request.params(":gender")), request.params(":studio")), new Rating("0"))),response);
       	     }
	    });
		
		//atualiza dados de um filme
	
		get(new Route("/movies/update/:id/:title/:duration/:year/:gender/:studio/:rating") {
			 @Override
			 public Object handle(Request request, Response response) {				 
			    	
			      return jsonResult(db.editMovie(new Movie(Integer.valueOf(request.params(":id")), Double.valueOf(request.params(":duration")),
								  request.params(":title"), Integer.valueOf(request.params(":year")),
								  new Espec(Gender.getValueByString(request.params(":gender")), request.params(":studio")), new Rating(request.params(":rating")))),response);
		     }
	    });
		
		//delete pelo ID		
		get(new Route("/movies/delete/:id") {
		  @Override
	       public Object handle(Request request, Response response) {
				
				 return jsonResult(db.delMovie(Integer.valueOf(request.params(":id"))),response);							     	    
		   }
	   });
		
		//adiciona nota ao filme
		get(new Route("/movies/grade/:id/:grade") {
			 @Override
			 public Object handle(Request request, Response response) {	  				 
			     
				return  jsonResult(db.addGrade(Integer.valueOf(request.params(":id")), Double.valueOf(request.params(":grade"))),response);			     	    
			   
			 }
		});	
		
		//retorna ultimo index atraves de rota
		get(new Route("/movies/last/") {
			@Override
			public Object handle(Request request, Response response) {	  				 
					     
				//allows everyone to access the resource
		        response.header("Access-Control-Allow-Origin", "*");
		        
				JSONObject jsonObj = new JSONObject();		
				JSONArray jsonResult = new JSONArray();	
		    	try {   
		    		   jsonObj.put("result", db.getLastIndex() + 1);		    			
		     	       jsonResult.put(jsonObj);		    	
				} catch (JSONException e) {					
					e.printStackTrace();
				}	     
		 	    return jsonResult;	
     		 }
		});	
		
	}	

	public void initializeStore(){
		
		db.addMovie(new Movie(103,180, "interstellar", 2014, new Espec(Gender.fiction, "warnner"),new Rating("0")));
		db.addMovie(new Movie(105,180, "night at the museum", 2015,new Espec(Gender.adventure, "warnner"),new Rating("0")));
		db.addMovie(new Movie(107,150, "300", 2013,new Espec(Gender.action, "universal"),new Rating("0")));
		db.addMovie(new Movie(109,150, "iroman", 2012,new Espec(Gender.action, "marvel"),new Rating("0")));
		db.addMovie(new Movie(111,180, "birdman", 2015,new Espec(Gender.drama, "warnner"),new Rating("0")));
		db.addMovie(new Movie(113,180, "american sniper", 2015,new Espec(Gender.action, "mgm"),new Rating("0")));
		db.addMovie(new Movie(115,150, "12 years a slave", 2012,new Espec(Gender.drama, "paramount"),new Rating("0")));
		db.addMovie(new Movie(118,150, "the fault in our stars", 2013,new Espec(Gender.romance, "mgm"),new Rating("0")));			
		db.addMovie(new Movie(122,90, "padington", 2014,new Espec(Gender.animation, "pixar"),new Rating("90"))); 

	   // System.out.println(iWeb.getLastIndex());
	}

}
