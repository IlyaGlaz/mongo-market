import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class MongoBase {

    private static final String DOMAIN = "localhost";
    private static final int PORT = 27017;
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> markets;
    private final MongoCollection<Document> goods;

    public MongoBase() {
        client = new MongoClient(DOMAIN, PORT);
        database = client.getDatabase("custom");
        markets = database.getCollection("markets");
        goods = database.getCollection("goods");
    }

    public void addMarket(String name) {
        if (getQuery(name, 1).length == 1) {
            markets.insertOne(new Document()
                    .append("name", name)
                    .append("list", new BsonArray()));
            System.out.println("Market was added");
        }
    }

    public void addGoods(String name) {
        String[] items = getQuery(name, 2);
        if (items.length == 2) {
            goods.insertOne(new Document()
                    .append("name", items[0])
                    .append("price", items[1]));
            System.out.println("Product was added");
        }
    }

    public void saleGoods(String name) {
        String[] items = getQuery(name, 2);
        if (items.length == 2) {
            markets.updateOne(new Document("name", items[0]),
                    new Document("$push", new Document("list", items[1])));
            System.out.println("Product is on sale");
        }
    }

    public String showInfo() {
        StringBuffer buffer = new StringBuffer();
        AggregateIterable<Document> result = markets.aggregate(List.of(new Document("$lookup",
                        new Document(Map.of("from", "goods",
                                "localField", "list",
                                "foreignField", "name",
                                "as", "market_info"))),
                new Document("$unwind", new Document("path", "$market_info")),
                new Document("$group", new Document(Map.of(
                        "_id", new Document("name", "$name"),
                        "total", new Document("$sum", 1),
                        "avgPrice", new Document("$avg", "$market_info.price"),
                        "minPrice", new Document("$min", "$market_info.price"),
                        "maxPrice", new Document("$max", "$market_info.price")
                )))));
        for (Document document : result) {
            buffer.append("\n" + document.toJson());
        }
        System.out.println(buffer);
        return buffer.toString();
    }

    private String[] getQuery(String query, int number) {
        return query.split(" ", number);
    }

    public void close() {
        client.close();
    }
}
