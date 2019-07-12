package org.hyperledger.justitia.farbic.sdk;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Set;

public class FabricUser implements User, Serializable{
    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment = null;
    private String mspId;

    //在Security中增加BouncyCastleProvider
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public FabricUser(String name, String affiliation){
        this.name = name;
        this.affiliation = affiliation;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public void setEnrollment(String certPem, String keyPem) throws IOException {
        PrivateKey privateKey = pemToPrivateKey(keyPem);
        this.enrollment = new X509Enrollment(privateKey, certPem);
    }

    @Override
    public String getMspId() {
        return this.mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    private PrivateKey pemToPrivateKey(String keyPem) throws IOException {
        final Reader pemReader = new StringReader(keyPem);
        PEMParser pemParser = new PEMParser(pemReader);
        PrivateKeyInfo pemPair = (PrivateKeyInfo) pemParser.readObject();
        return new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
    }
}
