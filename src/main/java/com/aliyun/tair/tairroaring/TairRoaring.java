package com.aliyun.tair.tairroaring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.aliyun.tair.ModuleCommand;
import com.aliyun.tair.util.JoinParameters;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.util.SafeEncoder;
import static redis.clients.jedis.Protocol.toByteArray;

public class TairRoaring {
    private Jedis jedis;

    public TairRoaring(Jedis jedis) {
        this.jedis = jedis;
    }

    private Jedis getJedis() {
        return jedis;
    }

    /**
     * TR.SETBIT    TR.SETBIT <key> <offset> <value>
     * setting the value at the offset in roaringbitmap
     *
     * @param key roaring key
     * @param offset the bit offset
     * @param value the bit value
     * @return Success: long; Fail: error
     */
    public long trsetbit(final String key, long offset, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBIT, SafeEncoder.encode(key), toByteArray(offset), SafeEncoder.encode(value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trsetbit(final String key, long offset, long value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBIT, SafeEncoder.encode(key), toByteArray(offset), toByteArray(value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trsetbit(byte[] key, long offset, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBIT, key, toByteArray(offset), value);
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.SETBITS    TR.SETBITS <key> <offset> [<offset2> <offset3> ... <offsetn>]
     * setting the value at the offset in roaringbitmap
     *
     * @param key roaring key
     * @param offset the bit offset
     * @return Success: long; Fail: error
     */
    public long trsetbits(final String key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBITS,
                JoinParameters.joinParameters(SafeEncoder.encode(key),  args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }
    public long trsetbits(byte[] key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBITS,
                JoinParameters.joinParameters(key, args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }

    /**
     * TR.GETBIT    TR.GETBIT <key> <offset>
     * getting the bit on the offset of roaringbitmap
     *
     * @param key roaring key
     * @param offset the bit offset
     * @return Success: long; Fail: error
     */
    public long trgetbit(final String key, long offset) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRGETBIT, SafeEncoder.encode(key), toByteArray(offset));
        return BuilderFactory.LONG.build(obj);
    }
    public long trgetbit(byte[] key, long offset) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRGETBIT, key, toByteArray(offset));
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.GETBITS    TR.GETBITS <key> <offset> [<offset2> <offset3> ... <offsetn>]
     * get the value at the offset in roaringbitmap
     *
     * @param key roaring key
     * @param fields the bit offset
     * @return Success: array long; Fail: error
     */
    public List<Long> trgetbits(final String key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRGETBITS,
                JoinParameters.joinParameters(SafeEncoder.encode(key),  args.toArray(new byte[args.size()][])));
        return BuilderFactory.LONG_LIST.build(obj);
    }
    public List<Long> trgetbits(byte[] key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRGETBITS,
                JoinParameters.joinParameters(key, args.toArray(new byte[args.size()][])));
        return BuilderFactory.LONG_LIST.build(obj);
    }


    /**
     * TR.CLEARBITS    TR.CLEARBITS <key> <offset> [<offset2> <offset3> ... <offsetn>]
     * remove the value at the offset in roaringbitmap
     *
     * @param key roaring key
     * @param offset the bit offset
     * @return Success: long; Fail: error
     */
    public long trclearbits(final String key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRCLEARBITS,
                JoinParameters.joinParameters(SafeEncoder.encode(key),  args.toArray(new byte[args.size()][])));
        return BuilderFactory.Long.build(obj);
    }
    public long trclearbits(byte[] key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRCLEARBITS,
                JoinParameters.joinParameters(key, args.toArray(new byte[args.size()][])));
        return BuilderFactory.Long.build(obj);
    }


    /**
     * TR.RANGE TR.RANGE <key> <start> <end>
     * retrive the setted bit between the closed range, return them with int array
     *
     * @param key roaring bitmap key
     * @param start range start
     * @param end range end
     * @return Success: array long; Fail: error
     */
    public List<Long> trrange(final String key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRRANGE, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG_LIST.build(obj);
    }
    public List<Long> trrange(byte[] key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRRANGE, key, toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG_LIST.build(obj);
    }

    /**
     * TR.RANGEBITARRAY TR.RANGEBITARRAY <key> <start> <end>
     * retrive the setted bit between the closed range, return them by the string bit-array
     *
     * @param key roaring bitmap key
     * @param start range start
     * @param end range end
     * @return Success: string; Fail: error
     */
    public String trrangebitarray(final String key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRRANGEBITARRAY, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
        return BuilderFactory.STRING.build(obj);
    }
    public String trrange(byte[] key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRRANGE, key, toByteArray(start), toByteArray(end));
        return BuilderFactory.STRING.build(obj);
    }


    /**
     * TR.APPENDBITARRAY TR.APPENDBITARRAY <key> <offset> <value>
     * append the bit array after the offset in roaringbitmap
     *
     * @param key roaring key
     * @param offset the bit offset
     * @param value the bit value
     * @return Success: long; Fail: error
     */
    public long trappendbitarray(final String key, long offset, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRAPPENDBITARRAY, SafeEncoder.encode(key), toByteArray(offset), SafeEncoder.encode(value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trappendbitarray(final String key, long offset, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRAPPENDBITARRAY, SafeEncoder.encode(key), toByteArray(offset), value);
        return BuilderFactory.LONG.build(obj);
    }
    public long trappendbitarray(byte[] key, long offset, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRAPPENDBITARRAY, key, toByteArray(offset), value);
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.SETRANGE TR.SETRANGE <key> <start> <end>
     * 设置 Roaring bitmap中range 区间内元素为1-bit, 返回设置成功的 bit 数
     *
     * @param key roaring bitmap key
     * @param start range start
     * @param end range end
     * @return Success: array long; Fail: error
     */
    public long trsetrange(final String key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETRANGE, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }
    public long trsetrange(byte[] key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETRANGE, key, toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.FLIPRANGE TR.FLIPRANGE <key> <start> <end>
     *  翻转 Roaring bitmap 中range 区间内所有bit, 返回设置成功的 bit 数
     *
     * @param key roaring bitmap key
     * @param start range start
     * @param end range end
     * @return Success: array long; Fail: error
     */
    public long trfliprange(final String key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRFLIPRANGE, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }
    public long trfliprange(byte[] key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRFLIPRANGE, key, toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.BITCOUNT	TR.BITCOUNT <key> [<start> <end>]
     * counting bit set as 1 in the roaringbitmap
     * start and end are optional, you can count 1-bit in range by passing start and end
     *
     * @param key roaring key
     * @param start range start
     * @param end range end
     * @return Success: long; Fail: error
     */
    public long trbitcount(final String key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITCOUNT, SafeEncoder.encode(key));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitcount(byte[] key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITCOUNT, key);
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitcount(final String key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITCOUNT, SafeEncoder.encode(key), toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitcount(byte[] key, long start, long end) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITCOUNT, key, toByteArray(start), toByteArray(end));
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.MIN	TR.MIN <key>
     * 返回key对应的bitmap集合中首个bit值为1的偏移量，不存在时返回-1。
     *
     * @param key roaring key
     * @return Success: long; Fail: error
     */
    public long trmin(final String key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRMIN, SafeEncoder.encode(key));
        return BuilderFactory.LONG.build(obj);
    }

    public long trmin(byte[] key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRMIN, key);
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.MAX	TR.MAX <key>
     * 返回key对应的bitmap集合中bit值为1的最大偏移量，不存在时返回-1。
     *
     * @param key roaring key
     * @return Success: long; Fail: error
     */
    public long trmax(final String key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRMAX, SafeEncoder.encode(key));
        return BuilderFactory.LONG.build(obj);
    }

    public long trmax(byte[] key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRMAX, key);
        return BuilderFactory.LONG.build(obj);
    }

    /**
     * TR.OPTIMIZE	TR.OPTIMIZE <key>
     * 优化Roaring bitmap的存储空间。如果目标对象相对较大，且创建后以只读操作为主，可以主动执行此命令。
     *
     * @param key roaring key
     * @return Success: +OK; Fail: error
     */
    public String troptimize(final String key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TROPTIMIZE, SafeEncoder.encode(key));
        return BuilderFactory.STRING.build(obj);
    }

    public String troptimize(byte[] key) {
        Object obj = getJedis().sendCommand(ModuleCommand.TROPTIMIZE, key);
        return BuilderFactory.STRING.build(obj);
    }


    /**
     * TR.STAT	TR.STAT <key>
     * 返回当前bitmap的统计信息, 包括各种 roaringbitmap 容器的数量以及内存使用状况等信息。
     *
     * @param key roaring key
     * @return Success: string; Fail: error
     */
    public String trstat(final String key, boolean json = false) {
        if json {
            Object obj = getJedis().sendCommand(ModuleCommand.TRSTAT, SafeEncoder.encode(key), SafeEncoder.encode("JSON"));
            return BuilderFactory.STRING.build(obj);
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSTAT, SafeEncoder.encode(key));
        return BuilderFactory.STRING.build(obj);
    }
    public String trstat(byte[] key, boolean json = false) {
        if json {
            Object obj = getJedis().sendCommand(ModuleCommand.TRSTAT, key, SafeEncoder.encode("JSON"));
            return BuilderFactory.STRING.build(obj);
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSTAT, key);
        return BuilderFactory.STRING.build(obj);
    }


    /**
     * TR.BITPOS	TR.BITPOS <key> <value> [counting]
     * 传入一个value值（1或者0），在目标Key（TairRoaring数据结构）中查找首个被设置为指定值的bit位，并返回该bit位的偏移量（offset），偏移量（offset）从0开始。
     *  通过传入额外参数 counting 可以控制查找第 counting 个元素，如果 counting 为负则表示从后先前查找.
     *
     * @param key roaring key
     * @param value bit value
     * @param count count of the bit, negetive count indecate the reverse iteration
     * @return Success: long; Fail: error
     */
    public long trbitpos(final String key, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, SafeEncoder.encode(key), SafeEncoder.encode(value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitpos(final String key, final String value, long count) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, SafeEncoder.encode(key), SafeEncoder.encode(value), toByteArray(count));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitpos(final String key, long value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, SafeEncoder.encode(key), toByteArray(value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitpos(final String key, long value, long count) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, SafeEncoder.encode(key), toByteArray(value), toByteArray(count));
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitpos(byte[] key, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, key, value);
        return BuilderFactory.LONG.build(obj);
    }
    public long trbitpos(byte[] key, byte[] value, byte[] count) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITPOS, key, value, count);
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.BITOP	TR.BITOP <destkey> <operation> <key> [<key2> <key3>...]
     * 对Roaring bitmap执行集合运算操作，计算结果存储在destkey中。
     * 说明 集群架构暂不支持该命令。
     *
     * @param destkey   result store int destkey
     * @param operation operation type: AND OR NOT XOR DIFF
     * @param keys   operation joining keys
     * @return Success: long; Fail: error
     */
    public long trbitop(final String destkey, final String operation, final String... keys) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITOP,
            JoinParameters.joinParameters(SafeEncoder.encode(destkey), SafeEncoder.encode(operation), SafeEncoder.encodeMany(keys)));
        return BuilderFactory.LONG.build(obj);
    }

    public long trbitop(byte[] destkey, byte[] operation, byte[]... keys) {
        Object obj = getJedis().sendCommand(destkey, ModuleCommand.TRBITOP, JoinParameters.joinParameters(operation, keys));
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.BITOPCARD	TR.BITOPCARD <operation> <key> [<key2> <key3>...]
     * 对Roaring bitmap执行集合运算操作，返回结算结果中 1-bit 数
     * 说明 集群架构暂不支持该命令。
     *
     * @param operation operation type: AND OR NOT XOR DIFF
     * @param keys   operation joining keys
     * @return Success: long; Fail: error
     */
    public long trbitopcard(final String operation , final String... keys) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITOPCARD,
            JoinParameters.joinParameters(SafeEncoder.encode(operation), SafeEncoder.encodeMany(keys)));
        return BuilderFactory.LONG.build(obj);
    }

    public long trbitopcard(byte[] operation, byte[]... keys) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRBITOPCARD, JoinParameters.joinParameters(operation, keys));
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.SCAN TR.SCAN <key> <cursor> [COUNT <count>]
     * iterating element from cursor, COUNT indecate the max elements count per request
     *
     * @param key roaring bitmap key
     * @param cursor scan cursor, 0 stand for the very first value
     * @param count iteration counting by scan
     * @return Success: cursor and array long; Fail: error
     */
    public ScanResult<Entry<Long, Long>> trscan(final String key, long cursor) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSCAN, SafeEncoder.encode(key), toByteArray(cursor));
        return RoaringBuilderFactory.TRSCAN_RESULT_LONG.build(obj);
    }
    public ScanResult<Entry<Long, Long>> trscan(final String  key, long cursor, long count) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSCAN, SafeEncoder.encode(key), toByteArray(cursor), SafeEncoder.encode("COUNT"), toByteArray(count));
        return RoaringBuilderFactory.TRSCAN_RESULT_LONG.build(obj);
    }
    public ScanResult<Entry<byte[], byte[]>> trscan(byte[] key, byte[] cursor) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSCAN, key, cursor);
        return RoaringBuilderFactory.TRSCAN_RESULT_BYTE.build(obj);
    }
    public ScanResult<Entry<byte[], byte[]>> trscan(byte[] key, byte[] cursor, byte[] count) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSCAN, key, cursor, SafeEncoder.encode("COUNT"), count);
        return RoaringBuilderFactory.TRSCAN_RESULT_BYTE.build(obj);
    }


    /**
     * TR.LOAD TR.LOAD <key> <value>
     * Loading CRoaring serilizing style data into a empty key
     *
     * @param key   result store int key
     * @param value data
     * @return Success: long; Fail: error
     */
    public long trload(final String key, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRLOAD,
            JoinParameters.joinParameters(SafeEncoder.encode(key), SafeEncoder.encode(value)));
        return BuilderFactory.LONG.build(obj);
    }
    public long trload(final String key, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRLOAD,
            JoinParameters.joinParameters(SafeEncoder.encode(key), value));
        return BuilderFactory.LONG.build(obj);
    }
    public long trload(byte[] key, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRLOAD, key, value);
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.LOADSTRING TR.LOAD <key> <stringkey>
     * Loading string into into a empty roaringbitmap
     *
     * @param key   result store int key
     * @param stringkey string, aka origional bitmap
     * @return Success: long; Fail: error
     */
    public long trloadstring(final String key, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRLOADSTRING,
            JoinParameters.joinParameters(SafeEncoder.encode(key), SafeEncoder.encode(value)));
        return BuilderFactory.LONG.build(obj);
    }

    public long trloadstring(byte[] key, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRLOADSTRING, key, value);
        return BuilderFactory.LONG.build(obj);
    }


    /**
     * TR.DIFF	TR.DIFF <destkey> <key1> <key2>
     * 计算key1与key2对应Roaring Bitmap的差集，并将结果储到destkey所指的键中。
     * 说明 集群架构暂不支持该命令。
     *
     * @param destkey   result store int key
     * @param key1 operation diff key
     * @param key2 operation diff key
     * @return Success: OK; Fail: error
     */
    public String trdiff(final String destkey, final String key1, final String key2) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRDIFF,
            JoinParameters.joinParameters(SafeEncoder.encode(destkey), SafeEncoder.encode(key1), SafeEncoder.encode(key2)));
        return BuilderFactory.STRING.build(obj);
    }

    public String trdiff(byte[] destkey, byte[] key1, byte[] key2) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRDIFF, JoinParameters.joinParameters(destkey, key1, key2));
        return BuilderFactory.STRING.build(obj);
    }


    /**
     * TR.SETINTARRAY	TR.SETINTARRAY <key> <value1> [<value2> <value3> ... <valueN>]
     * 根据传入的整形数组来设置对应的Roaring bitmap, 该命令会覆盖已存在的Roaring bitmap对象。
     *
     * @param key roaring bitmap key
     * @param fields bit offset value
     * @return Success: +OK; Fail: error
     */
    public String trsetintarray(final String key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETINTARRAY,
            JoinParameters.joinParameters(SafeEncoder.encode(key), args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }

    public String trsetintarray(byte[] key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETINTARRAY,
                JoinParameters.joinParameters(key, args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }

    /**
     * TR.APPENDINTARRAY	TR.APPENDINTARRAY <key> <value1> [<value2> <value3> ... <valueN>]
     * 将bitmap中指定bit位的值（value）设置为1，支持传入多个值。
     *
     * @param key roaring bitmap key
     * @param fields bit offset value
     * @return Success: +OK; Fail: error
     */
    public String trappendintarray(final String key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRAPPENDINTARRAY,
                JoinParameters.joinParameters(SafeEncoder.encode(key),  args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }
    public String trappendintarray(byte[] key, long... fields) {
        final List<byte[]> args = new ArrayList<byte[]>();
        for (long value : fields) {
            args.add(toByteArray(value));
        }
        Object obj = getJedis().sendCommand(ModuleCommand.TRAPPENDINTARRAY,
                JoinParameters.joinParameters(key, args.toArray(new byte[args.size()][])));
        return BuilderFactory.STRING.build(obj);
    }


    /**
     * TR.SETBITARRAY	TR.SETBITARRAY <key> <value>
     * 根据传入的bit（由0和1组成的字符串），来创建一个位图（bitmap）。执行该命令时，Redis会逐个字符判断，只操作bit为1的字符，如果目标Key已存在则会覆盖已有数据。
     *
     * @param key roaring bitmap key
     * @param value bit offset value
     * @return Success: +OK; Fail: error
     */
    public String trsetbitarray(final String key, final String value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBITARRAY, SafeEncoder.encode(key), SafeEncoder.encode(value));
        return BuilderFactory.STRING.build(obj);
    }
    public String trsetbitarray(byte[] key, byte[] value) {
        Object obj = getJedis().sendCommand(ModuleCommand.TRSETBITARRAY, key, value);
        return BuilderFactory.STRING.build(obj);
    }

}
