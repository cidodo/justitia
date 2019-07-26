package org.hyperledger.justitia.scheduler.controller.identity.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetOrdererBean extends SetNodeBean {
    private String systemChainId;
}
