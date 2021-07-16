package com.informationsys.consultaPokemon;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.informationsys.main.RespostaHttp;

public class ConsultaPokemon {

	// Melhores praticas ** getInstance()- Garante que a classe seja criada uma
	// unica vez
	private static ConsultaPokemon instance = null;

	public static ConsultaPokemon getInstance() {
		if (instance == null) {
			instance = new ConsultaPokemon();
		}
		return instance;
	}

	// Loggers
	private static final Logger logger = LoggerFactory.getLogger(ConsultaPokemon.class);

	public JSONObject executeConsultaPokemon(String id_pokemon, String ambiente, String ip, String ura,
			String uniqueID) {

		JSONObject resultado = null;
		logger.debug("{} - Ambiente selecionado: {}", uniqueID, ambiente);

		if (ambiente == null)
			ambiente = "Producao";

		switch (ambiente) {
		case "Homologacao":
			resultado = prodConsultaPokemon(id_pokemon, ambiente, ip, ura, uniqueID);
			// resultado = homologConsultaPokemon(nome_pokemon, ambiente, ip, ura,
			// uniqueID);
			break;
		case "QA":
			resultado = prodConsultaPokemon(id_pokemon, ambiente, ip, ura, uniqueID);
			// resultado = qaConsultaPokemon(nome_pokemon, ambiente, ip, ura, uniqueID);
			break;
		case "Producao":
			resultado = prodConsultaPokemon(id_pokemon, ambiente, ip, ura, uniqueID);
			break;
		default:
			resultado = prodConsultaPokemon(id_pokemon, ambiente, ip, ura, uniqueID);
			break;
		}

		return resultado;
	}

	public JSONObject prodConsultaPokemon(String id_pokemon, String ambiente, String ip, String nome_ura,
			String uniqueID) {

		RespostaHttp resposta = new RespostaHttp();

		String retorno = "";
		String nome = "";
		String idpokemon = "";
		String ura = "";
		String peso = "";
		String altura = "";
		ArrayList<String> habilidades = new ArrayList<String>();
		JSONArray status = new JSONArray();
		float tempoResposta = 0;
		int statusCode = 0;

		JSONObject retornoHttp = null;

		HttpURLConnection conn = null;

		try {

			URL cURL = null;
			 
			logger.info("{} - URL: {}", uniqueID, cURL);

			/* Abre conexï¿½o com a URL informada */
			conn = (HttpURLConnection) cURL.openConnection();

			/* Monta o Header da requisicao */
			conn.addRequestProperty("content-type", "application/x-www-form-urlencoded");
			conn.addRequestProperty("user-agent", "");
			// tempo inicial da chamada do servico
			long tempInicial = System.currentTimeMillis();

			/*
			 * Trata retorno HTTP para saber se houve um erro na requisiï¿½ï¿½o ou se
			 * retornou o esperado.
			 */
			retornoHttp = resposta.respostaHttp(conn);
			// logger.debug("{} - Resposta retornoHttp: {}", uniqueID, retornoHttp);

			// tempo final da chamada do servico
			long tempFinal = System.currentTimeMillis();

			long dif = (tempFinal - tempInicial);

			logger.info("{} - Tempo para consultar o servico: {} ms", uniqueID, dif);

			tempoResposta = dif;

			/* Fecha conexï¿½o */
			conn.disconnect();

			statusCode = (Integer) retornoHttp.get("codigo");
			logger.info("{} - Status code: {}", uniqueID, statusCode);

			// Verifica se houve um erro HTTP
			if (statusCode != 200) {
				if (statusCode == 404) {
					retorno = "0";
				} else {
					retorno = "1";
				}
			} else {

				JSONObject jsonObject = new JSONObject((String) retornoHttp.get("resposta"));
				// logger.info(jsonObject.toString());

				// Verifica a altura do Pokemon
				altura = String.valueOf(jsonObject.getDouble("height") / 10);

				// Verifica o peso do Pokemon
				peso = String.valueOf(jsonObject.getDouble("weight") / 10);

				// Verifica o nome do Pokemon
				nome = jsonObject.get("name").toString();

				// Verifica as Status do Pokemon
				// name a key e base_state o valor

				JSONArray statsArray = jsonObject.getJSONArray("stats");

				if (statsArray != null) {

					for (int i = 0; i < statsArray.length(); i++) {

						JSONObject stats = statsArray.getJSONObject(i);

						Object baseStat = stats.get("base_stat");

						String nameStat = stats.getJSONObject("stat").getString("name");

						JSONObject newStat = new JSONObject();

						newStat.put(nameStat, baseStat);

						status.put(newStat);

					}
				}

				// Verifica as habilidades do Pokemon

				JSONArray habArray = jsonObject.getJSONArray("abilities");

				if (habArray != null) {
					for (int i = 0; i < habArray.length(); i++) {

						JSONObject object2 = habArray.getJSONObject(i).getJSONObject("ability");

						String name = object2.getString("name");

						habilidades.add(name);
					}
				}

			}
		} catch (Exception e) {
			retorno = "9";
			logger.error("{} - erro: {}", uniqueID, retorno);
			logger.error("{} - erro: {}", uniqueID, e.toString());
		}

		logger.info("{} - nome: {}", uniqueID, nome);

		if (statusCode == 200 || statusCode == 201)
			retorno = "OK";
		else
			retorno = "NOK";

		JSONObject result;

		if (statusCode == 200) {
			ConsultaPokemon_Result response = new ConsultaPokemon_Result(200, id_pokemon, retorno, nome, peso, altura,
					habilidades, status, ura);
			result = response.getObject();
		} else if (statusCode == 404) {
			ConsultaPokemon_Result response = new ConsultaPokemon_Result(201, id_pokemon, retorno, nome, peso, altura,
					habilidades, status, ura);
			result = response.getObject();
		} else {
			ConsultaPokemon_Result response = new ConsultaPokemon_Result(202, id_pokemon, retorno, nome, peso, altura,
					habilidades, status, ura);
			result = response.getObject();
		}
		// result.put("retornoHttp", retornoHttp);
		return result;
	}

}