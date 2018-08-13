package no.nb.nna.veidemann.brregseedimport;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoBrreg;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoVeidemann;
import no.nb.nna.veidemann.brregseedimport.service.CompanyService;
import no.nb.nna.veidemann.brregseedimport.service.EntitySeedService;
import no.nb.nna.veidemann.brregseedimport.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeidemannEntitySeedUpdater {

    private static final Logger logger = LoggerFactory.getLogger(VeidemannEntitySeedUpdater.class);
    private static final Settings SETTINGS;

    static {
        Config config = ConfigFactory.load();
        config.checkValid(ConfigFactory.defaultReference());
        SETTINGS = ConfigBeanFactory.create(config, Settings.class);
    }

    public VeidemannEntitySeedUpdater() {
    }

    public void updateEntityAndSeed() {


        RethinkRepoVeidemann veidemannRepo = null;
        RethinkRepoBrreg brregRepo = null;

        try {
            veidemannRepo = new RethinkRepoVeidemann(SETTINGS.getDbHost(),SETTINGS.getDbPort(), SETTINGS.getDbUser(), SETTINGS.getDbPassword());
        } catch (Exception ex) {
            logger.error("Could not connect to DB");
        }

        try {
            brregRepo = new RethinkRepoBrreg(SETTINGS.getDbHost(),SETTINGS.getDbPort(),SETTINGS.getDbUser(), SETTINGS.getDbPassword());
        }catch (Exception e) {
            logger.error("Cold not connect to DB");
        }
        EntitySeedService entitySeedService = new EntitySeedService(veidemannRepo);
        CompanyService companyService = new CompanyService(veidemannRepo, entitySeedService, brregRepo);
        companyService.createListAndCheckForUpdates();
    }

    public static Settings getSettings() {
        return SETTINGS;
    }
}
