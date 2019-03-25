/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.metrics.reporter.bin;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileBackend extends AbstractBackend{

    protected final boolean readOnly;
    protected final File file;

    protected final static int MAX_FIELD_LENGTH = 4096;

	protected AbstractFileBackend(String path, boolean readOnly) {
		super(path);
        this.readOnly = readOnly;
        this.file = new File(path);
	}

    public long getLength() {
        return file.length();
    }

    public abstract void init() throws IOException;

    public abstract void close() throws IOException;

    public abstract void setLength(long length) throws IOException;

}
