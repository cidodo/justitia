package org.hyperledger.justitia.scheduler.service.bdc;

import org.hyperledger.justitia.service.face.channel.ChannelManageService;
import org.hyperledger.justitia.common.ExternalResources;
import org.hyperledger.justitia.common.exception.ServiceException;
import org.hyperledger.justitia.common.utils.file.file.FileUtils;
import org.hyperledger.justitia.common.utils.file.file.YamlFileUtils;
import org.hyperledger.justitia.common.utils.file.file.ZipFileUtils;
import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.tools.ConfigTxGen;
import org.hyperledger.justitia.farbic.tools.Cryptogen;
import org.hyperledger.justitia.farbic.utils.ssh.CallShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BDCService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BDCService.class);
    private final ConfigTxGen configtxgen;
    private final ChannelManageService channelManageService;

    @Autowired
    public BDCService(ConfigTxGen configtxgen, ChannelManageService channelManageService) {
        this.configtxgen = configtxgen;
        this.channelManageService = channelManageService;
    }

    public InputStream generateIdentityAndAddMember(String channelId, String orgName, String orgMspId, String identity, int userCount, int peerCount) {
        File organizationCrypto = generateIdentity(orgName, identity, userCount, peerCount);
        if (!organizationCrypto.exists() || !organizationCrypto.isDirectory()) {
            throw new ServiceException("Generate crypto failed.");
        }
        String msp =  organizationCrypto.getPath() + File.separator + "msp";
        LOGGER.info("Generated crypto.");

        String orgConfig = generateOrgConfig(orgName, orgMspId, new File(msp));
        LOGGER.info("Generated organization config.");


        channelManageService.addMember(channelId, orgName, orgConfig, "");
        LOGGER.info("Organization {} successfully joined channel {}.", orgName, channelId);


        return packageIdentityFile(organizationCrypto.getPath());
    }

    private InputStream packageIdentityFile(String source) {
        String packgeFile = ExternalResources.getCrypto("crypto.zip");
        ZipFileUtils zipFileUtils = new ZipFileUtils();
        try {
            zipFileUtils.createWithSelf(source, packgeFile);
            LOGGER.info("The crypto is packaged.");
            return new FileInputStream(packgeFile);
        } catch (IOException e) {
            throw new ServiceException("Certificate file packaging failed");
        } finally {
            FileUtils.delete(packgeFile);
        }
    }

    public InputStream generateIdentityIs(String orgName, String identity, int userCount, int peerCount) {
        File file = generateIdentity(orgName, identity, userCount, peerCount);
        return packageIdentityFile(file.getPath());
    }

    public File generateIdentity(String orgName, String identity, int userCount, int peerCount) {
        File cryptogenFile = new File(ExternalResources.getCrypto("cryptogen"));
        File configFile = new File(ExternalResources.getCrypto("crypto-config.yaml"));

        try {
            modifyConfigFile(configFile, orgName, identity, userCount, peerCount);
        } catch (FabricToolsException e) {
            throw new ServiceException("Failed to modify configuration file.", e);
        }

        try {
            Cryptogen cryptogen = Cryptogen.getInstance(cryptogenFile, configFile);
            cryptogen.extendCrypto();
        } catch (FabricToolsException e) {
            throw new ServiceException("Generate crypto failed.", e);
        }
        return new File(ExternalResources.getCrypto("crypto-config/peerOrganizations/"+ orgName.toLowerCase() + ".suzhou.com"));
    }

    private String generateOrgConfig(String orgName, String orgMspId, File msp) {
        String command = String.format("./configtxgen -printOrg %s", orgName);
        try {
            CallShell.Result result = configtxgen.callConfigTxGen(command, orgName, orgMspId, msp, null, null);
            if (result.isSuccess()) {
                return result.getPrintInfo();
            } else {
                throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
            }
        } catch (FabricToolsException e) {
            throw new ServiceException("Generate organization config failed.", e);
        }
    }

}
