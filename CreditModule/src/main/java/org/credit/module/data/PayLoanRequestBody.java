package org.credit.module.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PayLoanRequestBody {

    private Long customerId;
    private Long loanId;
    private Float amount;

}
