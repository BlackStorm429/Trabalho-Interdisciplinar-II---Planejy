package dao;

import model.Nota;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Classe NotaDAO que herda a superclasse DAO - Data Access Object
 * Conexao com o BD e' feita pela classe Pai
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao construidas as query's SQL para acesso ao BD
 * 
 * @method construtor
 * @method finalize
 * @method get (All)
 * @method post
 * @method delete
 * @method update
 */
public class NotaDAO extends DAO {
	/**
	 * Construtor padrao que referencia a classe Pai
	 */
	public NotaDAO() {
		super();
		conectar();
	}

	/**
	 * Fechar conexao com o BD
	 */
	public void finalize() {
		close();
	}

	/**
	 * Metodo GET para retornar todas as Notas referentes a um unico usuario
	 * A selecao do Usuario e' feita atraves do token de acesso unico fornecido
	 * durante o login
	 * 
	 * As notas sao construidas em uma Pilha Simplismente Encadeada contendo um topo
	 * de referencia e vazio
	 * 
	 * @see Nota.java
	 * @print erro se existir
	 * @param chave token unico de acesso do usuario
	 * @return topo da pilha
	 */
	public Nota get(String tokenUsuario) {
		Nota notas = new Nota();
		try {
			// Conexao
			String sql = "SELECT * FROM nota WHERE id_usuario = ( SELECT id FROM usuario WHERE token = ? )";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);
			ResultSet rs = stmt.executeQuery();
			// Para cada linha retornada adicionar uma nota a Pilha
			while (rs.next()) {
				Nota p = new Nota(rs.getInt("chave"), rs.getInt("id_usuario"), rs.getString("titulo"),
						rs.getDate("dia"),
						rs.getString("descricao"), rs.getTime("horario"), rs.getString("categoria"),
						rs.getString("cor"));
				notas.add(p);
			}
			// Fechar conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return notas;
	}

	/**
	 * Metodo POST para adicionar ao Banco de Dados a nota desejada
	 * 
	 * Os dados da nota sao recebidos atraves de uma String body que e' barseada no
	 * construtor da classe. E necessario um token de acesso unico do usuario para
	 * fazer a insercao da nota
	 * 
	 * @see Nota.java
	 * @print erro se existir
	 * @param token token de acesso unico do usuario
	 * @param body  dados da nota para construtor da classe
	 * @return status da query, true se bem sucedido
	 */
	public boolean post(String token, String body) {
		// Falso ate se tornar verdadeiro
		boolean status = false;
		// Criacao da nota atraves do body
		Nota nota = new Nota(body);
		System.out.println(nota.getChave());
		try {
			// ID selecionado atraves do token
			String sql = "";
			sql += "INSERT INTO nota (id_usuario, titulo, dia, descricao, horario, categoria, cor, chave) ";
			sql += "VALUES ((SELECT id FROM usuario WHERE token = ?), ?, ?, ?, ?, ?, ?, ?)";
			// Conexao
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, token);
			stmt.setString(2, nota.getTitulo());
			stmt.setDate(3, new Date(nota.getDia().getTime()));
			stmt.setString(4, nota.getDescricao());
			stmt.setTime(5, Time.valueOf(nota.getHorario())); 
			stmt.setString(6, nota.getCategoria());
			stmt.setString(7, nota.getCor());
			stmt.setLong(8, nota.getChave());
			stmt.executeUpdate();
			// Fechar conexao
			stmt.close();
			// Deu tudo certo!
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		// limpeza da variavel
		nota = null;
		return status;
	}

	/**
	 * metodo DELETE para apagar uma nota do Banco de Dados
	 * 
	 * O metodo utiliza a selecao da nota atraves da chave da nota e a autenticacao
	 * da acao e' feita atraves do token unico de acesso do usuario
	 * 
	 * @print erro se existir
	 * @param tokenUsuario token de acesso unico do usuario
	 * @param chave        primary key da nota
	 * @return status da query, true se bem sucedido
	 */
	public boolean delete(String tokenUsuario, String chave) {
		// Falso ate se tornar verdadeiro
		boolean status = false;
		try {
			// Conexao
			String sql = "DELETE FROM nota WHERE chave = ? AND id_usuario = (SELECT id FROM usuario WHERE token = ?)";
			// Conexao
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setLong(1, Long.parseLong(chave));
			stmt.setString(2, tokenUsuario);
			stmt.execute();
			// Fechar conexao
			stmt.close();
			// Deu tudo certo!
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}

	/**
	 * Metodo UPDATE para atualizar uma nota no Banco de Dados
	 * 
	 * Os dados da nota sao recebidos atraves de uma String body que e' parseada
	 * no construtor da classe. E necessario um token de acesso unico do usuario
	 * para fazer a atualizacao da nota
	 * 
	 * @see Nota.java
	 * @print erro se existir
	 * @param token token de acesso unico do usuario
	 * @param chave primary key da nota
	 * @param body  dados da nota para construtor da classe
	 * @return status da query, true se bem sucedido
	 */
	public boolean update(String token, int chave, String body) {
		// Falso ate se tornar verdadeiro
		boolean status = false;
		// Criacao da nota atraves do body
		Nota nota = new Nota(body, chave);
		try {
			// Id selecionado atraves do token de validacao
			String sql = "";
			sql += "UPDATE nota SET titulo = ?, dia = ?, descricao = ?, horario = ?, categoria = ?, cor = ? ";
			sql += "WHERE chave = ? AND id_usuario = (SELECT id FROM usuario WHERE token = ?)";
		
			// Conexao
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, nota.getTitulo());
			stmt.setDate(2, new Date(nota.getDia().getTime()));
			stmt.setString(3, nota.getDescricao());
			stmt.setTime(4, Time.valueOf(nota.getHorario())); 
			stmt.setString(5, nota.getCategoria());
			stmt.setString(6, nota.getCor());
			stmt.setLong(7, nota.getChave());
			stmt.setString(8, token);
			stmt.executeUpdate();
			// Fechar conexao
			stmt.close();
			// Deu tudo certo!
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		// limpeza da variavel
		nota = null;
		return status;
	}
}
