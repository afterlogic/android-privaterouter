package com.PrivateRouter.PrivateMail.network.responses;

import com.PrivateRouter.PrivateMail.model.Attachments;
import com.PrivateRouter.PrivateMail.model.MessageCollection;
import com.google.gson.annotations.SerializedName;

public class UploadAttachmentResponse extends BaseResponse {
    @SerializedName("Result")
    private
    Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    public class Result {
        @SerializedName("Attachment")
        private
        Attachments attachments;


        public Attachments getAttachments() {
            return attachments;
        }

        public void setAttachments(Attachments attachments) {
            this.attachments = attachments;
        }
    }

}
