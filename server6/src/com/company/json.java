package com.company;

/**
* Сохранение коллекции в заданном формате (json — поэтому и имя класса такое)
 */

import Classes.SpaceMarine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Vector;

public class json {

    private static final Logger logger = LoggerFactory.getLogger(json.class);
    private static String filePath = System.getenv("WORK_FILE_PATH");
    private static CollectionManager collectionManager = CollectionManager.getCollectionManager();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder
            .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    return ZonedDateTime.parse(in.nextString());
                }
            })
            .serializeNulls()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    public static void SaveCollectionToJson() {
        Gson gson = new Gson();
        //"C:\\Users\\user\\Desktop\\data.txt"
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(CollectionManager.getCollection(), writer);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void JsonToCollection() {
        //"C:\\Users\\user\\Desktop\\field.json"
        if (filePath!=null) {
            try (Reader reader = new FileReader(filePath)) {
                CollectionManager.initializeVector();
                Vector<SpaceMarine> spaceMarines = gson.fromJson(reader,new TypeToken<Vector<SpaceMarine>>(){}.getType());
                if (spaceMarines.size()!=0) {
                    for (SpaceMarine spaceMarine: spaceMarines) {
                        CollectionManager.addFromJson(spaceMarine);
                    }
                }

                logger.info("Сохраненная коллекция выгруженна");

            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (SecurityException e) {
                logger.error("Недостаточно прав для открытия файла.");
            } catch (NullPointerException e) {
                logger.error("В файле нет объектов");
            } catch (com.google.gson.JsonSyntaxException e) {
                logger.error("Ошибка в содержании файла " + e.getMessage());
            }

        } else {
             logger.error("Переменная окружения не выставлена");
        }

    }
}


