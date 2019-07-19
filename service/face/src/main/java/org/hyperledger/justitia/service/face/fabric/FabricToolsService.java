package org.hyperledger.justitia.service.face.fabric;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public interface FabricToolsService {
    /**
     * congiftxgen
     */
    File generateGenesisBlock(String systemChainId, String consortium, ArrayList<String> ordererAddresses);
    InputStream generateMemberConfig();
    byte[] generateCreateChannelTx(String channelId, String consortium);

    /**
     * configtxlator
     */
    byte[] encode(String data, String protoType);
    String decode(byte[] data, String protoType);
    byte[] computeUpdate(byte[] original, byte[] updated, String channelName);

    /**
     * cryptogen
     */
    void generateCrypto();
    void extendCrypto();
}
