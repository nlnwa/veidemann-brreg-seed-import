package no.nb.nna.veidemann.brregseedimport.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kode",
        "beskrivelse"
})
public class InstitusjonellSektorkode {

    @JsonProperty("kode")
    private String kode;
    @JsonProperty("beskrivelse")
    private String beskrivelse;

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

    @Override
    public String toString() {
        return "InstitusjonellSektorkode{" +
                "kode='" + kode + '\'' +
                ", beskrivelse='" + beskrivelse + '\'' +
                '}';
    }
}
