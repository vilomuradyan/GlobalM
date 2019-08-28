package com.globalm.platform.network;

import android.support.annotation.Nullable;

import com.globalm.platform.listeners.CallbackListener;
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
import com.globalm.platform.models.assingments.ResponseValidationError;
import com.globalm.platform.models.assingments.SendRespondResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class RequestManager extends BaseRequestManager {

    private static RequestManager sRequestManager;
    private GlobalmAPI mGlobalmAPI;

    private RequestManager() {
        mGlobalmAPI = ResourceFactory.createResource(GlobalmAPI.class);
    }

    public static RequestManager getInstance() {
        if (sRequestManager == null) {
            sRequestManager = new RequestManager();
        }

        return sRequestManager;
    }

    public void signUp(String email, String firstName, String lastName, String password, String passwordConfirmation, CallbackListener<SignUpResponse> callback) {
        mGlobalmAPI.signUp(email, firstName, lastName, password, passwordConfirmation).enqueue(getCallback(callback));
    }

    public void signIn(String email, String password, CallbackListener<SignInResponse> callback) {
        mGlobalmAPI.signIn(email, password).enqueue(getCallback(callback));
    }

    public void resetPassword(String email, CallbackListener<ResetPasswordResponse> callback) {
        mGlobalmAPI.resetPassword(email).enqueue(getCallback(callback));
    }

    public void requestRefreshToken(CallbackListener<RefreshTokenResponse> callback) {
        mGlobalmAPI.requestRefreshToken(getAccessToken()).enqueue(getCallback(callback));
    }

    public void changePassword(String oldPassword, String newPassword, CallbackListener<ChangePasswordResponse> callback) {
        mGlobalmAPI.changePassword(oldPassword, newPassword).enqueue(getCallback(callback));
    }

    public void logout(String email, String password, CallbackListener<LogoutResponse> callback) {
        mGlobalmAPI.logout(email, password).enqueue(getCallback(callback));
    }

    public void connectNetwork(CallbackListener<SocialSignInResponse> callback) {
        mGlobalmAPI.connectNetwork(getAccessToken()).enqueue(getCallback(callback));
    }

    public void socialSignIn(CallbackListener<SocialSignInResponse> callback) {
        mGlobalmAPI.socialSignIn("https://app-dev.goglobalm.com/login/account").enqueue(getCallback(callback));
    }

    public void getAccessToken(String code, CallbackListener<SignInResponse> callback) {
        mGlobalmAPI.getAccessToken(code).enqueue(getCallback(callback));
    }

    public void getContent(int number, CallbackListener<Content> callback) {
        mGlobalmAPI.getContent(getAccessToken(), number).enqueue(getCallback(callback));
    }

    public void getContent(int number, String query, CallbackListener<Content> callback) {
        mGlobalmAPI.getContent(getAccessToken(), number, query).enqueue(getCallback(callback));
    }

    public void getContentForTag(int number, int tagId, CallbackListener<Content> callback) {
        mGlobalmAPI.getContentForTag(getAccessToken(), number, tagId).enqueue(getCallback(callback));
    }

    public void getContentForLocation(String query, CallbackListener<Content> callback) {
        mGlobalmAPI.getContentForLocation(getAccessToken(), query).enqueue(getCallback(callback));
    }

//    public void createContent(String title, String subtitle, String lan, String lng, CallbackListener<Response<Item>> callback){
//        mGlobalmAPI.createContent("Bearer "+loadString("access_token"), title, subtitle, lan, lng).enqueue(new Callback<Item>() {
//            @Override
//            public void onResponse(Call<Item> call, Response<Item> response) {
//                if (200 == response.code()){
//                    Log.d("TESTING", "create Content onResponse");
//                    callback.onSuccess(response);
//                }else {
//                    Log.d("TESTING", "createContent 405");
//                    callback.onFailure(new Throwable());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Item> call, Throwable t) {
//                Log.d("TESTING", "create Content onFailure");
//            }
//        });
//    }

    public void content(Map<String, RequestBody> partMan,
                        MultipartBody.Part video,
                        CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> callback) {
        mGlobalmAPI.content(getAccessToken(), partMan, video)
                .enqueue(getCallback(callback));
    }

    public Response<BaseResponseBody<List<Void>, GetContentDataModel<Item>>> syncedContentUpdate(
            Map<String, RequestBody> partMan,
            MultipartBody.Part video,
            int contentId) throws IOException {
        return mGlobalmAPI.contentUpdate(getAccessToken(), partMan, video, contentId).execute();
    }

    public Response<BaseResponseBody<List<Void>, GetContentDataModel<Item>>>
        syncedContentUpload(Map<String, RequestBody> partMan,
                        MultipartBody.Part video) throws IOException {
        return mGlobalmAPI.content(getAccessToken(), partMan, video).execute();
    }


    public void getAccountStatus(
            int pageSize,
            int pageNumber,
            CallbackListener<AccountStatusResponse> listener) {
        mGlobalmAPI
                .getAccountStatus(getAccessToken(), pageSize, pageNumber)
                .enqueue(getCallback(listener));
    }

    public void disconnectSocialAccount(
            long accountId,
            CallbackListener<BaseResponseBody<List<Void>, List<Void>>> listener) {
        mGlobalmAPI.disconnectSocialAccount(getAccessToken(), accountId).enqueue(getCallback(listener));
    }

    public Response<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>> getSyncedFileContent(int fileId)
            throws IOException {
         return mGlobalmAPI.getFileContent(getAccessToken(), fileId).execute();
    }

    public void getFileContent(
            Integer fileId,
            CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<FileData>>> callbackListener) {
        mGlobalmAPI.getFileContent(getAccessToken(), fileId).enqueue(getCallback(callbackListener));
    }

    @Nullable
    public Response<BaseResponseBody<List<Void>, PaginationData<ItemFile>>> getUserContentList(int userId)
            throws IOException {
         return mGlobalmAPI.getUserContentList(getAccessToken(), userId).execute();
    }

    @Nullable
    public Response<Item> updateContent(Map<String, RequestBody> partMap, MultipartBody.Part file)
            throws IOException {
        return mGlobalmAPI.updateContent(getAccessToken(), partMap, file).execute();
    }

    public void getProfileMe(CallbackListener<
            BaseResponseBody<List<Void>, GetContentDataModel<Owner>>> callbackListener) {
        mGlobalmAPI.getProfileMe(getAccessToken()).enqueue(getCallback(callbackListener));
    }

    public void saveProfileImage(Map<String, RequestBody> partMap,
                                 MultipartBody.Part file,
                                 CallbackListener<BaseResponseBody<
                                         List<Void>,
                                         GetContentDataModel<Owner>>> callbackListener) {
        mGlobalmAPI.saveProfileImage(getAccessToken(), partMap, file)
                .enqueue(getCallback(callbackListener));
    }

    public void addSkill(
            AddSkillBody body,
            CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>> callbackListener) {
        mGlobalmAPI.addSkill(getAccessToken(), body).enqueue(getCallback(callbackListener));
    }

    public void getSkills(
            int pageNumber,
            CallbackListener<BaseResponseBody<List<Void>, PaginationData<Tag>>> callbackListener) {
        mGlobalmAPI.getSkills(getAccessToken(), pageNumber).enqueue(getCallback(callbackListener));
    }

    public void deleteSkill(
            int skillId,
            CallbackListener<BaseResponseBody<List<Void>, GetContentDataModel<List<Tag>>>> callbackListener) {
        mGlobalmAPI.deleteSkill(getAccessToken(), skillId).enqueue(getCallback(callbackListener));
    }

    public void getMyContacts(int page, String q, @Nullable Integer skillId, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener) {
        mGlobalmAPI.getMyContacts(getAccessToken(), page, q, skillId).enqueue(getCallback(callbackListener));
    }

    public void getContacts(int page, String q, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener) {
        mGlobalmAPI.getContacts(getAccessToken(), page, q).enqueue(getCallback(callbackListener));
    }

    public void removeContact(int id, CallbackListener<BaseResponseBody<List<Void>, Object[]>> callbackListener) {
        mGlobalmAPI.removeContact(getAccessToken(), id).enqueue(getCallback(callbackListener));
    }

    public void sendContactRequest(int id, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Object>>>> callbackListener) {
        ContactRequest request = new ContactRequest(id);
        mGlobalmAPI.sendContactRequest(getAccessToken(), request).enqueue(getCallback(callbackListener));
    }

    public void acceptContactRequest(long id, CallbackListener<BaseResponseBody<List<Void>, Object[]>> callbackListener) {
        mGlobalmAPI.acceptContactRequest(getAccessToken(), id).enqueue(getCallback(callbackListener));
    }

    public void rejectContactRequest(long id, CallbackListener<BaseResponseBody<List<Void>, Object[]>> callbackListener) {
        mGlobalmAPI.rejectContactRequest(getAccessToken(), id).enqueue(getCallback(callbackListener));
    }

    public void cancelContactRequest(long id, CallbackListener<BaseResponseBody<List<Void>, Object[]>> callbackListener) {
        mGlobalmAPI.cancelContactRequest(getAccessToken(), id).enqueue(getCallback(callbackListener));
    }

    public void getContactRequests(int page, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener) {
        mGlobalmAPI.getContactRequests(getAccessToken(), page).enqueue(getCallback(callbackListener));
    }

    public void getContactRequestsPending(int page, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Contact>>>> callbackListener) {
        mGlobalmAPI.getContactRequestsPending(getAccessToken(), page).enqueue(getCallback(callbackListener));
    }

    public void getOrganizations(int page, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>> callbackListener) {
        mGlobalmAPI.getOrganizations(getAccessToken(), page).enqueue(getCallback(callbackListener));
    }

    public void getOrganizations(int page, String q, CallbackListener<BaseResponseBody<List<Void>, GetContentListModel<List<Organization>>>> callbackListener) {
        mGlobalmAPI.getOrganizations(getAccessToken(), page, q).enqueue(getCallback(callbackListener));
    }

    public void getAssignments(int pageNumber, CallbackListener<
            BaseResponseBody<List<Void>, PaginationData<Assignment>>> callbackListener) {
        mGlobalmAPI.getAssignments(getAccessToken(), pageNumber).enqueue(getCallback(callbackListener));
    }

    public void getAssignmentById(int assignmentId, CallbackListener<
            BaseResponseBody<List<Void>, GetContentDataModel<Assignment>>> callbackListener) {
        mGlobalmAPI.getAssignmentById(getAccessToken(), assignmentId)
                .enqueue(getCallback(callbackListener));
    }

    public void getResponses(
            int assignmentId,
            int pageNumber,
            CallbackListener<BaseResponseBody<List<Void>,
                    PaginationData<com.globalm.platform.models.assingments.Response>>> callbackListener) {
        mGlobalmAPI.getResponses(getAccessToken(), assignmentId, pageNumber)
                .enqueue(getCallback(callbackListener));
    }

    public void createResponse(
            int assignmentId,
            CreateResponseBody body,
            CallbackListener<SendRespondResponse> callbackListener) {
        mGlobalmAPI
                .createResponse(getAccessToken(), assignmentId, body)
                .enqueue(getCallback(callbackListener));
    }
}