package dao;

import model.Usuario;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.*;
import java.math.*;


/**
 * Classe UsuarioDAO que herda a superclasse DAO - Data Access Object
 * Conexao com o BD e' feita pela classe Pai
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao construidas as query's SQL para acesso ao BD
 * 
 * @method construtor
 * @method finalize
 * @method md5
 * @method get
 * @method login
 * @method insert
 * @method update
 * @method confirmarEmail
 * @method mudarSenhaToke
 */
public class UsuarioDAO extends DAO {
	
	private int maxId = 0;
	/**
	 * Construtor padrao que referencia a classe Pai
	 */
	public UsuarioDAO() {
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
	 * Metodo para retornar valor Id atual inserido no banco
	 * @return
	 */
	public int getMaxId() {
		return maxId;
	}

	/**
	 * Metodo de criptografia em Hash md5
	 * 
	 * O tamanho da string gerada e' definida no toString do return
	 * 
	 * @print erro se existir
	 * @param s String a ser criptografada
	 * @return String criptografada ou "erro"
	 */
	public static String md5(String s) {
		try {
			MessageDigest m;
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			return (new BigInteger(1, m.digest()).toString(16));
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		}
		return "erro";
	}

	/**
	 * Metodo GET para retornar os dados do usuario guardados no BD
	 * os dados do usuario serao recuperados atraves do Token de acesso unico do
	 * usuario que e' gerado durante o login
	 * 
	 * @see Usuario.java
	 * @print erro se existir
	 * @param token token unico de acesso do usuario
	 * @return objeto Usuario construido
	 */
	public Usuario get(String token) {
		// Objeto vazio para popular
		Usuario usuario = null;
		try {
			// Conexao
			String sql = "SELECT * FROM usuario WHERE token = ?";

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, token);
			ResultSet rs = stmt.executeQuery();

			// Se receber, popular
			if (rs.next()) {
				usuario = new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getDate("nascimento"),
						rs.getString("nick"), rs.getString("senha"), rs.getString("email"),
						rs.getString("genero"), rs.getString("token"));
			}
			// Fim de conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return usuario;
	}

	/**
	 * Metodo GET para excluir um usuario atraves do seu token e id
	 * 
	 * @print erro se existir
	 * @param token token unico de acesso do usuario
	 * @param id    primary key do usuario
	 * @return objeto Usuario construido
	 */
	public boolean delete(String token, String id) {
		// false ate se provar o contrario
		boolean status = false;
		try {
			// Conexao
			String sql = "DELETE FROM usuario WHERE token = ? AND id = ?";

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, token);
			stmt.setInt(2, Integer.parseInt(id));
			stmt.executeUpdate();

			// Fim de conexao
			stmt.close();
			// Deu tudo certo!
			status = true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return status;
	}

	/**
	 * Metodo LOGIN para comparar se email e senha estao corretos e em seguida
	 * registrar um token de acesso unico no banco de dados
	 * A senha sera criptografada em md5
	 * 
	 * @see md5
	 * @print erro se existir
	 * @param email
	 * @param senha
	 * @param token para validacao do login
	 * @return id do usuario logado
	 */
	public int login(String email, String senha, String token) {
		// Criptografia
		senha = md5(senha);
		// Nao encontrado ate se provar o contrario
		int id = -1;
		try {
			// Conexao
			String sql = "SELECT id FROM usuario WHERE email = ? AND senha = ?";

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, senha);
			ResultSet rs = stmt.executeQuery();

			// Se retornar algo salvar
			if (rs.next()) {
				id = (rs.getInt("id"));
			} else {
				id = -1;
			}
			
			// Se Id for encontrado efetuar o login e registrar o Token de acesso unico
			if (id != -1) {
				// Nova Conexao para registro
				sql = "UPDATE usuario SET token = ? WHERE id = ?";

				stmt = getConnection().prepareStatement(sql);
				stmt.setString(1, token);
				stmt.setInt(2, id);
				stmt.executeQuery();

				// Fim da conexao
			}

			// Fim de conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return id;
	}

