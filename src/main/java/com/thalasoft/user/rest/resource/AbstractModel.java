package com.thalasoft.user.rest.resource;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractModel extends RepresentationModel<AbstractModel> {

    @JsonProperty("id")
    private Long modelId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.modelId == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
            return false;
        }

        AbstractModel that = (AbstractModel) obj;

        return this.modelId.equals(that.getModelId());
    }

    @Override
    public int hashCode() {
        return modelId == null ? 0 : modelId.hashCode();
    }

}
