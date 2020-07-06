package api;

import model.BarcodeResponse;
import model.request.UpdateUserPwd;
import model.response.UserLoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountApi {

    @PUT("UserUpdatePassword/{id}")
    Call<BarcodeResponse> updateUserPassword(@Path("id") String userId, @Body UpdateUserPwd pwdRequest);

    @GET("CheckLogin")
    Call<UserLoginResponse> checkLogin(@Query("msbd") String msdb, @Query("mat_khau") String pwd);

    @GET("GetCurrentAppVersion")
    Call<UserLoginResponse> getVersion();

}
