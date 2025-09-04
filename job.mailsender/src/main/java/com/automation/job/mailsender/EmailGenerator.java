package com.automation.job.mailsender;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmailGenerator {

	public List<Map<String, String>> email_generator(String jobPost) throws Exception {

		/*
		 * This is our API key for Hugging Face, it allows us to authenticate and use
		 * the DeepSeek model.
		 */
		String pathToken = "C:\\Users\\touhafi\\eclipse-workspace\\HaggingFaceToken.txt";
		String token = Files.readString(Paths.get(pathToken)).trim();
		String hfToken = token;


		/* This ArrayList contains a list of maps which contain the generated emails */
		List<Map<String, String>> last_data = new ArrayList<Map<String, String>>();


		/* This is our ai prompt */
		String prompt = "You are an AI that generates professional job application emails. "
				+ "Based on the language of the job offer (French or English), generate a JSON object with the following keys:\n\n"
				+ "1. subject: The subject of the email. Use the job post's subject if mentioned, otherwise create one based on the job position.\n"
				+ "2. message: The full email message including a short introduction, mention of attached CV and motivation letter, "
				+ "   and a polite closing signature with my name Touhafi Mouad and phone +212649244322. If you need infos about me to add,"
				+ "   use this : i am TOUHAFI Mouad, i have 3 and half years of experience in the automotive industry and also as automation engineer."
				+ "3. receiver_email: The email address of the receiver if mentioned in the job post; otherwise null.\n\n"
				+ "Rules:\n" + "- Do not leave any blank fields.\n" + "- Use a professional and polite tone.\n"
				+ "- Avoid uncertain information.\n\n" + "Job posting:\n" + jobPost + "\n\n"
				+ "Output ONLY the JSON object without extra text and use [receiver_email, message, subject] as json keys."
				+ "And if the offer contains more than one job, for each job you must generate a JSON object, separate them by commas like a real JSON file.";

		/*
		 * - The line ObjectMapper mapper = new ObjectMapper(); creates a tool from the
		 * Jackson library that allows us to work with JSON in Java, letting us build,
		 * read, and convert JSON objects. - The line ObjectNode root =
		 * mapper.createObjectNode(); creates an empty JSON object, equivalent to {} in
		 * JSON, which will hold the data we send to the Hugging Face API. - Then,
		 * root.put("model", "deepseek-ai/DeepSeek-V3.1:fireworks-ai"); adds a key-value
		 * pair to this object, so now it contains "model":
		 * "deepseek-ai/DeepSeek-V3.1:fireworks-ai". This is necessary because the API
		 * expects a JSON object with a "model" field specifying which AI model to use,
		 * and later we will add the "messages" array containing our prompt. We can
		 * think of it like writing a letter to the AI: the ObjectMapper is our pen,
		 * root is the blank paper, and root.put(...) writes the first line telling the
		 * AI which model to use, while additional lines (the messages) are added
		 * afterward. - It builds something like : { "model": "MODEL_NAME", }
		 */
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		root.put("model", "deepseek-ai/DeepSeek-V3.1:fireworks-ai");

		/*
		 * This block of code builds the "messages" part of the JSON that the Hugging
		 * Face API expects. - ArrayNode messages = mapper.createArrayNode(); creates an
		 * empty JSON array ([]) to hold one or more messages. - ObjectNode userMessage
		 * = mapper.createObjectNode(); creates a JSON object ({}) representing a single
		 * message. - Then, userMessage.put("role", "user"); adds a "role" key with
		 * value "user" to indicate that this message is coming from the user. - The
		 * userMessage.put("content", prompt); adds the "content" key with your actual
		 * prompt text. - messages.add(userMessage); places this message object inside
		 * the array, so the array now contains your prompt. - Finally,
		 * root.set("messages", messages); attaches the "messages" array to the
		 * top-level JSON object (root), resulting in a JSON structure like { "model":
		 * "...", "messages": [ { "role": "user", "content": "..." } ] } that the API
		 * can understand.
		 */
		ArrayNode messages = mapper.createArrayNode();
		ObjectNode userMessage = mapper.createObjectNode();
		userMessage.put("role", "user");
		userMessage.put("content", prompt);
		messages.add(userMessage);

		/*
		 * The reason we use root.set("messages", messages); instead of .put is that
		 * .put works only for simple values like strings, numbers, or booleans, whereas
		 * .set is used when the value is a JSON object or array, such as an ObjectNode
		 * or ArrayNode. In this case, messages is an array of JSON objects, not a
		 * simple string, so .set is required to attach it correctly to the top-level
		 * JSON object. In contrast, root.put("model", "deepseek-ai/DeepSeek-V3.1");
		 * works with .put because the value is just a string. The general rule is: use
		 * .put for simple values and .set for objects or arrays.
		 */
		root.set("messages", messages);

		/*
		 * This line, String requestBody = mapper.writeValueAsString(root);, converts
		 * the entire JSON object we’ve built (root) into a JSON-formatted string that
		 * can be sent over HTTP. In Java, we can’t directly send an ObjectNode or
		 * ArrayNode in an HTTP request; the server expects a string in proper JSON
		 * format. writeValueAsString(root) takes your top-level object, including all
		 * nested objects and arrays like "messages", and turns it into something like :
		 * 
		 * { 
		 * 		"model": "deepseek-ai/DeepSeek-V3.1:fireworks-ai", 
		 * 		"messages": [ 
		 * 			{ 
		 * 				"role": "user", 
		 * 				"content": "Write a short professional recruiter email..." 
		 * 			} 
		 * 		] 
		 * }
		 *
		 */
		String requestBody = mapper.writeValueAsString(root);

		/*
		 * This block builds the HTTP request that our Java program sends to Hugging
		 * Face. - Using HttpRequest.newBuilder(), we start creating a new request. -
		 * The .uri(URI.create("https://router.huggingface.co/v1/chat/completions"))
		 * sets the endpoint URL of the Hugging Face chat completions API, where the
		 * request will be sent. - The .header("Authorization", "Bearer " + hfToken)
		 * adds our API key so the server knows which account is making the request. -
		 * The .header("Content-Type", "application/json") tells the server that we are
		 * sending JSON data. - The
		 * .POST(HttpRequest.BodyPublishers.ofString(requestBody)) specifies that this
		 * is a POST request and includes the JSON string (requestBody) as the body. -
		 * Finally, .build() creates the actual HttpRequest object that can be sent by
		 * the HTTP client. - Essentially, this line prepares a fully formed request
		 * with the URL, headers, and JSON body, ready to go to the Hugging Face API.
		 */
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://router.huggingface.co/v1/chat/completions"))
				.header("Authorization", "Bearer " + hfToken).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

		/*
		 * This line sends the HTTP request to the Hugging Face API and waits for a
		 * response. - client.send(request, HttpResponse.BodyHandlers.ofString()) uses
		 * the HttpClient we created earlier to send the request we built. -
		 * HttpResponse.BodyHandlers.ofString() tells Java to read the response body as
		 * a plain string. - The result, stored in response, contains both the HTTP
		 * status code (like 200 for success) and the body of the response, which in
		 * this case is the JSON generated by the AI. Essentially, this line is where
		 * our program actually talks to Hugging Face and retrieves the AI’s output.
		 */
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		/*
		 * This block of code checks the response from the Hugging Face API and extracts
		 * the generated email message. - First, if (response.statusCode() == 200)
		 * ensures the request was successful (HTTP status 200). - JsonNode rootNode =
		 * mapper.readTree(response.body()); parses the JSON string returned by the API
		 * into a JSON object that Java can navigate. - JsonNode choices =
		 * rootNode.get("choices"); gets the "choices" array, which contains the AI’s
		 * responses. - The next lines check that choices exists, is an array, and has
		 * at least one item, then extract the first response’s "message" object. - If
		 * that object exists and contains "content", the code reads it as a string
		 * using .asText() and stores it in generatedEmail. - Finally, it prints the
		 * generated email. - If the HTTP status is not 200, the else block prints an
		 * error message with the status code. - In short, this part safely retrieves
		 * the AI’s output from the response and prints it, or reports an error if the
		 * request failed.
		 */
		if (response.statusCode() == 200) {
			JsonNode rootNode = mapper.readTree(response.body());
			JsonNode choices = rootNode.get("choices");
			if (choices != null && choices.isArray() && choices.size() > 0) {
				JsonNode messageNode = choices.get(0).get("message");
				if (messageNode != null && messageNode.has("content")) {
					String generatedEmail = messageNode.get("content").asText();

					/* Extracting the json response from the ai response */
					int startIndex = generatedEmail.indexOf("{");
					int endIndex = generatedEmail.lastIndexOf("}") + 1;
					if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
						String jsonString = generatedEmail.substring(startIndex, endIndex);

						/*
						 * Checking if the ai-generated json contains one block or more than one block
						 */
						char charTofind = '{';
						int charRepetition = 0;
						for (char c : jsonString.toCharArray()) {
							if (c == charTofind) {
								charRepetition++;
							}
						}

						/*
						 * If there is more than one json block, we need to surround them with [ ] to
						 * respect the json format [ {}, {} ]
						 */
						if (charRepetition > 1) {
							jsonString = "[" + jsonString + "]";
						}

						/*
						 * This code parses jsonString into a JsonNode using Jackson and converts it
						 * into a list of maps (last_data). It first checks whether the jsonStringToJSON
						 * JsonNode is an array or a single object. - If it’s an array, it iterates
						 * through each element, which represents a JSON object, extracts all key-value
						 * pairs as strings, stores them in a HashMap, and adds each map to last_data. -
						 * If the root is a single object, it directly iterates over its fields,
						 * collects the key-value pairs into a map, and adds it to last_data. This way,
						 * the code handles both JSON arrays of objects and standalone JSON objects,
						 * producing a consistent List<Map<String, String>> representation.
						 */
						try {
							JsonNode jsonStringToJSON = mapper.readTree(jsonString);

							if (jsonStringToJSON.isArray()) {
								for (JsonNode jsonNode : jsonStringToJSON) {
									Map<String, String> map_post_infos = new HashMap<>();
									Iterator<String> fieldNames = jsonNode.fieldNames();
									while (fieldNames.hasNext()) {
										String key = fieldNames.next();
										String value = jsonNode.get(key).asText();
										map_post_infos.put(key, value);
									}
									last_data.add(map_post_infos);
								}
							} else if (jsonStringToJSON.isObject()) {
								Map<String, String> map_post_infos = new HashMap<>();
								Iterator<String> fieldNames = jsonStringToJSON.fieldNames();
								while (fieldNames.hasNext()) {
									String key = fieldNames.next();
									String value = jsonStringToJSON.get(key).asText();
									map_post_infos.put(key, value);
								}
								last_data.add(map_post_infos);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						System.out.println("No valid JSON found in the response.");
					}
				}
			}
		} else {
			System.err.println("Error calling API for Job. Status: " + response.statusCode());
		}
		Thread.sleep(500);
		return last_data;
	}
}
