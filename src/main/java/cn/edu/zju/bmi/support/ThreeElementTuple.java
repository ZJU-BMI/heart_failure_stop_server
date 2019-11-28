package cn.edu.zju.bmi.support;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThreeElementTuple <A, B, C> {
    private A a;
    private B b;
    private C c;
}

