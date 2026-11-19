package testflow.TestFlow.infrastructure.entity.testCase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "teststep")
@Getter
@Setter
@NoArgsConstructor
public class TestStepEntity {
    @Id
    private UUID id;
    @Column(name = "testcaseid", nullable = false)
    private UUID testCaseId;
    @Column(name = "steporder", nullable = false)
    private int stepOrder;
    private String action;
    @Column(name = "expectedresult", nullable = false)
    private String expectedResult;
}
