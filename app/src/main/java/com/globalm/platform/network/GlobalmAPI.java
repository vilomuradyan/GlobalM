package com.globalm.platform.network;

import com.globalm.platform.models.AccountStatusResponse;
import com.globalm.platform.models.AddSkillBody;
import com.globalm.platform.models.BaseResponseBody;
import com.globalm.platform.models.ChangePasswordResponse;
import com.globalm.platform.models.Contact;
import com.globalm.platform.models.ContactRequest;
import com.globalm.platform.models.Content;
import com.globalm.platform.models.FileData;
import com.globalm.platform.models.GetContentDataModel;
import com.globalm.platform.models.GetContentListModel;
import com.globalm.platform.models.Item;
import com.globalm.platform.models.ItemFile;
import com.globalm.platform.models.LogoutResponse;
import com.globalm.platform.models.Organization;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.PaginationData;
import com.globalm.platform.models.RefreshTokenResponse;
import com.globalm.platform.models.ResetPasswordResponse;
import com.globalm.platform.models.SignInResponse;
import com.globalm.platform.models.SignUpResponse;
import com.globalm.platform.models.SocialSignInResponse;
import com.globalm.platform.models.Tag;
import com.globalm.platform.models.assingments.Assignment;
import com.globalm.platform.models.assingments.CreateResponseBody;
import com.globalm.platform.models.assingments.Response;
import com.globalm.platform.models.assingments.ResponseValidationError;
import com.globalm.platform.models.assingments.SendRespondResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GlobalmAPI {
    String AUTHORIZATION = "Authorization";
    int FIRST_PAGE = 1;

//    @GET("companies")
//    Call<Company> getCompanies();

//    @POST("companies/change/{id}")
//    Call<ResponseBody> changeCompanyAccount(@Path("id") long id);

    @FormUrlEncoded
    @POST("auth/signup")
    Call<SignUpResponse> signUp(@Field("email") String email,
                                @Field("first_name") String firstName,
                                @Field("last_name") String lastName,
                                @Field("password") String password,
                                @Field("password_confirmation") String passwordConfirmation);

    @FormUrlEncoded
    @POST("auth/token")
    Call<SignInResponse> signIn(@Field("email") String email,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/forgot")
    Call<ResetPasswordResponse> resetPassword(@Field("email") String email);

    @POST("auth/refresh")
    Call<RefreshTokenResponse> requestRefreshToken(@Header("Token") String token);

    @FormUrlEncoded
    @POST("auth/password")
    Call<ChangePasswordResponse> changePassword(@Field("old_password") String oldPassword,
                                                @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("auth/logout")
    Call<LogoutResponse> logout(@Field("email") String email,
                                @Field("password") String password);

    @POST("auth/connect")
    Call<SocialSignInResponse> connectNetwork(@Header(AUTHORIZATION) String header);

    @GET("auth/social")
    Call<SocialSignInResponse> socialSignIn(@Query("back") String app);

    @FormUrlEncoded
    @POST("auth/account")
    Call<SignInResponse> getAccessToken(@Field("code") String code);

    @GET("content?page[size]=20")
    Call<Content> getContent(@Header(AUTHORIZATION) String tokenWithBearer,
                             @Query("page[number]") int number);

    @GET("content?page[size]=20")
    Call<Content> getContent(@Header(AUTHORIZATION) String tokenWithBearer,
                             @Query("page[number]") int number, @Query("filter[title]") String query);

    @POST("content/search?page[size]=20&page[number]=1")
    Call<Content> getContentForLocation(@Header(AUTHORIZATION) String tokenWithBearer, @Query("terms[q]") String query);

    @GET("content?[size]=20")
    Call<Content> getContentForTag(@Header(AUTHORIZATION) String tokenWithBearer,
                                   @Query("page[number]") int number, @Query("filter[tag_id]") int tagId);


//    @FormUrlEncoded
//    @POST("content")
//    Call<Item> createContent(@Header("Authorization") String tokenWithBearer,
//                             @Field("title") String title,
//                             @Field("subtitle") String subtitle,
//                             @Field("geo_location[lat]")  String lat,
//                             @Field("geo_location[lng]") String lng);

    @Multipart
    @POST("content")
    Call<Item> content(@Header(AUTHORIZATION) String tokenWithBearer,
                          @Part("title") RequestBody title,
                          @Part("subtitle") RequestBody subtitle,
                          @Part("geo_name") RequestBody geoName,
                          @Part("geo_location[lat]") RequestBody geoLocationLat,
                          @Part("geo_location[lng]") RequestBody geoLocationLng,
                          @Part  MultipartBody.Part video);

    @Multipart
    @POST("content")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> content(
            @Header(AUTHORIZATION) String tokenWithBearer,
            @PartMap Map<String, RequestBody> partMan,
            @Part MultipartBody.Part video);

    @Multipart
    @POST("content/{content_id}")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> contentUpdate(
            @Header(AUTHORIZATION) String tokenWithBearer,
            @PartMap Map<String, RequestBody> partMan,
            @Part MultipartBody.Part video,
            @Path("content_id") int contentId);

    @GET("accounts?status=connected")
    Call<AccountStatusResponse> getAccountStatus(
            @Header(AUTHORIZATION) String header,
            @Query("page[size]") int pageSize,
            @Query("page[number]") int pageNumber);

    @DELETE("accounts/{acc_id}")
    Call<BaseResponseBody<List<Void>, List<Void>>> disconnectSocialAccount(
            @Header(AUTHORIZATION) String header,
            @Path("acc_id") long accountId);

    @GET("content/{file_id}")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>> getFileContent(
            @Header(AUTHORIZATION) String token,
            @Path("file_id") int fileId);

    @GET("content")
    Call<BaseResponseBody<List<Void>, PaginationData<ItemFile>>> getUserContentList(
            @Header(AUTHORIZATION) String token,
            @Query("filter[user_id]") int userId);

    @Multipart
    @PATCH("content")
    Call<Item> updateContent(
            @Header(AUTHORIZATION) String token,
            @PartMap Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);

    @GET("profiles/me")
    Call<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> getProfileMe(@Header(AUTHORIZATION) String token);

    @Multipart
    @POST("profiles/me")
    Call<BaseResponseBody<
            List<Void>,
            GetContentDataModel<Owner>>> saveProfileImage(@Header(AUTHORIZATION) String token,
                                                          @PartMap Map<String, RequestBody> partMap,
                                                          @Part MultipartBody.Part file);

    @POST("profiles/me/skills")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>> addSkill(
            @Header(AUTHORIZATION) String token,
            @Body AddSkillBody body);

    @GET("skills?page[size]=20")
    Call<BaseResponseBody<List<Void>, PaginationData<Tag>>> getSkills(
            @Header(AUTHORIZATION) String token,
            @Query("page[number]") int pageNumber);

    @DELETE("profiles/me/skills/{skillId}")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>> deleteSkill(
            @Header(AUTHORIZATION) String token,
            @Path("skillId") int skillId);

    @GET("contacts?filter[contact_group_id]=connected")//&filter[skill_id]=[1,2]")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>
    getMyContacts(
            @Header(AUTHORIZATION) String token,
            @Query("page[number]") int number,
            @Query("filter[q]") String q,
            @Query("filter[skill_id]") Integer skillId);

    @FormUrlEncoded
    @POST("contacts/search?page[size]=20")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>
    getContacts(
            @Header(AUTHORIZATION) String token,
            @Query("page[number]") int number,
            @Field("terms[q]") String q);

    @DELETE("users/me/connections/{id}")
    Call<BaseResponseBody<List<Void>, Object[]>> removeContact(@Header(AUTHORIZATION) String token,
                                                               @Path("id") int id);

    @POST("requests")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Object>>>>
    sendContactRequest(@Header(AUTHORIZATION) String token, @Body ContactRequest request);

    @POST("requests/{id}/accept")
    Call<BaseResponseBody<List<Void>, Object[]>> acceptContactRequest(
            @Header(AUTHORIZATION) String token,
            @Path("id") long id);

    @POST("requests/{id}/reject")
    Call<BaseResponseBody<List<Void>, Object[]>> rejectContactRequest(
            @Header(AUTHORIZATION) String token,
            @Path("id") long id);

    @POST("requests/{id}/cancel")
    Call<BaseResponseBody<List<Void>, Object[]>> cancelContactRequest(
            @Header(AUTHORIZATION) String token,
            @Path("id") long id);

    @GET("requests?page[size]=20&filter[target]=me")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>
    getContactRequests(@Header(AUTHORIZATION) String token, @Query("page[number]") int number);

    @GET("requests?page[size]=20&filter[creator]=me")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>>
    getContactRequestsPending(@Header(AUTHORIZATION) String token, @Query("page[number]") int number);

    @GET("organisations?page[size]=20")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>>
    getOrganizations(@Header(AUTHORIZATION) String token, @Query("page[number]") int number);

    @FormUrlEncoded
    @POST("organisations/search?page[size]=20")
    Call<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>>
    getOrganizations(
            @Header(AUTHORIZATION) String token,
            @Query("page[number]") int number,
            @Field("terms[q]") String q);

    @GET("assignments?page[size]=10")
    Call<BaseResponseBody<List<Void>, PaginationData<Assignment>>>
    getAssignments(@Header(AUTHORIZATION) String token, @Query("page[number]") int pageNumber);

    @GET("assignments/{assignmentId}")
    Call<BaseResponseBody<List<Void>, GetContentDataModel<Assignment>>> getAssignmentById(
            @Header(AUTHORIZATION) String token,
            @Path("assignmentId") Integer assignmentId);

    @GET("assignments/{assignmentId}/responses?page[size]=20")
    Call<BaseResponseBody<List<Void>, PaginationData<Response>>> getResponses(
            @Header(AUTHORIZATION) String token,
            @Path("assignmentId") int assignmentId,
            @Query("page[number]") int pageNumber);

    @POST("assignments/{assignmentId}/responses")
    Call<SendRespondResponse> createResponse(
            @Header(AUTHORIZATION) String token,
            @Path("assignmentId") int assignmentId,
            @Body CreateResponseBody body);
}