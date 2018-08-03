package no.nb.nna.veidemann.brregseedimport.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import no.nb.nna.veidemann.brregseedimport.model.Organization.Organization;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RethinkRepoBrreg implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(RethinkRepoBrreg.class);
    private final String DATABASE_NAME = "brreg";
    private final String IMPORT_TABLE = "brreg_import";
    private final String HISTORIC_TABLE = "brreg_import_history";
    private final String MD5SUM_TABLE = "importfil_md5sum";
    private final String ORGANISASJONSNUMMER = "organisasjonsnummer";

    public static final String INITIELL_DATABASE_OPPRETTET = "initiell_import";
    private static final RethinkDB r = RethinkDB.r;
    private Connection connection;


    private String dbHost;
    private int dbPort;

    /**
     * Oppkobling mot databasen
     */


    public RethinkRepoBrreg(String host, int port, String user, String password) {
        logger.info("Starting up RethinkRepository");

        try {
            connection = r.connection().hostname(host).port(port).user(user, password).connect();
            if (!connection.isOpen()) {
                throw new ReqlDriverError("Unable to connect to server at: " + host + " : " + port);
            }

            boolean databaseExist = r.dbList().contains(DATABASE_NAME).run(connection);
            if (!databaseExist) {
                logger.info("Database doesn't exist, will create it.");
                r.dbCreate(DATABASE_NAME).run(connection);
            }

            boolean tableExists = r.db(DATABASE_NAME).tableList().contains(IMPORT_TABLE).run(connection);
            if (!tableExists) {
                logger.info("Import table doesn't exist, we try to create it. Also creating 'organisasjonsnummer'-index.");
                r.db(DATABASE_NAME).tableCreate(IMPORT_TABLE).run(connection);
                r.db(DATABASE_NAME).table(IMPORT_TABLE).indexCreate(ORGANISASJONSNUMMER).run(connection);
            }

            tableExists = r.db(DATABASE_NAME).tableList().contains(MD5SUM_TABLE).run(connection);
            if (!tableExists) {
                logger.info("MD5sum-table doesn't exist, we try to create it.");
                r.db(DATABASE_NAME).tableCreate(MD5SUM_TABLE).run(connection);
                insertLastMd5sumForImportFile(INITIELL_DATABASE_OPPRETTET);
            }

            tableExists = r.db(DATABASE_NAME).tableList().contains(HISTORIC_TABLE).run(connection);
            if (!tableExists) {
                logger.info("Historic table doesn't exist");
                r.db(DATABASE_NAME).tableCreate(HISTORIC_TABLE).run(connection);
            }

        } catch (ReqlDriverError error) {
            logger.info("unable to connect to server " + host + " for port: " + port);
            throw error;
        }
    }

    public boolean isConnected() {
        logger.info("Connected to Brreg DB");
        return connection != null && connection.isOpen();
    }


    public void connectToDb() {
        connection = r.connection().hostname(dbHost).port(dbPort).connect();


        if (!connection.isOpen()) {
            throw new ReqlDriverError("Unable to connect to host: " + dbHost);
        }
    }

