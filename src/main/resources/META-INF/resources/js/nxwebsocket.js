function NxWebSocket(host, contextPath, websocketName, logVisible, logOnlyError, onmessageCallback) {
    this.logVisible = logVisible;
    this.logOnlyError = logOnlyError;    
    onmessage = onmessageCallback;
    onconnect = null;
    ondisconnect = null;
    connected = false;
    connectMessage = null;

    ws = createWebSocket();
    if (ws !== null) {
        ws.onopen = function(evt) {
            addLog('Connected', false);     
            connected = true;

            if (connectMessage !== null) {
                ws.send(connectMessage);
            }
            if (onconnect !== null) {                
                onconnect();
            }
        };

        ws.onclose = function(evt) {
            addLog('Disconnected', true);
            connected = false;
            if (ondisconnect !== null) {
                ondisconnect();
            }
        };

        ws.onmessage = function(evt) {
            addLog('Received: ' + evt.data, false);
            if (onmessage !== null) {
                onmessage(evt.data);
            }
        };

        ws.onerror = function(evt) {
            addLog('Error: ' + evt.data, true);
        };
    }
    
    this.setOnMessage = function(callback) {
        onmessage = callback;
    };
    
    this.setOnConnect = function(callback) {
        onconnect = callback;
    };
    
    this.setOnDisconnect = function(callback) {
        ondisconnect = callback;
    };
    
    this.sendMessage = function(message) {
        if (connected) {
            ws.send(message);
        } else {
            connectMessage = message;
        }
    };

    function createWebSocket() {
        if (window.location.protocol === 'https:') {
            wsProtocol = 'wss://';
        } else {
            wsProtocol = 'ws://';
        }
        
        if (!host || host.length === 0) {
            host = window.location.host;
        }
        
        if (!contextPath || contextPath.length === 0) {
            contextPath = location.pathname.split('/')[1];
        }
        
        url = wsProtocol + host + '/' + contextPath + '/' + websocketName;

        if ('WebSocket' in window) {
            return new WebSocket(url);
        } else if ('MozWebSocket' in window) {
            return new MozWebSocket(url);
        } else {
            addLog('Browser not supported.', true);
            return null;
        }
    }

    function addLog(message, isError) {
        line = 'WebSocket: ' + message;       
        
        log = document.getElementById("log");
        if (logVisible && log !== null && typeof log !== 'undefined' && (isError || !logOnlyError)) {
            setTimeout(function(){ 
                log.innerHTML = line;
            }, 2000);
        }

        if (typeof console !== 'undefined') {
            now = new Date();
            fmtDate =
                    formatField(now.getHours(), 2) + ':' +
                    formatField(now.getMinutes(), 2) + ':' +
                    formatField(now.getSeconds(), 2) + '.' +
                    formatField(now.getMilliseconds(), 3);
            
            console.log(fmtDate + ' ' + line);
        }        
    }

    function formatField(value, len) {
        s = String(value);
        while (s.length < len) {
            s = '0' + s;
        }
        return s;
    }
}