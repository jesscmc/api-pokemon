package com.informationsys.consultaPokemon;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.informationsys.authentication.Authentication;
import com.informationsys.main.ErrorResult;
import com.informationsys.monitoracao.MonitoracaoGrafana;


@Path("/")
public class API_ConsultaPokemon {
	
	private static final Logger logger = LoggerFactory.getLogger(API_ConsultaPokemon.class);
	private static final String ACCESS_CONTROL = "Access-Control-Allow-Origin";
	private static final String ENCODING_FORMAT = "utf-8";
	
	@GET
	@Path("/segmentation/consultaPokemon")
	@Consumes("application/x-www-form-urlencoded")
	public Response consulta(@Context HttpServletRequest req,	@QueryParam("usuario") String usuario,
																@QueryParam("senha") String senha,
																@QueryParam("ura") String ura,
																@QueryParam("ambiente") String ambiente,
																@QueryParam("id_pokemon") String id_pokemon) {

		Response resposta = null;

		String uniqueID = UUID.randomUUID().toString();
		logger.info("{} - Solicitacao de consultar pokemon pelo Usuario: {}", uniqueID, usuario);
		logger.info("{} - Solicitacao de consultar pokemon pelo Senha: {}", uniqueID, senha);
		logger.info("{} - Solicitacao de consultar pokemon  pelo Ambiente: {}", uniqueID, ambiente);
		logger.info("{} - Solicitacao de consultar pokemon pelo URA: {}", uniqueID, ura);
		logger.info("{} - Solicitacao de consultar pokemon pelo IP {}", uniqueID, req.getRemoteAddr());
		logger.info("{} - Solicitacao de consultar pokemon pelo ID: {}", uniqueID, id_pokemon);
		
		String ip = req.getRemoteAddr();

		// Verifica-se se as variaveis usuario e senha estão preenchidos
		logger.info("{} - Efetuando Authentication API", uniqueID);
		JSONObject retornoAuthent = null;

		Authentication auth = Authentication.getInstance();
		String resultado = auth.executeAuthentication(ambiente, usuario, senha, uniqueID).toString();
		logger.info("{} - Response: {}", uniqueID, resultado);

		retornoAuthent = new JSONObject(resultado);

		logger.info("{} - retornoAuthent: {}", uniqueID, retornoAuthent);

		if (retornoAuthent.get("code").equals(200)) {
			logger.info("{} - Usuario Autenticado com sucesso - usuario: {}", uniqueID, usuario);
			// Acessa o servico solicitado
			if (id_pokemon == null || id_pokemon.equals("")) {
				logger.warn("{} - Parametro obrigatorio nao informado id do pokemon", uniqueID);
				ErrorResult erro = new ErrorResult(400, "Parametro obrigatorio nao informado");
				resposta = Response.status(erro.getCode()).header(ACCESS_CONTROL, "*").entity(erro.getObject()).encoding(ENCODING_FORMAT).build();
			} else {
				// Servico para consultar ID Pokemon
				ConsultaPokemon consultaPokemon = ConsultaPokemon.getInstance();
				JSONObject retorno = consultaPokemon.executeConsultaPokemon(id_pokemon, ambiente, ip, ura,  uniqueID);
				logger.info("{} - Response obtido: {}", uniqueID, retorno);
				resposta = Response.status(200).header(ACCESS_CONTROL, "*").entity(retorno.toString()).encoding(ENCODING_FORMAT).build();

				//Chama o servico de Monitoracao do Grafana para enviar os dados do servico
				MonitoracaoGrafana grafana = MonitoracaoGrafana.getInstance();
				String retGrafana = grafana.executeEnviarAlertGrafana(ambiente, retorno.getLong("tempoResposta"), ura, retorno.getString("nome"), retorno.getString("url"), req.getRemoteAddr(), retorno.getString("retorno"), uniqueID).toString();
				logger.info("{} - retGrafana: {}", uniqueID, retGrafana);
			}
		} else if (retornoAuthent.get("code").equals(203)) {
			logger.warn("{} - Usuario NAO Autenticado ou Sem permissao de acesso - usuario: {}", uniqueID, usuario);
			ErrorResult erro = new ErrorResult(203, "NOK", "Usuario nao autenticado ou sem permissao de acesso");
			resposta = Response.status(erro.getCode()).header(ACCESS_CONTROL, "*").entity(erro.getObject()).encoding(ENCODING_FORMAT).build();

		} else {
			logger.warn("{} - ERRO ao conectar com Banco de Dados", uniqueID);
			ErrorResult erro = new ErrorResult(401, "NOK", "Sem conexao com Banco de Dados");
			resposta = Response.status(erro.getCode()).header(ACCESS_CONTROL, "*").entity(erro.getObject()).encoding(ENCODING_FORMAT).build();
		}

		return resposta;
	}
}
