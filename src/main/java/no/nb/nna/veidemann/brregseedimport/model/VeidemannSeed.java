package no.nb.nna.veidemann.brregseedimport.model;

import no.nb.nna.veidemann.brregseedimport.exceptions.InvalidSurtStringException;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VeidemannSeed {

    private String id;
    private String entityId;

    private VeidemannMeta meta;
    private Map<String, Object> scope;

    public String getId() {
        return id;
    }

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public VeidemannMeta getMeta() {
        return meta;
    }

    public void setMeta(VeidemannMeta meta) {
        this.meta = meta;
    }

    public Map<String, Object> getScope() {
        return scope;
    }

    public void setScope(Map<String, Object> scope) {
        this.scope = scope;
    }


    public void setScopeThroughString(String surtString) throws InvalidSurtStringException {

        HashMap<String, Object> hashMap = new HashMap<>();
        String formattedSurt;
        formattedSurt = generateSurtString(surtString);

        hashMap.put("surtPrefix", formattedSurt);
        setScope(hashMap);
    }


    /**
     * Metoden generer en formatert string for bruk i scopet til en seed.
     * Stringen genereres ut fra navnet på seed (en URL), som først blir kjørt gjennom et sett med validatorer
     * for å luke bort ugyldige url'er. Denne metoden hjelper også derfor til med å ikke opprette entiter
     * med ugyldige seeds.
     * @param urlString
     * @return
     * @throws InvalidSurtStringException
     */
    public static String generateSurtString(String urlString) throws InvalidSurtStringException {

        URL url;
        try {
            url = new URL(urlString);
            UrlValidator validator = new UrlValidator();
            if (!validator.isValid(urlString)) {
                throw new MalformedURLException("Invalid url: " + urlString);
            }
        } catch (MalformedURLException e) {

            urlString = "http://" + urlString;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e2) {
                url = null;
            }

        }


        if (url != null) {

            String hostName = url.getHost().toLowerCase();

            if (hostName.startsWith("www.")) {
                hostName = hostName.substring(hostName.indexOf("www.")+4);
            }

            String[] split = hostName.split("\\.");

            if (split.length >= 2) {
                int counter = split.length-1;

                String surt = "(";
                while (counter >= 0) {
                    surt += split[counter]+",";
                    counter--;
                }
                return surt;
            }
        }
        throw new InvalidSurtStringException("urlstring '" + urlString + "' is invalid.");
    }
}

