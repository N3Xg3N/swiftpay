package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Cash Account (DbtrAcct / CdtrAcct). */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CashAccount {

    @XmlElement(name = "Id", required = true)
    private AccountIdentification id;
}
