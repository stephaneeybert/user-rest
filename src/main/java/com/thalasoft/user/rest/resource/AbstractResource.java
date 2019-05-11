package com.thalasoft.user.rest.resource;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResource extends ResourceSupport {

    @JsonProperty("id")
    private Long resourceId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.resourceId == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
            return false;
        }

        AbstractResource that = (AbstractResource) obj;

        return this.resourceId.equals(that.getResourceId());
    }

    @Override
    public int hashCode() {
        return resourceId == null ? 0 : resourceId.hashCode();
    }

}
