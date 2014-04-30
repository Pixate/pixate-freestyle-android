package com.pixate.freestyle.util;

/**
 * A generic callback that can be used when loading data asynchronously. For
 * example, one can use this callback when downloading a remote Bitmap.
 * 
 * @author shalom
 */
public interface LoadingCallback<T> {

    /**
     * Called when a T was loaded.
     * 
     * @param T
     */
    public void onLoaded(T data);

    /**
     * Called on an error.
     * 
     * @param error
     */
    public void onError(Exception error);
}
