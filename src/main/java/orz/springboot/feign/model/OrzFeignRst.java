package orz.springboot.feign.model;

import lombok.Data;
import orz.springboot.web.model.OrzWebErrorRsp;
import orz.springboot.web.model.OrzWebProtocolB1;

@Data
public class OrzFeignRst<D> {
    private final String method;
    private final String url;
    private final OrzWebProtocolB1 protocol;
    private final D data;
    private final OrzWebErrorRsp error;

    private OrzFeignRst(String method, String url, OrzWebProtocolB1 protocol, D data, OrzWebErrorRsp error) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() {
        return protocol.isSuccess();
    }

    public static <D> OrzFeignRst<D> success(String method, String url, OrzWebProtocolB1 protocol, D data) {
        return new OrzFeignRst<>(method, url, protocol, data, null);
    }

    public static <D> OrzFeignRst<D> error(String method, String url, OrzWebProtocolB1 protocol, OrzWebErrorRsp error) {
        return new OrzFeignRst<>(method, url, protocol, null, error);
    }
}
