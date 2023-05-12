package service;

import dao.NotaDAO;
import model.Nota;
import spark.Request;
import spark.Response;

/**
 * Servico de tratamento de response/request para get/post do objeto Artigo
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao feitas as chamadas das DAO'S, redirecionamentos dos pedidos e
 * retorno com status de resposta
 * 
 * @method get
 * @method insert
 * @method delete
 * @method update
 */
public class NotaService {

	private NotaDAO notaDAO = new NotaDAO();
	private String respostaJSON;

	/**
	 * Construtor padrao
	 */
	public NotaService() {
	}

	/**
	 * Metodo GET para responder com um JSON contendo a Nota requisitada
	 * 
	 * Utiliza o metodo notaDAO.get(tokenUsuario)
	 * 
	 * @see Nota.java
	 * @see notaDAO.java
	 * @request tokenUsuario
	 * @response200 JSON (com objeto)
	 * @response404 nao encontrado
	 * @return JSON com notas ou Erro
	 */
	public Object get(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		Nota nota = (Nota) notaDAO.get(tokenUsuario);

		if (nota != null) {
			response.status(200); // success
			respostaJSON = "";
			respostaJSON += "{ \"Notas\": [";
			respostaJSON += nota.toJson();
			respostaJSON += " ] }";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "Usuario " + tokenUsuario + " não encontrado.";
		}

		return respostaJSON;
	}

	/**
	 * Metodo POST para inserir uma nota no Banco de Dados
	 * 
	 * Utiliza o metodo notaDAO.post(tokenUsuario, body)
	 * 
	 * @see Nota.java
	 * @see notaDAO.java
	 * @body idUsuario;titulo;dia;descricao;horario;categoria;cor
	 * @request tokenUsuario
	 * @request body
	 * @response200 bem sucedido
	 * @response404 mal sucedido
	 * @return String com resposta
	 */
	public Object insert(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		String body = request.body();
		boolean result = notaDAO.post(tokenUsuario, body);

		if (result) {
			response.status(200); // success
			respostaJSON = "bem sucedido! :D";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "mal sucedido! >:(";
		}

		return respostaJSON;
	}

	/**
	 * Metodo GET para deletar uma nota no Banco de Dados
	 * 
	 * Utiliza o metodo notaDAO.delete(tokenUsuario, chave)
	 * 
	 * @see Nota.java
	 * @see notaDAO.java
	 * @request tokenUsuario
	 * @request chave
	 * @response200 bem sucedido
	 * @response404 mal sucedido
	 * @return String com resposta
	 */
	public Object delete(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		String chave = request.params(":chave");
		boolean result = notaDAO.delete(tokenUsuario, chave);

		if (result) {
			response.status(200); // success
			respostaJSON = "bem sucedido! :D";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "mal sucedido! >:(";
		}

		return respostaJSON;
	}

	/**
	 * Metodo POST para atualizar uma nota no Banco de Dados
	 * 
	 * Utiliza o metodo notaDAO.update(tokenUsuario, chave, body)
	 * 
	 * @see Nota.java
	 * @see notaDAO.java
	 * @body idUsuario;titulo;dia;descricao;horario;categoria;cor
	 * @request tokenUsuario
	 * @request chave
	 * @response200 bem sucedido
	 * @response404 mal sucedido
	 * @return String com resposta
	 */
	public Object update(Request request, Response response) {
		String tokenUsuario = request.params(":tokenUsuario");
		long chave = Long.parseLong(request.params(":chave"));
		String body = request.body();
		boolean result = notaDAO.update(tokenUsuario, chave, body);

		if (result) {
			response.status(200); // success
			respostaJSON = "bem sucedido! :D";
		} else {
			response.status(404); // 404 Not found
			respostaJSON = "mal sucedido! >:(";
		}

		return respostaJSON;
	}
}