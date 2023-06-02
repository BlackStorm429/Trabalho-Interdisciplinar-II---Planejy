package model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.text.ParseException;

/**
 * Objeto Nota para ser usado de referencia para populacao do Banco de Dados
 * ou para construcao de JSON para o frontend
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao feitos os gets e construcao de JSON's
 * 
 * @method construtor de objeto vazio
 * @method construtor de objeto atraves de parse
 * @method construtor de objeto atraves de parse + chave
 * @method construtor de objeto preenchido
 * @method hasnext/add (para pilha simplismente encadeada)
 * @method toJson
 */
public class Nota {
    private int chave;
    private int idUsuario;
    private String titulo;
    private Date dia;
    private String descricao;
    private LocalTime horario;
    private String categoria;
    private String cor;
    private Nota next;
    private SimpleDateFormat diaFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Construtor padrao
     */
    public Nota() {
        this.chave = geradorAleatorio();
        this.idUsuario = -1;
        this.titulo = "";
        this.dia = new Date();
        this.descricao = "";
        this.horario = LocalTime.now();
        this.categoria = "";
        this.cor = "";
        this.next = null;
    }

    /**
     * Construtor atraves de parse
     * Este construtor nao salva a chave da nota
     * 
     * @param body corpo sem a chave
     */
    public Nota(String body) {
        String tmp[] = body.split(";");

        this.chave = geradorAleatorio();
        this.idUsuario = Integer.parseInt(tmp[0]);
        this.titulo = tmp[1];
        try {
            this.dia = diaFormat.parse(tmp[2]);
        } catch (ParseException e) {
        }
        this.descricao = tmp[3];
        this.horario = LocalTime.parse(tmp[4], DateTimeFormatter.ofPattern("HH:mm"));
        this.categoria = tmp[5];
        this.cor = tmp[6];
        this.next = null;
    }

    /**
     * Construtor atraves de parse
     * Chave deve ser enviada separadamente
     * 
     * @param body  corpo sem a chave
     * @param chave primary key
     */
    public Nota(String body, int chave) {
        String tmp[] = body.split(";");

        this.chave = chave;
        this.idUsuario = Integer.parseInt(tmp[0]);
        this.titulo = tmp[1];
        try {
            this.dia = diaFormat.parse(tmp[2]);
        } catch (ParseException e) {
        }
        this.descricao = tmp[3];
        this.horario = LocalTime.parse(tmp[4], DateTimeFormatter.ofPattern("HH:mm"));
        this.categoria = tmp[5];
        this.cor = tmp[6];
        this.next = null;
    }

    /**
     * Construtor populado
     * 
     * @param chave
     * @param idUsuario
     * @param titulo
     * @param dia
     * @param descricao
     * @param time
     * @param categoria
     * @param cor
     */
    public Nota(int chave, int idUsuario, String titulo, Date dia, String descricao, Time time,
            String categoria, String cor) {
        this.chave = chave;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.dia = dia;
        this.descricao = descricao;
        this.horario = time.toLocalTime();
        this.categoria = categoria;
        this.cor = cor;
        this.next = null;
    }

    public int getChave() {
        return this.chave;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Date getDia() {
        return this.dia;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public LocalTime getHorario() {
        return this.horario;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public String getCor() {
        return this.cor;
    }

    public Nota getNext() {
        return this.next;
    }
    
    
	private int geradorAleatorio() {
		Random n = new Random();
		int aleatorio = n.nextInt();
    	return (aleatorio < 0)?(aleatorio = aleatorio*-1):aleatorio;
    }

    /**
     * Metodo para lista encadeada
     * 
     * @return true se next nao for null
     */
    private boolean hasNext() {
        boolean has = true;
        if (this.next == null) {
            has = false;
        }
        return has;
    }

    /**
     * Metodo para construcao da Pilha encadeada
     * 
     * @param novo Artigo a ser inserido
     */
    public void add(Nota nova) {
        nova.next = this.next;
        this.next = nova;
    }

    /**
     * Metodo para geracao de arquivo JSON contendo os dados da nota
     * 
     * @return JSON sem o objeto pai
     */
    public String toJson() {
        String Json = "";
        Nota last = this;

        while (last.hasNext()) {
            last = last.next;
            Json += "{ ";
            Json += "\"id\":" + last.chave + ", \"idUsuario\":" + last.idUsuario + ", \"title\":\"" + last.titulo
                    + "\", \"start\":\"" + last.dia + "\", \"description\":\"" + last.descricao + "\", \"horario\":\""
                    + last.horario + "\", \"categoria\":\"" + last.categoria + "\", \"color\":\"" + last.cor + "\"";
            Json += "}";
            if (last.hasNext()) {
                Json += ",";
            }
        }

        // limpeza da variavel
        last = null;
        return Json;
    }
}
