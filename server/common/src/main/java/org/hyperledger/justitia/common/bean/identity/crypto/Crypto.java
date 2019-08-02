package org.hyperledger.justitia.common.bean.identity.crypto;

public abstract class Crypto {
    private Msp msp;
    private Ca ca;
    private Ca tlsCa;

    abstract protected String generateMspId();

    abstract protected String generateCaId();

    abstract protected String generateTlsCaId();

    public void setMsp(Msp msp) {
        msp.setId(generateMspId());
        this.msp = msp;
    }

    public Msp getMsp() {
        return msp;
    }

    public void setCa(Ca ca) {
        ca.setId(generateCaId());
        this.ca = ca;
    }

    public Ca getCa() {
        return ca;
    }

    public void setTlsCa(Ca tlsCa) {
        tlsCa.setId(generateTlsCaId());
        this.tlsCa = tlsCa;
    }

    public Ca getTlsCa() {
        return tlsCa;
    }
}
