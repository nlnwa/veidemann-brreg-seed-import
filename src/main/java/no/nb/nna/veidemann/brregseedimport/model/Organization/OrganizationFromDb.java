package no.nb.nna.veidemann.brregseedimport.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "organisasjonsnummer",
        "navn",
        "organisasjonsform",
        "hjemmeside",
        "postadresse",
        "registreringsdatoEnhetsregisteret",
        "registrertIMvaregisteret",
        "naeringskode1",
        "naeringskode2",
        "naeringskode3",
        "antallAnsatte",
        "forretningsadresse",
        "stiftelsesdato",
        "institusjonellSektorkode",
        "registrertIForetaksregisteret",
        "registrertIStiftelsesregisteret",
        "registrertIFrivillighetsregisteret",
        "sisteInnsendteAarsregnskap",
        "konkurs",
        "underAvvikling",
        "underTvangsavviklingEllerTvangsopplosning",
        "overornetEnhet",
        "maalform",
        "links",
        "timestamp"
})

public class OrganizationFromDb {

    @JsonProperty
    private String id;
    @JsonProperty("organisasjonsnummer")
    private String organisasjonsnummer;
    @JsonProperty("navn")
    private String navn;
    @JsonProperty("organisasjonsform")
    private Organisasjonsform organisasjonsform;
    @JsonProperty("hjemmeside")
    private String hjemmeside;
    @JsonProperty("postadresse")
    private Postadresse postadresse;
    @JsonProperty("registreringsdatoEnhetsregisteret")
    private String registreringsdatoEnhetsregisteret;
    @JsonProperty("registrertIMvaregisteret")
    private Boolean registrertIMvaregisteret;
    @JsonProperty("naeringskode1")
    private Naeringskode1 naeringskode1;
    @JsonProperty("naeringskode2")
    private Naeringskode2 naeringskode2;
    @JsonProperty("naeringskode3")
    private Naeringskode3 naeringskode3;
    @JsonProperty("antallAnsatte")
    private long antallAnsatte;
    @JsonProperty("forretningsadresse")
    private Forretningsadresse forretningsadresse;
    @JsonProperty("stiftelsesdato")
    private String stiftelsesdato;
    @JsonProperty("institusjonellSektorkode")
    private InstitusjonellSektorkode institusjonellSektorkode;
    @JsonProperty("registrertIForetaksregisteret")
    private Boolean registrertIForetaksregisteret;
    @JsonProperty("registrertIStiftelsesregisteret")
    private Boolean registrertIStiftelsesregisteret;
    @JsonProperty("registrertIFrivillighetsregisteret")
    private Boolean registrertIFrivillighetsregisteret;
    @JsonProperty("sisteInnsendteAarsregnskap")
    private String sisteInnsendteAarsregnskap;
    @JsonProperty("konkurs")
    private Boolean konkurs;
    @JsonProperty("underAvvikling")
    private Boolean underAvvikling;
    @JsonProperty("underTvangsavviklingEllerTvangsopplosning")
    private Boolean underTvangsavviklingEllerTvangsopplosning;
    @JsonProperty("overordnetEnhet")
    private String overordnetEnhet;
    @JsonProperty("maalform")
    private String maalform;
    @JsonProperty("links")
    private List<Object> links = null;
    @JsonProperty("timestamp")
    private long timestamp;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("organisasjonsnummer")
    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    @JsonProperty("organisasjonsnummer")
    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    @JsonProperty("navn")
    public String getNavn() {
        return navn;
    }

    @JsonProperty("navn")
    public void setNavn(String navn) {
        this.navn = navn;
    }

    @JsonProperty("organisasjonsform")
    public Organisasjonsform getOrganisasjonsform() {
        return organisasjonsform;
    }

    @JsonProperty("organisasjonsform")
    public void setOrganisasjonsform(Organisasjonsform organisasjonsform) {
        this.organisasjonsform = organisasjonsform;
    }

    @JsonProperty("hjemmeside")
    public String getHjemmeside() {
        return hjemmeside;
    }

    @JsonProperty("hjemmeside")
    public void setHjemmeside(String hjemmeside) {
        this.hjemmeside = hjemmeside;
    }

