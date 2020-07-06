package api;

import model.BarcodeResponse;
import model.request.BodyInsertInvoiceDetail;
import model.request.BodyInsertInvoiceDetailBrief;
import model.request.BodyUpdateInvoiceSendSMSStatus;
import model.request.MeterResetHis;
import model.request.ModelInsertInvoice;
import model.request.UpdateInvoice;
import model.request.UpdateMSSD;
import model.request.UpdateMoiNoi;
import model.request.UpdateUserPwd;
import model.response.GetMDSDByMoiNoi;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InvoiceApi {

    @GET("GetMucDichDungByMoiNoi")
    Call<GetMDSDByMoiNoi> getMDSDByMoiNoi(@Query("ms_mnoi") String ms_mnoi);

    @GET("GetThongTinDongHoaDon")
    Call<BarcodeResponse> getThongTinDongHoaDon(@Query("ms_dong") long ms_dong);

    @GET("GetUsePurposeByMoinoi")
    Call<BarcodeResponse> getUsePurposeByMoinoi(@Query("ms_mnoi") Double ms_mnoi);

    @GET("GetFriceByInvoiceFlow")
    Call<BarcodeResponse> getFriceByInvoiceFlow();

    @GET("LaySoHoaDonByMnoiTky")
    Call<BarcodeResponse> laySoHoaDonByMnoiTky(@Query("ms_mnoi") Double ms_mnoi, @Query("ms_tk") Double ms_tk);



    @PUT("CapNhatMucDichSuDung/{id}")
    Call<BarcodeResponse> capNhatMucDichSuDung(@Path("id") String ms_mnoi, @Body UpdateMSSD mdsdRequest);
    @PUT("CapNhatMoiNoi/{id}")
    Call<BarcodeResponse> capNhatMoiNoi(@Path("id") String ms_mnoi, @Body UpdateMoiNoi mnupdate);

    @PUT("UpdateInvoice/{id}")
    Call<BarcodeResponse> updateInvoice(@Path("id") long  so_hd, @Body UpdateInvoice hdupdate);

    @PUT("UpdateInvoicePayment/{id}")
    Call<BarcodeResponse> updateInvoicePayment(@Path("id") long  so_hd);

    @PUT("UpdateInvoiceSendSMSStatus/{id}")
    Call<BarcodeResponse> updateInvoiceSendSMSStatus(@Path("id") long  so_hd, @Body BodyUpdateInvoiceSendSMSStatus hdupdatesmsstatus);

    @PUT("UpdateNumberPrintInvoice/{id}")
    Call<BarcodeResponse> updateNumberPrintInvoice(@Path("id") long  so_hd);

    @GET("InvoiceGetSendSmsStatus")
    Call<BarcodeResponse> invoiceGetSendSmsStatus(@Query("so_hd") Double ms_mnoi);



    @POST("InsertInvoice")
    Call<Void> insertInvoice(@Body ModelInsertInvoice invoiceInsert);

    @POST("InsertInvoiceDetailBrief")
    Call<Void> insertInvoiceDetailBrief (@Body BodyInsertInvoiceDetailBrief bodyInsertInvoiceDetailBrief);

    @POST("InsertInvoiceDetail")
    Call<Void> insertInvoiceDetail (@Body BodyInsertInvoiceDetail bodyInsertInvoiceDetail);

    @DELETE("DeleteInvoice/{id}")
    Call<Void> deleteInvoice (@Path("id") long ms_hd);


}
