package com.informationsys.consultaPokemon;

import org.json.JSONObject;

public class ConsultaPokemonTester {

	public static void main(String[] args) {

		ConsultaPokemon pokemon = new ConsultaPokemon();

		for (int i = 0; i <= 2; ++i) {

			JSONObject jsonObject1 = pokemon.executeConsultaPokemon("37", "ambiente", "ip", "ura", "0123456789");
			JSONObject jsonObject2 = pokemon.executeConsultaPokemon("13", "ambiente", "ip", "ura", "0123456789");
			System.out.println(jsonObject1);
			System.out.println(jsonObject2);
		}

	}
}
