package no.nb.nna.veidemann.brregseedimport;

public final class Main {

    private Main(){ }

    public static void main (String[] args) throws Exception {
        OrganizationListUpdater brregUpdate = new OrganizationListUpdater();
        if(brregUpdate.updateBrregDb()){
            VeidemannEntitySeedUpdater veidemannUpdate = new VeidemannEntitySeedUpdater();
            veidemannUpdate.updateEntityAndSeed();
        }
        System.out.println("Done updating Brreg and Veidemann DB");
    }
}
