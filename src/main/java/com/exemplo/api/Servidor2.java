package com.exemplo.api;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Servidor2 {
    public static void main(String[] args) throws IOException {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        carregarConjuncoes();
        System.out.println("Teste de acentos: não, já, até, à, é, ção.");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", (HttpExchange exchange) -> {
        String msg = "API disponível nos caminhos /sortear e /verificar";
        byte[] responseBytes = msg.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
        });

        server.createContext("/verificar", (HttpExchange exchange) -> {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);

            String conjuncao = params.getOrDefault("conjuncao", "").toLowerCase().trim();
            String resposta = params.getOrDefault("resposta", "").toLowerCase().trim();

            String tipoEsperado = mapaConjuncoes.getOrDefault(conjuncao, "");

            String resultado = resposta.equals(tipoEsperado)
                    ? "Correto!"
                    : "Errado! Tipo: " + tipoEsperado;

            byte[] responseBytes = resultado.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        });

        server.createContext("/sortear", (HttpExchange exchange) -> {
            List<String> conjuncoes = new ArrayList<>(mapaConjuncoes.keySet());
            Collections.shuffle(conjuncoes);
            String sorteada = conjuncoes.get(0);

            byte[] responseBytes = sorteada.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        });

        System.out.println("Servidor rodando em http://localhost:8000");
        server.start();
    }

    private static final Map<String, String> mapaConjuncoes = new HashMap<>();

    private static void carregarConjuncoes() {
        adicionar("e, nem, não só...mas também, tampouco, tanto...quanto", "aditiva");
        adicionar("mas, porém, contudo, todavia, no entanto, entretanto, não obstante", "adversativa");
        adicionar("ou, ou...ou, ora...ora, já...já, quer...quer, seja...seja", "alternativa");
        adicionar("logo, pois, portanto, por conseguinte, assim, então, por isso", "conclusiva");
        adicionar("pois, que, porque, porquanto", "explicativa");
        adicionar("pois, porque, visto que, como, uma vez que, na medida em que, haja vista que, já que", "causal");
        adicionar("tão...que, de modo que, de maneira que, tamanho...que, tanto...que, tal...que", "consecutiva");
        adicionar("embora, conquanto, não obstante, ainda que, mesmo que, se bem que, posto que, por mais que, por pior que, apesar de que, a despeito de, malgrado, em que pese",
                "concessiva");
        adicionar("como, mais...do que, menos...do que, tão...como, tanto...quanto, tão...quanto, assim como",
                "comparativa");
        adicionar("se, caso, sem que, se não, a não ser que, exceto se, a menos que, contanto que, salvo se, desde que",
                "condicional");
        adicionar("conforme, consoante, como, segundo", "conformativa");
        adicionar("para, para que, a fim de que, de modo que, de forma que, de sorte que, porque", "final");
        adicionar("à proporção que, à medida que, quanto mais, ao passo que", "proporcional");
        adicionar("quando, enquanto, assim que, logo que, desde que, até que, mal, depois que, eis que", "temporal");
        adicionar("porque, visto que, pois, uma vez que, que, já que, porquanto", "explicativas");
    }

    private static void adicionar(String conjuncoes, String tipo) {
        for (String conjuncao : conjuncoes.split(",")) {
            mapaConjuncoes.put(conjuncao.trim().toLowerCase(), tipo);
        }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                result.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return result;
    }
}
