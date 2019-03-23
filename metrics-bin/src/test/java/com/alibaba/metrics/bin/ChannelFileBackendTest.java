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
package com.alibaba.metrics.bin;

import com.alibaba.metrics.reporter.bin.ChannelFileBackend;
import com.alibaba.metrics.utils.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ChannelFileBackendTest {

	@Test
	public void mainTest() throws IOException{
		try {
			testBigBytesWrite();
			testLittleBytesWrite();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clear();
		}
	}

	@Test
	public void testClean() throws IOException {
		ChannelFileBackend channelFileBackend = new ChannelFileBackend("src/test/resources/test", true);
		channelFileBackend.close();
	}

	private void testBigBytesWrite() throws IOException{

		int blockSize = 20000;
		String path = FileUtil.getBasePath("logs/metrics/testFile.txt");
		byte[] b = new byte[blockSize];

		Random random = new Random();
		random.nextBytes(b);
		ChannelFileBackend backend = new ChannelFileBackend(path, false);
		backend.write(50, b, 0, blockSize);
		backend.close();

		ChannelFileBackend backend1 = new ChannelFileBackend(path, false);
		backend1.init();
		byte[] c = new byte[blockSize + 3];
		backend1.read(50, c);

		for(int i = 0; i < blockSize; i++){
			assert b[i] == c[i];
		}

	}

	private void testLittleBytesWrite() throws IOException{

		int blockNum = 20;
		int blockSize = 1000;

		String path = FileUtil.getBasePath("logs/metrics/testFile1.txt");

		byte[] b = new byte[blockSize];

		Random random = new Random();
		random.nextBytes(b);

		ChannelFileBackend backend = new ChannelFileBackend(path, false);
		for(int i = 0;i < blockNum;i++){
			backend.write(50 + i * blockSize, b, 0, blockSize);
		}
		backend.close();

		ChannelFileBackend backend1 = new ChannelFileBackend(path, false);
		byte[] c = new byte[blockSize + 3];

		for(int i = 0;i < blockNum;i++){
			backend1.read(50 + i * blockSize, c);
			for(int j = 0; j < blockSize; j++){
				assert b[i] == c[i];
			}

			c = new byte[blockSize + 3];
		}
	}

	private void clear(){

		String path = FileUtil.getBasePath("logs/metrics/testFile.txt");
		String path1 = FileUtil.getBasePath("logs/metrics/testFile1.txt");

		new File(path).delete();
		new File(path1).delete();
	}
}
