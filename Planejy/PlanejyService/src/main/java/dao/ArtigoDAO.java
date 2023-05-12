package dao;

import model.Artigo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe ArtigoDAO que herda a superclasse DAO - Data Access Object
 * Conexao com o BD e' feita pela classe Pai
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao construidas as query's SQL para acesso ao BD
 * 
 * A classe Artigo serve apenas para consulta e nao possui POST
 * 
 * @method construtor
 * @method finalize
 * @method get
 * @method getAll
 */
public class ArtigoDAO extends DAO {
	/**
	 * Construtor padrao que referencia a classe Pai
	 */
	public ArtigoDAO() {
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
	 * Metodo GET para selecionar um artigo atraves de sua CHAVE
	 * 
	 * @see Artigo.java
	 * @print erro se existir
	 * @param chave Chave Primaria da tabela
	 * @return Objeto Artigo contendo os dados necessarios para consulta ou vazio se
	 *         um erro for gerado
	 */
	public Artigo get(int chave, String tokenUsuario) {
		Artigo artigo = null;
		try {
			// Conexao
			PreparedStatement stmt;
			ResultSet rs;
			String sql = "SELECT * FROM artigo WHERE chave = ?";
			stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave);
			rs = stmt.executeQuery();
			// se algo for retornado, chamar o construtor
			if (rs.next()) {
				clique(tokenUsuario, chave);
				double notas[] = getAvaliacao(rs.getInt("chave"));
				artigo = new Artigo(rs.getInt("chave"), rs.getString("imagem"), rs.getString("imagem_alt"),
						rs.getString("titulo"), rs.getString("conteudo"), rs.getString("resumo"),
						rs.getString("autor"), rs.getDate("dia").toLocalDate(),
						notas[0], (int) notas[1],
						getNotaUsuario(tokenUsuario, rs.getInt("chave")));
			}
			// fechar a conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return artigo;
	}

	public Artigo getAll(String tokenUsuario) {
		Artigo artigo = new Artigo();
		try {
			// Conexao
			PreparedStatement stmt;
			ResultSet rs;
			String sql = "";

			// Criar dicionario para traducao de classificacao do artigo
			sql += "SELECT * FROM classificacao_usuario ";
			sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? )";
			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);
			rs = stmt.executeQuery();
			rs.next();
			String nome[] = { "Trabalho", "Estudo", "Faculdade", "Curso", "Vida noturna", "Descanso", "Dia-a-dia",
					"Social", "Social Profissional", "saude" };
			int nota[] = {
					rs.getInt("trabalho"),
					rs.getInt("estudo"),
					rs.getInt("faculdade"),
					rs.getInt("curso"),
					rs.getInt("vida_noturna"),
					rs.getInt("descanso"),
					rs.getInt("dia_a_dia"),
					rs.getInt("social"),
					rs.getInt("social_profissional"),
					rs.getInt("saude")
			};
			Artigo.normalizarNotas(nota, nome);

			// Recuperar registros dos profissionais
			sql = "";
			sql += "SELECT A.*, string_agg(B.tipo, ',') AS classificacao ";
			sql += "FROM artigo AS A ";
			sql += "INNER JOIN tipo_de_usuario_do_artigo AS B ";
			sql += "ON A.chave = B.chave_artigo ";
			sql += "GROUP BY A.chave ";
			sql += "ORDER BY A.chave";
			stmt = getConnection().prepareStatement(sql);
			rs = stmt.executeQuery();

			// Para cada string retornada, adicionar a pilha
			while (rs.next()) {
				double notas[] = getAvaliacao(rs.getInt("chave"));
				Artigo p = new Artigo(rs.getInt("chave"), rs.getString("imagem"), rs.getString("imagem_alt"),
						rs.getString("titulo"), rs.getString("conteudo"), rs.getString("resumo"),
						rs.getString("autor"), rs.getDate("dia").toLocalDate(),
						notas[0], (int) notas[1], rs.getString("classificacao"), usuarioJaEntrou(tokenUsuario, rs.getInt("chave")));
				p.notaFinal(nome, nota);
				artigo.add(p);
			}

			artigo.ordenar();

			// fechar a conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return artigo;
	}

