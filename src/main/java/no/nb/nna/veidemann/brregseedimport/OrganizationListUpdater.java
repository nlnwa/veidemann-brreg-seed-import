package no.nb.nna.veidemann.brregseedimport;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoBrreg;
import no.nb.nna.veidemann.brregseedimport.service.BrregDownloadService;
import no.nb.nna.veidemann.brregseedimport.service.BrregImportService;
import no.nb.nna.veidemann.brregseedimport.service.JsonParser;
import no.nb.nna.veidemann.brregseedimport.service.Md5sumVerifierService;
import no.nb.nna.veidemann.brregseedimport.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganizationListUpdater {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationListUpdater.class);
    private static final Settings SETTINGS;


    static {
        Config config = ConfigFactory.load();
        config.checkValid(ConfigFactory.defaultReference());
        SETTINGS = ConfigBeanFactory.create(config, Settings.class);
    }

    public OrganizationListUpdater() {
    }


    public boolean updateBrregDb() throws Exception {

        String downloadLink = SETTINGS.getDownloadLink();
        String gzipFile = SETTINGS.getDownloadDir() + "/orglist.gz";
        String jsonFile = SETTINGS.getDownloadDir() + "/orglist.json";

        boolean fullImport = false;
        RethinkRepoBrreg db = null;

        try {
            db = new RethinkRepoBrreg(SETTINGS.getDbHost(), SETTINGS.getDbPort(), SETTINGS.getDbUser(),
                    SETTINGS.getDbPassword());
        } catch (Exception ex) {
            logger.error("Could not connect to DB");
        }

        try {

            logger.info("Downloading data set from: " + downloadLink);
            BrregDownloadService downloadService = new BrregDownloadService();
            downloadService.downloadAndUnzipDataset(downloadLink, gzipFile, jsonFile);
            logger.info("Downloading and unpacking file took: " + downloadService.getStopWatch().getTotalTimeSeconds() + " seconds");

            Md5sumVerifierService md5sumVerifierService = new Md5sumVerifierService(db);
            Md5sumVerifierService.STATE md5sumState = md5sumVerifierService.verifyFile(jsonFile);

            switch (md5sumState) {
                case NOTHING_NEW: {
                    logger.info("No updates needed.");
                    return false;
                }
                case DO_FULL_IMPORT: {
                    logger.info("Will do a full/complete import.");
                    fullImport = true;
                    break;
                }
                case DO_UPDATES_ONLY: {
                    logger.info("Will only import changes or new items.");
                    fullImport = false;
                    break;
                }
            }

            JsonParser parser = new JsonParser(jsonFile);
            BrregImportService service = new BrregImportService(fullImport, db);

            logger.info("Reading and parsing the JSON file");
            parser.parseJsonFile();
            logger.info("Reading the file took: " + parser.getStopWatch().getTotalTimeSeconds() + " seconds");
            service.importEntriesToDB(parser.getOrganzationList());
            logger.info("Importing it all took: " + service.getStopWatch().getTotalTimeSeconds() + " seconds");

        } finally {
            if (db != null) {
                db.close();
            }
        }
        return true;
    }

    public static Settings getSettings() {
        return SETTINGS;
    }
}
