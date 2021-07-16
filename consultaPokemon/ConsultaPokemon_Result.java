package com.informationsys.consultaPokemon;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConsultaPokemon_Result {

		private int statusCode;
		private String id_pokemon;
		private String retorno;
		private String nome;
		private String peso;
		private String altura;
		private List<String> habilidades;
		private JSONArray status;
		private String ura;
		private float tempoResposta;
		private String nomeServico = "ConsultaPokemon";
		private String urlServico;

	protected	ConsultaPokemon_Result(int i, String id_pokemon, String retorno, String nome, String peso,
				String altura, List<String> habilidades, JSONArray status, String ura) {
			
			this.id_pokemon = id_pokemon;
			this.retorno = retorno;
			this.nome = nome;
			this.peso = peso;
			this.altura = altura;
			this.habilidades = habilidades;
			this.status = status;
			this.ura = ura;
				
		}
		
		
		protected JSONObject getObject() {

			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("id_pokemon", id_pokemon);
			result.put("nome", nome);
			result.put("peso", peso + " kg");
			result.put("altura", altura + " m");
			result.put("habilidades", habilidades);
			result.put("direciona_ura", ura);
			result.put("status", status);
			result.put("tempoResposta", tempoResposta);
			result.put("nomeServico", nomeServico);
			result.put("url", urlServico);
			return result;
		}
	}

