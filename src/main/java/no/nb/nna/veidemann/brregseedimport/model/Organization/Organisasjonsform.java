package no.nb.nna.veidemann.brregseedimport.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kode",
        "beskrivelse",
        "links"
})
public class Organisasjonsform {

    @JsonProperty("kode")
    private String kode;
    @JsonProperty("beskrivelse")
    private String beskrivelse;
    @JsonProperty("links")
    private List<Object> links = null;

    @Override
    public String toString() {
        return "Organisasjonsform{" +
                "kode='" + kode + '\'' +
                ", beskrivelse='" + beskrivelse + '\'' +
                ", links=" + links +
                '}';
    }

    @JsonProperty("kode")
    public String getKode() {
        return kode;
    }

    @JsonProperty("kode")
    public void setKode(String kode) {
        this.kode = kode;
    }

    @JsonProperty("beskrivelse")
    public String getBeskrivelse() {
        return beskrivelse;
    }

    @JsonProperty("beskrivelse")
    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    @JsonProperty("links")
    public List<Object> getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(List<Object> links) {
        this.links = links;
    }
}