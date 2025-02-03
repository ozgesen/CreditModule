package org.credit.module.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CreateLoanRequestBody {

    private Long customerId;
    private Float amount;
    private Float interestRate;
    private Integer installmentsNum;

}
