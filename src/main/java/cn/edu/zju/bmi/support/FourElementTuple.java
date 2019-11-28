package cn.edu.zju.bmi.support;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FourElementTuple <A, B, C, D> {
    private A a;
    private B b;
    private C c;
    private D d;
}

