package no.nb.nna.veidemann.brregseedimport.service;

import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoBrreg;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5sumVerifierService {

    private static final Logger logger = LoggerFactory.getLogger(Md5sumVerifierService.class);

    public  enum STATE {DO_FULL_IMPORT, DO_UPDATES_ONLY, NOTHING_NEW}

    private RethinkRepoBrreg repo;

    public Md5sumVerifierService(RethinkRepoBrreg repo) {
        this.repo = repo;
    }

    public STATE verifyFile(String filename) throws NoSuchAlgorithmException, IOException {

        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md5Digest.digest();
        String myChecksum = DigestUtils.md5DigestAsHex(digest);
        logger.info("Generated md5 sum for the dataset");
        boolean doFullImport = false;
        boolean foundChangesInFile = false;

        try {
            JSONObject md5sumLastImportfile = repo.getLastMd5sumForImportFile();
            if (md5sumLastImportfile.containsKey("md5sum")) {
                String md5sumFraDatabase = (String) md5sumLastImportfile.get("md5sum");
                logger.info("Retrieving md5 sum from existing dataset and comparing it to the new one");
                // sjekk om md5sum fra database er lik den fra fil:
                if (md5sumFraDatabase.equalsIgnoreCase(myChecksum)) {
                    logger.info("New and old dataset is the same. Update not required");
                    return STATE.NOTHING_NEW;
                } else {
                    // sjekk om md5sum fra database er lik en 'initiell' verdi, som betyr at vi
                    // aldri har importert data før, og vi setter 'fullImport' til true:
                    if (md5sumFraDatabase.equalsIgnoreCase(RethinkRepoBrreg.INITIELL_DATABASE_OPPRETTET)) {
                        doFullImport = true;
                    }
                    logger.info("New dataset contains update. Will continue to update database");
                    repo.updateLastMd5sumForImportFile(md5sumLastImportfile,myChecksum);
                    foundChangesInFile = true;
                }
            } else {
                try {
                    repo.insertLastMd5sumForImportFile(myChecksum);
                    foundChangesInFile = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (foundChangesInFile) {
            if (doFullImport) {
                return STATE.DO_FULL_IMPORT;
            } else {
                return STATE.DO_UPDATES_ONLY;
            }
        } else {
            return STATE.NOTHING_NEW;
        }
    }

}
