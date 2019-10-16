package com.exomatik.kapcake.Retrofit;

import com.exomatik.kapcake.Model.ModelUser;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by IrfanRZ on 02/08/2019.
 */

public interface PostCekLogin {
    String BASE_URL = "https://api.telkomreg7.id/api/";

    @POST
    Call<ModelUser> searchMovie(@Url String url);
}
