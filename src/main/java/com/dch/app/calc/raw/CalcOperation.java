package com.dch.app.calc.raw;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public enum CalcOperation implements Operation {

    MULTIPLY {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return oldValue * requestValue;
        }
    },
    DIVIDE {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return oldValue / requestValue;
        }
    },
    ADD {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return oldValue + requestValue;
        }
    },
    SUBSTRUCT {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return oldValue - requestValue;
        }
    },

    SET_VALUE {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return requestValue;
        }
    },
    GET_VALUE {
        @Override
        public long calculate(long oldValue, long requestValue) {
            return oldValue;
        }
    };

}
