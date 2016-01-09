/******************************************************************************
 * Copyright (c) 2011 GitHub Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:
 * Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package com.alorma.github.utils;

import java.io.UnsupportedEncodingException;

/**
 * Encoding utilities
 */
public abstract class EncodingUtils {

    /**
     * Decode base64 encoded string
     *
     * @return byte array
     */
    public static final byte[] fromBase64(final String content) {
        return Base64.decode(content);
    }

    /**
     * Base64 encode given byte array
     *
     * @return byte array
     */
    public static final String toBase64(final byte[] content) {
        return Base64.encodeBytes(content);
    }

    /**
     * Base64 encode given byte array
     *
     * @return byte array
     */
    public static final String toBase64(final String content) {
        byte[] bytes;
        try {
            bytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = content.getBytes();
        }
        return toBase64(bytes);
    }
}