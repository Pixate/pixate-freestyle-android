/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * A synchronized object pool implementation.
 * 
 * @author Shalom Gibly
 */
public class ObjectPool<T, E> {

    private static final int DEFAULT_POOL_SIZE = 10;

    /**
     * Factory class for creating pool instance.
     * 
     * @author Shalom Gibly
     */
    public interface ObjectPoolFactory<T, E> {
        /**
         * Create an instance that will be returned by the pool.
         * 
         * @return T
         */
        public T createInstance();

        /**
         * Resets the instance as it's being inserted back to the pool.
         * 
         * @param object
         */
        public void resetInstance(T object);

        /**
         * Initialize the instance with an initialization object.
         * 
         * @param toInitialize
         * @param initializerObject
         */
        public void initializeInstance(T toInitialize, E initializerObject);
    }

    /**
     * An implementation for {@link Paint} instances pool.
     */
    public static ObjectPool<Paint, Paint> paintPool = new ObjectPool<Paint, Paint>(
            new ObjectPoolFactory<Paint, Paint>() {

                public Paint createInstance() {
                    return new Paint(Paint.ANTI_ALIAS_FLAG);
                }

                public void resetInstance(Paint paint) {
                    paint.reset();
                    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                }

                public void initializeInstance(Paint toInitialize, Paint initializerObject) {
                    toInitialize.set(initializerObject);
                }
            }, DEFAULT_POOL_SIZE);
    /**
     * An implementation for {@link Path} instances pool.
     */
    public static ObjectPool<Path, Path> pathPool = new ObjectPool<Path, Path>(
            new ObjectPoolFactory<Path, Path>() {

                public Path createInstance() {
                    return new Path();
                }

                public void resetInstance(Path path) {
                    path.reset();
                }

                public void initializeInstance(Path toInitialize, Path initializerObject) {
                    toInitialize.set(initializerObject);
                }
            }, DEFAULT_POOL_SIZE);

    private final List<T> available;
    private final ObjectPoolFactory<T, E> factory;
    private int limit;

    /**
     * Constructs a new object pool
     * 
     * @param factory
     * @param limit
     */
    public ObjectPool(ObjectPoolFactory<T, E> factory, int limit) {
        this.limit = limit;
        this.factory = factory;
        available = new ArrayList<T>(limit);
    }

    /**
     * Check out and return an instance. The instance will be taken out of the
     * pooled existing instances. In case none exist, a new instance will be
     * created and returned.
     * 
     * @return A new instance (or a re-used one)
     */
    public T checkOut() {
        T object = null;
        synchronized (available) {
            if (available.isEmpty()) {
                object = factory.createInstance();
            } else {
                object = available.remove(available.size() - 1);
            }
            return object;
        }
    }

    /**
     * Check out and return an instance. The instance will be taken out of the
     * pooled existing instances and initialized with the initialization object
     * before being returned. In case none exist, a new instance will be
     * created, initialized, and returned.
     * 
     * @return A new instance (or a re-used one)
     */
    public T checkOut(E initilizationObject) {
        T checkedOut = checkOut();
        if (initilizationObject != null) {
            factory.initializeInstance(checkedOut, initilizationObject);
        }
        return checkedOut;
    }

    /**
     * Check in an instance into the pool.
     * 
     * @param object
     */
    public void checkIn(T object) {
        if (object == null) {
            return;
        }
        synchronized (available) {
            if (available.size() < limit) {
                factory.resetInstance(object);
                available.add(object);
            }
        }
    }
}
