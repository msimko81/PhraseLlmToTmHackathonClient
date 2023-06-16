package com.phrase.hackathon;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DatasetClient {

    @GET("/api/v1/datasets")
    Call<List<DatasetIdNameResponse>> listDatasets();

    @GET("/api/v1/datasets/{datasetId}")
    Call<Dataset> getDataset(@Path("datasetId") String datasetId);

    @POST("/api/v1/datasets/vaporise")
    Call<Dataset> vaporiseDataset(@Body VaporiseRequest request,
                            @Query("model") String model,
                            @Query("promptFormatNumber") int promptFormatNumber);
}
