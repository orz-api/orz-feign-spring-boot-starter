package orz.springboot.feign.exception;

import feign.FeignException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import orz.springboot.feign.model.OrzFeignRst;
import orz.springboot.web.model.OrzWebErrorRsp;
import orz.springboot.web.model.OrzWebErrorTraceTo;
import orz.springboot.web.model.OrzWebProtocolBo;

import java.util.List;

import static orz.springboot.base.description.OrzDescriptionUtils.desc;

@Getter
public class OrzFeignException extends FeignException {
    private final String method;
    private final String url;
    private final OrzWebProtocolBo protocol;
    private final String reason;
    private final List<OrzWebErrorTraceTo> traces;
    private boolean alarm;
    private boolean logging;

    public OrzFeignException(String method, String url, OrzWebProtocolBo protocol, OrzWebErrorRsp error) {
        super(HttpStatus.OK.value(), desc(null, "method", method, "url", url, "protocol", protocol, "reason", error.getReason()));
        this.method = method;
        this.url = url;
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
