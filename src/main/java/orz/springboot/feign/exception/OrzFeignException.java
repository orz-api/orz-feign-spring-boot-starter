package orz.springboot.feign.exception;

import feign.FeignException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import orz.springboot.feign.model.OrzFeignRst;
import orz.springboot.web.model.OrzWebErrorRsp;
import orz.springboot.web.model.OrzWebErrorTraceT1;
import orz.springboot.web.model.OrzWebProtocolB1;

import java.util.List;

import static orz.springboot.base.OrzBaseUtils.message;

@Getter
public class OrzFeignException extends FeignException {
    private final OrzWebProtocolB1 protocol;
    private final String reason;
    private final List<OrzWebErrorTraceT1> traces;
    private boolean alarm;
    private boolean logging;

    public OrzFeignException(String method, String url, OrzWebProtocolB1 protocol, OrzWebErrorRsp error) {
        super(HttpStatus.OK.value(), message(null, "method", method, "url", url, "protocol", protocol, "errorReason", error.getReason()));
        this.protocol = protocol;
        this.reason = error.getReason();
        this.traces = error.getTraces();
        this.alarm = false;
        this.logging = true;
    }

    public OrzFeignException(OrzFeignRst<?> response) {
        this(response.getMethod(), response.getUrl(), response.getProtocol(), response.getError());
    }

    public OrzFeignException setAlarm(boolean alarm) {
        this.alarm = alarm;
        return this;
    }

    public OrzFeignException setLogging(boolean logging) {
        this.logging = logging;
        return this;
    }

    public boolean codeEquals(String code) {
        return protocol.codeEquals(code);
    }
}
