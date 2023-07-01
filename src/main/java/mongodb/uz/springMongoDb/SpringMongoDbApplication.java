package mongodb.uz.springMongoDb;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import mongodb.uz.springMongoDb.document.Address;
import mongodb.uz.springMongoDb.document.Users;
import org.bson.Document;
import org.bson.conversions.Bson;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpringMongoDbApplication {

	public static void main(String[] args) {

//		System.out.println(insert());

		
//		findByZipCodelastObject("4","9");

//		findGeoLatMinus();

//		findCom();

//		update(2, "hello");
	}

	private static boolean  insert(){
		MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");
		MongoDatabase db = mongoClient.getDatabase("pdpjava");

		MongoCollection<Document> usersCollection = db.getCollection("users");

		try {
			URL url = new URL("https://jsonplaceholder.typicode.com/users");

			ObjectMapper objectMapper = new ObjectMapper();
			List<Users> users = objectMapper.readValue(url, new TypeReference<>() {});

			List<Document> documents = new ArrayList<>();

			for (Users user : users) {

				Document document = new Document();

				document.append("id", user.getId());
				document.append("name", user.getName());
				document.append("email", user.getEmail());
				Address address = user.getAddress();
				document.append("adress", new Document(
						Map.of("street",address.getStreet(),
								"suite", address.getSuite(),
								"city", address.getCity(),
								"zipcode", address.getZipcode(),
								"geo", new Document(Map.of(
										"lat", address.getGeo().getLat(),
										"lng", address.getGeo().getLng()
								))
						)
				));
				document.append("phone", user.getPhone());
				document.append("website", user.getWebsite());
				document.append("company", new Document(Map.of(
						"name", user.getCompany().getName(),
						"catchPhrase", user.getCompany().getCatchPhrase(),
						"bs", user.getCompany().getBs()
				)));

				documents.add(document);

			}

			InsertManyResult insertManyResult = usersCollection.insertMany(documents);

			return insertManyResult.wasAcknowledged();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private static void findByZipCodelastObject(String zipcode1, String zipcode2){
		MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");
		MongoDatabase db = mongoClient.getDatabase("pdpjava");

		MongoCollection<Document> usersCollection = db.getCollection("users");

		Bson filter = Filters.or(Filters.regex("adress.zipcode", ".*"+zipcode1), Filters.regex("adress.zipcode", ".*"+zipcode2));

		FindIterable<Document> documents = usersCollection.find(filter);

		for (Document document : documents) {

			System.out.println(document+"\n");

		}

	}

	private static void findGeoLatMinus(){
		MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");
		MongoDatabase db = mongoClient.getDatabase("pdpjava");

		MongoCollection<Document> usersCollection = db.getCollection("users");

		Bson filter = Filters.regex("adress.geo.lat", "^-");

		FindIterable<Document> documents = usersCollection.find(filter);

		for (Document document : documents) {

			System.out.println(document+"\n");

		}

	}

	private static void findCom(){
		MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");
		MongoDatabase db = mongoClient.getDatabase("pdpjava");

		MongoCollection<Document> usersCollection = db.getCollection("users");

		Bson filter = Filters.regex("adress.geo.lat", ".*com");

		FindIterable<Document> documents = usersCollection.find(filter);

		for (Document document : documents) {

			System.out.println(document+"\n");

		}

	}
	private static void update(int id, String catchPhraseValue){
		MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/?directConnection=true");
		MongoDatabase db = mongoClient.getDatabase("pdpjava");

		MongoCollection<Document> usersCollection = db.getCollection("users");

		Bson filter = Filters.eq("id", id);

		Bson set = Updates.set("company.catchPhrade", catchPhraseValue);

		UpdateResult updateResult = usersCollection.updateOne(filter, set);

		System.out.println("Update Result-> "+updateResult.wasAcknowledged());


	}

}
