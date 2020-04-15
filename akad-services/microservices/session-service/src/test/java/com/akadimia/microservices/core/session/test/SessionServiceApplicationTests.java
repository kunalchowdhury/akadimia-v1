package com.akadimia.microservices.core.session.test;

import com.akadimia.core.event.Event;
import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.session.persistence.SessionRepository;
import com.akadimia.microservices.core.session.services.SessionMapper;
import com.akadimia.util.exceptions.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static com.akadimia.core.event.Event.Type.CREATE;
import static com.akadimia.core.event.Event.Type.DELETE;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class SessionServiceApplicationTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private SessionRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	private SessionMapper mapper = Mappers.getMapper(SessionMapper.class);
	@Test
	public void getSessionById() {

		String sessionId = "x123";

		assertNull(repository.findBySessionId(sessionId).block());
		assertEquals(0, (long)repository.count().block());


		//assertNotNull(repository.findBySessionId(sessionId).block());
		//assertEquals(1, (long)repository.count().block());

		//getAndVerifyProduct(sessionId, OK)
          //  .jsonPath("$.sessionId").isEqualTo(sessionId);
	}

	@Test
	public void getMultiParameterString() {

		sendCreateSessionEvent1("a123");
		sendCreateSessionEvent2("b123");
		sendCreateSessionEvent3("c123");
		sendCreateSessionEvent4("d123");
		sendCreateSessionEvent5("e123");
		sendCreateSessionEvent6("f123");
		sendCreateSessionEvent7("g123");
		sendCreateSessionEvent8("h123");



		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		WebTestClient.ListBodySpec<Session> bodyContentSpec = client.get()
				.uri(uriBuilder -> {
					try {
						return uriBuilder
								.path("/session/searchInstructor/")
								.queryParam("subject", "lifesciences")
								.queryParam("fromDate", System.currentTimeMillis())
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("latitude", 91.7465364)
								.queryParam("longitude", 26.1235353)
								.queryParam("radius", 10)
								.build();
					} catch (Exception e) {
						e.printStackTrace();
					}
				return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange()
				.expectBodyList(Session.class);
				//.expectStatus().isEqualTo(expectedStatus)
				//.expectHeader().contentType(APPLICATION_JSON_UTF8)
				//.expectBody();

		bodyContentSpec.isEqualTo(Arrays.asList(mapper.entityToApi(repository.findBySessionId("f123").block()),
				mapper.entityToApi(repository.findBySessionId("g123").block())));



		WebTestClient.ListBodySpec<Session> bodyContentSpec1 = client.get()
				.uri(uriBuilder -> {
					try {
						return uriBuilder
								.path("/session/searchInstructor/")
								.queryParam("subject", "english")
								.queryParam("fromDate", System.currentTimeMillis())
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("latitude", 77.600314)
								.queryParam("longitude", 12.9043771)
								.queryParam("radius", 10)
								.build();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange()
				.expectBodyList(Session.class);

		bodyContentSpec1.isEqualTo(Arrays.asList(mapper.entityToApi(repository.findBySessionId("a123").block()),
				mapper.entityToApi(repository.findBySessionId("b123").block()),
				mapper.entityToApi(repository.findBySessionId("c123").block())));


		//System.out.println(bodyContentSpec);
	}


	@Test
	public void duplicateError() {

		String sessionId = "x123";

		assertNull(repository.findBySessionId(sessionId).block());

		sendCreateSessionEvent(sessionId);

		assertNotNull(repository.findBySessionId(sessionId).block());

		try {
			sendCreateSessionEvent(sessionId);
			//fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Session Id: " + sessionId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteSession() {

		String sessionId = "x123";

		sendCreateSessionEvent(sessionId);
		assertNotNull(repository.findBySessionId(sessionId).block());

		sendDeleteProductEvent(sessionId);
		assertNull(repository.findBySessionId(sessionId).block());

		sendDeleteProductEvent(sessionId);
	}

	@Test
	public void getProductInvalidParameterString() {

		getAndVerifyProduct("/no-integer", NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/session/no-integer")
            .jsonPath("$.message").isEqualTo("No session found for sessionId: no-integer");
	}

	@Test
	public void getProductNotFound() {

		String sessionIdPath = "13";
		getAndVerifyProduct(sessionIdPath, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/session/" + sessionIdPath)
            .jsonPath("$.message").isEqualTo("No session found for sessionId: " + sessionIdPath);
	}

	@Test
	public void getProductInvalidParameterNegativeValue() {

		String sessionIdNotFound = " ";

		JsonPathAssertions pathAssertions = getAndVerifyProduct(sessionIdNotFound, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path");
		pathAssertions.isEqualTo("/session/%20")
            .jsonPath("$.message").isEqualTo("Invalid sessionId:" + sessionIdNotFound.trim());
	}

	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return getAndVerifyProduct("/" + productId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyProduct(String sessionIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("/session/" + sessionIdPath)
			.accept(APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON_UTF8)
			.expectBody();
	}

	private void sendCreateSessionEvent(String sessionId) {
		/*Session session =new Session("subject",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "english")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "kunalkumar.chowdhury@gmail.com")
								.queryParam("area", "Bangalore")
								.queryParam("lat", 3.02)
								.queryParam("lon", 4.50)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();


		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent1(String sessionId) {
		/*Session session =new Session("english",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalchowdhury@gmail.com",
				"Purva Heights, Bangalore",
				new double[]{77.603443, 12.9040269 },
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "english")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "kunalkumar.chowdhury@gmail.com")
								.queryParam("area", "Purva Heights, Bangalore")
								.queryParam("lat", 77.603443)
								.queryParam("lon", 12.9040269)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));

	}

	private void sendCreateSessionEvent2(String sessionId) {
		/*Session session =new Session("english",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"rafel.s@gmail.com",
				"Dollars Colony, Bangalore",
				new double[]{77.600314, 12.9043771},
				"www.youtube1.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "english")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "rafel.s@gmail.com")
								.queryParam("area", "Dollars Colony, Bangalore")
								.queryParam("lat", 77.600314)
								.queryParam("lon", 12.9043771)
								.queryParam("youTubeURL", "www.youtube1.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent3(String sessionId) {
		/*Session session =new Session("english",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"mkumar.chowdhury@gmail.com",
				"JP Nagar, Near Dollars Colony",
				new double[]{77.6004, 12.9043771},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "english")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "mkumar.chowdhury@gmail.com")
								.queryParam("area", "JP Nagar, Near Dollars Colony")
								.queryParam("lat", 77.6004)
								.queryParam("lon", 12.9043771)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent4(String sessionId) {
		/*Session session =new Session("chemistry",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"prabal.chowdhury@gmail.com",
				"Ulubari, Guwahati",
				new double[]{91.7466669, 26.1705325},
				"www.pytube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "chemistry")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "prabal.chowdhury@gmail.com")
								.queryParam("area", "Ulubari, Guwahati")
								.queryParam("lat", 91.7466669)
								.queryParam("lon",  26.1705325)
								.queryParam("youTubeURL", "www.pytube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent5(String sessionId) {
		/*Session session =new Session("chemistry",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"jon.rushmore@gmail.com",
				"Chatribari, Guwahati",
				new double[]{26.1755325,91.7467669},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "chemistry")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "jon.rushmore@gmail.com")
								.queryParam("area", "Chatribari, Guwahati")
								.queryParam("lat", 26.1755325)
								.queryParam("lon",  91.7467669)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent6(String sessionId) {
		/*Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"m.choudhury@gmail.com",
				"Saukuchi,Guwahati",
				new double[]{91.7463364, 26.1231353},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "lifesciences")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "m.choudhury@gmail.com")
								.queryParam("area", "Saukuchi,Guwahati")
								.queryParam("lat", 91.7463364)
								.queryParam("lon",  26.1231353)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent7(String sessionId) {
		/*Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"g.nath@gmail.com",
				"Rajgarh Road,Guwahati",
				new double[]{91.769381, 26.1725287},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "lifesciences")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "g.nath@gmail.com")
								.queryParam("area", "Rajgarh Road,Guwahati")
								.queryParam("lat", 91.769381)
								.queryParam("lon",  26.1725287)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}

	private void sendCreateSessionEvent8(String sessionId) {
		/*Session session =new Session("hindi",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"r.sharma@gmail.com",
				"Panathur, Bangalore",
				new double[]{12.9364818,77.695682},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));*/

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/sessionc/")
								.queryParam("subject", "hindi")
								.queryParam("fromDate", System.currentTimeMillis() )
								.queryParam("toDate", System.currentTimeMillis())
								.queryParam("userId", "r.sharma@gmail.com")
								.queryParam("area", "Panathur, Bangalore")
								.queryParam("lat", 12.9364818)
								.queryParam("lon",  77.695682)
								.queryParam("youTubeURL", "www.youtube.com")
								.queryParam("sessionId", sessionId)
								.build();
						System.out.println(build);
						return build;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}).accept(APPLICATION_JSON_UTF8)
				.exchange().expectBody().returnResult().getResponseBody();
		System.out.println("====== >>>>>" +new String(responseBody));
	}
/*

	private void sendCreateSessionEvent9(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent10(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent11(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent12(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent13(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent14(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent15(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}

	private void sendCreateSessionEvent16(String sessionId) {
		Session session =new Session("lifesciences",
				System.currentTimeMillis() ,
				System.currentTimeMillis(),
				"kunalkumar.chowdhury@gmail.com",
				"Bangalore",
				new double[]{3.02, 4.50},
				"www.youtube.com",
				sessionId);

		Event<Integer, Session> event = new Event(CREATE, sessionId, session);
		input.send(new GenericMessage<>(event));
	}
*/


	private void sendDeleteProductEvent(String sessionId) {
		Event<Integer, Session> event = new Event(DELETE, sessionId, null);
		input.send(new GenericMessage<>(event));
	}
}