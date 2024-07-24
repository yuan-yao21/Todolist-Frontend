package com.stu.minote.http;

import com.google.gson.JsonObject;
import com.stu.minote.entity.NoteBeen;
import com.stu.minote.entity.UserInfo;
import com.stu.minote.http.res.LoginRes;
import com.stu.minote.http.res.NoteRes;
import com.stu.minote.http.res.TypeRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetService {

    @PATCH("api/v1/login")
    Call<LoginRes> login(@Body JsonObject parmas);

    @POST("api/v1/register")
    Call<LoginRes> register(@Body JsonObject parmas);

    @POST("api/v1/update")
    @FormUrlEncoded
    Call<LoginRes> updateUser(@Header("authorization") String header, @Field("nickname") String nickname, @Field("mobile") String mobile, @Field("bio") String bio);

    @POST("api/v1/update")
    @Multipart
    Call<LoginRes> updateUserHead(@Header("authorization") String header, @Part MultipartBody.Part imageHead);

    @POST("api/v1/update")
    @FormUrlEncoded
    Call<LoginRes> updatePsd(@Header("authorization") String header, @Field("password") String password);

    @POST("api/v1/note/delete")
    @FormUrlEncoded
    Call<LoginRes> deleteNote(@Header("authorization") String header, @Field("note_id") String note_id);

    @Multipart
    @POST("api/v1/note/create")
    Call<LoginRes> addNote(@Header("authorization") String header, @Part("title") RequestBody title, @Part("textContent") RequestBody textContent, @Part("category") RequestBody category, @Part MultipartBody.Part image, @Part MultipartBody.Part audio);

    @Multipart
    @POST("api/v1/note/update")
    Call<LoginRes> updateNote(@Header("authorization") String header,
                              @Part("note_id") RequestBody noteId,
                              @Part("title") RequestBody title,
                              @Part("textContent") RequestBody textContent,
                              @Part("category") RequestBody category,
                              @Part MultipartBody.Part image,
                              @Part MultipartBody.Part audio);

    @GET("api/v1/note/list")
    Call<NoteRes> getNotes(@Header("authorization") String header, @Query("category") String category);

    @GET("api/v1/note/category")
    Call<TypeRes> getTypes(@Header("authorization") String header);

    @GET("api/v1/user")
    Call<UserInfo> getUserInfo(@Header("authorization") String header);

    @GET("api/v1/note/search")
    Call<NoteRes> searchNotes(@Header("authorization") String header, @Query("keyword") String keyword);

    @GET("api/v1/note/detail")
    Call<NoteBeen> getNoteDetail(@Header("authorization") String header, @Query("note_id") String note_id);

}
