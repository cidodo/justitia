package org.hyperledger.justitia.common.face.service.fabric;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public interface FabricToolsService {
    /**
     * congiftxgen
     */
    File generateGenesisBlock(String memberName, String mspId, File mspDirectory, String systemChainId, String consortium, ArrayList<String> ordererAddresses);
    InputStream generateMemberConfig(String memberName, String mspId, File mspDirectory, String organizationName);
    byte[] generateCreateChannelTx(String memberName, String mspId, File mspDirectory, String channelId, String consortium);

    /**
     * configtxlator
     */
    byte[] encode(String data, String protoType);
    String decode(byte[] data, String protoType);
    byte[] computeUpdate(byte[] original, byte[] updated, String channelName);

    /**
     * cryptogen
     */
    void generateCrypto(String orgName, String identity, int peerCount, int userCount);
    void extendCrypto(String orgName, String identity, int peerCount, int userCount);
}
