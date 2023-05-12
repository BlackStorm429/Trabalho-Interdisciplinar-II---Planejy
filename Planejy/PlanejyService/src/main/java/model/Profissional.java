package model;

import java.util.Random;

/**
 * Objeto Profissional para ser usado de referencia para populacao do Banco de
 * Dados ou para construcao de JSON para o frontend
 * 
 * Hierarquia de chamada: Aplicacao -> Service -> DAO -> Model
 * Aqui sao feitos os gets e construcao de JSON's
 * 
 * @method construtor de objeto vazio
 * @method construtor de objeto preenchido
 * @method hasnext/add (para pilha simplismente encadeada)
 * @method toJson
 */
public class Profissional {
    private int registro;
    private String nome;
    private String servico;
    private Float preco;
    private String foto;
    private String facebook;
    private String twitter;
    private String instagram;
    private String linkedin;
    private double nota;
    private int numNotas;
    private int notaUsuario;
    private String classificacao[];
    private double classificacaoFinal;
    private boolean premium;
    private Profissional next;

    /**
     * Construtor padrao
     */
    public Profissional() {
        this.registro = -1;
        this.nome = "";
        this.servico = "";
        this.preco = (float) -1.0;
        this.foto = "";
        this.facebook = "";
        this.twitter = "";
        this.instagram = "";
        this.linkedin = "";
        this.nota = 0;
        this.numNotas = 0;
        this.notaUsuario = 0;
        this.classificacao = null;
        this.classificacaoFinal = 0;
        this.premium = false;
        this.next = null;
    }

    public Profissional(int registro, String nome, String servico, Float preco, String foto, String facebook,
            String twitter, String instagram, String linkedin, double nota, int numNotas, int notaUsuario,
            String classificacao) {
        this.registro = registro;
        this.nome = nome;
        this.servico = servico;
        this.preco = preco;
        this.foto = foto;
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.linkedin = linkedin;
        this.nota = nota;
        this.numNotas = numNotas;
        this.notaUsuario = notaUsuario;
        this.classificacao = classificacao.split(",");
        this.classificacaoFinal = 0;
        this.premium = false;
        this.next = null;
    }

    public int getRegistro() {
        return this.registro;
    }

    public String getNome() {
        return this.nome;
    }

    public String getServico() {
        return this.servico;
    }

    public Float getPreco() {
        return this.preco;
    }

    public String getFoto() {
        return this.foto;
    }

    public String getFacebook() {
        return this.facebook;
    }

    public String getTwitter() {
        return this.twitter;
    }

    public String getInstagram() {
        return this.instagram;
    }

    public String getLinkedin() {
        return this.linkedin;
    }

    public Double getNota() {
        return this.nota;
    }

    public int getNumNotas() {
        return this.numNotas;
    }

    public int getNotaUsuario() {
        return this.notaUsuario;
    }

    public String[] getClassificacao() {
        return this.classificacao;
    }

    public double getClassificacaoFinal() {
        return this.classificacaoFinal;
    }

    public boolean getPremium() {
        return this.premium;
    }

