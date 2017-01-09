package burp;

public class CustomHttpService implements IHttpService {

    private String _host = "";
    private String _protocol = "";
    private int _port = 0;
    
    CustomHttpService(String host, int port, String protocol) {
        this._host = host;
        this._port = port;
        this._protocol = protocol;
    }
    
    @Override
    public String getHost() { return this._host; }

    @Override
    public int getPort() { return this._port; }

    @Override
    public String getProtocol() { return this._protocol; }
    
}
