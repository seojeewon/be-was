package webserver;

public class HttpResponse {
    private static Long numberOfResponses = 0L;
    private final Long responseId;
    private final HttpMessage messageInfo;

    public HttpResponse(HttpMessage messageInfo) {
        this.responseId = ++numberOfResponses;
        this.messageInfo = messageInfo;
    }

    public String toString() {
        return "Response ID: " + responseId+ " " + messageInfo.getResponseInfo();
    }

    public HttpMessage getMessage(){
        return messageInfo;
    }
}
