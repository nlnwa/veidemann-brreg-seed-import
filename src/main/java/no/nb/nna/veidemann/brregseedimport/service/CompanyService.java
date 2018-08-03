package no.nb.nna.veidemann.brregseedimport.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nb.nna.veidemann.brregseedimport.exceptions.InvalidSurtStringException;
import no.nb.nna.veidemann.brregseedimport.exceptions.TooManyResultsException;
import no.nb.nna.veidemann.brregseedimport.model.Organization.*;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannCrawlEntity;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannMeta;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannSeed;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoBrreg;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoVeidemann;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

public class CompanyService {

    private Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private int badUrlCounter = 0;
    private RethinkRepoVeidemann veidemannRepo;
    private EntitySeedService entitySeedService;
    private List<VeidemannCrawlEntity> crawlEntities = new ArrayList<>();

    private List<VeidemannSeed> seedList = new ArrayList<>();
    private RethinkRepoBrreg brregRepo;

    public CompanyService(RethinkRepoVeidemann veidemannRepo, EntitySeedService entitySeedService, RethinkRepoBrreg brregRepo) {
        this.veidemannRepo = veidemannRepo;
        this.entitySeedService = entitySeedService;
        this.brregRepo = brregRepo;
    }

    /**
     * Metoden gjør en spørringen mot Brreg DB, og bruker resultatetet til å lage entitet objekter med tilhørende seed objekt.
     * Metoden kaller så videre til en annen metode for å oppdatere entitet/seed tabellen for veidemann med ny data
     */
    public void createListAndCheckForUpdates() {
        StopWatch stopWatch = new StopWatch("Create_Seed_and_Entity_List");
        logger.info("Starting job with creating / updating entities and seeds in Veidemann DB, with available data in Brreg DB");
        System.out.println("Starting job with creating / updating entities and seeds in Veidemann DB, with available data in Brreg DB");
        if (!veidemannRepo.isConnected()) {
            veidemannRepo.connectToDb();
            logger.info("Connected to Veidemann DB");
        }
        try {
            stopWatch.start();
            List<HashMap> objectsWithWebpage = brregRepo.findObjectsWithWebpage();
            System.out.println("Har " + objectsWithWebpage.size() + "objekter med hjemmeside");
            createEntityAndSeedList(objectsWithWebpage);
            stopWatch.stop();
            logger.info("Fetching data and creating the list took in all: " + stopWatch.getTotalTimeSeconds() + " seconds");
            System.out.println("Fetching data and creating the list took in all: " + stopWatch.getTotalTimeSeconds() + " seconds");
            updateOrInsertEntityWithSeed();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoden brukerer resultatet fra spørringen mot brreg databasen til å opprette nye lister med objekter av
     * entiter og seeds.
     *
     * @param resultFromBromBrregDb
     * @throws IOException
     */

    private void createEntityAndSeedList(List<HashMap> resultFromBromBrregDb) throws IOException {
        logger.info("Creating lists with Enitity and Seed objects from brreg data");
        System.out.println("Creating lists with Enitity and Seed objects from brreg data");
        List<OrganizationFromDb> OrganizationList = getCompaniesFromList(resultFromBromBrregDb);

        // Går gjennom ArrayList med resultatet fra Brreg DB og lager entitet/seed objekter
        String timestamp = getTimestamp();
        for (OrganizationFromDb org : OrganizationList) {

            // bygg opp crawlEntity:
            try {
                VeidemannMeta veidemannMeta = createNewEntityMetaObject(org, timestamp);
                VeidemannCrawlEntity newEntity = createNewEntity(org, timestamp, veidemannMeta);
                VeidemannSeed veidemannSeed = createVeidemannSeed(org, timestamp);

                seedList.add(veidemannSeed);
                crawlEntities.add(newEntity);

            } catch (InvalidSurtStringException e) {
                badUrlCounter++;
            }

        }
    }

    /**
     * Metoden bruker listen som returneres fra databasesøket for å opprette en liste med company objekter basert på
     * samme datamodell. Listen brukes så videre for å opprette entitet/seed objekter.
     *
     * @param resultFromBromBrregDb
     * @return
     * @throws IOException
     */
    public static List<OrganizationFromDb> getCompaniesFromList(List<HashMap> resultFromBromBrregDb) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Lager en ArrayList med objekter basert på resultatet fra databasen
        List<OrganizationFromDb> organizationList = new ArrayList<>(resultFromBromBrregDb.size());
        for (Object o : resultFromBromBrregDb) {
            mapper.writeValue(stream, o);
            OrganizationFromDb company = mapper.readValue(stream.toByteArray(), OrganizationFromDb.class);
            organizationList.add(company);
            stream.reset();
        }
        return organizationList;
    }

    private static String getTimestamp() {
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'");
//        df.setTimeZone(tz);
//        String timestamp = df.format(new Date());
        OffsetDateTime dateTime = OffsetDateTime.now();

        //String timestamp = Instant.now().toString();
        String timestamp = dateTime.toString();
        return timestamp;
    }

    /**
     * Lager et nytt meta-objekt for en entitet.
     * Input kommer fra listen som blir laget av resultat av spørringen mot brreg db, over alle organisasjoner med en
     * oppført hjemmeside.
     *
     * @param c
     * @param timestamp
     * @return
     */
    public static VeidemannMeta createNewEntityMetaObject(OrganizationFromDb c, String timestamp) {


        VeidemannMeta entityMeta = new VeidemannMeta();
        entityMeta.setName(c.getNavn());
        String description;
        Naeringskode1 naeringskode1 = c.getNaeringskode1();
        Naeringskode2 naeringskode2 = c.getNaeringskode2();
        Naeringskode3 naeringskode3 = c.getNaeringskode3();

        if (naeringskode1 != null) {
            description = naeringskode1.getBeskrivelse();
        } else {
            if (naeringskode2 != null) {
                description = naeringskode2.getBeskrivelse();
            } else {
                if (naeringskode3 != null) {
                    description = naeringskode3.getBeskrivelse();
                } else {
                    description = "";
                }
            }
        }
        entityMeta.setDescription(description);
        entityMeta.setCreated(timestamp);
        entityMeta.setCreatedBy("Auto update script");
        entityMeta.setLastModified(timestamp);
        entityMeta.setLastModifiedBy("Auto update script");

        HashMap entityMetalabelHashmap = new HashMap();
        entityMetalabelHashmap.put("key", "Orgnummer");
        entityMetalabelHashmap.put("value", c.getOrganisasjonsnummer());

        entityMeta.setLabel(Arrays.asList(entityMetalabelHashmap));

        return entityMeta;

    }

    /**
     * Lager en ny entitet basert på data fra brreg db.
     * Entiteten blir konstruert med et metaobjekt og en tilhørende seed.
     * Dersom Seeden ikke har en gyldig Url opprettes ikke entiteten.
     *
     * @param organization
     * @param timestamp
     * @param veidemannMeta
     * @return
     * @throws InvalidSurtStringException
     */
    public static VeidemannCrawlEntity createNewEntity(OrganizationFromDb organization, String timestamp, VeidemannMeta veidemannMeta) throws InvalidSurtStringException {

        VeidemannCrawlEntity crawlEntity = new VeidemannCrawlEntity();
        crawlEntity.setId(organization.getId());
        crawlEntity.setMeta(veidemannMeta);


        return crawlEntity;

    }

    public static VeidemannSeed createVeidemannSeed(OrganizationFromDb organization, String timestamp) throws InvalidSurtStringException {
        VeidemannMeta seedMeta = new VeidemannMeta();
        VeidemannSeed seed = new VeidemannSeed();

        // Bygger opp seed
        seedMeta.setName(organization.getHjemmeside());
        seedMeta.setCreated(timestamp);
        seedMeta.setCreatedBy("Auto update script");
        seedMeta.setLastModified(timestamp);
        seedMeta.setLastModifiedBy("Auto update script");

        seed.setScopeThroughString(organization.getHjemmeside());
        // Dersom seed har en ugyldig url, stopp oppretting av seed og slett tilhørende entitet.

        HashMap seedMetalabelHashmap = new HashMap();
        seedMetalabelHashmap.put("key", "Orgnummer");
        seedMetalabelHashmap.put("value", organization.getOrganisasjonsnummer());

        HashMap seedMetalabel2Hashmap = new HashMap();
        seedMetalabel2Hashmap.put("key", "checked by curator");
        seedMetalabel2Hashmap.put("value", false);

        seedMeta.setLabel(Arrays.asList(seedMetalabelHashmap, seedMetalabel2Hashmap));
        seed.setMeta(seedMeta);
        seed.setEntityId(organization.getId());
        seed.generateId();

        return seed;


    }


    public List<VeidemannCrawlEntity> getCrawlEntities() {
        return crawlEntities;
    }

    public List<VeidemannSeed> getSeedList() {
        return seedList;
    }


    /**
     * Metoden sørger for å sette inn eller oppdatere entiteter/seeds i Veidemann DB.
     * Den går gjennom listen over entiteter som er generert på bakgrunn av dataene fra brreg.
     * For hvert organsisasjonsnummer sjekkes det om en entitet med dette orgnummeret eksisterer i Veidemann, og
     * oppretter denne  samt tilhørende seed dersom den ikke gjør det. Finnest entiteten fra før, blir det utført
     * metoder for å sjekke om det finnes oppdateringer for seed eller entitet.
     */
    private void updateOrInsertEntityWithSeed() {

        logger.info("Skipped creating: " + badUrlCounter + " entities/seeds because of invalid Url");
        logger.info("Will start checking the created list of entities/seeds with the existing ones in Veidemann DB: ");
        System.out.println("Skipped creating: " + badUrlCounter + " entities/seeds because of invalid Url");
        System.out.println("Will start checking the created list of entities/seeds with the existing ones in Veidemann DB: ");
        StopWatch stopWatch = new StopWatch("Update_Veidemann_DB");
        stopWatch.start();
        int insertedEntities = 0;
        int insertedSeeds = 0;
        int printLimit = 0;
        int entitiesToCheck = crawlEntities.size();
        int entitiesCheckedForUpdates = 0;
        // Går gjennom liste med Entiteter
        for (VeidemannCrawlEntity ce : crawlEntities) {

            HashMap entityFromVeidemannDb;
            if (ce.getMeta() != null) {
                // Henter ut organisasjonsnummeret knyttet til entiteten.
                VeidemannMeta metaObj = ce.getMeta();
                String orgnr = ce.getMeta().getOrgNr(metaObj);

                try {
                    // Ser om entiteten eksisterer i Veidemann DB
                    entityFromVeidemannDb = veidemannRepo.findEntityByOrgNr(orgnr);

                    if (entityFromVeidemannDb != null) {
                        // Om entiteten eksisterer finner vi tilhørende seed og sjekker etter oppdateringer
                        for (VeidemannSeed s : seedList) {
                            if (s.getEntityId().equalsIgnoreCase(ce.getId())) {
                                entitySeedService.checkEntityForUpdates(ce, s, entityFromVeidemannDb);
                                entitiesCheckedForUpdates++;
                            } else {
                                //throw error
                            }
                        }
                        // Entiteten eksisterer ikke oppretter denne og tilhørende seed,
                    } else {
                        veidemannRepo.insertNewEntityToVeidemannDb(ce);
                        insertedEntities++;
                        for (VeidemannSeed s : seedList) {
                            if (s.getEntityId().equalsIgnoreCase(ce.getId())) {
                                veidemannRepo.insertNewSeedForEntityToVeidemannDb(s);
                                insertedSeeds++;
                            }
                        }
                        logger.info("Inserting new entity and seed for id: " + ce.getId() + " to Veidemann DB");
                        System.out.println("Inserting new entity and seed for id: " + ce.getId() + " to Veidemann DB");
                    }
                    if (entitiesCheckedForUpdates >= printLimit) {
                        logger.info("Entities/seed checked for updates: " + entitiesCheckedForUpdates + "/" + entitiesToCheck);
                        System.out.println("Entities/seed checked for updates: " + entitiesCheckedForUpdates + "/" + entitiesToCheck);
                        printLimit += 5000;
                    }
                    // Får tilbake flere oppføringer med dette organisasjonsnummeret.
                } catch (TooManyResultsException e) {
                    e.printStackTrace();
                }

                // Det er ikke blitt satt inn meta for denne ID'en (Skal ikke kunne skje)
            } else {
                System.out.println("finner ikke meta");
            }

        }
        stopWatch.stop();
        logger.info("Done with importing/updating entities and seeds in Veidemann DB");
        logger.info("The operation took: " + stopWatch.getTotalTimeSeconds() + " seconds. ");
        logger.info("New entities created in Veidemann DB: " + insertedEntities + " ,with " + insertedSeeds + " associated seeds");
        logger.info("Number of existing entities and seeds checked for updates: " + entitiesCheckedForUpdates);

        System.out.println("Done with importing/updating entities and seeds in Veidemann DB");
        System.out.println("The operation took: " + stopWatch.getTotalTimeSeconds() + " seconds. ");
        System.out.println("New entities created in Veidemann DB: " + insertedEntities + " ,with " + insertedSeeds + " associated seeds");

    }
}
