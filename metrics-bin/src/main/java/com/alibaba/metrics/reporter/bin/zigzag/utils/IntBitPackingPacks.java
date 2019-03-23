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
public class IntBitPackingPacks
{

    public static void pack1(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 31 |
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 29 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 1);
    }

    public static void pack2(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m);
        buf[ 1] =
            (filter.filterInt(src.get()) & m) << 30 |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 2);
    }

    public static void pack3(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 29 |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 1] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 3);
    }

    public static void pack4(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xf;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);
        buf[ 1] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);
        buf[ 2] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) << 28 |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 4);
    }

    public static void pack5(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1f;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 27 |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 1] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 2] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 3] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 5);
    }

    public static void pack6(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3f;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) << 26 |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 4] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 6);
    }

    public static void pack7(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7f;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 25 |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 1] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 2] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 3] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 4] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 5] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 6] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 7);
    }

    public static void pack8(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xff;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 1] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 2] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 4] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 5] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 6] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);
        buf[ 7] =
            (filter.filterInt(src.get()) & m) << 24 |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 8);
    }

    public static void pack9(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 23 |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 2] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 3] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 4] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 6] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 7] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 8] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 9);
    }

    public static void pack10(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 1] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 2] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 3] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m);
        buf[ 5] =
            (filter.filterInt(src.get()) & m) << 22 |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 6] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 7] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 8] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 9] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) << 10 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 10);
    }

    public static void pack11(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 21 |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 1] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 3] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 4] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 5] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 6] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 7] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 8] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[ 9] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[10] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) << 11 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 11);
    }

    public static void pack12(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 2] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 4] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 5] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);
        buf[ 6] =
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 7] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 8] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);
        buf[ 9] =
            (filter.filterInt(src.get()) & m) << 20 |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[10] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 12 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 12);
    }

    public static void pack13(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 19 |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 1] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 2] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 3] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[ 5] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 6] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 7] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 8] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 9] =
            (n << 21) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[10] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[11] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[12] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 13 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 13);
    }

    public static void pack14(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 1] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 2] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 3] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 4] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 5] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 6] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m);
        buf[ 7] =
            (filter.filterInt(src.get()) & m) << 18 |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 8] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 9] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[10] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[11] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[12] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 14 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 14);
    }

    public static void pack15(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 17 |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[ 1] =
            (n << 19) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 2] =
            (n << 21) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[ 3] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 4] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 5] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[ 6] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 14 |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 7] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 8] =
            (n << 18) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 9] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[10] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[12] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 13 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[14] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 15 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 15);
    }

    public static void pack16(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffff;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 1] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 2] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 4] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 5] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 6] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 7] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 8] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[ 9] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[10] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[11] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[12] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[13] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[14] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);
        buf[15] =
            (filter.filterInt(src.get()) & m) << 16 |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 16);
    }

    public static void pack17(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 15 |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 1] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 13 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 2] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 3] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 4] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 5] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 6] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 7] =
            (n << 18) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 8] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 9] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 14 |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[10] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[11] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[12] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[13] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[14] =
            (n << 21) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[15] =
            (n << 19) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[16] =
            (n << 17) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 17);
    }

    public static void pack18(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 14 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 1] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 2] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 3] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 4] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 6] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 7] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 8] =
            (n << 18) |
            (filter.filterInt(src.get()) & m);
        buf[ 9] =
            (filter.filterInt(src.get()) & m) << 14 |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[10] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[12] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[13] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[14] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[15] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[16] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[17] =
            (n << 18) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 18);
    }

    public static void pack19(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 13 |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 1] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 2] =
            (n << 20) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[ 3] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 4] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 5] =
            (n << 21) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[ 6] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 7] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 8] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 9] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[10] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[11] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[12] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[13] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[14] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[15] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[16] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[17] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[18] =
            (n << 19) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 19);
    }

    public static void pack20(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 1] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 2] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 3] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 4] =
            (n << 20) |
            (filter.filterInt(src.get()) & m);
        buf[ 5] =
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 6] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 7] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 8] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 9] =
            (n << 20) |
            (filter.filterInt(src.get()) & m);
        buf[10] =
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[12] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[14] =
            (n << 20) |
            (filter.filterInt(src.get()) & m);
        buf[15] =
            (filter.filterInt(src.get()) & m) << 12 |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[16] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[17] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[18] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[19] =
            (n << 20) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 20);
    }

    public static void pack21(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 11 |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 1] =
            (n << 22) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 2] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[ 3] =
            (n << 23) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[ 4] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 5] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[ 6] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 7] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[ 8] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 9] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[10] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[11] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[12] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[14] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[15] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[16] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[17] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[18] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[19] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[20] =
            (n << 21) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 21);
    }

    public static void pack22(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 1] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 2] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 3] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 4] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 5] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 6] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[ 7] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 8] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 9] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[10] =
            (n << 22) |
            (filter.filterInt(src.get()) & m);
        buf[11] =
            (filter.filterInt(src.get()) & m) << 10 |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[12] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[13] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[14] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[15] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[16] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[17] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[18] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[19] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[20] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[21] =
            (n << 22) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 22);
    }

    public static void pack23(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  9 |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 1] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 2] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[ 3] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 4] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 5] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[ 6] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[ 7] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 8] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 9] =
            (n << 21) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[10] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[11] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[12] =
            (n << 25) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 21;
        buf[13] =
            (n << 11) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[14] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[15] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[16] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[17] =
            (n << 24) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[18] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[19] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[20] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[21] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[22] =
            (n << 23) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 23);
    }

    public static void pack24(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 1] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 2] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 4] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 5] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[ 6] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 7] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 8] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[ 9] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[10] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[12] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[13] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[14] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[15] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[16] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[17] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[18] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[19] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[20] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);
        buf[21] =
            (filter.filterInt(src.get()) & m) <<  8 |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[22] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[23] =
            (n << 24) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 24);
    }

    public static void pack25(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1ffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  7 |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[ 1] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 2] =
            (n << 21) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 3] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[ 4] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[ 5] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 6] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[ 7] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[ 8] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 9] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[10] =
            (n << 27) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 23;
        buf[11] =
            (n <<  9) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[12] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[13] =
            (n << 23) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[14] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[15] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[16] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[17] =
            (n << 26) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[18] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[19] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[20] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[21] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 21;
        buf[22] =
            (n << 11) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[23] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[24] =
            (n << 25) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 25);
    }

    public static void pack26(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3ffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 1] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 2] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 3] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 4] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[ 5] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 6] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[ 7] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 8] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 9] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[10] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[11] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[12] =
            (n << 26) |
            (filter.filterInt(src.get()) & m);
        buf[13] =
            (filter.filterInt(src.get()) & m) <<  6 |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[14] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[15] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[16] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[17] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[18] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[19] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[20] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[21] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[22] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[23] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[24] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[25] =
            (n << 26) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 26);
    }

    public static void pack27(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7ffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  5 |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[ 1] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[ 2] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 3] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[ 4] =
            (n << 25) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 5] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 6] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[ 7] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 8] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[ 9] =
            (n << 23) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[10] =
            (n << 28) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 26;
        buf[11] =
            (n <<  6) |
            (n = filter.filterInt(src.get()) & m) >>> 21;
        buf[12] =
            (n << 11) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[13] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[14] =
            (n << 21) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[15] =
            (n << 26) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[16] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 23;
        buf[17] =
            (n <<  9) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[18] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[19] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[20] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[21] =
            (n << 29) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 25;
        buf[22] =
            (n <<  7) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[23] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[24] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[25] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[26] =
            (n << 27) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 27);
    }

    public static void pack28(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xfffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 1] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 2] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 3] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 4] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 5] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[ 6] =
            (n << 28) |
            (filter.filterInt(src.get()) & m);
        buf[ 7] =
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 8] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 9] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[10] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[11] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[12] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (filter.filterInt(src.get()) & m);
        buf[14] =
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[15] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[16] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[17] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[18] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[19] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[20] =
            (n << 28) |
            (filter.filterInt(src.get()) & m);
        buf[21] =
            (filter.filterInt(src.get()) & m) <<  4 |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[22] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[23] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[24] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[25] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[26] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[27] =
            (n << 28) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 28);
    }

    public static void pack29(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x1fffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  3 |
            (n = filter.filterInt(src.get()) & m) >>> 26;
        buf[ 1] =
            (n <<  6) |
            (n = filter.filterInt(src.get()) & m) >>> 23;
        buf[ 2] =
            (n <<  9) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 3] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[ 4] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 5] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[ 6] =
            (n << 21) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[ 7] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[ 8] =
            (n << 27) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[ 9] =
            (n << 30) |
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 28;
        buf[10] =
            (n <<  4) |
            (n = filter.filterInt(src.get()) & m) >>> 25;
        buf[11] =
            (n <<  7) |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[12] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[13] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[14] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[15] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[16] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[17] =
            (n << 25) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[18] =
            (n << 28) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[19] =
            (n << 31) |
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 27;
        buf[20] =
            (n <<  5) |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[21] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 21;
        buf[22] =
            (n << 11) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[23] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[24] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[25] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[26] =
            (n << 23) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[27] =
            (n << 26) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[28] =
            (n << 29) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 29);
    }

    public static void pack30(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x3fffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 28;
        buf[ 1] =
            (n <<  4) |
            (n = filter.filterInt(src.get()) & m) >>> 26;
        buf[ 2] =
            (n <<  6) |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 3] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[ 4] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[ 5] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[ 6] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[ 7] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[ 8] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[ 9] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[10] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[11] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[12] =
            (n << 26) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[13] =
            (n << 28) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[14] =
            (n << 30) |
            (filter.filterInt(src.get()) & m);
        buf[15] =
            (filter.filterInt(src.get()) & m) <<  2 |
            (n = filter.filterInt(src.get()) & m) >>> 28;
        buf[16] =
            (n <<  4) |
            (n = filter.filterInt(src.get()) & m) >>> 26;
        buf[17] =
            (n <<  6) |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[18] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[19] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[20] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[21] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[22] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[23] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[24] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[25] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[26] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[27] =
            (n << 26) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[28] =
            (n << 28) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[29] =
            (n << 30) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 30);
    }

    public static void pack31(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x7fffffff;
        int n;

        buf[ 0] =
            (filter.filterInt(src.get()) & m) <<  1 |
            (n = filter.filterInt(src.get()) & m) >>> 30;
        buf[ 1] =
            (n <<  2) |
            (n = filter.filterInt(src.get()) & m) >>> 29;
        buf[ 2] =
            (n <<  3) |
            (n = filter.filterInt(src.get()) & m) >>> 28;
        buf[ 3] =
            (n <<  4) |
            (n = filter.filterInt(src.get()) & m) >>> 27;
        buf[ 4] =
            (n <<  5) |
            (n = filter.filterInt(src.get()) & m) >>> 26;
        buf[ 5] =
            (n <<  6) |
            (n = filter.filterInt(src.get()) & m) >>> 25;
        buf[ 6] =
            (n <<  7) |
            (n = filter.filterInt(src.get()) & m) >>> 24;
        buf[ 7] =
            (n <<  8) |
            (n = filter.filterInt(src.get()) & m) >>> 23;
        buf[ 8] =
            (n <<  9) |
            (n = filter.filterInt(src.get()) & m) >>> 22;
        buf[ 9] =
            (n << 10) |
            (n = filter.filterInt(src.get()) & m) >>> 21;
        buf[10] =
            (n << 11) |
            (n = filter.filterInt(src.get()) & m) >>> 20;
        buf[11] =
            (n << 12) |
            (n = filter.filterInt(src.get()) & m) >>> 19;
        buf[12] =
            (n << 13) |
            (n = filter.filterInt(src.get()) & m) >>> 18;
        buf[13] =
            (n << 14) |
            (n = filter.filterInt(src.get()) & m) >>> 17;
        buf[14] =
            (n << 15) |
            (n = filter.filterInt(src.get()) & m) >>> 16;
        buf[15] =
            (n << 16) |
            (n = filter.filterInt(src.get()) & m) >>> 15;
        buf[16] =
            (n << 17) |
            (n = filter.filterInt(src.get()) & m) >>> 14;
        buf[17] =
            (n << 18) |
            (n = filter.filterInt(src.get()) & m) >>> 13;
        buf[18] =
            (n << 19) |
            (n = filter.filterInt(src.get()) & m) >>> 12;
        buf[19] =
            (n << 20) |
            (n = filter.filterInt(src.get()) & m) >>> 11;
        buf[20] =
            (n << 21) |
            (n = filter.filterInt(src.get()) & m) >>> 10;
        buf[21] =
            (n << 22) |
            (n = filter.filterInt(src.get()) & m) >>>  9;
        buf[22] =
            (n << 23) |
            (n = filter.filterInt(src.get()) & m) >>>  8;
        buf[23] =
            (n << 24) |
            (n = filter.filterInt(src.get()) & m) >>>  7;
        buf[24] =
            (n << 25) |
            (n = filter.filterInt(src.get()) & m) >>>  6;
        buf[25] =
            (n << 26) |
            (n = filter.filterInt(src.get()) & m) >>>  5;
        buf[26] =
            (n << 27) |
            (n = filter.filterInt(src.get()) & m) >>>  4;
        buf[27] =
            (n << 28) |
            (n = filter.filterInt(src.get()) & m) >>>  3;
        buf[28] =
            (n << 29) |
            (n = filter.filterInt(src.get()) & m) >>>  2;
        buf[29] =
            (n << 30) |
            (n = filter.filterInt(src.get()) & m) >>>  1;
        buf[30] =
            (n << 31) |
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 31);
    }

    public static void pack32(
            int[] buf,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0xffffffff;

        buf[ 0] =
            (filter.filterInt(src.get()) & m);
        buf[ 1] =
            (filter.filterInt(src.get()) & m);
        buf[ 2] =
            (filter.filterInt(src.get()) & m);
        buf[ 3] =
            (filter.filterInt(src.get()) & m);
        buf[ 4] =
            (filter.filterInt(src.get()) & m);
        buf[ 5] =
            (filter.filterInt(src.get()) & m);
        buf[ 6] =
            (filter.filterInt(src.get()) & m);
        buf[ 7] =
            (filter.filterInt(src.get()) & m);
        buf[ 8] =
            (filter.filterInt(src.get()) & m);
        buf[ 9] =
            (filter.filterInt(src.get()) & m);
        buf[10] =
            (filter.filterInt(src.get()) & m);
        buf[11] =
            (filter.filterInt(src.get()) & m);
        buf[12] =
            (filter.filterInt(src.get()) & m);
        buf[13] =
            (filter.filterInt(src.get()) & m);
        buf[14] =
            (filter.filterInt(src.get()) & m);
        buf[15] =
            (filter.filterInt(src.get()) & m);
        buf[16] =
            (filter.filterInt(src.get()) & m);
        buf[17] =
            (filter.filterInt(src.get()) & m);
        buf[18] =
            (filter.filterInt(src.get()) & m);
        buf[19] =
            (filter.filterInt(src.get()) & m);
        buf[20] =
            (filter.filterInt(src.get()) & m);
        buf[21] =
            (filter.filterInt(src.get()) & m);
        buf[22] =
            (filter.filterInt(src.get()) & m);
        buf[23] =
            (filter.filterInt(src.get()) & m);
        buf[24] =
            (filter.filterInt(src.get()) & m);
        buf[25] =
            (filter.filterInt(src.get()) & m);
        buf[26] =
            (filter.filterInt(src.get()) & m);
        buf[27] =
            (filter.filterInt(src.get()) & m);
        buf[28] =
            (filter.filterInt(src.get()) & m);
        buf[29] =
            (filter.filterInt(src.get()) & m);
        buf[30] =
            (filter.filterInt(src.get()) & m);
        buf[31] =
            (filter.filterInt(src.get()) & m);

        dst.write(buf, 0, 32);
    }

}
