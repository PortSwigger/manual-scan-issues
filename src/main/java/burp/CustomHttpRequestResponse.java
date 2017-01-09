package burp;

public class CustomHttpRequestResponse implements IHttpRequestResponse {    

    private String _comment = "";
    private String _highlight = "";
    private byte[] _request = null;
    private byte[] _response = null;
    private CustomHttpService _httpService = null;
    
    CustomHttpRequestResponse(String comment, String highlight, CustomHttpService httpService, byte[] request, byte[] response) {
        this._comment = comment;
        this._highlight = highlight;
        this._httpService = httpService; 
        this._request = request;
        this._response = response;
    }
    
    @Override
    public byte[] getRequest() { return this._request; }

    @Override
    public void setRequest(byte[] message) { this._request = message; }

    @Override
    public byte[] getResponse() { return this._response; }

    @Override
    public void setResponse(byte[] message) { this._response = message; }

    @Override
    public String getComment() { return this._comment; }

    @Override
    public void setComment(String comment) { this._comment = comment; }

    @Override
    public String getHighlight() { return this._highlight; }

    @Override
    public void setHighlight(String color) { this._highlight = color; }

    @Override
    public IHttpService getHttpService() { return this._httpService; }

    @Override
    public void setHttpService(IHttpService httpService) { this._httpService = (CustomHttpService)httpService; }
    
}
