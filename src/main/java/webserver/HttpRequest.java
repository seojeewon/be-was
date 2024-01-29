package webserver;

public class HttpRequest {
    private static Long numberOfRequests = 0L;
    private final Long requestId;
    private final HttpMessage messageInfo;

    public HttpRequest(HttpMessage messageInfo) {
        this.requestId = ++numberOfRequests;
        this.messageInfo = messageInfo;
    }

    @Override
    public String toString() {
        return "Request ID: " + requestId + " " + messageInfo.getRequestInfo();
    }

    public HttpMessage getMessage(){
        return messageInfo;
    }

    public String getHeader(String headerName){
        return messageInfo.getHeader(headerName);
    }

}
