package no.nb.nna.veidemann.brregseedimport.repo;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import no.nb.nna.veidemann.brregseedimport.exceptions.TooManyResultsException;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannCrawlEntity;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannSeed;
import no.nb.nna.veidemann.brregseedimport.service.EntitySeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RethinkRepoVeidemann implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(RethinkRepoVeidemann.class);
    private Connection connection;
    private static final RethinkDB r = RethinkDB.r;
    private EntitySeedService service;

    private final String VEIDEMANN_DB = "veidemann"; // Databasen for veidemann applikasjonen
    private final String VEIDEMANN_ENTITY_TABLE = "config_crawl_entities"; // Tabell for entiteter
    private final String VEIDEMANN_SEED_TABLE = "config_seeds"; // Tabell med seeds for entiteter

    private String host;
    private int port;

    /**
     * Oppkobling mot databasen
     */

    public RethinkRepoVeidemann(String host, int port, String user, String password) {
        try {
            connection = r.connection().hostname(host).port(port).user(user, password).connect();
            if (!connection.isOpen()) {
                throw new ReqlDriverError("Unable to connect to server at: " + host + " : " + port);
            }
        } catch (ReqlDriverError error) {
            logger.info("unable to connect to server " + host + " for port: " + port);
            throw error;
        }
    }


    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    public void connectToDb() {
        logger.info("Connecting to Veidemann DB");
        connection = r.connection().hostname(host).port(port).connect();
//
        if (!connection.isOpen()) {
            throw new ReqlDriverError("Unable to connect to host: " + host);
        }
    }

    /**
     * Metoden leter gjennom oppføringene i entitet-tabellen til Veidemann, for å finne en entitet som samsvarer med
     * det innsendte organisasjonsnummeret.
     * Returner entiteten som et HashMap dersom den finnes, og null dersom den ikke eksisterer.
     *
     * @param orgnr
     * @return
     * @throws TooManyResultsException
     */
    public HashMap findEntityByOrgNr(String orgnr) throws TooManyResultsException {

        Object result = r.db(VEIDEMANN_DB).table(VEIDEMANN_ENTITY_TABLE)
                .getAll(r.array("orgnummer", orgnr))
                .optArg("index", "label")
                .run(connection);

        List list = ((Cursor) result).toList();


        if (list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new TooManyResultsException("I expected one result, found more than one: " + list.size());
        }

        return (HashMap) list.get(0);
    }

    /**
     * Metoden leter gjennom seed-tabellen til Veidemann for å finne tilhørende seed for en entitet.
     * Returnerer seeden som et HashMap dersom den eksisterer, og null dersom den ikke eksisterer.
     *
     * @param id
     * @return
     * @throws TooManyResultsException
     */

    public HashMap findSeedByEntityId(String id) throws TooManyResultsException {

        Object result = r.db(VEIDEMANN_DB).table(VEIDEMANN_SEED_TABLE)
                .getAll(id).optArg("index", "entityId")
                .limit(1)
                .run(connection);


        List list = ((Cursor) result).toList();


        if (list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new TooManyResultsException("I expected one result, found more than one: " + list.size());
        }

        return (HashMap) list.get(0);
    }


    /**
     * Metoden setter inn en ny entitet i entitet-tabellen til Veidemann.
     *
     * @param entity
     */

    public void insertNewEntityToVeidemannDb(VeidemannCrawlEntity entity) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_ENTITY_TABLE).insert(entity).run(connection);
    }

    /**
     * Metoden setter inn en ny seed tilknyttet en entitet.
     *
     * @param seed
     */

    public void insertNewSeedForEntityToVeidemannDb(VeidemannSeed seed) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_SEED_TABLE).insert(seed).run(connection);

    }

    /**
     * Oppdaterer navnet for entiteten i Veidemann Entitet tabell
     *
     * @param newName
     */
    public void updateEntityName(String entityId, String newName) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_ENTITY_TABLE)
                .get(entityId)
                .update(
                        r.hashMap("meta",
                                r.hashMap("name", newName)))
                .run(connection);
    }

    /**
     * Oppdaterer beskrivelsen for entiteten i Veidemann Entitet tabell
     *
     * @param newDescription
     */
    public void updateEntityDescription(String entityId, String newDescription) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_ENTITY_TABLE)
                .get(entityId)
                .update(
                        r.hashMap("meta",
                                r.hashMap("description", newDescription)))
                .run(connection);
    }

    /**
     * Oppdaterer navnet (URL) på en seed
     *
     * @param seedEntityId
     * @param newName
     */
    public void updateSeedName(String seedEntityId, String newName, String orgNr) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_SEED_TABLE)
                .getAll(seedEntityId)
                .optArg("index", "entityId")
                .filter(row -> row.g("meta").g("label").g("value").contains(orgNr)
                )
                .update(
                        r.hashMap("meta",
                                r.hashMap("name", newName)))
                .optArg("return_changes", true)
                .run(connection);
    }

    /**
     * Oppdaterer scopet (SurtPrefix) for en seed
     *
     * @param seedEntityId
     * @param newScope
     */
    public void updateSeedScope(String seedEntityId, Map newScope, String orgNr) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_SEED_TABLE)
                .getAll(seedEntityId)
                .optArg("index", "entityId")
                .filter(row -> row.g("meta").g("label").g("value").contains(orgNr))
                .update(
                        r.hashMap("scope", newScope))
                .optArg("return_changes", true)
                .run(connection);
    }

    /**
     * Oppdaterer timestamp for når entitet/seed sist ble oppdatert
     *
     * @param newTimestamp
     */
    public void updateEntityTimestamp(String id, String newTimestamp) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_ENTITY_TABLE)
                .get(id)
                .update(
                        r.hashMap("meta",
                                r.hashMap("lastModified", newTimestamp)))
                .run(connection);
    }

    public void updateSeedTimestamp(String id, String orgNr, String newTimestamp) {
        r.db(VEIDEMANN_DB).table(VEIDEMANN_SEED_TABLE)
                .getAll(id)
                .optArg("index", "entityId")
                .filter(row -> row.g("meta").g("label").g("value").contains(orgNr))
                .update(
                        r.hashMap("meta",
                                r.hashMap("lastModified", newTimestamp)))
                .run(connection);
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
