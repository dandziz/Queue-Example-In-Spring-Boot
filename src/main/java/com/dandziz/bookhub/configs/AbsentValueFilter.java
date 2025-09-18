package com.dandziz.bookhub.configs;

import com.dandziz.bookhub.domains.FieldWrapper;

public class AbsentValueFilter {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FieldWrapper<?> fw)) return false;
        return !fw.isPresent();
    }
}
