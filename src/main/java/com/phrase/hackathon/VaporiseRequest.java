package com.phrase.hackathon;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaporiseRequest {

    String datasetId;

    String promptType;

}
