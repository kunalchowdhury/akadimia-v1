package com.akadimia.microservices.core.user;

import com.akadimia.core.event.Event;
import com.akadimia.core.session.Session;
import com.akadimia.core.user.User;
import com.akadimia.microservices.core.user.persistence.UserRepository;
import com.akadimia.util.exceptions.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static com.akadimia.core.event.Event.Type.CREATE;
import static com.akadimia.core.event.Event.Type.DELETE;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class UserServiceApplicationTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private UserRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getProductById() {

		String userId = "u124";

		assertNull(repository.findByUserId(userId).block());
		assertEquals(0, (long)repository.count().block());

		sendCreateUserEvent(userId);

		assertNotNull(repository.findByUserId(userId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyProduct(userId, OK)
            .jsonPath("$.userId").isEqualTo(userId);
	}

	@Test
	public void duplicateError() {

		String userId = "u123";

		assertNull(repository.findByUserId(userId).block());

		sendCreateUserEvent(userId);

		assertNotNull(repository.findByUserId(userId).block());

		try {
			sendCreateUserEvent(userId);
			//fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, User Id: " + userId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteProduct() {

		String userId = "u123";

		sendCreateUserEvent(userId);
		assertNotNull(repository.findByUserId(userId).block());

		sendDeleteUserEvent(userId);
		assertNull(repository.findByUserId(userId).block());

		sendDeleteUserEvent(userId);
	}

	@Test
	public void getProductInvalidParameterString() {

		getAndVerifyProduct("/no-integer", NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/user/no-integer")
            .jsonPath("$.message").isEqualTo("No user found for userId: no-integer");
	}

	@Test
	public void getProductNotFound() {

		String productIdNotFound = "113";
		getAndVerifyProduct(productIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/user/" + productIdNotFound)
            .jsonPath("$.message").isEqualTo("No user found for userId: " + productIdNotFound);
	}

	@Test
	public void getProductInvalidParameterNegativeValue() {

        String userIdInvalid = " ";

		getAndVerifyProduct(userIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/user/%20")
            .jsonPath("$.message").isEqualTo("Invalid userId:");
	}

	private WebTestClient.BodyContentSpec getAndVerifyProduct(String userIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("/user/" + userIdPath)
			.accept(APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON_UTF8)
			.expectBody();
	}

	private void sendCreateUserEvent(String userId) {
		/*User api = new User(userId, "Kunal" , "Chowdhury", "kunalkumar.chowdhury@gmail.com",
				"Karnakata", "Bangalore", 719009, 9742652223L, true, "abragasdkk akkkk a", "www.youtube.com");

		Event<Integer, User> event = new Event(CREATE, userId, api);
		input.send(new GenericMessage<>(event));
*/
		/*
		* @RequestParam(value = "firstName", required = true) String firstName,
                       @RequestParam(value = "lastName", required = true) String lastName,
                       @RequestParam(value = "emailId", required = true) String emailId,
                       @RequestParam(value = "state", required = true) String state,
                       @RequestParam(value = "city", required = true) String city,
                       @RequestParam(value = "zipCode", required = true) int zipCode,
                       @RequestParam(value = "instructor", required = true) boolean instructor,
                       @RequestParam(value = "briefDescription", required = false) String briefDescription,
                       @RequestParam(value = "briefDescription", required = true
		* */

		byte[] responseBody = client.get()
				.uri(uriBuilder -> {
					try {
						URI build = uriBuilder
								.path("/userc/")
								.queryParam("userId", userId)
								.queryParam("firstName", "Kunal")
								.queryParam("lastName", "Chowdhury")
								.queryParam("emailId", "kunalkumar.chowdhury@gmail.com")
								.queryParam("state", "Karnataka")
								.queryParam("city", "Bangalore")
								.queryParam("zipCode", 780016)
								.queryParam("phone", 9742652223L)
								.queryParam("instructor", true)
								.queryParam("briefDescription", "first time teacher")
								.queryParam("youTubeURL", "www.youtube.com")
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

	private void sendDeleteUserEvent(String productId) {
		Event<Integer, User> event = new Event(DELETE, productId, null);
		input.send(new GenericMessage<>(event));
	}
}