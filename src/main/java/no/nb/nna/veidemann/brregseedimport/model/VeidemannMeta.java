package no.nb.nna.veidemann.brregseedimport.model;

import java.util.List;
import java.util.Map;

public class VeidemannMeta {

    private String created;
    private String createdBy;
    private String description;
    private String lastModified;
    private String lastModifiedBy;
    private String name;
    private List<Map<String, Object>> label;

    public List<Map<String, Object>> getLabel() {
        return label;
    }

    public void setLabel(List<Map<String, Object>> label) {
        this.label = label;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static String getOrgNr(VeidemannMeta metaObj) {
        if (metaObj.label != null) {
            for (Map<String, Object> map : metaObj.label) {
                boolean foundKey = false;
                String searchKey = "key";
                if (map.containsKey(searchKey)) {
                    String key = (String) map.get(searchKey);
                    if ("Orgnummer".equals(key)) {
                        foundKey = true;
                    }
                }
                searchKey = "value";
                if (foundKey) {
                    if (map.containsKey(searchKey)) {
                        String key = (String) map.get(searchKey);
                        if (!"".equals(key)) {
                            return (String) map.get(searchKey);
                        }
                    }

                }

            }

        }
        return "Meta object doesn't contain a label";
    }

}


