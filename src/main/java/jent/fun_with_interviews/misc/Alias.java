package jent.fun_with_interviews.misc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Alias {

    public static class Amount extends BigDecimal {
        public Amount(char[] in, int offset, int len) {
            super(in, offset, len);
        }

        public Amount(char[] in, int offset, int len, MathContext mc) {
            super(in, offset, len, mc);
        }

        public Amount(char[] in) {
            super(in);
        }

        public Amount(char[] in, MathContext mc) {
            super(in, mc);
        }

        public Amount(String val) {
            super(val);
        }

        public Amount(String val, MathContext mc) {
            super(val, mc);
        }

        public Amount(double val) {
            super(val);
        }

        public Amount(double val, MathContext mc) {
            super(val, mc);
        }

        public Amount(BigInteger val) {
            super(val);
        }

        public Amount(BigInteger val, MathContext mc) {
            super(val, mc);
        }

        public Amount(BigInteger unscaledVal, int scale) {
            super(unscaledVal, scale);
        }

        public Amount(BigInteger unscaledVal, int scale, MathContext mc) {
            super(unscaledVal, scale, mc);
        }

        public Amount(int val) {
            super(val);
        }

        public Amount(int val, MathContext mc) {
            super(val, mc);
        }

        public Amount(long val) {
            super(val);
        }

        public Amount(long val, MathContext mc) {
            super(val, mc);
        }
    }
}
