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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class OrganizationListUpdater {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationListUpdater.class);
    private static final Settings SETTINGS;
    private boolean noUpdates = false;


    static {
        Config config = ConfigFactory.load();
        config.checkValid(ConfigFactory.defaultReference());
        SETTINGS = ConfigBeanFactory.create(config, Settings.class);
    }

    public OrganizationListUpdater() {
    }

    //  public OrganizationListUpdater start() throws Exception {

    public boolean updateBrregDb() throws Exception {

        String dbHost = "127.0.0.1";
        int dbPort = 28015;
        String dbUser = "admin";
        String dbPassword = "";

        String DOWNLOAD_LINK = "https://data.brreg.no/enhetsregisteret/api/enheter/lastned";
        String GZIP_FILE = "/home/andreasbo/app/brregdownload/orglist.gz";

        String JSON_FILE = "/home/andreasbo/app/brregdownload/orglist.json";

        boolean fullImport = false;
        RethinkRepoBrreg db = null;

        try {
            db = new RethinkRepoBrreg(dbHost, dbPort, dbUser, dbPassword);
        } catch (Exception ex) {
            logger.error("Could not connect to DB");
        }

        try {

            logger.info("Downloading data set from: " + DOWNLOAD_LINK);
            System.out.println("Downloading data set from: " + DOWNLOAD_LINK);
          //  BrregDownloadService downloadService = new BrregDownloadService();
          //  downloadService.downloadAndUnzipDataset(DOWNLOAD_LINK);
//            logger.info("Downloading and unpacking file took: " + downloadService.getStopWatch().getTotalTimeSeconds() + " seconds");
//            System.out.println("Downloading and unpacking file took: " + downloadService.getStopWatch().getTotalTimeSeconds() + " seconds");

            Md5sumVerifierService md5sumVerifierService = new Md5sumVerifierService(db);
            Md5sumVerifierService.STATE md5sumState = md5sumVerifierService.verifyFile(JSON_FILE);

            switch (md5sumState) {
                case NOTHING_NEW: {
                    logger.info("No updates needed.");
                    System.out.println("No updates needed.");
                    return false;
                }
                case DO_FULL_IMPORT: {
                    logger.info("Will do a full/complete import.");
                    System.out.println("Will do a full/complete import.");
                    fullImport = true;
                    break;
                }
                case DO_UPDATES_ONLY: {
                    logger.info("Will only import changes or new items.");
                    System.out.println("Will only import changes or new items.");
                    fullImport = false;
                    break;
                }
            }

            JsonParser parser = new JsonParser(JSON_FILE);
            BrregImportService service = new BrregImportService(fullImport, db);

            logger.info("Reading and parsing the JSON file");
            System.out.println("Reading and parsing the JSON file");
            parser.parseJsonFile();
            logger.info("Reading the file took: " + parser.getStopWatch().getTotalTimeSeconds() + " seconds");
            System.out.println("Reading the file took: " + parser.getStopWatch().getTotalTimeSeconds() + " seconds");
            service.importEntriesToDB(parser.getOrganzationList());
            logger.info("Importing it all took: " + service.getStopWatch().getTotalTimeSeconds() + " seconds");
            System.out.println("Importing it all took: " + service.getStopWatch().getTotalTimeSeconds() + " seconds");


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
