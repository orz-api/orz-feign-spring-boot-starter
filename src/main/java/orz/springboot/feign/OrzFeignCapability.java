package orz.springboot.feign;

import feign.Capability;
import feign.codec.Decoder;
import orz.springboot.web.OrzWebProps;

public class OrzFeignCapability implements Capability {
    private final OrzWebProps webProps;

    public OrzFeignCapability(OrzWebProps webProps) {
        this.webProps = webProps;
    }

    @Override
    public Decoder enrich(Decoder decoder) {
        return new OrzFeignDecoder(decoder, webProps);
    }
}
