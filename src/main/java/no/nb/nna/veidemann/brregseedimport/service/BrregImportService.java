package no.nb.nna.veidemann.brregseedimport.service;

import no.nb.nna.veidemann.brregseedimport.model.Organization.Organization;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoBrreg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class BrregImportService {
    private static final Logger logger = LoggerFactory.getLogger(BrregImportService.class);
    private final int IMPORT_MAXSIZE = 4000;
    private boolean fullImport;
    private RethinkRepoBrreg repository;
    private StopWatch stopWatch = new StopWatch("Importing_to_RethinkDB");

    public BrregImportService(boolean fullImport, RethinkRepoBrreg rethinkRepository) {
        this.fullImport = fullImport;
        this.repository = rethinkRepository;
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void importEntriesToDB(List<Organization> brregEntries) throws IOException {
        System.out.println("should start import of entries to brreg");
        stopWatch = new StopWatch("Importing_to_RethinkDB");
        stopWatch.start();
        long changeCounter = 0;
        long newCounter = 0;

        if (!fullImport) {
            logger.info("Starting import of item by item. This might take a while, 15-20-30 minutes.");
            System.out.println("Starting import of item by item. This might take a while, 15-20-30 minutes.");
            for (Organization m : brregEntries) {
                Organization searchResult = repository.findByOrgNummer(m.getOrganisasjonsnummer());

                // ettersom vi har søkt og forhåpentligvis funnet en oppføring, kan vi sette et tidspunkt
                // på objektet slik at tidspunktet blir satt på et objekt før det evt lagres i databasen.
                m.setTimestamp(Calendar.getInstance().getTimeInMillis());

                // hvis vi finner et søkeresultat så sammenlikner vi dette med linja vår, vha av 'toString()'.
                // 'toString()' returnerer en tekst-versjon av objektet og kan således sammenliknes mot et annet objekt.

                if (searchResult != null) {
                    if (!m.toString().equals(searchResult.toString())) {
                        logger.info("Updating element: " + m.getOrganisasjonsnummer() + " with new data.");
                        System.out.println("Updating element: " + m.getOrganisasjonsnummer() + " with new data.");
                        repository.moveOldAndInsertNew(m);
                        changeCounter++;
                    }
                } else {
                    repository.writeBrregEntryToDb(m);
                    newCounter++;
                }
            }
        } else {
            logger.info("Doing initial import");
            int updateCnt = 0;
            while (updateCnt < brregEntries.size()) {
                logger.info("Importing: " + updateCnt + " --> " + (updateCnt + IMPORT_MAXSIZE));
                System.out.println("Importing: " + updateCnt + " --> " + (updateCnt + IMPORT_MAXSIZE));
                if (updateCnt + IMPORT_MAXSIZE < brregEntries.size()) {
                    logger.info("Importing item: " + updateCnt + " -> " + (updateCnt + IMPORT_MAXSIZE));
                    System.out.println("Importing item: " + updateCnt + " -> " + (updateCnt + IMPORT_MAXSIZE));
                    repository.writeBrregEntryToDb(brregEntries.subList(updateCnt, updateCnt + IMPORT_MAXSIZE));
                    updateCnt += IMPORT_MAXSIZE;
                } else {
                    logger.info("Importing item: " + updateCnt + " -> " + (brregEntries.size()));
                    System.out.println("Importing item: " + updateCnt + " -> " + (brregEntries.size()));
                    repository.writeBrregEntryToDb(brregEntries.subList(updateCnt, brregEntries.size()));
                    updateCnt = brregEntries.size();
                }
                newCounter = updateCnt;
            }
            logger.info("Done doing initial import...");
            System.out.println("Done doing initial import...");
        }
        logger.info("Statistics: Number of new items: " + newCounter + ",  number of modified elements: " + changeCounter );
        System.out.println("Statistics: Number of new items: " + newCounter + ",  number of modified elements: " + changeCounter );
        stopWatch.stop();
    }
}