	public double[] getAvaliacao(int chave_artigo) {
		// Inicializacao de valores
		double totalNotas = 0;
		double numNotas = 0;
		double notaFinal = 0;
		try {
			// Conexao
			String sql = "SELECT * FROM Entrega_Artigo WHERE chave_artigo = ? AND avaliacao IS NOT NULL";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave_artigo);
			ResultSet rs = stmt.executeQuery();
			// Para cada registro atualizar valores
			while (rs.next()) {
				totalNotas += rs.getInt("avaliacao");
				numNotas++;
			}
			// Calcular nota
			if (numNotas > 0) {
				notaFinal = totalNotas / numNotas;
			}
			// Fechar conexao
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			notaFinal = -1;
		}
		double result[] = new double[2];
		result[0] = notaFinal;
		result[1] = numNotas;
		return result;
	}

	public int getNotaUsuario(String tokenUsuario, int chave_artigo) {
		// Inicializacao de valores
		int notaUsuario = 0;
		try {
			// Conexao
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql = "";
			sql += "SELECT avaliacao FROM Entrega_Artigo WHERE chave_artigo = ? ";
			sql += "AND id_usuario = (SELECT id FROM usuario WHERE token = ? AND avaliacao is NOT NULL ) ";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave_artigo);
			stmt.setString(2, tokenUsuario);
			ResultSet rs = stmt.executeQuery();
			// Para cada registro atualizar valores
			if (rs.next()) {
				notaUsuario = rs.getInt("avaliacao");
			}
			// Fechar conexao
			st.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return notaUsuario;
	}

	public boolean usuarioJaEntrou(String tokenUsuario, int chave_artigo) {
		// Falso ate se provar o contrario
		boolean jaEntrou = false;;
		try {
			// Conexao
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql = "";
			sql += "SELECT avaliacao FROM Entrega_Artigo WHERE chave_artigo = ? ";
			sql += "AND id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave_artigo);
			stmt.setString(2, tokenUsuario);
			ResultSet rs = stmt.executeQuery();
			// Para cada registro atualizar valores
			if (rs.next()) {
				jaEntrou = true;
			}
			// Fechar conexao
			st.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return jaEntrou;
	}

	public boolean avaliar(String tokenUsuario, int chave, int nota) {
		// False ate se provar o contrario
		boolean status = false;
		try {
			// objetos de conexao
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql;

			sql = "UPDATE Entrega_artigo ";
			sql += "SET avaliacao = ?, cliques = (cliques + 1) ";
			sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";
			sql += "AND chave_artigo = ?" ;

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, nota);
			stmt.setString(2, tokenUsuario);
			stmt.setInt(3, chave);

			// Execucao
			stmt.executeUpdate();

			// Fechar conexao
			st.close();
			stmt.close();
			// Deu tudo certo!
			status = atualizarClassificacaoUsuario(tokenUsuario, chave);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return status;
	}

	public boolean tempo(String tokenUsuario, int chave) {
		// False ate se provar o contrario
		boolean status = false;
		try {
			// objetos de conexao
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql;

			sql = "UPDATE Entrega_artigo ";
			sql += "SET tempo = (tempo + 1) ";
			sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";
			sql += "AND chave_artigo = ?";

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);
			stmt.setInt(2, chave);

			// Execucao
			stmt.executeUpdate();

			// Fechar conexao
			st.close();
			stmt.close();
			// Deu tudo certo!
			status = atualizarClassificacaoUsuario(tokenUsuario, chave);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return status;
	}

	public boolean clique(String tokenUsuario, int chave) {
		// False ate se provar o contrario
		boolean status = false;
		try {
			// objetos de conexao
			PreparedStatement stmt;
			String sql;
			ResultSet rs;

			// Testar se ja existe avaliacao
			boolean existe = true;
			sql = "SELECT * FROM Planejy.Entrega_artigo ";
			sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";
			sql += "AND chave_artigo = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);
			stmt.setInt(2, chave);
			rs = stmt.executeQuery();


			if (!rs.next()) {
				existe = false;
			}

			// Se existir UPDATE, se nao INSERT
			if (existe) {
				sql = "UPDATE Entrega_artigo ";
				sql += "SET cliques = (cliques + 1) ";
				sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";
				sql += "AND chave_artigo = ?";
			} else {
				sql = "INSERT INTO Entrega_artigo (id_usuario, chave_artigo, cliques) ";
				sql += "VALUES ((SELECT id FROM planejy.usuario WHERE token = ? ), ?, 1)";
			}

			// Execucao
			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);
			stmt.setInt(2, chave);

			stmt.executeUpdate();

			// Fechar conexao
			stmt.close();
			// Deu tudo certo!
			status = atualizarClassificacaoUsuario(tokenUsuario, chave);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return status;
	}

	public boolean atualizarClassificacaoUsuario(String tokenUsuario, int chave_artigo) {
		// False ate se provar o contrario
		boolean status = false;
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sql;
			ResultSet rs;

			// Recuperar a quantidade de classificacoes do profissional
			sql = "SELECT count(chave_artigo) FROM tipo_de_usuario_do_artigo ";
			sql += "WHERE chave_artigo = ?";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave_artigo);
			
			rs = stmt.executeQuery();
			rs.next();
			int numClassificacoes = rs.getInt("count");

			// Recuperar a classe das classificacoes/* */
			sql = "SELECT tipo FROM tipo_de_usuario_do_artigo ";
			sql += "WHERE chave_artigo = ?";

			stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, chave_artigo);

			rs = stmt.executeQuery();
			String classificacoes[] = new String[numClassificacoes];
			int i = 0;
			String tmp;
			while (rs.next()) {
				classificacoes[i] = rs.getString("tipo");
				tmp = "";
				for (int j = 0; j < classificacoes[i].length(); j++) {
					if (classificacoes[i].charAt(j) == '-') {
						tmp += "_";
					} else if (classificacoes[i].charAt(j) == ' ') {
						tmp += "_";
					} else {
						tmp += classificacoes[i].charAt(j);
					}
				}
				classificacoes[i] = tmp;
				i++;
			}

			// Atualizar a classificacao do usuario
			sql = "UPDATE classificacao_usuario ";
			sql += "SET ";
			for (i = 0; i < numClassificacoes; i++) {
				sql += classificacoes[i] + " = (" + classificacoes[i] + " + 1)";
				if (i < (numClassificacoes - 1)) {
					sql += ", ";
				} else {
					sql += " ";
				}
			}
			sql += "WHERE id_usuario = (SELECT id FROM usuario WHERE token = ? ) ";

			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, tokenUsuario);

			// Execucao
			stmt.executeUpdate();

			// Fechar conexao
			st.close();
			stmt.close();
			// Deu tudo certo!
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
}