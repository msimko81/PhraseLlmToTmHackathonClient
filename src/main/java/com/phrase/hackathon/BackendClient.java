package com.phrase.hackathon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BackendClient {

    //private final String BASE_URL = "http://localhost:8080";
    private final String BASE_URL = "https://phrase-hackathon-llm-tm.azurewebsites.net";

    private final DatasetClient datasetClient;
    private final OpenAiClient openAiClient;

    public BackendClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient.Builder()
                        .callTimeout(Duration.of(30, ChronoUnit.SECONDS))
                        .readTimeout(Duration.of(30, ChronoUnit.SECONDS))
                        .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                        .build())
                .build();

        datasetClient = retrofit.create(DatasetClient.class);
        openAiClient = retrofit.create(OpenAiClient.class);
    }

    @SneakyThrows
    public List<String> listSupportedGptModels() {
        return openAiClient.listSupportedModels().execute().body();
    }

    @SneakyThrows
    public List<DatasetIdNameResponse> listDatasets() {
        return datasetClient.listDatasets().execute().body();
    }

    @SneakyThrows
    public Dataset getDataset(String datasetId) {
        return datasetClient.getDataset(datasetId).execute().body();
    }

    @RequiredArgsConstructor
    public enum PromptType {
        Transform(2),

        PropagateChange(3);

        private final int promptTypeNumber;
    }

    @SneakyThrows
    public Dataset vaporiseDataset(String datasetId, String prompt, PromptType promptType, String gptModel) {
        return datasetClient.vaporiseDataset(
                VaporiseRequest.builder().datasetId(datasetId).promptType(prompt).build(),
                gptModel,
                promptType.promptTypeNumber
        ).execute().body();
    }

    public static void main(String[] args) {
        BackendClient backendClient = new BackendClient();
        List<DatasetIdNameResponse> datasets = backendClient.listDatasets();
        System.out.println(datasets);

        Dataset dataset = backendClient.getDataset("dataset1");
        System.out.println(dataset);
    }
}
