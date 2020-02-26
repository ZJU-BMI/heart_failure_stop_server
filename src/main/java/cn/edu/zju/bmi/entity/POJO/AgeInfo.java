package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgeInfo {
    private int zeroToNine;
    private int tenToNineteen;
    private int twentyToTwentyNine;
    private int thirtyToThirtyNine;
    private int fortyToFortyNine;
    private int fiftyToFiftyNine;
    private int sixtyToSixtyNine;
    private int seventyToSeventyNine;
    private int eightyToEightyNine;
    private int ninetyToNinetyNine;
    private int largerThanOneHundred;
}
