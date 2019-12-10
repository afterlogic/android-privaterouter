package com.PrivateRouter.PrivateMail.network.requests;

import android.content.Context;
import android.text.TextUtils;

import com.PrivateRouter.PrivateMail.model.Account;
import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.FolderType;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.network.ApiFactory;
import com.PrivateRouter.PrivateMail.network.ApiMethods;
import com.PrivateRouter.PrivateMail.network.ApiModules;
import com.PrivateRouter.PrivateMail.network.responses.BaseResponse;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.view.utils.EmailUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONObject;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallSendMessage extends CallRequest<BaseResponse> implements Callback<BaseResponse> {
    SendMessageParameter parameters = new SendMessageParameter();
    protected String method = ApiMethods.SEND_MESSAGE;
    private boolean send;

    public CallSendMessage(CallRequestResult callback) {
        super(callback);
    }


    public void setMessage(Context context, Message message, boolean send) {
        this.send = send;
        parameters.fromMessage(context, message);
    }


    @Override
    public void start() {
        Account account = LoggedUserRepository.getInstance().getActiveAccount();
        parameters.AccountID = account.getAccountID();
        if (send) {
            parameters.method = "SendMessage";
            parameters.SentFolder = account.getFolders().getFolderName(FolderType.Sent);
        } else {
            parameters.DraftFolder = account.getFolders().getFolderName(FolderType.Drafts);
        }

//IdentityID

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AttachmentObj.class, new AttachmentsSerializer()).create();
        String json = gson.toJson(parameters);

        Call<BaseResponse> call = ApiFactory.getService().sendMessage(ApiModules.MAIL, method, json);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

        if (response.isSuccessful()) {
            BaseResponse getMessageResponse = response.body();
            if (getMessageResponse != null && getMessageResponse.isSuccess()) {

                if (callback != null)
                    callback.onSuccess(getMessageResponse);
            } else {
                if (callback != null)
                    callback.onFail(ErrorType.ERROR_REQUEST, getMessageResponse.getErrorMessage(), getMessageResponse.getErrorCode());
            }

        } else {
            if (callback != null)
                callback.onFail(ErrorType.SERVER_ERROR, "", response.code());
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        if (callback != null)
            callback.onFail(ErrorType.FAIL_CONNECT, "", 0);
    }

    class AttachmentObj {
        JsonObject data;
    }

    ;

    class SendMessageParameter {
        int AccountID;
        String To;
        String Subject;
        String Text;
        boolean IsHtml;
        String SentFolder;
        String DraftFolder;
        int IdentityID;
        AttachmentObj Attachments;
        String method;


        public void fromMessage(Context context, Message message) {
            try {
                if (message != null) {
                    String toString = EmailUtils.getString(context, message.getTo(), false);
                    this.To = toString;

                    this.Subject = message.getSubject();

                    if (!TextUtils.isEmpty(message.getHtml())) {
                        this.IsHtml = true;
                        this.Text = message.getHtml();
                    } else {
                        this.IsHtml = false;
                        this.Text = message.getPlain();
                    }

                    parameters.IdentityID = message.getIdentityID();

                    if (message.getAttachments() != null && message.getAttachments().getAttachments() != null) {
                        Attachments = new AttachmentObj();
                        Attachments.data = new JsonObject();
                        for (Attachments attachments : message.getAttachments().getAttachments()) {
                            JsonArray jsonArray = new JsonArray();
                            jsonArray.add(attachments.getFileName());
                            jsonArray.add("");
                            jsonArray.add("0");
                            jsonArray.add("0");
                            jsonArray.add("");
                            Attachments.data.add(attachments.getTempName(), jsonArray);
                        }
                        //this.Attachments = attachmentsJson.toString();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class AttachmentsSerializer implements JsonSerializer<AttachmentObj> {
        public JsonElement serialize(final AttachmentObj attachmentObj, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            /*result.add("id", new JsonPrimitive(person.getId()));
            result.add("name", new JsonPrimitive(person.getName()));
            Person parent = person.getParent();
            if (parent != null) {
                result.add("parent", new JsonPrimitive(parent.getId()));
            }*/
            return attachmentObj.data;
        }
    }
}