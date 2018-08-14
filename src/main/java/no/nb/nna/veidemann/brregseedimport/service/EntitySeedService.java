package no.nb.nna.veidemann.brregseedimport.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nb.nna.veidemann.brregseedimport.exceptions.TooManyResultsException;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannCrawlEntity;
import no.nb.nna.veidemann.brregseedimport.model.VeidemannSeed;
import no.nb.nna.veidemann.brregseedimport.repo.RethinkRepoVeidemann;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EntitySeedService {

    private static final Logger logger = LoggerFactory.getLogger(EntitySeedService.class);
    private RethinkRepoVeidemann veidemannRepo;

    public EntitySeedService(RethinkRepoVeidemann repo) {
        this.veidemannRepo = repo;
    }


    /**
     * Metoden ser etter oppdateringer i Entitet/Seed i Veidemann DB i forhold til det som ligger
     * Brreg DB.
     *
     * Ser om eniteten har endret navn eller beskrivelse, og oppdaterer entiteten i Veidemann DB i henhold til siste brreg data.
     * Kaller videre til metoden for å se etter oppdateringer i en seed.
     * @param entityFromBrreg
     * @param seedFromBrreg
     * @param entityFromVeidemann
     */

    public void checkEntityForUpdates(VeidemannCrawlEntity entityFromBrreg, VeidemannSeed seedFromBrreg, HashMap entityFromVeidemann) {
        if (!veidemannRepo.isConnected()) {
            veidemannRepo.connectToDb();
        }

        VeidemannCrawlEntity veidemannEntity = null;
        // Konverterer HashMap fra DB over til VeidemannCrawlEntity for videre sammenligning
        try {
            veidemannEntity = makeEntityObjectOfHashmapFromDb(entityFromVeidemann);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String veidemannEntityId = veidemannEntity.getId();
        String veidemannEntityName = veidemannEntity.getMeta().getName();
        String veidemannEntityDescription = veidemannEntity.getMeta().getDescription();

        String brregEntityId = entityFromBrreg.getId();
        String brregEntityName = entityFromBrreg.getMeta().getName();
        String brregEntityDescription = entityFromBrreg.getMeta().getDescription();
        String brregEntityLastMod = entityFromBrreg.getMeta().getLastModified();

        if (brregEntityId.equalsIgnoreCase( veidemannEntityId)) {
            // Sjekker om navnet på entiteten er endret
            if (!(brregEntityName.equalsIgnoreCase(veidemannEntityName))) {
                logger.info("The name of entity: "+brregEntityId+  " ,has changed and will be updated");
                // Oppdaterer navn og setter nytt timestamp
                veidemannRepo.updateEntityName(brregEntityId, brregEntityName);
                veidemannRepo.updateEntityTimestamp(brregEntityId, brregEntityLastMod);
            }
            // Sjekker om beskrivelsen for entiteten er endret
            if (!(brregEntityDescription.equalsIgnoreCase(veidemannEntityDescription))) {
                logger.info("The description of entity: "+brregEntityId+  " ,has changed and will be updated");
                // Oppdaterer beskrivelsen og setter nytt timestamp
                veidemannRepo.updateEntityDescription(brregEntityId,brregEntityDescription);
                veidemannRepo.updateEntityTimestamp(brregEntityId, brregEntityLastMod);
            }

            // Prøver å hente tilhørende seed for entiteten og kaller metode for å sjekke etter oppdateringer i denne
            HashMap seedHashFromVeidemann = null;
            try {
                seedHashFromVeidemann = veidemannRepo.findSeedByEntityId(veidemannEntityId);
            } catch (TooManyResultsException e) {
                e.printStackTrace();
            }
            checkSeedForUpdates(seedFromBrreg, seedHashFromVeidemann);

        }
    }

    /**
     *  Metoden ser etter oppdateringer i seeden for entiteten.
     * Her ser vi etter endringer i navnet, altså URL. En endring av denne kan også medføre en endring av scope.
     * @param seedFromBrreg
     * @param seedFromVeidemann
     */

    public void checkSeedForUpdates(VeidemannSeed seedFromBrreg, HashMap seedFromVeidemann) {
        VeidemannSeed veidemannSeed = null;
        // Konverterer resultat fra DB over til VeidemannSeed objekt for videre sammenligning
        try {
            veidemannSeed = makeSeedObjectOfHashmapFromDb(seedFromVeidemann);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String brregSeedEntityId = seedFromBrreg.getEntityId();
        String brregSeedName =  seedFromBrreg.getMeta().getName();
        String brregSeedLastMod = seedFromBrreg.getMeta().getLastModified();
        String brregSeedOrgnrLabel =  seedFromBrreg.getMeta().getOrgNr(seedFromBrreg.getMeta());
        Map<String, Object> brregSeedScope = seedFromBrreg.getScope();

        String veidemannSeedEntityId = veidemannSeed.getEntityId();
        String veidemannSeedName = veidemannSeed.getMeta().getName();
        Map<String, Object> veidemannSeedScope = veidemannSeed.getScope();

        if (brregSeedEntityId.equalsIgnoreCase(veidemannSeedEntityId)) {
           // Sjekker om seed har byttet navn. Oppdaterer navn og timestamp om det er tilfellet.
            if (!(brregSeedName.equalsIgnoreCase(veidemannSeedName))) {
                logger.info("The name of the seed for entity: " +brregSeedEntityId+ " ,has changed and will be updated");
                veidemannRepo.updateSeedName(brregSeedEntityId, brregSeedName,brregSeedOrgnrLabel);
                veidemannRepo.updateSeedTimestamp(brregSeedEntityId, brregSeedOrgnrLabel, brregSeedLastMod);
            }
            // Sjekker om seed har endret scope. Oppdaterer navn og timestamp om det er tilfellet.
            if (!(brregSeedScope.equals(veidemannSeedScope))) {
                logger.info("The Scope of the seed for entity: " +brregSeedEntityId+ " ,has changed and will be updated");
                veidemannRepo.updateSeedScope(brregSeedEntityId, brregSeedScope, brregSeedOrgnrLabel);
                veidemannRepo.updateSeedTimestamp(brregSeedEntityId, brregSeedOrgnrLabel,brregSeedLastMod);
            }
            // Exception dersom seed og entitet ikke har samme id?
        }
    }

    /**
     * Metoden konverterer et HashMap til et VeidemannCrawlEntity Objekt i henhold til datamodell.
     *
     * @param entityFromVeidemann
     * @return
     * @throws IOException
     */

    public VeidemannCrawlEntity makeEntityObjectOfHashmapFromDb(HashMap entityFromVeidemann) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            mapper.writeValue(bos, entityFromVeidemann);
            VeidemannCrawlEntity veidemannEntity = mapper.readValue(bos.toByteArray(), VeidemannCrawlEntity.class);
            return veidemannEntity;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Metoden konverterer et HashMap  til et VeidemannSeed Objekt i henhold til datamodell.
     *
     * @param seedFromVeidemann
     * @return
     * @throws IOException
     */

    public VeidemannSeed makeSeedObjectOfHashmapFromDb(HashMap seedFromVeidemann) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            mapper.writeValue(bos, seedFromVeidemann);
            VeidemannSeed veidemannSeed = mapper.readValue(bos.toByteArray(), VeidemannSeed.class);
            return veidemannSeed;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
