package no.nb.nna.veidemann.brregseedimport.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "land",
        "landkode",
        "postnummer",
        "poststed",
        "adresse",
        "kommune",
        "kommunenummer"
})

public class Forretningsadresse {


    @JsonProperty("land")
    private String land;
    @JsonProperty("landkode")
    private String landkode;
    @JsonProperty("postnummer")
    private String postnummer;
    @JsonProperty("poststed")
    private String poststed;
    @JsonProperty("adresse")
    private List<String> adresse = null;
    @JsonProperty("kommune")
    private String kommune;
    @JsonProperty("kommunenummer")
    private String kommunenummer;




    @JsonProperty("land")
    public String getLand() {
        return land;
    }

    @JsonProperty("land")
    public void setLand(String land) {
        this.land = land;
    }

    @JsonProperty("landkode")
    public String getLandkode() {
        return landkode;
    }

    @JsonProperty("landkode")
    public void setLandkode(String landkode) {
        this.landkode = landkode;
    }

    @JsonProperty("postnummer")
    public String getPostnummer() {
        return postnummer;
    }

    @JsonProperty("postnummer")
    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    @JsonProperty("poststed")
    public String getPoststed() {
        return poststed;
    }

    @JsonProperty("poststed")
    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    @JsonProperty("adresse")
    public List<String> getAdresse() {
        return adresse;
    }

    @JsonProperty("adresse")
    public void setAdresse(List<String> adresse) {
        this.adresse = adresse;
    }

    @JsonProperty("kommune")
    public String getKommune() {
        return kommune;
    }

    @JsonProperty("kommune")
    public void setKommune(String kommune) {
        this.kommune = kommune;
    }

    @JsonProperty("kommunenummer")
    public String getKommunenummer() {
        return kommunenummer;
    }

    @JsonProperty("kommunenummer")
    public void setKommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

    @Override
    public String toString() {
        return "Forretningsadresse{" +
                "land='" + land + '\'' +
                ", landkode='" + landkode + '\'' +
                ", postnummer='" + postnummer + '\'' +
                ", poststed='" + poststed + '\'' +
                ", adresse=" + adresse +
                ", kommune='" + kommune + '\'' +
                ", kommunenummer='" + kommunenummer + '\'' +
                '}';
    }

}
