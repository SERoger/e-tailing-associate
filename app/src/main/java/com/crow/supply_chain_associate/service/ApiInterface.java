package com.crow.supply_chain_associate.service;

import com.crow.supply_chain_associate.dto.Category;
import com.crow.supply_chain_associate.dto.Product;
import com.crow.supply_chain_associate.dto.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @POST("users/new")
    Call<Object> loginUser(@Body User user);

    @Multipart
    @POST("/image/upload")
    Call<String> uploadAttachment(@Part MultipartBody.Part filePart);

    @GET("/product/categories")
    Call<List<Category>> getCategories();

    @POST("product/product")
    Call<Product> addProduct(@Body Product product);
}
