package orz.springboot.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import orz.springboot.feign.exception.OrzFeignException;
import orz.springboot.feign.model.OrzFeignRst;
import orz.springboot.web.OrzWebApiDefinition;
import orz.springboot.web.OrzWebProps;
import orz.springboot.web.model.OrzWebErrorRsp;
import orz.springboot.web.model.OrzWebProtocolB1;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static orz.springboot.base.OrzBaseUtils.message;

@Slf4j
public class OrzFeignDecoder implements Decoder {
    private final Decoder delegate;
    private final OrzWebProps webProps;

    public OrzFeignDecoder(Decoder delegate, OrzWebProps webProps) {
        this.delegate = delegate;
        this.webProps = webProps;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        var method = response.request().requestTemplate().methodMetadata().configKey();
        var url = response.request().url();
        var protocol = extractProtocol(response, method, url).orElse(null);
        if (protocol != null) {
            var wrapperType = false;
            var decodeType = type;
            if (type instanceof ParameterizedType pt && OrzFeignRst.class == pt.getRawType()) {
                wrapperType = true;
                decodeType = pt.getActualTypeArguments()[0];
            }

            if (protocol.isSuccess()) {
                if (wrapperType) {
                    var data = delegate.decode(response, decodeType);
                    return OrzFeignRst.success(method, url, protocol, data);
                } else {
                    return delegate.decode(response, decodeType);
                }
            } else {
                var error = (OrzWebErrorRsp) delegate.decode(response, OrzWebErrorRsp.class);
                if (error == null) {
                    error = new OrzWebErrorRsp(protocol.getCode(), null, null);
                }
                if (wrapperType) {
                    return OrzFeignRst.error(method, url, protocol, error);
                } else {
                    throw new OrzFeignException(method, url, protocol, error);
                }
            }
        }
        return delegate.decode(response, type);
    }

    private Optional<OrzWebProtocolB1> extractProtocol(Response response, String method, String url) {
        if (response.status() == HttpStatus.OK.value()) {
            var field = webProps.getResponseHeaders();
            var version = getHeader(response, field.getVersion()).map(Integer::parseInt).orElse(null);
            if (version != null) {
                if (version < OrzWebApiDefinition.VERSION_MIN) {
                    var message = message("orz-api version not support", "version", version, "method", method, "url", url);
                    throw new DecodeException(HttpStatus.OK.value(), message, response.request());
                }
                var code = getHeader(response, field.getCode()).orElse(null);
                if (StringUtils.isNotBlank(code)) {
                    String notice = getHeader(response, field.getNotice()).map(s -> URLDecoder.decode(s, UTF_8)).orElse(null);
                    return Optional.of(OrzWebProtocolB1.error(version, code, notice));
                } else {
                    return Optional.of(OrzWebProtocolB1.success(version));
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<String> getHeader(Response response, String header) {
        return Optional.ofNullable(response.headers().get(header)).flatMap(h -> h.stream().findFirst());
    }
}
