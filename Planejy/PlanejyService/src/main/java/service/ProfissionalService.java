package service;

import dao.ProfissionalDAO;
import model.Profissional;
import spark.Request;
import spark.Response;

/**
 * Servico de tratamento de response/request para get/post do objeto
 * Profissional
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao feitas as chamadas das DAO'S, redirecionamentos dos pedidos e
 * retorno com status de resposta
 * 
 * @method getAll
 */
public class ProfissionalService {

	private ProfissionalDAO profissionalDAO = new ProfissionalDAO();
	private String respostaJSON;

	/**
	 * Construtor padrao
	 */
	public ProfissionalService() {
	}

	public Object getAll(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		Profissional profissional = (Profissional) profissionalDAO.getAll(tokenUsuario);

		if (profissional != null) {
			response.status(200); // success
			respostaJSON = "";
			respostaJSON += "{ \"Profissional\": [";
			respostaJSON += profissional.toJson();
			respostaJSON += " ] }";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "Profissionais não encontrados.";
		}

		return respostaJSON;
	}

	public Object getNth(Request request, Response response){
		String tokenUsuario = request.params(":tokenUsuario");
		int chave = Integer.parseInt(request.params(":chave"));
		Profissional profissional = (Profissional) profissionalDAO.getNth(tokenUsuario, chave);

		if(profissional != null){
			response.status(200);
			respostaJSON = "";
			respostaJSON += "{ \"Profissional\": [";
			respostaJSON += profissional.toJson();
			respostaJSON += " ] }";
		}else{
			response.status(404);
			respostaJSON = "Profissional não encontrado";
		}

		return respostaJSON;
	}

	/**
	 * Metodo POST para avaliar a classificacao de um profissional
	 * 
	 * Utiliza o metodo profissionalDAO.avaliar()
	 * 
	 * @see ProfissionalDAO.java
	 * @request tokenUsuario
	 * @request registro_profissional
	 * @request nota
	 * @response200 avaliado
	 * @response404 erro encontrado
	 * @return Mensagem a ser exibida
	 */
	public Object avaliar(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		int registro_profissional = Integer.parseInt(request.params(":registro_profissional"));
		int nota = Integer.parseInt(request.params(":nota"));

		boolean result = profissionalDAO.avaliar(tokenUsuario, registro_profissional, nota);

		if (result) {
			response.status(200); // success
			respostaJSON = "avaliado com sucesso";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "erro ao avaliar";
		}

		return respostaJSON;
	}

	public Object getAvaliacao(Request request, Response response) {
		int registro_profissional = Integer.parseInt(request.params(":registro_profissional"));
		double nota = profissionalDAO.getAvaliacao(registro_profissional)[0];

		if (nota > 0) {
			response.status(200); // success
			respostaJSON = "";
			respostaJSON += " { \"Profissional\": [";
			respostaJSON += " { ";
			respostaJSON += " \"registro_profissional\": " + registro_profissional + ", ";
			respostaJSON += " \"nota\": " + nota;
			respostaJSON += " } ";
			respostaJSON += " ] }";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "Profissional não encontrados.";
		}

		return respostaJSON;
	}
}