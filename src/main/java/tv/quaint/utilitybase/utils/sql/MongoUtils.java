package tv.quaint.utilitybase.utils.sql;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.config.storage.ConfiguredMongo;
import tv.quaint.utilitybase.config.storage.StorageComponent;
import tv.quaint.utilitybase.config.storage.StorageManager;
import tv.quaint.utilitybase.utils.MongoSettingsKey;

public class MongoUtils {
    public static MongoClient client;
    public static MongoDatabase database;

    public static MongoDatabase loadDatabase() {
        ConfiguredMongo configuredMongo = PluginBase.CONFIG.configuredMongo;
        MongoClientURI uri = new MongoClientURI(configuredMongo.getParsedUri());
        client = new MongoClient(uri);

        MongoDatabase db = client.getDatabase(configuredMongo.name);
        return db;
    }

    public static MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName); // Gets the collection.
        return collection;
    }

    public static Object getData(String searchKey, String searchValue, String targetData, String collectionname) {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionname);
        Object data = null;
        if(collection.find(Filters.eq(searchKey, searchValue)).first() != null)
            data = collection.find(Filters.eq(searchKey, searchValue)).first().get(targetData);
        return data;
    }

    public static Document getDocWithIdentifier(String identifierKey, Object identifierValue, String collectionName) throws Exception {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionName);

        for (Document document : collection.find()) {
            if (document.get(identifierKey).equals(identifierKey)) return document;
        }

        throw new Exception("Document not found with key '" + identifierKey + "' and value '" + identifierValue + "' in '" + collectionName + "'!");
    }

    public static void updateDocument(Document document, String identifierKey, String collectionName) {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionName);

        for (Document doc : collection.find()) {
            if (document.getString(identifierKey).equals(doc.getString(identifierKey))) {
                collection.updateOne(doc, document);
            }
        }
    }

    public static void saveStorageComponent(StorageComponent component) {
        MongoCollection<Document> collection = getCollection(database, "balances");

        Document query = new Document();
        Document setData = new Document();
        Document update = new Document();

        query.put(MongoSettingsKey.UUID.string, component.key);
        setData.append(MongoSettingsKey.BALANCE.string, component.value);
        update.append("$set", setData);

        collection.updateOne(query, update);
    }

    public static void createFirstStorageComponent(StorageComponent component) {
        MongoCollection<Document> collection = getCollection(database, "balances");

        Document set = new Document();
        set.append(MongoSettingsKey.UUID.string, component.key);
        set.append(MongoSettingsKey.BALANCE.string, component.value);

        collection.insertOne(set);
    }

    public static double getStorageComponentValue(String key) {
        MongoCollection<Document> collection = getCollection(database, "balances");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(MongoSettingsKey.UUID.string, key);
        try {
            if (collection.find(whereQuery).first() == null) {
                StorageManager.createFirstComponent(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StorageManager.createFirstComponent(key);
        }
        Document doc = collection.find(whereQuery).first();
        if (doc == null) return 0d;

        return doc.getDouble(MongoSettingsKey.BALANCE.string);
    }
}