public void insertLastMd5sumForImportFile(String md5sum) {
    HashMap<String, String> map = new HashMap<>();
    map.put("md5sum", md5sum);
    r.db(DATABASE_NAME).table(MD5SUM_TABLE).insert(map).run(connection);
}

    public void updateLastMd5sumForImportFile(JSONObject md5sumObject, String newMd5sum) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", (String) md5sumObject.get("id"));
        map.put("md5sum", newMd5sum);

        r.db(DATABASE_NAME).table(MD5SUM_TABLE).replace(map).run(connection);
    }

    public JSONObject getLastMd5sumForImportFile() {

        Cursor run = r.db(DATABASE_NAME).table(MD5SUM_TABLE).limit(1).run(connection);

        List list = run.toList();
        if (list.size() > 0) {
            Map<String, String> o1 = (Map<String, String>) list.get(0);
            return new JSONObject(o1);
        }
        return new JSONObject();
    }

    /**
     * Skriv ett objekt til databasen
     *
     * @param d objektet vi skal skrive
     */
    public void writeBrregEntryToDb(Organization d) {
        r.db(DATABASE_NAME).table(IMPORT_TABLE).insert(d).run(connection);
    }

    public void moveOldAndInsertNew(Organization d) {
        Cursor organisasjonsnummer = r.db(DATABASE_NAME)
                .table(IMPORT_TABLE)
                .getAll(d.getOrganisasjonsnummer()).optArg("index", "organisasjonsnummer").run(connection);

        List list = organisasjonsnummer.toList();

        if (list.size() > 0) {

            // inserter gammelt objekt i historisk tabell
            r.db(DATABASE_NAME)
                    .table(HISTORIC_TABLE)
                    .insert(list)
                    .optArg("return_changes", true)
                    .run(connection);

            // deleter aktuelt objekt fra import-tabell

            r.db(DATABASE_NAME).table(IMPORT_TABLE)
                    .getAll(d.getOrganisasjonsnummer()).optArg("index", "organisasjonsnummer")
                    .delete()
                    .run(connection);
        }

        // inserter nytt objekt i import-tabell
        r.db(DATABASE_NAME)
                .table(IMPORT_TABLE)
                .insert(d)
                .optArg("return_changes", true)
                .run(connection);
    }

    /**
     * Skriv flere objekt til databasen vha en List
     *
     * @param d liste med objekt vi skal skrive til databasen
     */
    public void writeBrregEntryToDb(List<Organization> d) {
        r.db(DATABASE_NAME).table(IMPORT_TABLE).insert(d).optArg("return_changes", true).run(connection);
    }

    /**
     * Søk etter et objekt fra databasen
     *
     * @param orgnummer organisasjonsnummeret som objektet må være lagret med
     * @return returnerer objektet vi har funnet evt null
     * @throws IOException
     */
    public Organization findByOrgNummer(String orgnummer) throws IOException {

        // Søket nedenfor utfører følgende se:
        //
        // 1) Finn alle oppføringer som matcher orgnummer (int) med 'organisasjonsnummer,
        //    vha indexen 'organisasjonsnummer'
        // 2) sortér deretter spørringen på 'timestamp' desc, dermed får vi det høyeste
        //    timestampen, som resulterer i det siste treffet.
        // 3) limit(1) slik at vi kun får 1 treff tilbake

        List latest = r.db(DATABASE_NAME)
                .table(IMPORT_TABLE)
                .getAll(orgnummer).optArg("index", "organisasjonsnummer")
                .orderBy(r.desc("timestamp")).limit(1).run(connection);

        // ingen treff? returner 'null' tilbake til metoden som kalte denne metoden.
        // 'null' resultat indikerer 0 treff

        if (latest.size() == 0) {
            return null;
        }

        // vi skal i praksis vha spørringa over kun finne 1 treff:
        if (latest.size() == 1) {

            // vi trenger en object-mapper for å gjøre om fra hashmap -> string -> vår datamodell

            ObjectMapper mapper = new ObjectMapper();

            ByteArrayOutputStream searchResultAsJsonString = new ByteArrayOutputStream();

            Object orgElement = latest.get(0);
            try {
                // konverter objektet fra hashmap til json-string:
                mapper.writeValue(searchResultAsJsonString, orgElement);
                // les inn json-string fra input-stream og konvertér til datamodell
                Organization brregEntry = mapper.readValue(searchResultAsJsonString.toByteArray(), Organization.class);
                // vi har nå et komplett objekt, returnér dette objektet.
                return brregEntry;
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

        } else {
            throw new RuntimeException("Forventet nøyaktig 1 treff, men fant : " + latest.size() + " treff.");
        }
    }


    /**
     * Metoden henter ut oppføringer i tabellen brreg-import hvor det er satt inn en gyldig hjemmeside.
     * Begrensningen som er satt i spørringen er at feltet ikke kan være tomt, eller inneholde facebook.
     * Ved oppretting av Seed objektene fjernes ytterligere av siden som ikke er gyldige.
     * <p>
     * Av disse oppføringene genereres det objekter som sammenlignes med det som finnes av entiteter og seeds
     * i Veidemann. Dersom brreg-dataene er blitt oppdatert med enten ny data eller endringer for eksisterende
     * organisasjonsnummer, oppdateres eller opprettes entitet/seed i Veidemann.
     *
     * @throws IOException
     */

    // Leter gjennom brreg db, for å finne oppføringer hvor feltet hjemmeside ikke er tomt, eller er en facebookside.
    public List<HashMap> findObjectsWithWebpage() throws IOException {
        if (!isConnected()) {
            connectToDb();
        }
        logger.info("Fetching list with documents in Brreg DB, with a valid 'homepage' field");
        String BRREG_DB = "brreg"; // Databasen med data fra Brønnøysundregistrene
        String BRREG_IMPORT_TABLE = "brreg_import"; // Tabellen med alle oppføringene i det åpne datasettet fra brreg

        Object results = r.db(BRREG_DB)
                .table(BRREG_IMPORT_TABLE)
                .filter(
                        row ->
                                row.g("hjemmeside")
                                        .eq("")
                                        .not()
                                        .and(
                                                row.g("hjemmeside")
                                                        .match(".*facebook.*").not()
                                        ))
                .run(connection);

        List<HashMap> list = ((Cursor) results).toList();
        return list;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            if (connection.isOpen()) {
                connection.close();
            }
        }
    }

}



