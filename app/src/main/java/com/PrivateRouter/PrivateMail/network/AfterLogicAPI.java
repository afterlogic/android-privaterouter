package com.PrivateRouter.PrivateMail.network;


import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetAccountResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetCTagResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetContactInfoResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetContactsResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFolderResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetMessageBaseResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetMessageResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetMessagesBodiesResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetMessagesResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetFoldersMetaResponse;
import com.PrivateRouter.PrivateMail.network.responses.GetSettingsResponse;
import com.PrivateRouter.PrivateMail.network.responses.LoginResponse;
import com.PrivateRouter.PrivateMail.network.responses.UploadAttachmentResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface AfterLogicAPI {
    @POST("?/Api/" )
    @FormUrlEncoded
    Call<LoginResponse> login(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<BaseResponse> logout(@Field("Module")  String module, @Field("Method") String method   );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<BaseResponse> ping(@Field("Module")  String module, @Field("Method") String method );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetAccountResponse> getAccounts(@Field("Module")  String module, @Field("Method") String method );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetMessagesResponse> getMessages(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetMessageBaseResponse> getMessagesInfo(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetMessageResponse> getMessage(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetMessagesBodiesResponse> getMessagesBodies(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<BaseResponse> sendMessage(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<BaseResponse> moveMessages(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetFolderResponse> getFolder(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetFoldersMetaResponse> getRelevantFoldersInformation(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );


    @POST("?/Api/" )
    @FormUrlEncoded
    Call<BaseResponse> setEmailSafety(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetContactInfoResponse> getContactInfo(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetCTagResponse> getCTag(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetContactsResponse> getContactsByUids(@Field("Module")  String module, @Field("Method") String method , @Field("Parameters") String parameter  );

    @POST("?/Api/" )
    @Multipart
    Call<UploadAttachmentResponse> uploadAttachment(
            @Part MultipartBody.Part file, @Part("Module")  RequestBody  module, @Part("Method") RequestBody  method ,
                                                     @Part("Parameters") RequestBody  parameter  );
                                                     //@Part("jua-uploader\"; filename=\"pp.png\" ") RequestBody jua_uploader  );
                                                    //"jua-uploader"




    @Streaming
    @GET
    Call<ResponseBody> downloadAttachment(@Url String fileUrl);

    @POST("?/Api/" )
    @FormUrlEncoded
    Call<GetSettingsResponse> getSettings(@Field("Module")  String module, @Field("Method") String method);


}