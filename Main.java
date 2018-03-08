package propderotulos;

import java.io.*;
import java.util.*;

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

    public static int contaSementes(String linha) {
        int n = 0;
        String[] dividelinha = linha.split(":");
        if (dividelinha.length == 2 && dividelinha[0].equals("Seeds")) {
            n = Integer.parseInt(dividelinha[1]);
        }
        return n;
    }

    public static boolean comparaVetoresInt (int vetor1[], int vetor2[]){
        if (vetor1.length!=vetor2.length)
            return false;
        for (int i = 0; i < vetor1.length; i++){
            if(vetor1[i]!=vetor2[i])
                return false;
        }
        return true;
    }

    public static void contaVerticeseArestas(String linha, Contador cont) {
        String[] dividelinha = linha.split(",");
        if (dividelinha.length == 12 && !dividelinha[0].equals("nodedef> name VARCHAR")) {
            cont.vertices++;
        } else if ((dividelinha.length == 6 || dividelinha.length == 7) && !dividelinha[0].equals("edgedef> node1 VARCHAR")) {
            cont.arestas++;
        }
    }

    public static void criaMatrizAtributos(Contador cont, int MatrizAtributos[][], int seeds[]) {
        for (int[] linha : MatrizAtributos) //Encher a Matriz com -1
        {
            Arrays.fill(linha, -1);
        }
        for (int i = 0; i < cont.sementes; i++) {
            MatrizAtributos[seeds[i]][i] = 0;
        }
    }

    public static void arrumaVetores (Vertice nodes[], Aresta edges[], Contador cont, int seeds[]){
        for (int i = 0; i < cont.arestas; i++) { // linhas da MatrizdeArestas, ou cada aresta
            for (int j = 0; j < cont.vertices; j++) { // compara com os vertices
                if (edges[i].origem == nodes[j].indice) { // se a origem for igual ao indice
                    edges[i].origem = j;
                    break;
                }
                if (edges[i].destino == nodes[j].indice) {
                    edges[i].destino = j;
                    break;
                }
            }
        }

        for (int i = 0; i < cont.sementes; i++) { // arrumar seeds com vertices
            for (int j = 0; j < cont.vertices; j++) {
                if (seeds[i] == nodes[j].indice) {
                    seeds[i] = j;
                    break;
                }
            }
        }

        for (int i = 0; i < cont.vertices; i++) {
            nodes[i].indice = i; // acertar os vertices
        }
    }

    public static void iniciaArrayListsdeArrayLists (ArrayList<ArrayList<Integer>> ListaAdj, Vertice nodes[], Aresta edges[], Contador cont){
        ArrayList<Integer> adjacentes = new ArrayList<Integer>(); // Lista dos elementos adjacentes a cada vertice
        for (int i = 0; i < cont.vertices; i++) { // adiciona listas ao ListaAdj igual ao numero de vertices
            adjacentes = new ArrayList<Integer>();
            ListaAdj.add(adjacentes);
        }

        for (int i = 0; i < cont.arestas; i++) { // adiciona os vertices "destino" nas listas "origem"
            ListaAdj.get(edges[i].origem).add(edges[i].destino);
        }
    }

    public static VerificadorFamilia verificadorDePaiUnico (ArrayList<ArrayList<Integer>> ListaAdj, Contador cont, int filho, int atributosPai1[], int atributosPai2[], int MatrizAtributos[][]){
        VerificadorFamilia verificador = new VerificadorFamilia();
        verificador.paiunico = 0;
        verificador.mesmaFamilia = true;
        for (int i = 0; i < cont.vertices; i++) { // verificador de pai unico
            if (ListaAdj.get(i).contains(filho)) {
                if (verificador.paiunico==0){
                    for (int atributo = 0; atributo < cont.sementes; atributo++){
                        atributosPai1[atributo]=MatrizAtributos[i][atributo];
                    }
                }
                else {
                    for (int atributo = 0; atributo < cont.sementes; atributo++){
                        atributosPai2[atributo]=MatrizAtributos[i][atributo];
                    }
                    verificador.mesmaFamilia = comparaVetoresInt(atributosPai1, atributosPai2);
                }
                verificador.paiunico++;
                verificador.pai = i;
            }
        }
        return verificador;
    }

    public static String criarTexto (Vertice nodes[], Aresta edges[], Contador cont, int MatrizAtributos[][]){
        String texto = "";
        texto = texto + "nodedef> name VARCHAR, label VARCHAR, GrandeArea VARCHAR, Area VARCHAR, Instituicao VARCHAR, Bolsa VARCHAR, CVLattes VARCHAR, CVData VARCHAR, rotulo VARCHAR, foto VARCHAR, menorAnoFormacao DOUBLE, nivelGeracao DOUBLE";
        for (int i = 1; i<=cont.sementes; i++){
            texto = texto + ", atributo" + i + " DOUBLE";
        }
        texto = texto + "\n";

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
            for (int j = 0; j<cont.sementes; j++){
                texto = texto + "," + MatrizAtributos[i][j];
            }
            texto = texto + "\n";
        }

        texto = texto + "edgedef> node1 VARCHAR, node2 VARCHAR, nivelOrientacao VARCHAR, tipoOrientacao VARCHAR, anoInicio DOUBLE, anoConclusao DOUBLE, directed BOOLEAN\n";

        for (int i = 0; i < cont.arestas; i++){
            texto = texto + edges[i].origem + "," + edges[i].destino;
            texto = texto + "," + edges[i].nivelOrientacao;
            texto = texto + "," + edges[i].tipoOrientacao;
            texto = texto + "," + edges[i].anoInicio;
            texto = texto + "," + edges[i].anoConclusao;
            texto = texto + ",true\n";
        }
        return texto;
    }

    public static void imprimeMatriz(int n, int m, int Matriz[][]) {
        System.out.println("Matriz:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (j == m - 1) {
                    System.out.println(Matriz[i][j]);
                } else {
                    System.out.print(Matriz[i][j] + " ");
                }
            }
        }
    }

    public static void imprimeMatrizBordas(int n, int m, int Matriz[][]) {
        System.out.println("Matriz:");
        System.out.print("  ");
        for (int k = 0; k < m; k++) {
            if (k == m - 1) {
                System.out.println(k);
            } else {
                System.out.print(k + "  ");
            }
        }

        for (int i = 0; i < n; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < m; j++) {
                if (j == m - 1) {
                    System.out.println(Matriz[i][j]);
                } else {
                    System.out.print(Matriz[i][j] + " ");
                }
            }
        }
    }

    public static void imprimeMatrizEmCSV(int n, int m, int Matriz[][]) {
        System.out.println("Matriz em CSV:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (j == m - 1) {
                    System.out.println(Matriz[i][j]);
                } else {
                    System.out.print(Matriz[i][j] + ", ");
                }
            }
        }
    }

    public static void imprimeArrayList(ArrayList<Integer> VerticesdeDistX, int tamanho, int distancia) {
        System.out.print("Vertices de Distancia: ");
        System.out.println(distancia);
        for (int i = 0; i < tamanho; i++) {
            if (i != tamanho - 1) {
                System.out.print(VerticesdeDistX.get(i) + ", ");
            } else {
                System.out.println(VerticesdeDistX.get(i));
            }
        }
    }

    public static void imprimeArrayListdeArrayList(ArrayList<ArrayList<Integer>> lista, String S) {
        System.out.println (S);
        for (int i = 0; i < lista.size(); i++) { // i e o numero do array
            System.out.print("Array: " + i + "\n");
            for (int j = 0; j < lista.get(i).size(); j++) {
                if (j != lista.get(i).size() - 1) {
                    System.out.print(lista.get(i).get(j) + ", ");
                } else {
                    System.out.println(lista.get(i).get(j));
                }
            }
        }
    }

    public static void imprimeArrayListVertices(ArrayList<String> VerticesdeDistX, int tamanho, int distancia) {
        System.out.println("Lista de Vertices: ");
        for (int i = 0; i < tamanho; i++) {
            if (i != tamanho - 1) {
                System.out.print(VerticesdeDistX.get(i) + ", ");
            } else {
                System.out.println(VerticesdeDistX.get(i));
            }
        }
    }

    public static void imprimeArrayListArestas(ArrayList<String> VerticesdeDistX, int tamanho, int distancia) {
        System.out.println("Lista de Arestas: ");
        for (int i = 0; i < tamanho * 2; i = i + 2) {
            if (i != (tamanho - 1) * 2) {
                System.out.print("(" + VerticesdeDistX.get(i) + "," + VerticesdeDistX.get(i + 1) + ")" + ", ");
            } else {
                System.out.println("(" + VerticesdeDistX.get(i) + "," + VerticesdeDistX.get(i + 1) + ")");
            }
        }
    }

    public static String abordagemGeracoes(String arquivoEntrada, String sementes) {
        Contador cont = new Contador();
        Contador cont2 = new Contador();
        String texto = "";


        try {
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

            in = new Scanner(new FileReader(sementes)); // leitura das sementes
            linha = in.nextLine();
            cont.sementes = contaSementes(linha); // le a quantidade de sementes
            int seeds[] = new int[cont.sementes]; // cria o vetor de sementes
            while (in.hasNextLine()) {
                linha = in.nextLine();
                if (linha != null) {
                    seeds[cont2.sementes] = Integer.parseInt(linha);
                    cont2.sementes++;
                }
            }

            for (int i = 0; i < cont.arestas; i++) { // linhas da MatrizdeArestas, ou cada aresta
                for (int j = 0; j < cont.vertices; j++) { // compara com os vertices
                    if (edges[i].origem == nodes[j].indice) { // se a origem for igual ao indice
                        edges[i].origem = j;
                        break;
                    }
                    if (edges[i].destino == nodes[j].indice) {
                        edges[i].destino = j;
                        break;
                    }
                }
            }

            for (int i = 0; i < cont.sementes; i++) { // arrumar seeds com vertices
                for (int j = 0; j < cont.vertices; j++) {
                    if (seeds[i] == nodes[j].indice) {
                        seeds[i] = j;
                        break;
                    }
                }
            }

            for (int i = 0; i < cont.vertices; i++) {
                nodes[i].indice = i; // acertar os vertices
            }

            ArrayList<ArrayList<Integer>> ListaAdj = new ArrayList<ArrayList<Integer>>();
            ArrayList<Integer> adjacentes = new ArrayList<Integer>(); // Lista dos elementos adjacentes a cada vertice

            for (int i = 0; i < cont.vertices; i++) { // adiciona listas ao ListaAdj igual ao numero de vertices
                adjacentes = new ArrayList<Integer>();
                ListaAdj.add(adjacentes);
            }

            for (int i = 0; i < cont.arestas; i++) { // adiciona os vertices "destino" nas listas "origem"
                ListaAdj.get(edges[i].origem).add(edges[i].destino);
            }

            int MatrizAtributos[][] = new int[cont.vertices][cont.sementes]; //Matriz de Atributos nº vertices x nº sementes
            criaMatrizAtributos(cont, MatrizAtributos, seeds); //Preenche Matriz com -1 e 0 nas origens

            int pai;
            boolean continuar = true;

            ArrayList<ArrayList<Integer>> distancias = new ArrayList<ArrayList<Integer>>(); // ArrayList de ArrayList, aprender a usar
            ArrayList<Integer> array = new ArrayList<Integer>();
            ArrayList<Integer> contelement = new ArrayList<Integer>();

            distancias.add(array); // Adiciona a ArrayList seeds a ArrayList de ArrayList Distancias

            for (int i = 0; i < cont.sementes; i++) { // Cria um array list para seeds
                distancias.get(0).add(seeds[i]);
            }

            contelement.add(cont.sementes);
            int distanciamax = 0;
            short paiunico; //verificador de pai unico
            int filho;

            while (continuar) {
                array = new ArrayList<Integer>(); // Reseta o array
                for (int i = 0; i < distancias.get(distanciamax).size(); i++) { // Pesquisa todos os elementos na distancia atual
                    pai = distancias.get(distanciamax).get(i); //aqui - pega os elementos do ArrayList anterior
                    for (int j = 0; j < ListaAdj.get(pai).size(); j++) { // Vai pegando os elementos filhos do pai
                        paiunico = 0;
                        filho = ListaAdj.get(pai).get(j); //
                        if (!array.contains(ListaAdj.get(pai).get(j))) { // Evita elementos repetidos
                            array.add(ListaAdj.get(pai).get(j));
                        }
                        if (distanciamax == 0) { // caso especial para seeds
                            if (MatrizAtributos[filho][i] == -1) {
                                MatrizAtributos[filho][i] = 1;
                            }
                        }
                        for (int l = 0; l < cont.sementes; l++) {
                            if (MatrizAtributos[filho][l] == -1) {
                                if (MatrizAtributos[pai][l] != -1) {
                                    MatrizAtributos[filho][l] = MatrizAtributos[pai][l] + 1;

                                }
                            }
                        }
                    }
                }
                if (array.isEmpty()) {
                    continuar = false;
                } else {
                    distancias.add(array);
                    distanciamax++;
                    contelement.add(array.size());
                }
            }

            //for (int i=0; i<=distanciamax; i++){
            //imprimeArrayList (Distancias.get(i), contelement.get(i), i);
            //}
            //for (int i=0; i<contv; i++){
            //imprimeArrayList (ListaAdj.get(i), elementosadj[i], i);
            //}
            //System.out.println(distanciamax);
            imprimeArrayListdeArrayList(ListaAdj, "Imprimindo Lista de Adjacencia");
            imprimeArrayListdeArrayList(distancias, "Imprimindo Lista de Distancias");
            //imprimeMatriz(conta, 2, MatrizdeArestas);
            //imprimeArrayListdeArrayList(Distancias, distanciamax, contelement);
            //imprimeMatriz(cont.vertices, cont.sementes, MatrizAtributos); //Teste: Imprime Matriz de Atributos
            //ImprimeMatriz(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia
            imprimeMatrizBordas(cont.vertices, cont.sementes, MatrizAtributos); //Teste: Imprime Matriz de Atributos
            //ImprimeMatrizBordas(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia
            //ImprimeMatrizEmCSV(contv, contorigens, MatrizAtributos); //Teste: Imprime Matriz de Atributos em csv
            //ImprimeMatrizEmCSV(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia em csv
            //ImprimeArrayListVertices (vertices, contv, 0);
            //ImprimeArrayListArestas (arestas, conta, 0);
            //ImprimeOrigens(contorigens, origens); //Teste: Imprime informacoes das origens

            // Comecar a gravar dados na variavel texto.
            texto = texto + "nodedef> name VARCHAR, label VARCHAR, GrandeArea VARCHAR, Area VARCHAR, Instituicao VARCHAR, Bolsa VARCHAR, CVLattes VARCHAR, CVData VARCHAR, rotulo VARCHAR, foto VARCHAR, menorAnoFormacao DOUBLE, nivelGeracao DOUBLE";
            for (int i = 1; i<=cont.sementes; i++){
                texto = texto + ", atributo" + i + " DOUBLE";
            }
            texto = texto + "\n";

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
                for (int j = 0; j<cont.sementes; j++){
                    texto = texto + "," + MatrizAtributos[i][j];
                }
                texto = texto + "\n";
            }

            texto = texto + "edgedef> node1 VARCHAR, node2 VARCHAR, nivelOrientacao VARCHAR, tipoOrientacao VARCHAR, anoInicio DOUBLE, anoConclusao DOUBLE, directed BOOLEAN\n";

            for (int i = 0; i < cont.arestas; i++){
                texto = texto + edges[i].origem + "," + edges[i].destino;
                texto = texto + "," + edges[i].nivelOrientacao;
                texto = texto + "," + edges[i].tipoOrientacao;
                texto = texto + "," + edges[i].anoInicio;
                texto = texto + "," + edges[i].anoConclusao;
                texto = texto + ",true\n";
            }

            return texto;

        } catch (IOException e) {
            System.err.printf("ERRO: %s.\n", e.getMessage());
        }
        return texto;
    } //

    public static String abordagemFamilias(String arquivoEntrada, String sementes) {
        Contador cont = new Contador();
        Contador cont2 = new Contador();
        String texto = "";

        try {
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

            in = new Scanner(new FileReader(sementes)); // leitura das sementes
            linha = in.nextLine();
            cont.sementes = contaSementes(linha); // le a quantidade de sementes
            int seeds[] = new int[cont.sementes]; // cria o vetor de sementes
            while (in.hasNextLine()) {
                linha = in.nextLine();
                if (linha != null) {
                    seeds[cont2.sementes] = Integer.parseInt(linha);
                    cont2.sementes++;
                }
            }

            cont.imprimir();

            arrumaVetores(nodes, edges, cont, seeds);

            ArrayList<ArrayList<Integer>> ListaAdj = new ArrayList<ArrayList<Integer>>(); // Lista de Adjacencia
            ArrayList<Integer> Visitados = new ArrayList<Integer>(); // ArrayList de ArrayList para verificar quais arestas ja foram percorridas


            iniciaArrayListsdeArrayLists(ListaAdj, nodes, edges, cont); //

            int MatrizAtributos[][] = new int[cont.vertices][cont.sementes]; //Matriz de Atributos nº vertices x nº sementes
            criaMatrizAtributos(cont, MatrizAtributos, seeds); //Preenche Matriz com -1 e 0 nas origens

            ArrayList<ArrayList<Integer>> distancias = new ArrayList<ArrayList<Integer>>(); // ArrayList de ArrayList, aprender a usar
            ArrayList<Integer> array = new ArrayList<Integer>();
            ArrayList<Integer> contelement = new ArrayList<Integer>();


            distancias.add(array); // Adiciona a ArrayList seeds a ArrayList de ArrayList Distancias

            for (int i = 0; i < cont.sementes; i++) { // Cria um array list para seeds
                distancias.get(0).add(seeds[i]);
            }

            contelement.add(cont.sementes);

            // ate aqui, igual ao geracoes
            short paiunico; //verificador de pai unico
            boolean mesmaFamilia = true;
            boolean continuar = true;
            int distanciamax = 0;
            int pai;
            int filho;
            int atributosPai1[] = new int [cont.sementes]; // vetor para guardar as informacoes do pai
            int atributosPai2[] = new int [cont.sementes]; // vetor para guardar as informacoes de outro pai, se houver
            VerificadorFamilia verificador;

            while (continuar) {
                array = new ArrayList<Integer>(); // Reseta o array
                for (int i = 0; i < distancias.get(distanciamax).size(); i++) { // Pesquisa todos os elementos na distancia atual
                    pai = distancias.get(distanciamax).get(i); //aqui - pega os elementos do ArrayList anterior
                    if (!Visitados.contains(pai)) {
                        Visitados.add(pai);
                        for (int j = 0; j < ListaAdj.get(pai).size(); j++) { // Vai pegando os elementos filhos do pai
                            filho = ListaAdj.get(pai).get(j); //
                            if (!array.contains(ListaAdj.get(pai).get(j))) { // Evita elementos repetidos
                                array.add(ListaAdj.get(pai).get(j));
                            }
                            verificador = verificadorDePaiUnico(ListaAdj, cont, filho, atributosPai1, atributosPai2, MatrizAtributos);
                            if (distanciamax == 0) { // caso especial para seeds
                                if (MatrizAtributos[filho][i] == -1) {
                                    MatrizAtributos[filho][i] = 1;
                                }
                            }
                            if (verificador.mesmaFamilia) { // se for da mesma familia
                                for (int l = 0; l < cont.sementes; l++) {
                                    MatrizAtributos[filho][l] = MatrizAtributos[verificador.pai][l]; // atributo do filho igual ao do pai
                                    // ou MatrizAtributos[filho][l] = atributosPai1[l];
                                }
                            } else { // nao e da mesma familia
                                for (int l = 0; l < cont.sementes; l++) {
                                    if (MatrizAtributos[filho][l] == -1) {
                                        if (MatrizAtributos[pai][l] != -1) {
                                            MatrizAtributos[filho][l] = MatrizAtributos[pai][l] + 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (array.isEmpty()) {
                    continuar = false;
                } else {
                    distancias.add(array);
                    distanciamax++;
                    contelement.add(array.size());
                }
            }

            //for (int i=0; i<=distanciamax; i++){
            //imprimeArrayList (Distancias.get(i), contelement.get(i), i);
            //}
            //for (int i=0; i<contv; i++){
            //imprimeArrayList (ListaAdj.get(i), elementosadj[i], i);
            //}
            //System.out.println(distanciamax);
            //imprimeArrayListdeArrayList(ListaAdj, "Imprimindo Lista de Adjacencia");
            //imprimeArrayListdeArrayList(distancias, "Imprimindo Lista de Distancias");
            //imprimeMatriz(conta, 2, MatrizdeArestas);
            //imprimeArrayListdeArrayList(Distancias, distanciamax, contelement);
            //imprimeMatriz(cont.vertices, cont.sementes, MatrizAtributos); //Teste: Imprime Matriz de Atributos
            //ImprimeMatriz(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia
            //imprimeMatrizBordas(cont.vertices, cont.sementes, MatrizAtributos); //Teste: Imprime Matriz de Atributos
            //ImprimeMatrizBordas(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia
            //ImprimeMatrizEmCSV(contv, contorigens, MatrizAtributos); //Teste: Imprime Matriz de Atributos em csv
            //ImprimeMatrizEmCSV(contv, contv, MatrizAdj); //Teste: Imprime Matriz de Adjacencia em csv
            //ImprimeArrayListVertices (vertices, contv, 0);
            //ImprimeArrayListArestas (arestas, conta, 0);
            //ImprimeOrigens(contorigens, origens); //Teste: Imprime informacoes das origens


            // Comecar a gravar dados na variavel texto.
            texto = criarTexto(nodes, edges, cont, MatrizAtributos);

            return texto;

        } catch (IOException e) {
            System.err.printf("ERRO: %s.\n", e.getMessage());
        }
        return texto;
    }


    public static void main(String[] args) {
        String arquivoEntrada = "/home/erick/Desktop/IC/grafos-exemplo/br-engenharia-software-doutores.clean.gdf";
        String sementes = arquivoEntrada.substring(0, arquivoEntrada.length()-3)+"seeds";

        System.out.print("1.Geracoes\n2.Familias (incompleto)\nEscolha a abordagem: ");
        Scanner scan = new Scanner(System.in);
        short tipodeabordagem = scan.nextShort();
        String texto = "";

        switch (tipodeabordagem) {
            case 1:
                texto = abordagemGeracoes(arquivoEntrada, sementes);
                break;
            case 2:
                texto = abordagemFamilias(arquivoEntrada, sementes);
                break;
            default:
                System.out.println("Abordagem selecionada incorretamente.");
        }

        try {
            File arquivoSaida = new File(sementes + "A" + tipodeabordagem + ".gdf");
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

    //    public Aresta (int origem, int destino, String nivelOrientacao, String tipoOrientacao, int anoInicio, int anoConclusao){
//      this.origem  = origem;
//      this.destino = destino;
//      this.nivelOrientacao = nivelOrientacao;
//      this.tipoOrientacao = tipoOrientacao;
//      this.anoInicio = anoInicio;
//      this.anoConclusao = anoConclusao;
//    }
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

    //    public Vertice (int indice, String nome, String grandeArea, String area, String instituicao, String bolsa, String cvLattes, String cvData, String rotulo, String foto, int menorAnoFormacao, int nivelGeracao){
//      this.indice = indice;
//      this.nome = nome;
//      this.grandeArea = grandeArea;
//      this.area = area;
//      this.instituicao = instituicao;
//      this.bolsa = bolsa;
//      this.cvLattes = cvLattes;
//      this.cvData = nome;
//      this.rotulo = rotulo;
//      this.foto = foto;
//      this.menorAnoFormacao = menorAnoFormacao;
//      this.nivelGeracao = nivelGeracao;
//    }
    public void imprimir() {
        //System.out.println(this.origem + " --> " + this.destino);
    }

}

class Contador {

    public int vertices;
    public int arestas;
    public int sementes;

    public Contador() {
        this.vertices = 0;
        this.arestas = 0;
        this.sementes = 0;
    }

    public void imprimir() {
        System.out.println("Vertices: " + this.vertices);
        System.out.println("Arestas: " + this.arestas);
        System.out.println("Sementes: " + this.sementes);
    }
}

class VerificadorFamilia {
    public short paiunico;
    public boolean mesmaFamilia;
    public int pai;
}