    @JsonProperty("postadresse")
    public Postadresse getPostadresse() {
        return postadresse;
    }

    @JsonProperty("postadresse")
    public void setPostadresse(Postadresse postadresse) {
        this.postadresse = postadresse;
    }

    @JsonProperty("registreringsdatoEnhetsregisteret")
    public String getRegistreringsdatoEnhetsregisteret() {
        return registreringsdatoEnhetsregisteret;
    }

    @JsonProperty("registreringsdatoEnhetsregisteret")
    public void setRegistreringsdatoEnhetsregisteret(String registreringsdatoEnhetsregisteret) {
        this.registreringsdatoEnhetsregisteret = registreringsdatoEnhetsregisteret;
    }

    @JsonProperty("registrertIMvaregisteret")
    public Boolean getRegistrertIMvaregisteret() {
        return registrertIMvaregisteret;
    }

    @JsonProperty("registrertIMvaregisteret")

    public void setRegistrertIMvaregisteret(Boolean registrertIMvaregisteret) {
        this.registrertIMvaregisteret = registrertIMvaregisteret;
    }

    @JsonProperty("naeringskode1")
    public Naeringskode1 getNaeringskode1() {
        return naeringskode1;
    }

    @JsonProperty("naeringskode1")
    public void setNaeringskode1(Naeringskode1 naeringskode1) {
        this.naeringskode1 = naeringskode1;
    }

    @JsonProperty("naeringskode2")
    public Naeringskode2 getNaeringskode2() {
        return naeringskode2;
    }

    @JsonProperty("naeringskode2")
    public void setNaeringskode2(Naeringskode2 naeringskode2) {
        this.naeringskode2 = naeringskode2;
    }

    @JsonProperty("naeringskode3")
    public Naeringskode3 getNaeringskode3() {
        return naeringskode3;
    }

    @JsonProperty("naeringskode3")
    public void setNaeringskode3(Naeringskode3 naeringskode3) {
        this.naeringskode3 = naeringskode3;
    }

    @JsonProperty("antallAnsatte")
    public long getAntallAnsatte() {
        return antallAnsatte;
    }

    @JsonProperty("antallAnsatte")
    public void setAntallAnsatte(long antallAnsatte) {
        this.antallAnsatte = antallAnsatte;
    }

    @JsonProperty("forretningsadresse")
    public Forretningsadresse getForretningsadresse() {
        return forretningsadresse;
    }

    @JsonProperty("forretningsadresse")
    public void setForretningsadresse(Forretningsadresse forretningsadresse) {
        this.forretningsadresse = forretningsadresse;
    }

    @JsonProperty("stiftelsesdato")
    public String getStiftelsesdato() {
        return stiftelsesdato;
    }

    @JsonProperty("stiftelsesdato")
    public void setStiftelsesdato(String stiftelsesdato) {
        this.stiftelsesdato = stiftelsesdato;
    }

    @JsonProperty("institusjonellSektorkode")
    public InstitusjonellSektorkode getInstitusjonellSektorkode() {
        return institusjonellSektorkode;
    }

    @JsonProperty("institusjonellSektorkode")
    public void setInstitusjonellSektorkode(InstitusjonellSektorkode institusjonellSektorkode) {
        this.institusjonellSektorkode = institusjonellSektorkode;
    }

    @JsonProperty("registrertIForetaksregisteret")
    public Boolean getRegistrertIForetaksregisteret() {
        return registrertIForetaksregisteret;
    }

    @JsonProperty("registrertIForetaksregisteret")
    public void setRegistrertIForetaksregisteret(Boolean registrertIForetaksregisteret) {
        this.registrertIForetaksregisteret = registrertIForetaksregisteret;
    }

    @JsonProperty("registrertIStiftelsesregisteret")
    public Boolean getRegistrertIStiftelsesregisteret() {
        return registrertIStiftelsesregisteret;
    }

    @JsonProperty("registrertIStiftelsesregisteret")
    public void setRegistrertIStiftelsesregisteret(Boolean registrertIStiftelsesregisteret) {
        this.registrertIStiftelsesregisteret = registrertIStiftelsesregisteret;
    }

