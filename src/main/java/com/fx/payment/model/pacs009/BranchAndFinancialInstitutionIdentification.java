package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Branch and Financial Institution Identification – wrapper used for Dbtr, Cdtr, DbtrAgt, CdtrAgt. */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class BranchAndFinancialInstitutionIdentification {

    @XmlElement(name = "FinInstnId", required = true)
    private FinancialInstitutionIdentification finInstnId;
}
