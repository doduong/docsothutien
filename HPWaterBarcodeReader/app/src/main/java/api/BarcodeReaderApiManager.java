package api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.CommonText;

public class BarcodeReaderApiManager {
    CommonText common = new CommonText();
    private final static String BASE_URL = "http://113.160.100.217:8080/api/";
    private static BarcodeReaderApiManager instance;
    private Retrofit retrofit;
    private BarcodeReaderApiManager(){
        retrofit = new Retrofit.Builder()
                .baseUrl(common.URL_API+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static BarcodeReaderApiManager getInstance(){
        if(instance == null){
            instance = new BarcodeReaderApiManager();
        }
        return instance;
    }

    public  AccountApi accountApi(){

        return retrofit.create(AccountApi.class);
    }

    public WaterApi waterApi(){
        return  retrofit.create(WaterApi.class);
    }

    public InvoiceApi invoiceApi(){
        return  retrofit.create(InvoiceApi.class);
    }

}
