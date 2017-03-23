package burp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender, IContextMenuFactory, ActionListener {
    String _applicationName = "ManScanAdd";
    String _version = "v1.0.2";
    String _request = "";
    String _response = "";
    String _issueName = "";
    String _issueDetail = "";
    String _issueBackground = "";
    String _confidence = "";
    String _remediationBackground = "";
    String _remediationDetail = "";
    String _severity = "";
    String _protocol = "";
    String _hostName = "";
    String _comment = "";
    int _port = 0;
    URL _url = null;
    CustomHttpService _httpService = null;
    CustomHttpRequestResponse _httpMessages = null;
    CustomScanIssue _scanIssue = null;
    IBurpExtenderCallbacks _callbacks = null;
    IContextMenuInvocation _invocation = null;
    IExtensionHelpers _helpers = null;
    JDialog _dialog = null;
    JDialog _addCommentDialog = null;
    JPanel _jPanel = null;
    JPanel _mainPanel = null;
    JButton _importFindingButton = null;
    JButton _addCommentButton = null;
    JTextArea _addCommentTextField = null;
    JTextArea _issueNameTextField = null;
    JTextArea _issueBackgroundTextField = null;
    JTextArea _urlTextField = null;
    JTextArea _portTextField = null;
    JTextArea _issueDetailTextArea = null;
    JTextArea _remediationBackgroundTextArea = null;
    JTextArea _requestTextArea = null;
    JTextArea _responseTextArea = null;
    JTextArea _remediationDetailTextArea = null;
    JComboBox _confidenceComboBox = null;
    JComboBox _severityComboBox = null;
    JComboBox _protocolComboBox = null;
    JScrollPane _scrollPane = null;
    JTabbedPane _tabbedPane = null;
    JMenuItem _menuItem = null;
    JMenuItem _addCommentMenuItem = null;
    List<JMenuItem> _menuItems = new ArrayList<>();
    PrintWriter stdout = null;
    PrintWriter stderr = null;
    IHttpRequestResponse[] _currentMessages = null;

    private URL getUrl(IScanIssue[] issues, IHttpRequestResponse[] messages) {
        if (issues != null && issues.length > 0)
        {
            return issues[0].getUrl();
        }

        if (messages != null && messages.length > 0)
        {
            return _helpers.analyzeRequest(messages[0]).getUrl();
        }

        return null;
    }
    
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        this._invocation = invocation;
        URL url = getUrl(invocation.getSelectedIssues(), invocation.getSelectedMessages());
        if (url == null) return null;

        // Get the address, protocol, and port from the current issue.
        stdout.println("Address grabbed: "+url.toString());
        this._portTextField.setText(Integer.toString(url.getPort()));
        if(url.getProtocol().equalsIgnoreCase("http"))
        {
            this._protocolComboBox.setSelectedIndex(0);
        }
        else if(url.getProtocol().equalsIgnoreCase("https"))
        {
            this._protocolComboBox.setSelectedIndex(1);
        }

        this._urlTextField.setText(url.toString());
        
        return this._menuItems;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { this._dialog.setVisible(true); }
    
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        // Setup output streams.
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        // Create a link on the menu.
        this._menuItem = new JMenuItem("Add Issue");
        this._menuItem.addActionListener(this);
        this._addCommentMenuItem = new JMenuItem("Add Comment");
        this._addCommentMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _addCommentDialog.setVisible(true);
            }
        });
        try {
            this._menuItems.add(0, _menuItem);
//            this._menuItems.add(1, _addCommentMenuItem);
        } catch(NullPointerException e) {
            stderr.println(e.getMessage());
        }
        
        // Display message when extension is loaded.
        stdout.println(this._applicationName + " " + this._version +"\nManually Add Scan Results.\n"
                + "Joshua Smith <joshua.smith@dynetics.com>\n"
                + "Benjamin Wireman <ben.wireman@dynetics.com>\n");
        
        // Initial extension prep.
        this._callbacks = callbacks;
        this._helpers = callbacks.getHelpers();
        callbacks.setExtensionName(this._applicationName);
        callbacks.registerContextMenuFactory(this);
        
        // Setup tab layout.
        SetupTab();
//        SetupCommentTab();
    }
    
    private void SetupCommentTab() {
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
        
        // Setup Issue Detail.
        JPanel addCommentPanel = new JPanel();
        this._addCommentTextField = new JTextArea("Enter comment...");
        this._addCommentTextField.setLineWrap(true);
//        this._addCommentTextField.setWrapStyleWord(true);
        addCommentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Comment:"));
        addCommentPanel.setLayout(new BoxLayout(addCommentPanel, BoxLayout.X_AXIS));
        _addCommentTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _addCommentTextField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _addCommentTextField.select(0, 0);
            }
        });
        addCommentPanel.add(this._addCommentTextField);
        commentPanel.add(addCommentPanel);
        
        JPanel addCommentButtonPanel = new JPanel();
        addCommentButtonPanel.setLayout(new BoxLayout(addCommentButtonPanel, BoxLayout.X_AXIS));
        this._addCommentButton = new JButton("Add");
        this._addCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "SHOWN!","Error", JOptionPane.ERROR_MESSAGE);
