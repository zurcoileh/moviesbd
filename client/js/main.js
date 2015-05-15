//Verfica entrada de dados pelo textbox de busca e radiobox selecionados e retorna rota de acordo com a selecao
function getData(){		    
	var result = "search/key/"+document.getElementById("keyword").value; //define uma rota para busca por palavra chave 			
	var radio_group = document.getElementsByName("searchkey");	
	for ( i in radio_group){ //itera nos elementos do tipo checkbox e verifica qual foi selecionado
		if (radio_group[i].checked){
			result = "search/"+ radio_group[i].value +"/"+document.getElementById("keyword").value;	//atribui rota busca por elemento da especificacao	
			break;
		}
	}			
	if (document.getElementById("keyword").value == "")  result = "list"; //define rota para listar caso texbox esteja vazio
	return result;
}

//funcao que verifica se um item da tabela foi selecionado pelo checkbox e faz uma busca pelo id desse item em uma nova rota
function getSelectedItem(request,callback){		    
	var checkbox_group = document.getElementsByName("select");	
	for ( i in checkbox_group){ //faz iteracao entre os elementos checkbox da tabela
		if (checkbox_group[i].checked) {	                        				    
			callback(request + checkbox_group[i].id,true); //chamada de uma funcao callback com id selecionado                       					
			$('#bar').show();
			$('#panel').hide();						
			break;
		}
	}				
}  
//funcao que seta os campos da tabela para serem editaveis
function editElements(val){
	var  td_group = document.getElementsByName("info"); //campos editaveis da tabela
	for ( i in td_group){
		td_group[i].setAttribute("contenteditable",val);					
	}
}		

$(document).ready(function(){
	//funcao para busca atraves de uma rota passada como argumento		
	function getDBData(request,edit){
		//clear elements
		document.getElementById("keyword").value = "";	
		document.getElementById("gender").checked = false;	
		document.getElementById("studio").checked = false;                       					   
		//passa a rota como argumento pra funcao que retorna um json
		$.getJSON( "http://127.0.0.1:4567/movies/"+request+"?format=json&jsoncallback=", function(data) {
			if (data.length){ //verifica se json nao retornou vazio			
				var items = [];								
				items.push("<thead><td>MOVIES</td><thead>");
				items.push("<tr><caption><th>title</th><th>duration</th><th>year</th><th>gender</th><th>studio</th><th>rate \%</th><th>select</th></caption></tr>");
				$.each(data, function(key, val){								        
					items.push("<tr>");
					items.push("<td id='" + key + "' name='info' style='display:none'>" + val.id + "</td>");
					items.push("<td name='info' align='left'>"+ val.title + "</td>");
					items.push("<td name='info'>" + val.duration + "</td>");
					items.push("<td name='info'>" + val.year + "</td>");
					items.push("<td name='info'>" + val.gender + "</td>");
					items.push("<td name='info'>" + val.studio + "</td>");
					items.push("<td name='info'>" + val.rating + "</td>");											
					items.push("<td><input type='checkbox' class='tb' name='select' id='"+val.id+"' ></td>");//atribui o id a um elemento chekcbox																	
					items.push("</tr>");  														
				});                                							
				$("#foo").html("");	//limpa o html que havia no div #foo                             								
				$("<table />", {"id": "data","class":"flat-table","align":"center",html: items.join("")}).appendTo("#foo");						
				document.getElementById("save").setAttribute("value","update"); 
				editElements(edit);	//os atributos da tabela passam a ser editaveis							
			}					
		});				
	}	
	//funcao para busca atraves de rota que retorna apenas um resultado para os casos de delete, insercao ou edit
	function requestDB(request,callback){ 
		$.getJSON("http://127.0.0.1:4567/movies/"+request+"?format=json&jsoncallback=", function(data) {			
			callback(data[0].result); //chamda da funcao callback passada como argumento o resultado do getjson						
		}); 			                   				   
	}			
	//funcao para salvar os campos editaveis
	function saveItem(value){                   
		var  td_group = document.getElementsByName("info");//campos da tabela que sao editaveis
		var data = [];
		for ( i in td_group){				    
			data.push(td_group[i].innerHTML)
		}
		var request = value+"/"+data[0]+"/"+data[1]+"/"+data[2]+"/"+data[3]+"/"+data[4]+"/"+data[5]+"/"+data[6];                				
		requestDB(request);//passa uma requisicao e os valores para rota edit no banco 
	}
	//funcao para criacao de uma nova linha na tabela
	function newItem(value){
		var items = [];                                 			 
		items.push("<thead><td>MOVIES</td><thead>");
		items.push("<tr><caption><th>title</th><th>duration</th><th>year</th><th>gender</th><th>studio</th><th>rate \%</th></caption></tr>");
		items.push("<tr><td id='new' name='info' style='display:none'>"+value+"</td>");
		items.push("<td name='info' align='left'>Untitled</td>");
		items.push("<td name='info'>0.0</td>");
		items.push("<td name='info'>2015</td>");
		items.push("<td name='info'>undef</td>");
		items.push("<td name='info'>undef</td>");
		items.push("<td name='info'>0.0</td>");								                  							
		$("#foo").html("");	                             								
		$("<table />", {"id": "data","class":"flat-table","align":"center",html: items.join("")}).appendTo("#foo");	               
		document.getElementById("save").setAttribute("value","save");//atribui valor add para botao save
		editElements(true); //elementos passam a ser editaveis					
	}

	//evento do click botao search
	$("#search").click(function(){	
		getDBData(getData(),false); //passa como argumento a funcao que retorna resultado de acordo com entrada de dados                  					
	});	

	//evento do click na tabela
	$("#foo").click(function(){                 			 
		getSelectedItem("search/id/",getDBData); //busca pelo id do item que foi selecionado na tabela                				
	});  

	//evento do click botao prev
	$("#prev").click(function(){                 		
		getDBData("list");//retorna um busca em branco 			
		$('#bar').hide();	
		$('#panel').show();					
	});	
	//evento do click botao delete
	$("#delete").click(function(){                 
		if (confirm("Are you sure you want to remove the selected item?")) {
			requestDB("delete/"+document.getElementById("0").innerHTML);              				
			$('#bar').hide();
			$('#panel').show();							
			getDBData("list"); 
		} 						
	});	
	//evento do click botao save
	$("#save").click(function(){  
		if (confirm("Are you sure you want to save the data?")) {	
			saveItem(document.getElementById("save").value); //chama funcao salvar passando parametro valor botao para adicionar ou atualizar
			$('#bar').hide();				
			$('#panel').show();	
			getDBData("list"); 
		}             			  
	});
	//evento do click botao novo
	$("#new").click(function(){
		requestDB("last/",newItem); //chama funcao que faz requisicao do ultimo index e passa funcao que cria nova linha na tabela como callback                            		   
		$('#bar').show();
		$('#panel').hide();			   
	});			 
});	