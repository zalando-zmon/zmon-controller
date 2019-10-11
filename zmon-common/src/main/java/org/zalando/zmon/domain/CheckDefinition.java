package org.zalando.zmon.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.zmon.adapter.EntityListAdapter;
import org.zalando.zmon.diff.StatusDiff;

import de.zalando.typemapper.annotations.DatabaseField;

// TODO check command encoding
@XmlAccessorType(XmlAccessType.NONE)
@JsonFilter("checkRuntimeConfigFilter")
public class CheckDefinition implements StatusDiff {

    @XmlElement(required = true)
    @DatabaseField
    private Integer id;

    @XmlElement(required = true)
    @DatabaseField
    private String name;

    @XmlElement(required = true)
    @DatabaseField
    private String description;

    @XmlElement
    @DatabaseField
    private String technicalDetails;

    @XmlElement
    @DatabaseField
    private String potentialAnalysis;

    @XmlElement
    @DatabaseField
    private String potentialImpact;

    @XmlElement
    @DatabaseField
    private String potentialSolution;

    @XmlElement(required = true)
    @DatabaseField
    private String owningTeam;

    /* Map passing
     * JAXB also doesn't support Maps.  It handles Lists great, but Maps are
     * not supported directly. Use of a XmlAdapter to map the maps into beans that JAXB can use.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(EntityListAdapter.class)
    @DatabaseField
    private List<Map<String, String>> entities;

    @XmlElement(required = true)
    @DatabaseField
    private Long interval;

    @XmlElement(required = true)
    @DatabaseField
    private String command;

    @XmlElement(required = true)
    @DatabaseField
    private DefinitionStatus status;

    @XmlElement
    @DatabaseField
    private String sourceUrl;

    @XmlElement(required = true)
    @DatabaseField
    private String lastModifiedBy;

    @DatabaseField
    private Date lastModified;

    @XmlElement
    @DatabaseField
    private DefinitionRuntime runtime;

    @Transient
    private Tier tier = Tier.EMPTY;

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTechnicalDetails() {
        return technicalDetails;
    }

    public void setTechnicalDetails(final String technicalDetails) {
        this.technicalDetails = technicalDetails;
    }

    public String getPotentialAnalysis() {
        return potentialAnalysis;
    }

    public void setPotentialAnalysis(final String potentialAnalysis) {
        this.potentialAnalysis = potentialAnalysis;
    }

    public String getPotentialImpact() {
        return potentialImpact;
    }

    public void setPotentialImpact(final String potentialImpact) {
        this.potentialImpact = potentialImpact;
    }

    public String getPotentialSolution() {
        return potentialSolution;
    }

    public void setPotentialSolution(final String potentialSolution) {
        this.potentialSolution = potentialSolution;
    }

    public String getOwningTeam() {
        return owningTeam;
    }

    public void setOwningTeam(final String owningTeam) {
        this.owningTeam = owningTeam;
    }

    public List<Map<String, String>> getEntities() {
        return entities;
    }

    public void setEntities(final List<Map<String, String>> entities) {
        this.entities = entities;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(final Long interval) {
        this.interval = interval;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public DefinitionStatus getStatus() {
        return status;
    }

    public void setStatus(final DefinitionStatus status) {
        this.status = status;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(final String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public DefinitionRuntime getRuntime() {
        return runtime;
    }

    public void setRuntime(DefinitionRuntime runtime) {
        this.runtime = runtime;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public boolean isDeleted() {
        return getStatus() == DefinitionStatus.DELETED;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CheckDefinition [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", technicalDetails=");
        builder.append(technicalDetails);
        builder.append(", potentialAnalysis=");
        builder.append(potentialAnalysis);
        builder.append(", potentialImpact=");
        builder.append(potentialImpact);
        builder.append(", potentialSolution=");
        builder.append(potentialSolution);
        builder.append(", owningTeam=");
        builder.append(owningTeam);
        builder.append(", entities=");
        builder.append(entities);
        builder.append(", interval=");
        builder.append(interval);
        builder.append(", command=");
        builder.append(command);
        builder.append(", status=");
        builder.append(status);
        builder.append(", sourceUrl=");
        builder.append(sourceUrl);
        builder.append(", lastModifiedBy=");
        builder.append(lastModifiedBy);
        builder.append(", runtime=");
        builder.append(runtime);
        builder.append(", tier=");
        builder.append(tier);
        builder.append("]");
        return builder.toString();
    }

    public enum Tier {
        @JsonProperty("empty")
        EMPTY,

        @JsonProperty("critical")
        CRITICAL,

        @JsonProperty("important")
        IMPORTANT,

        @JsonProperty("others")
        OTHER;
    }
}