//                _currentIssue[0].getHttpMessages()[0].setComment(_addCommentTextField.getText());
//                stdout.println("Comment: " + _currentIssue[0].getHttpMessages()[0].getComment());
                stdout.println("here!!");
                _comment = _addCommentTextField.getText();
                _addCommentDialog.setVisible(false);
            }  
        });
        addCommentButtonPanel.add(this._addCommentButton);
        commentPanel.add(addCommentButtonPanel);
        
        this._addCommentDialog = new JDialog(this._addCommentDialog);
        this._addCommentDialog.setBounds(400, 100, 400, 300);
        this._addCommentDialog.setTitle("Add Comment");
        this._addCommentDialog.add(commentPanel);
    }
    
    private void SetupTab() {
        // Create tab
        this._mainPanel = new JPanel();
        this._mainPanel.setLayout(new BoxLayout(this._mainPanel, BoxLayout.Y_AXIS));
        this._tabbedPane = new JTabbedPane();
        this._jPanel = new JPanel();
        this._jPanel.setLayout(new BoxLayout(this._jPanel, BoxLayout.Y_AXIS));
        
        // Setup Issue Name text field.
        JPanel issueNamePanel = new JPanel();
        this._issueNameTextField = new JTextArea("Enter Issue Name...");
        this._issueNameTextField.setLineWrap(true);
        issueNamePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Issue Name:"));
        issueNamePanel.setLayout(new BoxLayout(issueNamePanel, BoxLayout.X_AXIS));
        this._issueNameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                _issueNameTextField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _issueNameTextField.select(0, 0);
            }
        });
        issueNamePanel.add(this._issueNameTextField);
        this._jPanel.add(issueNamePanel);
        
        // Setup Issue Detail.
        JPanel issueDetailPanel = new JPanel();
        this._issueDetailTextArea = new JTextArea("Enter Issue Detail...");
        this._issueDetailTextArea.setLineWrap(true);
        issueDetailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Issue Detail:"));
        issueDetailPanel.setLayout(new BoxLayout(issueDetailPanel, BoxLayout.X_AXIS));
        _issueDetailTextArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _issueDetailTextArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _issueDetailTextArea.select(0, 0);
            }
        });
        issueDetailPanel.add(this._issueDetailTextArea);
        this._jPanel.add(issueDetailPanel);
        
        // Setup Issue Background.
        JPanel issueBackgroundPanel = new JPanel();
        this._issueBackgroundTextField = new JTextArea("Enter Issue Background...");
        this._issueBackgroundTextField.setLineWrap(true);
        issueBackgroundPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Issue Background:"));
        issueBackgroundPanel.setLayout(new BoxLayout(issueBackgroundPanel, BoxLayout.X_AXIS));
        _issueBackgroundTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _issueBackgroundTextField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _issueBackgroundTextField.select(0, 0);
            }
        });
        issueBackgroundPanel.add(this._issueBackgroundTextField);
        this._jPanel.add(issueBackgroundPanel);
        
        // Setup HTTP Request text area.
        JPanel requestPanel = new JPanel();
        this._requestTextArea = new JTextArea("Enter HTTP Request...");
        this._requestTextArea.setLineWrap(true);
        requestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "HTTP Request:"));
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.X_AXIS));
        _requestTextArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _requestTextArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _requestTextArea.select(0, 0);
            }
        });
        JScrollPane requestScroll = new JScrollPane(this._requestTextArea);
        requestScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        requestPanel.add(requestScroll);
//        this._jPanel.add(requestPanel);
        
        // Setup HTTP Response text area.
        JPanel responsePanel = new JPanel();
        this._responseTextArea = new JTextArea("Enter HTTP Response...");
        this._responseTextArea.setLineWrap(true);
        responsePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "HTTP Response:"));
        responsePanel.setLayout(new BoxLayout(responsePanel, BoxLayout.X_AXIS));
        _responseTextArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _responseTextArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _responseTextArea.select(0, 0);
            }
        });
        JScrollPane responseScroll = new JScrollPane(this._responseTextArea);
        responseScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        responsePanel.add(responseScroll);
