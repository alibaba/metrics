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
package com.alibaba.metrics.reporter.bin.zigzag.utils;

import com.alibaba.metrics.reporter.bin.zigzag.filters.IntFilter;
import com.alibaba.metrics.reporter.bin.zigzag.io.IntOutputStream;

import java.nio.IntBuffer;

// This class was generated automatically, don't modify.
public class IntBitPackingUnpacks
{

    public static void unpack1(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 31 & m);
        buf[ 1] = filter.filterInt(n >>> 30 & m);
        buf[ 2] = filter.filterInt(n >>> 29 & m);
        buf[ 3] = filter.filterInt(n >>> 28 & m);
        buf[ 4] = filter.filterInt(n >>> 27 & m);
        buf[ 5] = filter.filterInt(n >>> 26 & m);
        buf[ 6] = filter.filterInt(n >>> 25 & m);
        buf[ 7] = filter.filterInt(n >>> 24 & m);
        buf[ 8] = filter.filterInt(n >>> 23 & m);
        buf[ 9] = filter.filterInt(n >>> 22 & m);
        buf[10] = filter.filterInt(n >>> 21 & m);
        buf[11] = filter.filterInt(n >>> 20 & m);
        buf[12] = filter.filterInt(n >>> 19 & m);
        buf[13] = filter.filterInt(n >>> 18 & m);
        buf[14] = filter.filterInt(n >>> 17 & m);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>> 15 & m);
        buf[17] = filter.filterInt(n >>> 14 & m);
        buf[18] = filter.filterInt(n >>> 13 & m);
        buf[19] = filter.filterInt(n >>> 12 & m);
        buf[20] = filter.filterInt(n >>> 11 & m);
        buf[21] = filter.filterInt(n >>> 10 & m);
        buf[22] = filter.filterInt(n >>>  9 & m);
        buf[23] = filter.filterInt(n >>>  8 & m);
        buf[24] = filter.filterInt(n >>>  7 & m);
        buf[25] = filter.filterInt(n >>>  6 & m);
        buf[26] = filter.filterInt(n >>>  5 & m);
        buf[27] = filter.filterInt(n >>>  4 & m);
        buf[28] = filter.filterInt(n >>>  3 & m);
        buf[29] = filter.filterInt(n >>>  2 & m);
        buf[30] = filter.filterInt(n >>>  1 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack2(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 30 & m);
        buf[ 1] = filter.filterInt(n >>> 28 & m);
        buf[ 2] = filter.filterInt(n >>> 26 & m);
        buf[ 3] = filter.filterInt(n >>> 24 & m);
        buf[ 4] = filter.filterInt(n >>> 22 & m);
        buf[ 5] = filter.filterInt(n >>> 20 & m);
        buf[ 6] = filter.filterInt(n >>> 18 & m);
        buf[ 7] = filter.filterInt(n >>> 16 & m);
        buf[ 8] = filter.filterInt(n >>> 14 & m);
        buf[ 9] = filter.filterInt(n >>> 12 & m);
        buf[10] = filter.filterInt(n >>> 10 & m);
        buf[11] = filter.filterInt(n >>>  8 & m);
        buf[12] = filter.filterInt(n >>>  6 & m);
        buf[13] = filter.filterInt(n >>>  4 & m);
        buf[14] = filter.filterInt(n >>>  2 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 30 & m);
        buf[17] = filter.filterInt(n >>> 28 & m);
        buf[18] = filter.filterInt(n >>> 26 & m);
        buf[19] = filter.filterInt(n >>> 24 & m);
        buf[20] = filter.filterInt(n >>> 22 & m);
        buf[21] = filter.filterInt(n >>> 20 & m);
        buf[22] = filter.filterInt(n >>> 18 & m);
        buf[23] = filter.filterInt(n >>> 16 & m);
        buf[24] = filter.filterInt(n >>> 14 & m);
        buf[25] = filter.filterInt(n >>> 12 & m);
        buf[26] = filter.filterInt(n >>> 10 & m);
        buf[27] = filter.filterInt(n >>>  8 & m);
        buf[28] = filter.filterInt(n >>>  6 & m);
        buf[29] = filter.filterInt(n >>>  4 & m);
        buf[30] = filter.filterInt(n >>>  2 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack3(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 29 & m);
        buf[ 1] = filter.filterInt(n >>> 26 & m);
        buf[ 2] = filter.filterInt(n >>> 23 & m);
        buf[ 3] = filter.filterInt(n >>> 20 & m);
        buf[ 4] = filter.filterInt(n >>> 17 & m);
        buf[ 5] = filter.filterInt(n >>> 14 & m);
        buf[ 6] = filter.filterInt(n >>> 11 & m);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        buf[ 8] = filter.filterInt(n >>>  5 & m);
        buf[ 9] = filter.filterInt(n >>>  2 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 31);
        buf[11] = filter.filterInt(n >>> 28 & m);
        buf[12] = filter.filterInt(n >>> 25 & m);
        buf[13] = filter.filterInt(n >>> 22 & m);
        buf[14] = filter.filterInt(n >>> 19 & m);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>> 13 & m);
        buf[17] = filter.filterInt(n >>> 10 & m);
        buf[18] = filter.filterInt(n >>>  7 & m);
        buf[19] = filter.filterInt(n >>>  4 & m);
        buf[20] = filter.filterInt(n >>>  1 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 30);
        buf[22] = filter.filterInt(n >>> 27 & m);
        buf[23] = filter.filterInt(n >>> 24 & m);
        buf[24] = filter.filterInt(n >>> 21 & m);
        buf[25] = filter.filterInt(n >>> 18 & m);
        buf[26] = filter.filterInt(n >>> 15 & m);
        buf[27] = filter.filterInt(n >>> 12 & m);
        buf[28] = filter.filterInt(n >>>  9 & m);
        buf[29] = filter.filterInt(n >>>  6 & m);
        buf[30] = filter.filterInt(n >>>  3 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack4(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xf;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 28 & m);
        buf[ 1] = filter.filterInt(n >>> 24 & m);
        buf[ 2] = filter.filterInt(n >>> 20 & m);
        buf[ 3] = filter.filterInt(n >>> 16 & m);
        buf[ 4] = filter.filterInt(n >>> 12 & m);
        buf[ 5] = filter.filterInt(n >>>  8 & m);
        buf[ 6] = filter.filterInt(n >>>  4 & m);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>> 28 & m);
        buf[ 9] = filter.filterInt(n >>> 24 & m);
        buf[10] = filter.filterInt(n >>> 20 & m);
        buf[11] = filter.filterInt(n >>> 16 & m);
        buf[12] = filter.filterInt(n >>> 12 & m);
        buf[13] = filter.filterInt(n >>>  8 & m);
        buf[14] = filter.filterInt(n >>>  4 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 28 & m);
        buf[17] = filter.filterInt(n >>> 24 & m);
        buf[18] = filter.filterInt(n >>> 20 & m);
        buf[19] = filter.filterInt(n >>> 16 & m);
        buf[20] = filter.filterInt(n >>> 12 & m);
        buf[21] = filter.filterInt(n >>>  8 & m);
        buf[22] = filter.filterInt(n >>>  4 & m);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>> 28 & m);
        buf[25] = filter.filterInt(n >>> 24 & m);
        buf[26] = filter.filterInt(n >>> 20 & m);
        buf[27] = filter.filterInt(n >>> 16 & m);
        buf[28] = filter.filterInt(n >>> 12 & m);
        buf[29] = filter.filterInt(n >>>  8 & m);
        buf[30] = filter.filterInt(n >>>  4 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack5(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1f;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 27 & m);
        buf[ 1] = filter.filterInt(n >>> 22 & m);
        buf[ 2] = filter.filterInt(n >>> 17 & m);
        buf[ 3] = filter.filterInt(n >>> 12 & m);
        buf[ 4] = filter.filterInt(n >>>  7 & m);
        buf[ 5] = filter.filterInt(n >>>  2 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 29);
        buf[ 7] = filter.filterInt(n >>> 24 & m);
        buf[ 8] = filter.filterInt(n >>> 19 & m);
        buf[ 9] = filter.filterInt(n >>> 14 & m);
        buf[10] = filter.filterInt(n >>>  9 & m);
        buf[11] = filter.filterInt(n >>>  4 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 31);
        buf[13] = filter.filterInt(n >>> 26 & m);
        buf[14] = filter.filterInt(n >>> 21 & m);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>> 11 & m);
        buf[17] = filter.filterInt(n >>>  6 & m);
        buf[18] = filter.filterInt(n >>>  1 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 28);
        buf[20] = filter.filterInt(n >>> 23 & m);
        buf[21] = filter.filterInt(n >>> 18 & m);
        buf[22] = filter.filterInt(n >>> 13 & m);
        buf[23] = filter.filterInt(n >>>  8 & m);
        buf[24] = filter.filterInt(n >>>  3 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 30);
        buf[26] = filter.filterInt(n >>> 25 & m);
        buf[27] = filter.filterInt(n >>> 20 & m);
        buf[28] = filter.filterInt(n >>> 15 & m);
        buf[29] = filter.filterInt(n >>> 10 & m);
        buf[30] = filter.filterInt(n >>>  5 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack6(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3f;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 26 & m);
        buf[ 1] = filter.filterInt(n >>> 20 & m);
        buf[ 2] = filter.filterInt(n >>> 14 & m);
        buf[ 3] = filter.filterInt(n >>>  8 & m);
        buf[ 4] = filter.filterInt(n >>>  2 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 28);
        buf[ 6] = filter.filterInt(n >>> 22 & m);
        buf[ 7] = filter.filterInt(n >>> 16 & m);
        buf[ 8] = filter.filterInt(n >>> 10 & m);
        buf[ 9] = filter.filterInt(n >>>  4 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 30);
        buf[11] = filter.filterInt(n >>> 24 & m);
        buf[12] = filter.filterInt(n >>> 18 & m);
        buf[13] = filter.filterInt(n >>> 12 & m);
        buf[14] = filter.filterInt(n >>>  6 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 26 & m);
        buf[17] = filter.filterInt(n >>> 20 & m);
        buf[18] = filter.filterInt(n >>> 14 & m);
        buf[19] = filter.filterInt(n >>>  8 & m);
        buf[20] = filter.filterInt(n >>>  2 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 28);
        buf[22] = filter.filterInt(n >>> 22 & m);
        buf[23] = filter.filterInt(n >>> 16 & m);
        buf[24] = filter.filterInt(n >>> 10 & m);
        buf[25] = filter.filterInt(n >>>  4 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 30);
        buf[27] = filter.filterInt(n >>> 24 & m);
        buf[28] = filter.filterInt(n >>> 18 & m);
        buf[29] = filter.filterInt(n >>> 12 & m);
        buf[30] = filter.filterInt(n >>>  6 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack7(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7f;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 25 & m);
        buf[ 1] = filter.filterInt(n >>> 18 & m);
        buf[ 2] = filter.filterInt(n >>> 11 & m);
        buf[ 3] = filter.filterInt(n >>>  4 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 29);
        buf[ 5] = filter.filterInt(n >>> 22 & m);
        buf[ 6] = filter.filterInt(n >>> 15 & m);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        buf[ 8] = filter.filterInt(n >>>  1 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 26);
        buf[10] = filter.filterInt(n >>> 19 & m);
        buf[11] = filter.filterInt(n >>> 12 & m);
        buf[12] = filter.filterInt(n >>>  5 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 30);
        buf[14] = filter.filterInt(n >>> 23 & m);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>>  9 & m);
        buf[17] = filter.filterInt(n >>>  2 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 27);
        buf[19] = filter.filterInt(n >>> 20 & m);
        buf[20] = filter.filterInt(n >>> 13 & m);
        buf[21] = filter.filterInt(n >>>  6 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 31);
        buf[23] = filter.filterInt(n >>> 24 & m);
        buf[24] = filter.filterInt(n >>> 17 & m);
        buf[25] = filter.filterInt(n >>> 10 & m);
        buf[26] = filter.filterInt(n >>>  3 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 28);
        buf[28] = filter.filterInt(n >>> 21 & m);
        buf[29] = filter.filterInt(n >>> 14 & m);
        buf[30] = filter.filterInt(n >>>  7 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack8(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 24 & m);
        buf[ 1] = filter.filterInt(n >>> 16 & m);
        buf[ 2] = filter.filterInt(n >>>  8 & m);
        buf[ 3] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 4] = filter.filterInt(n >>> 24 & m);
        buf[ 5] = filter.filterInt(n >>> 16 & m);
        buf[ 6] = filter.filterInt(n >>>  8 & m);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>> 24 & m);
        buf[ 9] = filter.filterInt(n >>> 16 & m);
        buf[10] = filter.filterInt(n >>>  8 & m);
        buf[11] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[12] = filter.filterInt(n >>> 24 & m);
        buf[13] = filter.filterInt(n >>> 16 & m);
        buf[14] = filter.filterInt(n >>>  8 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 24 & m);
        buf[17] = filter.filterInt(n >>> 16 & m);
        buf[18] = filter.filterInt(n >>>  8 & m);
        buf[19] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[20] = filter.filterInt(n >>> 24 & m);
        buf[21] = filter.filterInt(n >>> 16 & m);
        buf[22] = filter.filterInt(n >>>  8 & m);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>> 24 & m);
        buf[25] = filter.filterInt(n >>> 16 & m);
        buf[26] = filter.filterInt(n >>>  8 & m);
        buf[27] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[28] = filter.filterInt(n >>> 24 & m);
        buf[29] = filter.filterInt(n >>> 16 & m);
        buf[30] = filter.filterInt(n >>>  8 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack9(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 23 & m);
        buf[ 1] = filter.filterInt(n >>> 14 & m);
        buf[ 2] = filter.filterInt(n >>>  5 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 28);
        buf[ 4] = filter.filterInt(n >>> 19 & m);
        buf[ 5] = filter.filterInt(n >>> 10 & m);
        buf[ 6] = filter.filterInt(n >>>  1 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        buf[ 8] = filter.filterInt(n >>> 15 & m);
        buf[ 9] = filter.filterInt(n >>>  6 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 29);
        buf[11] = filter.filterInt(n >>> 20 & m);
        buf[12] = filter.filterInt(n >>> 11 & m);
        buf[13] = filter.filterInt(n >>>  2 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 25);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>>  7 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 30);
        buf[18] = filter.filterInt(n >>> 21 & m);
        buf[19] = filter.filterInt(n >>> 12 & m);
        buf[20] = filter.filterInt(n >>>  3 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 26);
        buf[22] = filter.filterInt(n >>> 17 & m);
        buf[23] = filter.filterInt(n >>>  8 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 31);
        buf[25] = filter.filterInt(n >>> 22 & m);
        buf[26] = filter.filterInt(n >>> 13 & m);
        buf[27] = filter.filterInt(n >>>  4 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 27);
        buf[29] = filter.filterInt(n >>> 18 & m);
        buf[30] = filter.filterInt(n >>>  9 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack10(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 22 & m);
        buf[ 1] = filter.filterInt(n >>> 12 & m);
        buf[ 2] = filter.filterInt(n >>>  2 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 24);
        buf[ 4] = filter.filterInt(n >>> 14 & m);
        buf[ 5] = filter.filterInt(n >>>  4 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 26);
        buf[ 7] = filter.filterInt(n >>> 16 & m);
        buf[ 8] = filter.filterInt(n >>>  6 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 28);
        buf[10] = filter.filterInt(n >>> 18 & m);
        buf[11] = filter.filterInt(n >>>  8 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 30);
        buf[13] = filter.filterInt(n >>> 20 & m);
        buf[14] = filter.filterInt(n >>> 10 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 22 & m);
        buf[17] = filter.filterInt(n >>> 12 & m);
        buf[18] = filter.filterInt(n >>>  2 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 24);
        buf[20] = filter.filterInt(n >>> 14 & m);
        buf[21] = filter.filterInt(n >>>  4 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 26);
        buf[23] = filter.filterInt(n >>> 16 & m);
        buf[24] = filter.filterInt(n >>>  6 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 28);
        buf[26] = filter.filterInt(n >>> 18 & m);
        buf[27] = filter.filterInt(n >>>  8 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 30);
        buf[29] = filter.filterInt(n >>> 20 & m);
        buf[30] = filter.filterInt(n >>> 10 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack11(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 21 & m);
        buf[ 1] = filter.filterInt(n >>> 10 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 31);
        buf[ 3] = filter.filterInt(n >>> 20 & m);
        buf[ 4] = filter.filterInt(n >>>  9 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 30);
        buf[ 6] = filter.filterInt(n >>> 19 & m);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 29);
        buf[ 9] = filter.filterInt(n >>> 18 & m);
        buf[10] = filter.filterInt(n >>>  7 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 28);
        buf[12] = filter.filterInt(n >>> 17 & m);
        buf[13] = filter.filterInt(n >>>  6 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 27);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>>  5 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 26);
        buf[18] = filter.filterInt(n >>> 15 & m);
        buf[19] = filter.filterInt(n >>>  4 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 25);
        buf[21] = filter.filterInt(n >>> 14 & m);
        buf[22] = filter.filterInt(n >>>  3 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        buf[24] = filter.filterInt(n >>> 13 & m);
        buf[25] = filter.filterInt(n >>>  2 & m);
        c = n <<  9 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 23);
        buf[27] = filter.filterInt(n >>> 12 & m);
        buf[28] = filter.filterInt(n >>>  1 & m);
        c = n << 10 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 22);
        buf[30] = filter.filterInt(n >>> 11 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack12(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 20 & m);
        buf[ 1] = filter.filterInt(n >>>  8 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 28);
        buf[ 3] = filter.filterInt(n >>> 16 & m);
        buf[ 4] = filter.filterInt(n >>>  4 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 24);
        buf[ 6] = filter.filterInt(n >>> 12 & m);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>> 20 & m);
        buf[ 9] = filter.filterInt(n >>>  8 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 28);
        buf[11] = filter.filterInt(n >>> 16 & m);
        buf[12] = filter.filterInt(n >>>  4 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 24);
        buf[14] = filter.filterInt(n >>> 12 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 20 & m);
        buf[17] = filter.filterInt(n >>>  8 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 28);
        buf[19] = filter.filterInt(n >>> 16 & m);
        buf[20] = filter.filterInt(n >>>  4 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 24);
        buf[22] = filter.filterInt(n >>> 12 & m);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>> 20 & m);
        buf[25] = filter.filterInt(n >>>  8 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 28);
        buf[27] = filter.filterInt(n >>> 16 & m);
        buf[28] = filter.filterInt(n >>>  4 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 24);
        buf[30] = filter.filterInt(n >>> 12 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack13(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 19 & m);
        buf[ 1] = filter.filterInt(n >>>  6 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 25);
        buf[ 3] = filter.filterInt(n >>> 12 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 31);
        buf[ 5] = filter.filterInt(n >>> 18 & m);
        buf[ 6] = filter.filterInt(n >>>  5 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        buf[ 8] = filter.filterInt(n >>> 11 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 30);
        buf[10] = filter.filterInt(n >>> 17 & m);
        buf[11] = filter.filterInt(n >>>  4 & m);
        c = n <<  9 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 23);
        buf[13] = filter.filterInt(n >>> 10 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 29);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>>  3 & m);
        c = n << 10 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 22);
        buf[18] = filter.filterInt(n >>>  9 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 28);
        buf[20] = filter.filterInt(n >>> 15 & m);
        buf[21] = filter.filterInt(n >>>  2 & m);
        c = n << 11 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 21);
        buf[23] = filter.filterInt(n >>>  8 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 27);
        buf[25] = filter.filterInt(n >>> 14 & m);
        buf[26] = filter.filterInt(n >>>  1 & m);
        c = n << 12 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 20);
        buf[28] = filter.filterInt(n >>>  7 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 26);
        buf[30] = filter.filterInt(n >>> 13 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack14(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 18 & m);
        buf[ 1] = filter.filterInt(n >>>  4 & m);
        c = n << 10 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 22);
        buf[ 3] = filter.filterInt(n >>>  8 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 26);
        buf[ 5] = filter.filterInt(n >>> 12 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 30);
        buf[ 7] = filter.filterInt(n >>> 16 & m);
        buf[ 8] = filter.filterInt(n >>>  2 & m);
        c = n << 12 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 20);
        buf[10] = filter.filterInt(n >>>  6 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 24);
        buf[12] = filter.filterInt(n >>> 10 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 28);
        buf[14] = filter.filterInt(n >>> 14 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 18 & m);
        buf[17] = filter.filterInt(n >>>  4 & m);
        c = n << 10 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 22);
        buf[19] = filter.filterInt(n >>>  8 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 26);
        buf[21] = filter.filterInt(n >>> 12 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 30);
        buf[23] = filter.filterInt(n >>> 16 & m);
        buf[24] = filter.filterInt(n >>>  2 & m);
        c = n << 12 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 20);
        buf[26] = filter.filterInt(n >>>  6 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 24);
        buf[28] = filter.filterInt(n >>> 10 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 28);
        buf[30] = filter.filterInt(n >>> 14 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack15(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 17 & m);
        buf[ 1] = filter.filterInt(n >>>  2 & m);
        c = n << 13 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 19);
        buf[ 3] = filter.filterInt(n >>>  4 & m);
        c = n << 11 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 21);
        buf[ 5] = filter.filterInt(n >>>  6 & m);
        c = n <<  9 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 23);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 25);
        buf[ 9] = filter.filterInt(n >>> 10 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 27);
        buf[11] = filter.filterInt(n >>> 12 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 29);
        buf[13] = filter.filterInt(n >>> 14 & m);
        c = n <<  1 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 31);
        buf[15] = filter.filterInt(n >>> 16 & m);
        buf[16] = filter.filterInt(n >>>  1 & m);
        c = n << 14 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 18);
        buf[18] = filter.filterInt(n >>>  3 & m);
        c = n << 12 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 20);
        buf[20] = filter.filterInt(n >>>  5 & m);
        c = n << 10 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 22);
        buf[22] = filter.filterInt(n >>>  7 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        buf[24] = filter.filterInt(n >>>  9 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 26);
        buf[26] = filter.filterInt(n >>> 11 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 28);
        buf[28] = filter.filterInt(n >>> 13 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 30);
        buf[30] = filter.filterInt(n >>> 15 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack16(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 16 & m);
        buf[ 1] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 2] = filter.filterInt(n >>> 16 & m);
        buf[ 3] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 4] = filter.filterInt(n >>> 16 & m);
        buf[ 5] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 6] = filter.filterInt(n >>> 16 & m);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>> 16 & m);
        buf[ 9] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[10] = filter.filterInt(n >>> 16 & m);
        buf[11] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[12] = filter.filterInt(n >>> 16 & m);
        buf[13] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[14] = filter.filterInt(n >>> 16 & m);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 16 & m);
        buf[17] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[18] = filter.filterInt(n >>> 16 & m);
        buf[19] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[20] = filter.filterInt(n >>> 16 & m);
        buf[21] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[22] = filter.filterInt(n >>> 16 & m);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>> 16 & m);
        buf[25] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[26] = filter.filterInt(n >>> 16 & m);
        buf[27] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[28] = filter.filterInt(n >>> 16 & m);
        buf[29] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[30] = filter.filterInt(n >>> 16 & m);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack17(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 15 & m);
        c = n <<  2 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 30);
        buf[ 2] = filter.filterInt(n >>> 13 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 28);
        buf[ 4] = filter.filterInt(n >>> 11 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 26);
        buf[ 6] = filter.filterInt(n >>>  9 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        buf[ 8] = filter.filterInt(n >>>  7 & m);
        c = n << 10 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 22);
        buf[10] = filter.filterInt(n >>>  5 & m);
        c = n << 12 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 20);
        buf[12] = filter.filterInt(n >>>  3 & m);
        c = n << 14 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 18);
        buf[14] = filter.filterInt(n >>>  1 & m);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n <<  1 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 31);
        buf[17] = filter.filterInt(n >>> 14 & m);
        c = n <<  3 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 29);
        buf[19] = filter.filterInt(n >>> 12 & m);
        c = n <<  5 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 27);
        buf[21] = filter.filterInt(n >>> 10 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 25);
        buf[23] = filter.filterInt(n >>>  8 & m);
        c = n <<  9 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 23);
        buf[25] = filter.filterInt(n >>>  6 & m);
        c = n << 11 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 21);
        buf[27] = filter.filterInt(n >>>  4 & m);
        c = n << 13 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 19);
        buf[29] = filter.filterInt(n >>>  2 & m);
        c = n << 15 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 17);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack18(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 14 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 28);
        buf[ 2] = filter.filterInt(n >>> 10 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 24);
        buf[ 4] = filter.filterInt(n >>>  6 & m);
        c = n << 12 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 20);
        buf[ 6] = filter.filterInt(n >>>  2 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 16);
        c = n <<  2 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 30);
        buf[ 9] = filter.filterInt(n >>> 12 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 26);
        buf[11] = filter.filterInt(n >>>  8 & m);
        c = n << 10 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 22);
        buf[13] = filter.filterInt(n >>>  4 & m);
        c = n << 14 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 18);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 14 & m);
        c = n <<  4 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 28);
        buf[18] = filter.filterInt(n >>> 10 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 24);
        buf[20] = filter.filterInt(n >>>  6 & m);
        c = n << 12 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 20);
        buf[22] = filter.filterInt(n >>>  2 & m);
        c = n << 16 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 16);
        c = n <<  2 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 30);
        buf[25] = filter.filterInt(n >>> 12 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 26);
        buf[27] = filter.filterInt(n >>>  8 & m);
        c = n << 10 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 22);
        buf[29] = filter.filterInt(n >>>  4 & m);
        c = n << 14 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 18);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack19(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 13 & m);
        c = n <<  6 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 26);
        buf[ 2] = filter.filterInt(n >>>  7 & m);
        c = n << 12 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 20);
        buf[ 4] = filter.filterInt(n >>>  1 & m);
        c = n << 18 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 14);
        c = n <<  5 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 27);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        c = n << 11 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 21);
        buf[ 9] = filter.filterInt(n >>>  2 & m);
        c = n << 17 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 15);
        c = n <<  4 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 28);
        buf[12] = filter.filterInt(n >>>  9 & m);
        c = n << 10 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 22);
        buf[14] = filter.filterInt(n >>>  3 & m);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n <<  3 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 29);
        buf[17] = filter.filterInt(n >>> 10 & m);
        c = n <<  9 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 23);
        buf[19] = filter.filterInt(n >>>  4 & m);
        c = n << 15 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 17);
        c = n <<  2 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 30);
        buf[22] = filter.filterInt(n >>> 11 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        buf[24] = filter.filterInt(n >>>  5 & m);
        c = n << 14 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 18);
        c = n <<  1 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 31);
        buf[27] = filter.filterInt(n >>> 12 & m);
        c = n <<  7 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 25);
        buf[29] = filter.filterInt(n >>>  6 & m);
        c = n << 13 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 19);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack20(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 12 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 24);
        buf[ 2] = filter.filterInt(n >>>  4 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 16);
        c = n <<  4 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 28);
        buf[ 5] = filter.filterInt(n >>>  8 & m);
        c = n << 12 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 20);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>> 12 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 24);
        buf[10] = filter.filterInt(n >>>  4 & m);
        c = n << 16 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 16);
        c = n <<  4 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 28);
        buf[13] = filter.filterInt(n >>>  8 & m);
        c = n << 12 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 20);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 12 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 24);
        buf[18] = filter.filterInt(n >>>  4 & m);
        c = n << 16 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 16);
        c = n <<  4 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 28);
        buf[21] = filter.filterInt(n >>>  8 & m);
        c = n << 12 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 20);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>> 12 & m);
        c = n <<  8 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 24);
        buf[26] = filter.filterInt(n >>>  4 & m);
        c = n << 16 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 16);
        c = n <<  4 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 28);
        buf[29] = filter.filterInt(n >>>  8 & m);
        c = n << 12 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 20);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack21(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 11 & m);
        c = n << 10 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 22);
        buf[ 2] = filter.filterInt(n >>>  1 & m);
        c = n << 20 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 12);
        c = n <<  9 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 23);
        buf[ 5] = filter.filterInt(n >>>  2 & m);
        c = n << 19 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 13);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        buf[ 8] = filter.filterInt(n >>>  3 & m);
        c = n << 18 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 14);
        c = n <<  7 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 25);
        buf[11] = filter.filterInt(n >>>  4 & m);
        c = n << 17 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 15);
        c = n <<  6 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 26);
        buf[14] = filter.filterInt(n >>>  5 & m);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n <<  5 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 27);
        buf[17] = filter.filterInt(n >>>  6 & m);
        c = n << 15 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 17);
        c = n <<  4 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 28);
        buf[20] = filter.filterInt(n >>>  7 & m);
        c = n << 14 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 18);
        c = n <<  3 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 29);
        buf[23] = filter.filterInt(n >>>  8 & m);
        c = n << 13 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 19);
        c = n <<  2 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 30);
        buf[26] = filter.filterInt(n >>>  9 & m);
        c = n << 12 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 20);
        c = n <<  1 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 31);
        buf[29] = filter.filterInt(n >>> 10 & m);
        c = n << 11 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 21);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack22(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>> 10 & m);
        c = n << 12 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 20);
        c = n <<  2 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 30);
        buf[ 3] = filter.filterInt(n >>>  8 & m);
        c = n << 14 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 18);
        c = n <<  4 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 28);
        buf[ 6] = filter.filterInt(n >>>  6 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 16);
        c = n <<  6 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 26);
        buf[ 9] = filter.filterInt(n >>>  4 & m);
        c = n << 18 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 14);
        c = n <<  8 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 24);
        buf[12] = filter.filterInt(n >>>  2 & m);
        c = n << 20 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 12);
        c = n << 10 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 22);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>> 10 & m);
        c = n << 12 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 20);
        c = n <<  2 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 30);
        buf[19] = filter.filterInt(n >>>  8 & m);
        c = n << 14 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 18);
        c = n <<  4 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 28);
        buf[22] = filter.filterInt(n >>>  6 & m);
        c = n << 16 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 16);
        c = n <<  6 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 26);
        buf[25] = filter.filterInt(n >>>  4 & m);
        c = n << 18 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 14);
        c = n <<  8 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 24);
        buf[28] = filter.filterInt(n >>>  2 & m);
        c = n << 20 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 12);
        c = n << 10 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 22);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack23(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  9 & m);
        c = n << 14 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 18);
        c = n <<  5 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 27);
        buf[ 3] = filter.filterInt(n >>>  4 & m);
        c = n << 19 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 13);
        c = n << 10 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 22);
        c = n <<  1 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 31);
        buf[ 7] = filter.filterInt(n >>>  8 & m);
        c = n << 15 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 17);
        c = n <<  6 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 26);
        buf[10] = filter.filterInt(n >>>  3 & m);
        c = n << 20 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 12);
        c = n << 11 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 21);
        c = n <<  2 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 30);
        buf[14] = filter.filterInt(n >>>  7 & m);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n <<  7 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 25);
        buf[17] = filter.filterInt(n >>>  2 & m);
        c = n << 21 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 11);
        c = n << 12 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 20);
        c = n <<  3 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 29);
        buf[21] = filter.filterInt(n >>>  6 & m);
        c = n << 17 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 15);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        buf[24] = filter.filterInt(n >>>  1 & m);
        c = n << 22 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 10);
        c = n << 13 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 19);
        c = n <<  4 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 28);
        buf[28] = filter.filterInt(n >>>  5 & m);
        c = n << 18 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 14);
        c = n <<  9 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 23);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack24(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 24);
        buf[ 3] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 4] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 24);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 24);
        buf[11] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[12] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 24);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 24);
        buf[19] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[20] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 24);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 24);
        buf[27] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[28] = filter.filterInt(n >>>  8 & m);
        c = n << 16 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 16);
        c = n <<  8 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 24);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack25(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  7 & m);
        c = n << 18 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 14);
        c = n << 11 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 21);
        c = n <<  4 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 28);
        buf[ 4] = filter.filterInt(n >>>  3 & m);
        c = n << 22 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 10);
        c = n << 15 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 17);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        c = n <<  1 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 31);
        buf[ 9] = filter.filterInt(n >>>  6 & m);
        c = n << 19 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 13);
        c = n << 12 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 20);
        c = n <<  5 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 27);
        buf[13] = filter.filterInt(n >>>  2 & m);
        c = n << 23 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>>  9);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n <<  9 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 23);
        c = n <<  2 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 30);
        buf[18] = filter.filterInt(n >>>  5 & m);
        c = n << 20 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 12);
        c = n << 13 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 19);
        c = n <<  6 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 26);
        buf[22] = filter.filterInt(n >>>  1 & m);
        c = n << 24 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>>  8);
        c = n << 17 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 15);
        c = n << 10 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 22);
        c = n <<  3 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 29);
        buf[27] = filter.filterInt(n >>>  4 & m);
        c = n << 21 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 11);
        c = n << 14 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 18);
        c = n <<  7 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 25);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack26(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  6 & m);
        c = n << 20 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 12);
        c = n << 14 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 18);
        c = n <<  8 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 24);
        c = n <<  2 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 30);
        buf[ 5] = filter.filterInt(n >>>  4 & m);
        c = n << 22 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 10);
        c = n << 16 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 16);
        c = n << 10 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 22);
        c = n <<  4 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 28);
        buf[10] = filter.filterInt(n >>>  2 & m);
        c = n << 24 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>>  8);
        c = n << 18 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 14);
        c = n << 12 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 20);
        c = n <<  6 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 26);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>>  6 & m);
        c = n << 20 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 12);
        c = n << 14 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 18);
        c = n <<  8 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 24);
        c = n <<  2 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 30);
        buf[21] = filter.filterInt(n >>>  4 & m);
        c = n << 22 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 10);
        c = n << 16 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 16);
        c = n << 10 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 22);
        c = n <<  4 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 28);
        buf[26] = filter.filterInt(n >>>  2 & m);
        c = n << 24 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>>  8);
        c = n << 18 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 14);
        c = n << 12 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 20);
        c = n <<  6 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 26);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack27(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  5 & m);
        c = n << 22 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>> 10);
        c = n << 17 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 15);
        c = n << 12 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 20);
        c = n <<  7 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 25);
        c = n <<  2 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 30);
        buf[ 6] = filter.filterInt(n >>>  3 & m);
        c = n << 24 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>>  8);
        c = n << 19 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 13);
        c = n << 14 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 18);
        c = n <<  9 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 23);
        c = n <<  4 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 28);
        buf[12] = filter.filterInt(n >>>  1 & m);
        c = n << 26 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>>  6);
        c = n << 21 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 11);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n << 11 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 21);
        c = n <<  6 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 26);
        c = n <<  1 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 31);
        buf[19] = filter.filterInt(n >>>  4 & m);
        c = n << 23 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>>  9);
        c = n << 18 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 14);
        c = n << 13 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 19);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        c = n <<  3 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 29);
        buf[25] = filter.filterInt(n >>>  2 & m);
        c = n << 25 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>>  7);
        c = n << 20 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 12);
        c = n << 15 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 17);
        c = n << 10 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 22);
        c = n <<  5 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 27);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack28(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  4 & m);
        c = n << 24 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>>  8);
        c = n << 20 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>> 12);
        c = n << 16 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 16);
        c = n << 12 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 20);
        c = n <<  8 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 24);
        c = n <<  4 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 28);
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>>  4 & m);
        c = n << 24 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>>  8);
        c = n << 20 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 12);
        c = n << 16 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 16);
        c = n << 12 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 20);
        c = n <<  8 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 24);
        c = n <<  4 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 28);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>>  4 & m);
        c = n << 24 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>>  8);
        c = n << 20 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 12);
        c = n << 16 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 16);
        c = n << 12 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 20);
        c = n <<  8 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 24);
        c = n <<  4 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 28);
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>>  4 & m);
        c = n << 24 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>>  8);
        c = n << 20 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 12);
        c = n << 16 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 16);
        c = n << 12 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 20);
        c = n <<  8 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 24);
        c = n <<  4 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 28);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack29(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  3 & m);
        c = n << 26 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>>  6);
        c = n << 23 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>>  9);
        c = n << 20 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>> 12);
        c = n << 17 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 15);
        c = n << 14 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 18);
        c = n << 11 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 21);
        c = n <<  8 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 24);
        c = n <<  5 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 27);
        c = n <<  2 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 30);
        buf[10] = filter.filterInt(n >>>  1 & m);
        c = n << 28 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>>  4);
        c = n << 25 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>>  7);
        c = n << 22 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 10);
        c = n << 19 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 13);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n << 13 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 19);
        c = n << 10 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 22);
        c = n <<  7 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 25);
        c = n <<  4 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 28);
        c = n <<  1 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 31);
        buf[21] = filter.filterInt(n >>>  2 & m);
        c = n << 27 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>>  5);
        c = n << 24 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>>  8);
        c = n << 21 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 11);
        c = n << 18 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 14);
        c = n << 15 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 17);
        c = n << 12 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 20);
        c = n <<  9 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 23);
        c = n <<  6 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 26);
        c = n <<  3 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 29);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack30(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  2 & m);
        c = n << 28 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>>  4);
        c = n << 26 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>>  6);
        c = n << 24 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>>  8);
        c = n << 22 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>> 10);
        c = n << 20 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>> 12);
        c = n << 18 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>> 14);
        c = n << 16 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>> 16);
        c = n << 14 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>> 18);
        c = n << 12 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 20);
        c = n << 10 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 22);
        c = n <<  8 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 24);
        c = n <<  6 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 26);
        c = n <<  4 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 28);
        c = n <<  2 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 30);
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>>  2 & m);
        c = n << 28 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>>  4);
        c = n << 26 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>>  6);
        c = n << 24 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>>  8);
        c = n << 22 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 10);
        c = n << 20 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 12);
        c = n << 18 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 14);
        c = n << 16 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 16);
        c = n << 14 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 18);
        c = n << 12 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 20);
        c = n << 10 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 22);
        c = n <<  8 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 24);
        c = n <<  6 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 26);
        c = n <<  4 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 28);
        c = n <<  2 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 30);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack31(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  1 & m);
        c = n << 30 & m;
        n = src.get();
        buf[ 1] = filter.filterInt(c | n >>>  2);
        c = n << 29 & m;
        n = src.get();
        buf[ 2] = filter.filterInt(c | n >>>  3);
        c = n << 28 & m;
        n = src.get();
        buf[ 3] = filter.filterInt(c | n >>>  4);
        c = n << 27 & m;
        n = src.get();
        buf[ 4] = filter.filterInt(c | n >>>  5);
        c = n << 26 & m;
        n = src.get();
        buf[ 5] = filter.filterInt(c | n >>>  6);
        c = n << 25 & m;
        n = src.get();
        buf[ 6] = filter.filterInt(c | n >>>  7);
        c = n << 24 & m;
        n = src.get();
        buf[ 7] = filter.filterInt(c | n >>>  8);
        c = n << 23 & m;
        n = src.get();
        buf[ 8] = filter.filterInt(c | n >>>  9);
        c = n << 22 & m;
        n = src.get();
        buf[ 9] = filter.filterInt(c | n >>> 10);
        c = n << 21 & m;
        n = src.get();
        buf[10] = filter.filterInt(c | n >>> 11);
        c = n << 20 & m;
        n = src.get();
        buf[11] = filter.filterInt(c | n >>> 12);
        c = n << 19 & m;
        n = src.get();
        buf[12] = filter.filterInt(c | n >>> 13);
        c = n << 18 & m;
        n = src.get();
        buf[13] = filter.filterInt(c | n >>> 14);
        c = n << 17 & m;
        n = src.get();
        buf[14] = filter.filterInt(c | n >>> 15);
        c = n << 16 & m;
        n = src.get();
        buf[15] = filter.filterInt(c | n >>> 16);
        c = n << 15 & m;
        n = src.get();
        buf[16] = filter.filterInt(c | n >>> 17);
        c = n << 14 & m;
        n = src.get();
        buf[17] = filter.filterInt(c | n >>> 18);
        c = n << 13 & m;
        n = src.get();
        buf[18] = filter.filterInt(c | n >>> 19);
        c = n << 12 & m;
        n = src.get();
        buf[19] = filter.filterInt(c | n >>> 20);
        c = n << 11 & m;
        n = src.get();
        buf[20] = filter.filterInt(c | n >>> 21);
        c = n << 10 & m;
        n = src.get();
        buf[21] = filter.filterInt(c | n >>> 22);
        c = n <<  9 & m;
        n = src.get();
        buf[22] = filter.filterInt(c | n >>> 23);
        c = n <<  8 & m;
        n = src.get();
        buf[23] = filter.filterInt(c | n >>> 24);
        c = n <<  7 & m;
        n = src.get();
        buf[24] = filter.filterInt(c | n >>> 25);
        c = n <<  6 & m;
        n = src.get();
        buf[25] = filter.filterInt(c | n >>> 26);
        c = n <<  5 & m;
        n = src.get();
        buf[26] = filter.filterInt(c | n >>> 27);
        c = n <<  4 & m;
        n = src.get();
        buf[27] = filter.filterInt(c | n >>> 28);
        c = n <<  3 & m;
        n = src.get();
        buf[28] = filter.filterInt(c | n >>> 29);
        c = n <<  2 & m;
        n = src.get();
        buf[29] = filter.filterInt(c | n >>> 30);
        c = n <<  1 & m;
        n = src.get();
        buf[30] = filter.filterInt(c | n >>> 31);
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

    public static void unpack32(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffffffff;
        int n, c;

        n = src.get();
        buf[ 0] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 1] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 2] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 3] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 4] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 5] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 6] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 7] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 8] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[ 9] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[10] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[11] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[12] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[13] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[14] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[15] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[16] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[17] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[18] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[19] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[20] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[21] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[22] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[23] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[24] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[25] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[26] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[27] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[28] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[29] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[30] = filter.filterInt(n >>>  0 & m);
        n = src.get();
        buf[31] = filter.filterInt(n >>>  0 & m);

        dst.write(buf, 0, 32);
    }

}
