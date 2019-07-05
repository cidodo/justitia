package org.hyperledger.justitia.farbic.tools;

import org.hyperledger.justitia.farbic.exception.FabricToolsException;
import org.hyperledger.justitia.farbic.utils.ssh.CallLocalShell;
import org.hyperledger.justitia.farbic.utils.ssh.CallShell;

import java.io.File;
import java.io.IOException;

public class Cryptogen {
    private final File cryptogenFile;
    private final File configFile;

    private static Cryptogen cryptogen = null;

    public static Cryptogen getInstance(File cryptogen, File config) throws FabricToolsException {
        if(null == Cryptogen.cryptogen) {
            synchronized (Cryptogen.class) {
                if (null == Cryptogen.cryptogen) {
                    Cryptogen.cryptogen = new Cryptogen(cryptogen, config);
                }
            }
        }
        return Cryptogen.cryptogen;
    }


    private Cryptogen(File cryptogen, File config) throws FabricToolsException {
        if (!cryptogen.exists()) {
            throw new FabricToolsException("Not found fabric tool cryptogen in directory " + cryptogen.getPath());
        }

        if (!config.exists()) {
            throw new FabricToolsException("Not found config file in directory " + config.getPath());
        }

        this.cryptogenFile = cryptogen;
        this.configFile = config;
    }

    public void extendCrypto() throws FabricToolsException {
        String command = String.format("./cryptogen extend --config=%s --input=./crypto-config", configFile);
        CallShell.Result result = callCryptogen(command);

        if (!result.isSuccess()) {
            throw new FabricToolsException("Configtxgen call failed:" + result.getErrorInfo());
        }
    }

    private synchronized CallShell.Result callCryptogen(String command) throws FabricToolsException {
        CallLocalShell shell = new CallLocalShell();
        try {
            return shell.execCmd(command, cryptogenFile.getParentFile());
        } catch (IOException | InterruptedException e) {
            throw new FabricToolsException("Configtxgen tool call failed.", e);
        }
    }
}
