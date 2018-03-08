import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {

    public static void leituraArquivo(String linha, Vertice nodes[], Aresta edges[], Contador cont) {
        String[] dividelinha = linha.split(",");
        if (dividelinha.length == 12 && !dividelinha[0].equals("nodedef> name VARCHAR")) {
            nodes[cont.vertices] = new Vertice();
            nodes[cont.vertices].indice = Integer.parseInt(dividelinha[0]);
            nodes[cont.vertices].nome = dividelinha[1];
            nodes[cont.vertices].grandeArea = dividelinha[2];
            nodes[cont.vertices].area = dividelinha[3];
            nodes[cont.vertices].instituicao = dividelinha[4];
            nodes[cont.vertices].bolsa = dividelinha[5];
            nodes[cont.vertices].cvLattes = dividelinha[6];
            nodes[cont.vertices].cvData = dividelinha[7];
            nodes[cont.vertices].rotulo = dividelinha[8];
            nodes[cont.vertices].foto = dividelinha[9];
            nodes[cont.vertices].menorAnoFormacao = dividelinha[10];
            nodes[cont.vertices].nivelGeracao = dividelinha[11];
            cont.vertices++;
        } else if ((dividelinha.length == 6 || dividelinha.length == 7) && !dividelinha[0].equals("edgedef> node1 VARCHAR")) { // 6 se tiver o directed
            edges[cont.arestas] = new Aresta();
            edges[cont.arestas].origem = Integer.parseInt(dividelinha[0]);
            edges[cont.arestas].destino = Integer.parseInt(dividelinha[1]);
            edges[cont.arestas].nivelOrientacao = dividelinha[2];
            edges[cont.arestas].tipoOrientacao = dividelinha[3];
            edges[cont.arestas].anoInicio = dividelinha[4];
            edges[cont.arestas].anoConclusao = dividelinha[5];
            edges[cont.arestas].directed = true;
            if (edges[cont.arestas].origem != edges[cont.arestas].origem || !edges[cont.arestas].anoConclusao.isEmpty()) { // Pergunta se origem igual a destino e se possui dado de ano de conclusao
                cont.arestas++;
            }
        }
        else if (!dividelinha[0].equals("nodedef> name VARCHAR") && !dividelinha[0].equals("edgedef> node1 VARCHAR")){
            if (dividelinha.length == 12) {
                System.out.println("Erro na leitura do vertice de indice " + Integer.parseInt(dividelinha[0]));
            }
            else if ((dividelinha.length == 6 || dividelinha.length == 7)) {
                System.out.println("Erro na leitura da aresta de origem " + Integer.parseInt(dividelinha[0]) + " e destino " + Integer.parseInt(dividelinha[0]));
            }
        }
    } // Realiza a leitura do arquivo

    public static void contaVerticeseArestas(String linha, Contador cont) {
        String[] dividelinha = linha.split(",");
        if (dividelinha.length == 12 && !dividelinha[0].equals("nodedef> name VARCHAR")) {
            cont.vertices++;
        }
        else if ((dividelinha.length == 6 || dividelinha.length == 7) && !dividelinha[0].equals("edgedef> node1 VARCHAR")) {
            cont.arestas++;
        }
    } // Conta a quantidade de vertices e arestas

    public static String criarTexto (Vertice nodes[], ArrayList<ArrayList<Aresta>> ListaIncidencia, Contador cont){
        System.out.println("Gerando novo grafo...\n ");
        String texto = "";
        texto = texto + "nodedef> name VARCHAR, label VARCHAR, GrandeArea VARCHAR, Area VARCHAR, Instituicao VARCHAR, Bolsa VARCHAR, CVLattes VARCHAR, CVData VARCHAR, rotulo VARCHAR, foto VARCHAR, menorAnoFormacao DOUBLE, nivelGeracao DOUBLE\n";

        for (int i = 0; i<cont.vertices; i++){
            texto = texto + nodes[i].indice + "," + nodes[i].nome;
            texto = texto + "," + nodes[i].grandeArea;
            texto = texto + "," + nodes[i].area;
            texto = texto + "," + nodes[i].instituicao;
            texto = texto + "," + nodes[i].bolsa;
            texto = texto + "," + nodes[i].cvLattes;
            texto = texto + "," + nodes[i].cvData;
            texto = texto + "," + nodes[i].rotulo;
            texto = texto + "," + nodes[i].foto;
            texto = texto + "," + nodes[i].menorAnoFormacao;
            texto = texto + "," + nodes[i].nivelGeracao;
            texto = texto + "\n";
        }

        texto = texto + "edgedef> node1 VARCHAR, node2 VARCHAR, nivelOrientacao VARCHAR, tipoOrientacao VARCHAR, anoInicio DOUBLE, anoConclusao DOUBLE, directed BOOLEAN\n";
        Aresta edge;
        cont.arestas = 0;

        for (int i = 0; i < cont.vertices; i++){
            for (int j = 0; j < ListaIncidencia.get(i).size(); j++) {
                cont.arestas++;
                edge = ListaIncidencia.get(i).get(j);
                texto = texto + edge.origem + "," + edge.destino;
                texto = texto + "," + edge.nivelOrientacao;
                texto = texto + "," + edge.tipoOrientacao;
                texto = texto + "," + edge.anoInicio;
                texto = texto + "," + edge.anoConclusao;
                texto = texto + ",true\n";
            }
        }
        System.out.println("Grafo gerado com sucesso!\n");
        return texto;
    }

    public static void arrumaVetores (Vertice nodes[], Aresta edges[], Contador cont){
        for (int i = 0; i < cont.arestas; i++) { // linhas da MatrizdeArestas, ou cada aresta
            for (int j = 0; j < cont.vertices; j++) { // compara com os vertices
                if (edges[i].origem == nodes[j].indice) { // se a origem for igual ao indice
                    edges[i].origem = j;
                }
                if (edges[i].destino == nodes[j].indice) {
                    edges[i].destino = j;
                }
            }
        }
        for (int i = 0; i < cont.vertices; i++) {
            nodes[i].indice = i; // acertar os vertices
        }
    }

    public static void criarListaIncidencia (ArrayList<ArrayList<Aresta>> ListaIncidencia, Vertice nodes[], Aresta edges[], Contador cont){
        ArrayList<Aresta> incidentes = new ArrayList<Aresta>(); // Lista dos elementos reincidentes a cada vertice
        for (int i = 0; i < cont.vertices; i++) { // adiciona listas ao ListaReincidencia igual ao numero de vertices
            incidentes = new ArrayList<Aresta>();
            ListaIncidencia.add(incidentes);
        }

        for (int i = 0; i < cont.arestas; i++) { // adiciona as arestas nas listas "destino"
            //System.out.println(edges[i].destino + "<-" + edges[i].origem);
            ListaIncidencia.get(edges[i].destino).add(edges[i]);
        }
    }

    public static void tirarCoorientacoes (ArrayList<ArrayList<Aresta>> ListaIncidencia, Aresta edges[], Contador cont){
        int incidentes = 0;
        int menorAno, indMenor, anoConclusao;
        for (int i = 0; i < cont.vertices; i++){ // Consulta todos os vertices
            if (ListaIncidencia.get(i).size()>1){ // Pergunta se possui mais de uma aresta reincidente
                incidentes = ListaIncidencia.get(i).size();
                indMenor = 0;
                menorAno = Integer.parseInt(ListaIncidencia.get(i).get(0).anoConclusao);
                for (int j = 0; j < incidentes; j++){ // Encontra o menor ano de conclusao
                    anoConclusao = Integer.parseInt(ListaIncidencia.get(i).get(j).anoConclusao);
                    if (menorAno > anoConclusao){
                        indMenor = j;
                        menorAno = anoConclusao;
                    }
                }
                for (int j = incidentes-1; j >= 0; j--) { // Excluir as outras arestas, precisa ser ao contrario, pois remove da lista, podendo dar out of bounds, ou poderia usar reincidentes--
                    if (j != indMenor) {
                        ListaIncidencia.get(i).remove(j);
                        cont.arestasremovidascoorient++;
                    }
                }
            }
        }
    }

    public static int encontraAnoConclusao (int destino, int origem, Aresta edges[], Contador cont){
        int anoConclusao = 0;
        for (int i = 0; i < cont.arestas; i++){
            if (edges[i].origem == origem){
                if (edges[i].destino == destino){
                    anoConclusao = Integer.parseInt(edges[i].anoConclusao);
                }
            }
        }
        return anoConclusao;
    }


    public static String limpador(String arquivoEntrada) {
        Contador cont = new Contador();
        Contador cont2 = new Contador();
        String texto = "";

        try {
            System.out.println("Realizando a leitura do grafo inicial...\n");
            Scanner in = new Scanner(new FileReader(arquivoEntrada)); // primeira leitura do grafo, para contagem dos vertices e arestas
            String linha;
            while (in.hasNextLine()) {
                linha = in.nextLine();
                contaVerticeseArestas(linha, cont); // conta os vertices e arestas
            }

            Vertice nodes[] = new Vertice[cont.vertices]; // criacao dos vetores de vertices e arestas
            Aresta edges[] = new Aresta[cont.arestas];

            in = new Scanner(new FileReader(arquivoEntrada)); // segunda leitura do grafo
            while (in.hasNextLine()) {
                linha = in.nextLine();
                leituraArquivo(linha, nodes, edges, cont2); // anota as informacoes dos vertices e arestas
            }
            System.out.println("Grafo Inicial:");
            cont.imprimir();
            System.out.println("\nProcessando...\n");

            cont2.arestasremovidasinf = cont.arestas-cont2.arestas;

            arrumaVetores(nodes, edges, cont2);

            ArrayList<ArrayList<Aresta>> ListaIncidencia = new ArrayList<ArrayList<Aresta>>(); // Lista de incidencia

            criarListaIncidencia(ListaIncidencia, nodes, edges, cont2); // cria a Matriz de incidencia

            tirarCoorientacoes(ListaIncidencia, edges, cont2);

            System.out.println(cont2.arestasremovidasinf + " arestas removidas por falta de informacoes corretas.");
            System.out.println(cont2.arestasremovidascoorient + " arestas removidas por coorientacoes.\n");

            texto = criarTexto(nodes, ListaIncidencia, cont2);

            System.out.println("Grafo Final:");
            cont2.imprimir();

            return texto;

        } catch (IOException e) {
            System.err.printf("ERRO: %s.\n", e.getMessage());
        }
        return texto;
    }

    public static void main(String[] args) {
        String arquivoEntrada = "/home/erick/Desktop/IC/grafos-exemplo/br-bolsistas-ci2016.gdf";

        Scanner scan = new Scanner(System.in);
        String texto = "";

        texto = limpador(arquivoEntrada);


        try {
            File arquivoSaida = new File(arquivoEntrada.substring(0, arquivoEntrada.length()-3) + "clean.gdf");
            if (!arquivoSaida.exists()) {
                arquivoSaida.createNewFile();
            }
            FileWriter fw = new FileWriter(arquivoSaida.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(texto);
            bw.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        System.out.println("\nEndereco do novo grafo: \"" + (arquivoEntrada.substring(0, arquivoEntrada.length()-3) + "clean.gdf") + "\"");
    }

}


class Aresta {
    public int origem;
    public int destino;
    public String nivelOrientacao;
    public String tipoOrientacao;
    public String anoInicio;
    public String anoConclusao;
    public boolean directed;
    public void imprimir() {
        System.out.println(this.origem + " --> " + this.destino);
    }
}

class Vertice {
    public int indice;
    public String nome;
    public String grandeArea;
    public String area;
    public String instituicao;
    public String bolsa;
    public String cvLattes;
    public String cvData;
    public String rotulo;
    public String foto;
    public String menorAnoFormacao;
    public String nivelGeracao;
}

class Contador {
    public int vertices;
    public int arestas;
    public int sementes;
    public int arestasremovidasinf;
    public int arestasremovidascoorient;

    public Contador() {
        this.vertices = 0;
        this.arestas = 0;
        this.sementes = 0;
        this.arestasremovidasinf = 0;
        this.arestasremovidascoorient = 0;
    }
    public void imprimir() {
        System.out.println("Vertices: " + this.vertices);
        System.out.println("Arestas: " + this.arestas);
    }
}
