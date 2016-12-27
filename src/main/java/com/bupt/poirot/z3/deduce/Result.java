package com.bupt.poirot.z3.deduce;

import org.bson.Document;

public class Result extends Document {
    public Result() {
        super();
    }

    public Result append(final String key, final Object value) {
        super.append(key, value);
        return this;
    }
}