	/**
	 * Metodo POST para registrar um novo usuario no Banco de Dados
	 * 
	 * E importante dizer que os dados registrados nao correspondem a todos os dados
	 * existentes do usuario, demais dados devem utilizar o metodo update
	 * 
	 * A senha sera criptografada em md5 para depois o objeto Usuario ser cadastrado
	 * 
	 * @see md5
	 * @print erro se existir
	 * @param nome
	 * @param nick
	 * @param senha sem criptografia
	 * @param email
	 * @return Objeto JSON com o status de constraint do BD
	 */
	public String insert(Usuario usuario) {
		// Objeto JSON com o status da constraint
		String result = "";
		// Criptografia da senha
		usuario.setSenha( md5(usuario.getSenha())); 
		// Gerar Id automaticamente
		this.maxId = (usuario.getId() > this.maxId) ? usuario.getId() : this.maxId;
		try {
			// objetos de conexao
			PreparedStatement stmt;
			String sql;
			ResultSet rs;

			// Validacao de Nick
			sql = "SELECT nick FROM usuario WHERE nick = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, usuario.getNick());
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				result += ("{ \"Usuario\": [{ \"nick\":false , ");
			}else {
				result += ("{ \"Usuario\": [{ \"nick\":true , ");
			}

			// Validacao de Email
			sql = "SELECT email FROM usuario WHERE email = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, usuario.getEmail());
			rs = stmt.executeQuery();

			if (rs.next()) {
				result += ("\"email\":false , ");
			} else {
				result += ("\"email\":true , ");
			}
			

			// Cadastros subsequentes
			// Cadastro na tabela de classificacao
			/*
			sql = "INSERT INTO classificacao_usuario (id_usuario) VALUES ((SELECT id FROM usuario WHERE email = ?))";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, email);
			stmt.execute();
			*/

			// realiza cadastro se validação de email e/ou nick foram realizadas com sucesso 
			if(result.contains("false")) {
				result += ("\"sucesso\":false } ] }");
			}else {
				result += ("\"sucesso\":true } ] }");
				// Insercao propriamente dita
				sql = "INSERT INTO usuario (nome, nick, email, senha, id) VALUES (?, ?, ?, ?, ?)";
				stmt = getConnection().prepareStatement(sql);
				stmt.setString(1, usuario.getNome());
				stmt.setString(2, usuario.getNick());
				stmt.setString(3, usuario.getEmail());
				stmt.setString(4, usuario.getSenha());
				stmt.setInt(5, usuario.getId());
				stmt.execute();
			}
			// Fim de conexao
			stmt.close();
		} catch (Exception e) {
			result += ("\"sucesso\":false } ] }");
			System.err.println(e.getMessage());
		}
		return result;
	}
	

	/**
	 * Metodo UPDATE para atualizar os dados de um usuario no Banco de Dados
	 * 
	 * O metodo utiliza o token de acesso unico do usuario para validar a sua
	 * entrada Mas tambem utiliza o ID, por mesmo que redundante mas para seguranca
	 * da validacao
	 * 
	 * Os dados do usuario a serem atualizado sao recebidos atraves da string 'body'
	 * que sera parseada dentro do construtor da classe Usuario
	 * 
	 * @see Usuario.java
	 * @print erro se existir
	 * @param token token de validacao unico do usuario
	 * @param id
	 * @param body  dados a serem atualizados
	 * @return Objeto JSON com o status de constraint do BD
	 */
	public String update(String token, int id, String body) {
		// Objeto JSON com o status da constraint
		String result = "";
		// Criacao de um usuario a ser populado
		Usuario usuario = new Usuario(body);
		try {
			// Conexao
			PreparedStatement stmt;
			String sql;
			ResultSet rs;

			// Validacao de Nick
			sql = "SELECT nick FROM usuario WHERE nick = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, usuario.getNick());
			rs = stmt.executeQuery();

			if (!rs.next()) {
				result += ("{ \"Usuario\": [{ \"nick\":true , ");
			} else {
				result += ("{ \"Usuario\": [{ \"nick\":false , ");
			}

			// Validacao de Email
			sql = "SELECT email FROM usuario WHERE email = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, usuario.getEmail());
			rs = stmt.executeQuery();

			if (!rs.next()) {
				result += ("\"email\":true , ");
			} else {
				result += ("\"email\":false , ");
			}

			// Update propriamente dito
			sql = "UPDATE usuario SET email = ?, nome = ?, nascimento = ?, nick = ?, sexo = ? ";
			sql += "WHERE id = ? AND token = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, usuario.getEmail());
			stmt.setString(2, usuario.getNome());
			stmt.setDate(3, new Date(usuario.getNascimento().getTime()));
			stmt.setString(4, usuario.getNick());
			stmt.setString(5, usuario.getSexo());
			stmt.setInt(6, id);
			stmt.setString(7, token);
			stmt.executeUpdate();
			
			result += ("\"sucesso\":true } ] }");

			// fim de conexao
			stmt.close();
		} catch (Exception e) {
			result += ("\"sucesso\":false } ] }");
			System.err.println(e.getMessage());
		}
		// limpeza da variavel
		usuario = null;
		return result;
	}

	/**
	 * Metodo para confirmacao de existencia de email para mudanca de senha
	 * Caso o email seja valido sera inserido no Banco de Dados um token de
	 * recuperacao de senha
	 * 
	 * @see mudarSenhaToken
	 * @print erro se exitir
	 * @param token token de recuperacao de senha
	 * @param email email a ser verificado
	 * @return id do usuario a ter a senha alterada
	 */
	public int confirmarEmail(String token, String email) {
		// Usuario nao encontrado ate se provar o contrario
		int id = -1;
		try {
			// Recuperar o ID equivalente ao email e Conexao
			PreparedStatement stmt;
			ResultSet rs;
			String sql = "SELECT id FROM usuario WHERE email = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, email);
			rs = stmt.executeQuery();

			if (rs.next()) {
				id = (rs.getInt("id"));
			} else {
				id = -1;
			}

			// Se o email existir o token de recuperacao de senha sera registrado a conta
			if (id != -1) {
				// Nova conexao
				sql = "UPDATE usuario SET token = ? WHERE id = ?";

				stmt = getConnection().prepareStatement(sql);
				stmt.setString(1, token);
				stmt.setInt(2, id);
				stmt.executeQuery();
			}

			// Fim de conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return id;
	}

	/**
	 * Metodo para efetivar a mudanca de senha do usuario
	 * 
	 * O metodo fara a mudanca de senha atraves da validacao do Token de recuperacao
	 * gerado. A senha entregue sera criptografada em MD5 antes de ser efetivamente
	 * armazenada
	 * 
	 * @see confirmarEmail
	 * @see md5
	 * @print erro se existir
	 * @param token token de recuperacao de senha
	 * @param senha nova senha a ser inserida
	 * @return id do usuario que teve a senha alterada
	 */
	public int mudarSenhaToken(String token, String senha) {
		// Usuario nao encontrado ate se provar o contrario
		int id = -1;
		// criptografia da senha
		senha = md5(senha);
		try {
			// Recuperacao do ID atraves do token gerado e conexao
			PreparedStatement stmt;
			ResultSet rs;
			String sql = "";
			sql += "SELECT id FROM usuario WHERE token = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, token);
			rs = stmt.executeQuery();

			if (rs.next()) {
				id = (rs.getInt("id"));
			} else {
				id = -1;
			}
			// Fim de conexao

			// Efetivar a mudanca de senha
			if (id != -1) {
				// Conexao
				sql = "UPDATE usuario SET senha = ? WHERE id = ?";

				stmt = getConnection().prepareStatement(sql);
				stmt.setString(1, senha);
				stmt.setInt(2, id);
				stmt.executeQuery();
			}

			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return id;
	}
	
}
