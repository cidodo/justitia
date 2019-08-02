package org.hyperledger.justitia.common.bean.chaincode;

import lombok.Data;
import org.hyperledger.fabric.sdk.BlockEvent;

import java.util.Date;

@Data
public class ChaincodeInvokeResult {
    private boolean success;
    private ChaincodeInfo chaincodeInfo;
    private String channelId;
    private String transactionId;
    private Date timestamp;
    private String type;

    public ChaincodeInvokeResult(BlockEvent.TransactionEvent event) {
        if (null == event) {
            success = false;
            return;
        }

        success = event.isValid();
        transactionId = event.getTransactionID();
        channelId = event.getChannelId();
        timestamp = event.getTimestamp();

        switch (event.getType()) {
            case TRANSACTION_ENVELOPE:
                type = "TRANSACTION_ENVELOPE";
                break;
            case ENVELOPE:
                type = "ENVELOPE";
            default:
                type = "UNKNOWN";
        }
    }
}
