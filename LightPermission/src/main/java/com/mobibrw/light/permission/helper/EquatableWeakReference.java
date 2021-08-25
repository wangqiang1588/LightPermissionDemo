package com.mobibrw.light.permission.helper;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by longsky on 2017/9/3.
 */

public class EquatableWeakReference<T> extends WeakReference<T> {

    /**
     * Creates a new instance of EquatableWeakReference.
     *
     * @param referent The object that this weak reference should reference.
     */
    public EquatableWeakReference(final T referent) {
        super(referent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        Object strong_obj = get();
        if (obj instanceof Reference) {
            if (null != strong_obj) {
                return strong_obj.equals(((Reference<?>) obj).get());
            } else {
                return null == ((Reference<?>) obj).get();
            }
        } else {
            if (null != strong_obj) {
                return strong_obj.equals(obj);
            } else {
                return null == obj;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        Object strong_obj = get();
        if (null != strong_obj) {
            return strong_obj.hashCode();
        } else {
            return 0;
        }
    }
}