//        this._jPanel.add(responsePanel);
        
        // Setup Remediation Background.
        JPanel remediationBackgroundPanel = new JPanel();
        this._remediationBackgroundTextArea = new JTextArea("Enter Remediation Background...");
        this._remediationBackgroundTextArea.setLineWrap(true);
        remediationBackgroundPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Remediation Background:"));
        remediationBackgroundPanel.setLayout(new BoxLayout(remediationBackgroundPanel, BoxLayout.X_AXIS));
        _remediationBackgroundTextArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _remediationBackgroundTextArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _remediationBackgroundTextArea.select(0, 0);
            }
        });
        remediationBackgroundPanel.add(this._remediationBackgroundTextArea);
        this._jPanel.add(remediationBackgroundPanel);
        
        // Setup Remediation Detail.
        JPanel remediationDetailPanel = new JPanel();
        this._remediationDetailTextArea = new JTextArea("Enter Remediation Detail...");
        this._remediationDetailTextArea.setLineWrap(true);
        remediationDetailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Remediation Detail:"));
        remediationDetailPanel.setLayout(new BoxLayout(remediationDetailPanel, BoxLayout.X_AXIS));
        _remediationDetailTextArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _remediationDetailTextArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _remediationDetailTextArea.select(0, 0);
            }
        });
        remediationDetailPanel.add(this._remediationDetailTextArea);
        this._jPanel.add(remediationDetailPanel);
        
        // Setup URL.
        JPanel urlPanel = new JPanel();
        this._urlTextField = new JTextArea("Enter URL...");
        this._urlTextField.setLineWrap(true);
        urlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "URL (path = http://domain/path):"));
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.X_AXIS));
        _urlTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _urlTextField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _urlTextField.select(0, 0);
            }
        });
        urlPanel.add(this._urlTextField);
        this._jPanel.add(urlPanel);
        
        // Setup Port.
        JPanel portPanel = new JPanel();
        this._portTextField = new JTextArea("Enter port #...");
        this._portTextField.setLineWrap(true);
        portPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false), "Port:"));
        portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
        _portTextField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                _portTextField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                _portTextField.select(0, 0);
            }
        });
        portPanel.add(this._portTextField);
        this._jPanel.add(portPanel);
        
        // Setup confidence.
        JPanel confidencePanel = new JPanel();
        this._confidenceComboBox = new JComboBox();
        this._confidenceComboBox.addItem(makeObj("Certain"));
        this._confidenceComboBox.addItem(makeObj("Firm"));
        this._confidenceComboBox.addItem(makeObj("Tentative"));
        confidencePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Confidence:"));
        confidencePanel.setLayout(new BoxLayout(confidencePanel, BoxLayout.X_AXIS));
        confidencePanel.add(this._confidenceComboBox);
        this._jPanel.add(confidencePanel);
        
        // Setup Severity.
        JPanel severityPanel = new JPanel();
        this._severityComboBox = new JComboBox();
        this._severityComboBox.addItem("High");
        this._severityComboBox.addItem("Medium");
        this._severityComboBox.addItem("Low");
        this._severityComboBox.addItem("Information");
//        this._severityComboBox.addItem("False positive");
        severityPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Severity:"));
        severityPanel.setLayout(new BoxLayout(severityPanel, BoxLayout.X_AXIS));
        severityPanel.add(this._severityComboBox);
        this._jPanel.add(severityPanel);
        
        // Setup Protocol.
        JPanel protocolPanel = new JPanel();
        this._protocolComboBox = new JComboBox();
        this._protocolComboBox.addItem(makeObj("HTTP"));
        this._protocolComboBox.addItem(makeObj("HTTPS"));