    @JsonProperty("registrertIFrivillighetsregisteret")
    public Boolean getRegistrertIFrivillighetsregisteret() {
        return registrertIFrivillighetsregisteret;
    }

    @JsonProperty("registrertIFrivillighetsregisteret")
    public void setRegistrertIFrivillighetsregisteret(Boolean registrertIFrivillighetsregisteret) {
        this.registrertIFrivillighetsregisteret = registrertIFrivillighetsregisteret;
    }

    @JsonProperty("sisteInnsendteAarsregnskap")
    public String getSisteInnsendteAarsregnskap() {
        return sisteInnsendteAarsregnskap;
    }

    @JsonProperty("sisteInnsendteAarsregnskap")
    public void setSisteInnsendteAarsregnskap(String sisteInnsendteAarsregnskap) {
        this.sisteInnsendteAarsregnskap = sisteInnsendteAarsregnskap;
    }

    @JsonProperty("konkurs")
    public Boolean getKonkurs() {
        return konkurs;
    }

    @JsonProperty("konkurs")
    public void setKonkurs(Boolean konkurs) {
        this.konkurs = konkurs;
    }

    @JsonProperty("underAvvikling")
    public Boolean getUnderAvvikling() {
        return underAvvikling;
    }

    @JsonProperty("underAvvikling")
    public void setUnderAvvikling(Boolean underAvvikling) {
        this.underAvvikling = underAvvikling;
    }

    @JsonProperty("underTvangsavviklingEllerTvangsopplosning")
    public Boolean getUnderTvangsavviklingEllerTvangsopplosning() {
        return underTvangsavviklingEllerTvangsopplosning;
    }

    @JsonProperty("underTvangsavviklingEllerTvangsopplosning")
    public void setUnderTvangsavviklingEllerTvangsopplosning(Boolean underTvangsavviklingEllerTvangsopplosning) {
        this.underTvangsavviklingEllerTvangsopplosning = underTvangsavviklingEllerTvangsopplosning;
    }

    @JsonProperty("overordnetEnhet")
    public String getOverordnetEnhet() {
        return overordnetEnhet;
    }

    @JsonProperty("overordnetEnhet")
    public void setOverordnetEnhet(String overordnetEnhet) {
        this.overordnetEnhet = overordnetEnhet;
    }

    @JsonProperty("maalform")
    public String getMaalform() {
        return maalform;
    }

    @JsonProperty("maalform")
    public void setMaalform(String maalform) {
        this.maalform = maalform;
    }

    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("links")
    public List<Object> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Object> links) {
        this.links = links;
    }


    @Override
    public String toString() {
        return "Organization{" +
                "organisasjonsnummer='" + organisasjonsnummer + '\'' +
                ", navn='" + navn + '\'' +
                ", organisasjonsform=" + organisasjonsform +
                ", hjemmeside='" + hjemmeside + '\'' +
                ", postadresse=" + postadresse +
                ", registreringsdatoEnhetsregisteret='" + registreringsdatoEnhetsregisteret + '\'' +
                ", registrertIMvaregisteret=" + registrertIMvaregisteret +
                ", naeringskode1=" + naeringskode1 +
                ", naeringskode2=" + naeringskode2 +
                ", naeringskode3=" + naeringskode3 +
                ", antallAnsatte=" + antallAnsatte +
                ", forretningsadresse=" + forretningsadresse +
                ", stiftelsesdato='" + stiftelsesdato + '\'' +
                ", institusjonellSektorkode=" + institusjonellSektorkode +
                ", registrertIForetaksregisteret=" + registrertIForetaksregisteret +
                ", registrertIStiftelsesregisteret=" + registrertIStiftelsesregisteret +
                ", registrertIFrivillighetsregisteret=" + registrertIFrivillighetsregisteret +
                ", sisteInnsendteAarsregnskap='" + sisteInnsendteAarsregnskap + '\'' +
                ", konkurs=" + konkurs +
                ", underAvvikling=" + underAvvikling +
                ", underTvangsavviklingEllerTvangsopplosning=" + underTvangsavviklingEllerTvangsopplosning +
                ", overordnetEnhet='" + overordnetEnhet + '\'' +
                ", maalform='" + maalform + '\'' +
                ", links=" + links +
                '}';
    }
}
