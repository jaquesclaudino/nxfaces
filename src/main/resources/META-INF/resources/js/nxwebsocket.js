function NxWebSocket(host, contextPath, websocketName, log, showLog, onmessage) {
    this.log = log;
    this.showLog = showLog;
    this.onmessage = onmessage;

    this.setOnMessage = function(onmessage) {
        this.onmessage = onmessage;
    };

    ws = createWebSocket();
    ws.onopen = function(evt) {
        addLog('Conectado');
        //ws.send("teste cliente conectado")
    };

    ws.onclose = function(evt) {
        addLog('Desconectado');
    };

    ws.onmessage = function(evt) {
        addLog('Recebido: ' + evt.data);
        if (onmessage !== null) {
            onmessage(evt.data);
        }
    };

    ws.onerror = function(evt) {
        addLog('Erro: ' + evt.data);
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
            addLog('Não é suportado neste navegador.');
            return null;
        }
    }

    function addLog(message) {
        if (!showLog) {
            return;
        }

        now = new Date();
        fmtDate =
                formatField(now.getHours(), 2) + ':' +
                formatField(now.getMinutes(), 2) + ':' +
                formatField(now.getSeconds(), 2) + ':' +
                formatField(now.getMilliseconds(), 3);

        line = fmtDate + ' WebSocket: ' + message;
        if (typeof console !== 'undefined') {
            console.log(line);
        }
        if (log !== null) {
            log.innerHTML = line;
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