//        this._protocolComboBox.addItem(makeObj("OTHER"));
        protocolPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Protocol:"));
        protocolPanel.setLayout(new BoxLayout(protocolPanel, BoxLayout.X_AXIS));
        protocolPanel.add(this._protocolComboBox);
        this._jPanel.add(protocolPanel);
        
        // Setup Import Finding button.
        JPanel importFindingButtonPanel = new JPanel();
        this._importFindingButton = new JButton("Import Finding");
        this._importFindingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // When "Import Finding" is clicked, this is what happens.
                    ImportFinding();
                } catch (MalformedURLException | ArrayIndexOutOfBoundsException | NullPointerException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
                    stdout.println(ex.getMessage());
                }
            }  
        });
        importFindingButtonPanel.add(_importFindingButton);
        this._jPanel.add(importFindingButtonPanel);
        
        // Setup tabs and main dialog window.
        JScrollPane panelScroll = new JScrollPane(this._jPanel);
        panelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this._tabbedPane.addTab("General", panelScroll);
        this._tabbedPane.addTab("HTTP Request",requestPanel);
        this._tabbedPane.addTab("HTTP Response",responsePanel);
        this._mainPanel.add(this._tabbedPane);
        this._dialog = new JDialog(this._dialog);
        this._dialog.setBounds(400, 100, 680, 730);
        this._dialog.setTitle(this._applicationName);
        this._dialog.add(_tabbedPane);
    }
    
    // For menus. 
    private Object makeObj(final String item) {
        return new Object() { 
            @Override 
            public String toString() { return item; } 
        }; 
    }
    
    private void ImportFinding() throws MalformedURLException, NumberFormatException, ArrayIndexOutOfBoundsException, NullPointerException, IllegalArgumentException {
        this._issueName = this._issueNameTextField.getText();
        this._issueDetail = this._issueDetailTextArea.getText();
        this._issueBackground = this._issueBackgroundTextField.getText();
        this._request = this._requestTextArea.getText();
        this._response = this._responseTextArea.getText();
        
        // Gets the host name out of the url..
        String tmpUrl = this._urlTextField.getText();
        String prot = "";
        String delims = "[/:]";
        String[] tmpStr; 
        if(this._urlTextField.getText().contains("https://") || this._urlTextField.getText().contains("http://")) {
            tmpStr = tmpUrl.split(delims);
            this._hostName = tmpStr[3];
            prot = tmpStr[0];
        }
        
        this._url = new URL(this._urlTextField.getText());
        this._confidence = this._confidenceComboBox.getSelectedItem().toString(); 
        this._remediationBackground = this._remediationBackgroundTextArea.getText();
        this._remediationDetail = this._remediationDetailTextArea.getText();
        this._severity = this._severityComboBox.getSelectedItem().toString();
        
        // Sets the port.
        this._protocol = this._protocolComboBox.getSelectedItem().toString();
        if(prot.equalsIgnoreCase("http") || this._protocol.equalsIgnoreCase("http"))
            this._portTextField.setText("80");
        else if(prot.equalsIgnoreCase("https") || this._protocol.equalsIgnoreCase("https"))
            this._portTextField.setText("443");
        this._port = Integer.parseInt(this._portTextField.getText());
        
        // Http Service...
        _httpService = new CustomHttpService(this._hostName, this._port, this._protocol);
        // Http Request Response Message...
        _httpMessages = new CustomHttpRequestResponse(this._comment, "", this._httpService, this._request.getBytes(), this._response.getBytes());
        // Custom Scan Issue...
        _scanIssue = new CustomScanIssue(this._httpService, this._url, new CustomHttpRequestResponse[]{this._httpMessages}, this._issueName,
            this._issueDetail, 134217728, this._confidence, this._severity, this._issueBackground, this._remediationBackground, this._remediationDetail);
        
        // Helpful output to check values.
        stdout.println("\nHere are the HttpService methods:");
        stdout.printf("HttpService Host: %s\n", this._httpService.getHost());
        stdout.printf("HttpService Port: %s\n", this._httpService.getPort());
        stdout.printf("HttpService Protocol: %s\n", this._httpService.getProtocol());
        stdout.println("\nHere are the HttpRequestResponse methods:");
        stdout.printf("HttpRequestResponse Comment: %s\n", this._httpMessages.getComment());
        stdout.printf("HttpRequestResponse Highlight: %s\n", this._httpMessages.getHighlight());
        stdout.printf("HttpRequestResponse HttpService: %s\n", this._httpMessages.getHttpService().toString());
        stdout.printf("HttpRequestResponse Request: %s\n", Arrays.toString(this._httpMessages.getRequest()));
        stdout.printf("HttpRequestResponse Response: %s\n", Arrays.toString(this._httpMessages.getResponse()));
//        stdout.printf("HttpRequestResponse Comment: %s\n", this._httpMessages.getComment());
        stdout.println("\nHere are the ScanIssue methods:");
        stdout.printf("ScanIssue Confidence: %s\n", this._scanIssue.getConfidence());
        stdout.printf("ScanIssue HttpMessages: %s\n", Arrays.toString(this._scanIssue.getHttpMessages()));
        stdout.printf("ScanIssue HttpService: %s\n", this._scanIssue.getHttpService());
        stdout.printf("ScanIssue IssueBackground: %s\n", this._scanIssue.getIssueBackground());
        stdout.printf("ScanIssue IssueDetail: %s\n", this._scanIssue.getIssueDetail());
        stdout.printf("ScanIssue IssueName: %s\n", this._scanIssue.getIssueName());
        stdout.printf("ScanIssue RemediationBackground: %s\n", this._scanIssue.getRemediationBackground());
        stdout.printf("ScanIssue RemediationDetail: %s\n", this._scanIssue.getRemediationDetail());
        stdout.printf("ScanIssue Severity: %s\n", this._scanIssue.getSeverity());
        stdout.printf("ScanIssue URL: %s\n", this._scanIssue.getUrl());
        stdout.println("\n");

        // Add our issue and hide the window.
        this._callbacks.addScanIssue(this._scanIssue);
        if(this._dialog.isVisible())
            this._dialog.setVisible(false);
    }
}
