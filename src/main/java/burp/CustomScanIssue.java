package burp;
import java.net.URL;

public class CustomScanIssue implements IScanIssue {
    
    private CustomHttpService _httpService = null;
    private URL _url = null;
    private CustomHttpRequestResponse[] _httpMessages = null;
    private String _issueName = "";
    private String _issueDetail = "";
    private String _confidence = "";
    private String _severity = "";
    private String _issueBackground = "";
    private String _remediationBackground = "";
    private String _remediationDetial = "";
    private int _issueType = 134217728;
    
    CustomScanIssue(CustomHttpService httpService, URL url, CustomHttpRequestResponse[] httpMessages, String issueName, 
            String issueDetail, int issueType, String confidence, String severity, String issueBackground, String remediationBackground,
            String remediationDetail) {
        _httpService = httpService;
        _url = url;
        _httpMessages = httpMessages;
        _issueName = issueName;
        _issueDetail = issueDetail;
        _confidence = confidence;
        _severity = severity;
        _issueBackground = issueBackground;
        _remediationBackground = remediationBackground;
        _remediationDetial = remediationDetail;
    }

    @Override
    public URL getUrl() { return this._url; }

    @Override
    public String getIssueName() { return this._issueName; }

    @Override
    public int getIssueType() { return this._issueType; }

    @Override
    public String getSeverity() { return this._severity; }

    @Override
    public String getConfidence() { return this._confidence; }

    @Override
    public String getIssueBackground() { return this._issueBackground; }

    @Override
    public String getRemediationBackground() { return this._remediationBackground; }

    @Override
    public String getIssueDetail() { return this._issueDetail; }

    @Override
    public String getRemediationDetail() { return this._remediationDetial; }

    @Override
    public IHttpRequestResponse[] getHttpMessages() { return this._httpMessages; }

    @Override
    public IHttpService getHttpService() { return this._httpService; }   
}