    public Profissional getNext() {
        return this.next;
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
    public void add(Profissional novo) {
        novo.next = this.next;
        this.next = novo;
    }

    public void notaFinal(String nome[], int nota[]) {
        int n = this.classificacao.length;
        // Parse para correcao de padrao ortografico
        for (int i = 0; i < n; i++) {
            String tmp = "";

            for (int k = 0; k < nome.length; k++) {
                if (this.classificacao[i].equalsIgnoreCase(nome[k])) {
                    tmp = String.valueOf(nota[k]);
                    k = nome.length;
                }
            }
            this.classificacao[i] = tmp;
        }

        // insertion sort
        for (int i = 1; i < n; i++) {
            String tmp = this.classificacao[i];
            int j = i - 1;

            while ((j >= 0) && (this.classificacao[j].compareTo(tmp) < 0)) {
                this.classificacao[j + 1] = this.classificacao[j];
                j--;
            }
            this.classificacao[j + 1] = tmp;
        }

        String tmp = this.classificacao[0] + ".";
        for (int i = 1; i < n; i++) {
            tmp += this.classificacao[i];
        }
        tmp += "0";
        this.classificacaoFinal = Double.parseDouble(tmp);
    }

    public static void normalizarNotas(int nota[], String nome[]) {
        int n = nota.length;

        // Diminuir a extensao para otimizar a ordenacao
        float maior = nota[0];
        for (int i = 1; i < n; i++) {
            if (nota[i] > maior) {
                maior = nota[i];
            }
        }

        if (maior > 0) {
            // Normalizando
            for (int i = 0; i < n; i++) {
                nota[i] = (int) ((nota[i] / maior) * 100);
            }

            // CountingSort!!!
            // Array para contar o numero de ocorrencias de cada elemento
            int[] count = new int[101];
            int[] ordenado = new int[n];
            String[] nomeOrd = new String[n];

            // Inicializar cada posicao do array de contagem
            for (int i = 0; i < count.length; i++) {
                count[i] = 0;
            }

            // Agora, o count[i] contem o numero de elemento iguais a i
            for (int i = 0; i < n; i++) {
                count[nota[i]]++;
            }

            // Agora, o count[i] contem o numero de elemento menores ou iguais a i
            for (int i = 1; i < count.length; i++) {
                count[i] += count[i - 1];
            }

            // Ordenando
            for (int i = n - 1; i >= 0; i--) {
                nomeOrd[count[nota[i]] - 1] = nome[i];
                ordenado[count[nota[i]] - 1] = nota[i];
                count[nota[i]]--;
            }

            // Copiando para o array original
            for (int i = 0; i < n; i++) {
                nome[i] = nomeOrd[i];
                nota[i] = ordenado[i];
            }

            // Normalizando denovo!
            int inicio = 0;
            for (inicio = 0; nota[inicio] < 1;) {
                inicio++;
            }

            for (int i = 1; inicio < n;) {
                if (inicio < n - 1) {
                    if (nota[inicio] == nota[inicio + 1]) {
                        nota[inicio] = i;
                        inicio++;
                    } else {
                        nota[inicio] = i;
                        inicio++;
                        i++;
                    }
                } else {
                    nota[inicio] = i;
                    inicio++;
                    i++;
                }
            }
        }
    }

    public void ordenar() {
        // Contar o tamanho da pilha
        int n = 0;
        Profissional last = this;
        while (last.hasNext()) {
            n++;
            last = last.getNext();
        }

        // Desempilhar
        Profissional array[] = new Profissional[n];
        last = this.next;
        for (int i = 0; i < n; i++) {
            array[i] = last;
            last = last.getNext();
            array[i].next = null;
        }
        this.next = null;

        // Ordenar
        // Array para contar o numero de ocorrencias de cada elemento
        int count[] = new int[101];
        Profissional ordenado[] = new Profissional[n];

        // Inicializar cada posicao do array de contagem
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }

        // Agora, o count[i] contem o numero de elemento iguais a i
        for (int i = 0; i < n; i++) {
            count[(int) (array[i].getClassificacaoFinal() * 10)]++;
        }

        // Agora, o count[i] contem o numero de elemento menores ou iguais a i
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        // Ordenando
        for (int i = n - 1; i >= 0; i--) {
            ordenado[count[(int) (array[i].getClassificacaoFinal() * 10)] - 1] = array[i];
            count[(int) (array[i].getClassificacaoFinal() * 10)]--;
        }

        // Copiando para o array original
        for (int i = 0, j = n - 1; i < n; i++, j--) {
            array[j] = ordenado[i];
        }

        for (int i = 1; i < n; i++) {
            Profissional tmp = array[i];
            int j = i - 1;

            while ((j >= 0) && (array[j].classificacaoFinal < tmp.classificacaoFinal)) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = tmp;
        }

        // Decidir aleatoriamente pelos premiums
        Random gerador = new Random();
        long seed = (long) (array[1].registro + array[4].registro + array[8].registro + array[12].registro + array[15].registro);
        gerador.setSeed(seed);
        int numeroDePremiums = gerador.nextInt(2) + 1;
        int posicaoPremium[] = new int[numeroDePremiums];
        for (int i = 0; i < numeroDePremiums; i++) {
            posicaoPremium[i] = gerador.nextInt(4);
        }
        for (int i = 0; i < numeroDePremiums; i++) {
            array[posicaoPremium[i]].premium = true;
        }

        // Empilhar
        for (int i = 0; i < n; i++) {
            this.add(array[i]);
        }
    }

    /**
     * Metodo recursivo para gerar o arquivo JSON da pilha encadeada em ordem
     * correta, afinal os valores sao adicionados de forma inversa, apenas
     * desempilhar ira gerar uma ordem invertida
     * 
     * @param artigo primeiro artigo da lista
     * @return String JSON com os dados da pilha
     */
    public String toJsonRec(Profissional tmp) {
        String Json = "";
        if (tmp != null) {
            // empilhamento recursivo
            Json += toJsonRec(tmp.next);
            // construcao da string apos empilhamento
            Json += "{ ";
            Json += "\"registro\":" + tmp.registro + ",\"nome\":\"" + tmp.nome + "\",\"servico\":\"" + tmp.servico
                    + "\",\"preco\":" + tmp.preco + ",\"foto\":\"" + tmp.foto + "\",\"facebook\":\"" + tmp.facebook
                    + "\",\"twitter\":\"" + tmp.twitter + "\",\"instagram\":\"" + tmp.instagram + "\",\"linkedin\":\""
                    + tmp.linkedin + "\",\"nota\":\"" + tmp.nota + "\",\"notaUsuario\":\"" + tmp.notaUsuario
                    + "\",\"numNotas\":\"" + tmp.numNotas + "\",\"brilhinho\":\"" + tmp.premium + "\"";
            Json += "}";
            Json += ",";
        }
        return Json;
    }

    /**
     * Gerador padrao de JSON
     * 
     * Primeiro se analisa se e uma pilha ou um registro unico, caso seja uma pilha
     * sera chamado o metodo toJsonRec
     * 
     * @return JSON sem objeto pai
     */
    public String toJson() {
        String Json = "";
        // Se for pilha, chamar toJsonRec
        if (this.hasNext()) {
            Profissional last = this.next;
            Json += toJsonRec(last);
            Json = Json.substring(0, Json.length() - 1);
            // limpeza da variavel
            last = null;
        } else {
            Json += "{ ";
            Json += "\"registro\":" + registro + ",\"nome\":\"" + nome + "\",\"servico\":\"" + servico + "\",\"preco\":"
                    + preco + ",\"foto\":\"" + foto + "\",\"facebook\":\"" + facebook + "\",\"twitter\":\"" + twitter
                    + "\",\"instagram\":" + instagram + "\",\"linkedin\":\"" + linkedin + "\",\"nota\":\"" + nota
                    + "\",\"notaUsuario\":\"" + notaUsuario + "\",\"numNotas\":\"" + numNotas + "\",\"brilhinho\":\""
                    + premium + "\"";
            Json += "}";
        }
        return Json;
    }
}
