package com.aliyun.tair.tairvector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aliyun.tair.ModuleCommand;
import com.aliyun.tair.tairhash.factory.HashBuilderFactory;
import com.aliyun.tair.tairvector.factory.VectorBuilderFactory;
import com.aliyun.tair.tairvector.params.DistanceMethod;
import com.aliyun.tair.tairvector.params.HscanParams;
import com.aliyun.tair.tairvector.params.IndexAlgorithm;
import com.aliyun.tair.util.JoinParameters;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

import static redis.clients.jedis.Protocol.toByteArray;

public class TairVectorPipeline extends Pipeline {
    public void quit() {
        getClient("").close();
    }

    /**
     * TVS.CREATEINDEX  TVS.CREATEINDEX index_name dims algorithm distance_method  [(attribute_key attribute_value) ... ]
     * <p>
     * create tair-vector index
     *
     * @param index     index name
     * @param dims      vector dims
     * @param algorithm index algorithm
     * @param method    vector distance method
     * @param attrs     other columns, optional
     * @return Success: +OK; Fail: error
     */
    public Response<String> tvscreateindex(final String index, int dims, IndexAlgorithm algorithm, DistanceMethod method, final String... attrs) {
        getClient(index).sendCommand(ModuleCommand.TVSCREATEINDEX, JoinParameters.joinParameters(SafeEncoder.encode(index), toByteArray(dims), SafeEncoder.encode(algorithm.name()), SafeEncoder.encode(method.name()), SafeEncoder.encodeMany(attrs)));
        return getResponse(BuilderFactory.STRING);
    }

    public Response<byte[]> tvscreateindex(byte[] index, int dims, IndexAlgorithm algorithm, DistanceMethod method, final byte[]... params) {
        getClient(index).sendCommand(ModuleCommand.TVSCREATEINDEX, JoinParameters.joinParameters(index, toByteArray(dims), SafeEncoder.encode(algorithm.name()), SafeEncoder.encode(method.name()), params));
        return getResponse(BuilderFactory.BYTE_ARRAY);
    }

    /**
     * TVS.GETINDEX TVS.GETINDEX index_name
     * <p>
     * get index schema info, including: index_name, algorithm, distance_method, data_count, ...
     *
     * @param index index name
     * @return Success: string_map, Fail:  empty
     */
    public Response<Map<String, String>> tvsgetindex(final String index) {
        getClient(index).sendCommand(ModuleCommand.TVSGETINDEX, SafeEncoder.encode(index));
        return getResponse(BuilderFactory.STRING_MAP);
    }

    public Response<Map<byte[], byte[]>> tvsgetindex(byte[] index) {
        getClient(index).sendCommand(ModuleCommand.TVSGETINDEX, index);
        return getResponse(BuilderFactory.BYTE_ARRAY_MAP);
    }

    /**
     * TVS.DELINDEX TVS.DELINDEX index_name
     * <p>
     * delete index
     *
     * @param index index name
     * @return Success: 1; Fail: 0
     */
    public Response<Long> tvsdelindex(final String index) {
        getClient(index).sendCommand(ModuleCommand.TVSDELINDEX, SafeEncoder.encode(index));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvsdelindex(byte[] index) {
        getClient(index).sendCommand(ModuleCommand.TVSDELINDEX, index);
        return getResponse(BuilderFactory.LONG);
    }


    /**
     * TVS.SCANINDEX TVS.SCANINDEX index_name
     * <p>
     * scan index
     *
     * @param cursor start offset
     * @param params the params: [MATCH pattern] [COUNT count]
     *               `MATCH` - Set the pattern which is used to filter the results
     *               `COUNT` - Set the number of fields in a single scan (default is 10)
     *               `NOVAL` - The return result contains no data portion, only cursor information
     * @return A ScanResult. {@link HashBuilderFactory#EXHSCAN_RESULT_STRING}
     */
    public Response<ScanResult<String>> tvsscanindex(Long cursor, HscanParams params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(toByteArray(cursor));
        args.addAll(params.getParams());
        getClient("").sendCommand(ModuleCommand.TVSSCANINDEX, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.SCAN_CURSOR_STRING);
    }

    /**
     * TVS.HSET TVS.HSET index entityid vector [(attribute_key attribute_value) ...]
     * <p>
     * insert entity into tair-vector module
     *
     * @param index    index name
     * @param entityid entity id
     * @param vector   vector info
     * @param params   scalar attribute key, value
     * @return integer-reply specifically:
     * {@literal k} if success, k is the number of fields that were added..
     * throw error like "(error) Illegal vector dimensions" if error
     */
    public Response<Long> tvshset(final String index, final String entityid, final String vector, final String... params) {
        getClient(index).sendCommand(ModuleCommand.TVSHSET, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG), SafeEncoder.encode(vector), SafeEncoder.encodeMany(params)));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvshset(byte[] index, byte[] entityid, byte[] vector, final byte[]... params) {
        getClient(index).sendCommand(ModuleCommand.TVSHSET, JoinParameters.joinParameters(index, entityid, SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG), vector, params));
        return getResponse(BuilderFactory.LONG);
    }

    /**
     * TVS.HGETALL TVS.HGETALL index entityid
     * <p>
     * get entity from tair-vector module
     *
     * @param index    index name
     * @param entityid entity id
     * @return Map, an empty list when {@code entityid} does not exist.
     */
    public Response<Map<String, String>> tvshgetall(final String index, final String entityid) {
        getClient(index).sendCommand(ModuleCommand.TVSHGETALL, SafeEncoder.encode(index), SafeEncoder.encode(entityid));
        return getResponse(BuilderFactory.STRING_MAP);
    }

    public Response<Map<byte[], byte[]>> tvshgetall(byte[] index, byte[] entityid) {
        getClient(index).sendCommand(ModuleCommand.TVSHGETALL, index, entityid);
        return getResponse(BuilderFactory.BYTE_ARRAY_MAP);
    }

    /**
     * TVS.HMGETALL TVS.HMGETALL index entityid attribute_key [attribute_key ...]
     * <p>
     * get entity attrs from tair-vector module
     *
     * @param index    index name
     * @param entityid entity id
     * @param attrs    attrs
     * @return List, an empty list when {@code entityid} or {@code attrs} does not exist .
     */
    public Response<List<String>> tvshmget(final String index, final String entityid, final String... attrs) {
        getClient(index).sendCommand(ModuleCommand.TVSHMGET, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encodeMany(attrs)));
        return getResponse(BuilderFactory.STRING_LIST);
    }

    public Response<List<byte[]>> tvshmget(byte[] index, byte[] entityid, byte[]... attrs) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHMGET, JoinParameters.joinParameters(index, entityid, attrs));
        return getResponse(BuilderFactory.BYTE_ARRAY_LIST);
    }


    /**
     * TVS.DEL TVS.DEL index entityid
     * <p>
     * delete entity from tair-vector module
     *
     * @param index    index name
     * @param entityid entity id
     * @return Long integer-reply the number of fields that were removed from the tair-vector
     * not including specified but non existing fields.
     */
    public Response<Long> tvsdel(final String index, final String entityid) {
        getClient(index).sendCommand(ModuleCommand.TVSDEL, SafeEncoder.encode(index), SafeEncoder.encode(entityid));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvsdel(byte[] index, byte[] entityid) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSDEL, index, entityid);
        return getResponse(BuilderFactory.LONG);
    }


    /**
     * TVS.DEL TVS.DEL index entityid1 entityi2
     * <p>
     * delete entity from tair-vector module
     *
     * @param index    index name
     * @param entityids entity id
     * @return Long integer-reply the number of fields that were removed from the tair-vector
     * not including specified but non existing fields.
     */
    public Response<Long> tvsdel(final String index, final String... entityids) {
        getClient(index).sendCommand(ModuleCommand.TVSDEL, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encodeMany(entityids)));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvsdel(byte[] index, byte[]... entityids) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSDEL, JoinParameters.joinParameters(index, entityids));
        return getResponse(BuilderFactory.LONG);
    }

    /**
     * TVS.HDEL TVS.HDEL index entityid attribute_key [attribute_key ...]
     * <p>
     * delete entity attrs from tair-vector module
     *
     * @param index    index name
     * @param entityid entity id
     * @param attrs    attrs
     * @return Long integer-reply the number of fields that were removed from the tair-vector
     * not including specified but non existing fields.
     */
    public Response<Long> tvshdel(final String index, final String entityid, final String... attrs) {
        getClient(index).sendCommand(ModuleCommand.TVSHDEL, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encodeMany(attrs)));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvshdel(byte[] index, byte[] entityid, byte[]... attrs) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHDEL, JoinParameters.joinParameters(index, entityid, attrs));
        return getResponse(BuilderFactory.LONG);
    }


    /**
     * TVS.SCAN TVS.SCAN index_name cursor [MATCH pattern] [COUNT count]
     * <p>
     * scan entity from tair-vector module
     *
     * @param index  index name
     * @param cursor start offset
     * @param params the params: [MATCH pattern] [COUNT count]
     *               `MATCH` - Set the pattern which is used to filter the results
     *               `COUNT` - Set the number of fields in a single scan (default is 10)
     *               `NOVAL` - The return result contains no data portion, only cursor information
     * @return A ScanResult.
     */
    public Response<ScanResult<String>> tvsscan(final String index, Long cursor, HscanParams params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(SafeEncoder.encode(index));
        args.add(toByteArray(cursor));
        args.addAll(params.getParams());
        getClient(index).sendCommand(ModuleCommand.TVSSCAN, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.SCAN_CURSOR_STRING);
    }

    public Response<ScanResult<byte[]>> tvsscan(byte[] index, Long cursor, HscanParams params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(index);
        args.add(toByteArray(cursor));
        args.addAll(params.getParams());
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSSCAN, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.SCAN_CURSOR_BYTE);
    }

    /**
     * TVS.KNNSEARCH TVS.KNNSEARCH index_name topn vector
     * <p>
     * query entity by vector
     *
     * @param index  index name
     * @param topn   topn result
     * @param vector query vector
     * @param params for HNSW, params include:
     *               ef_search     range [0, 1000]
     * @return Knn<String>
     */
    public Response<VectorBuilderFactory.Knn<String>> tvsknnsearch(final String index, Long topn, final String vector, final String... params) {
        return tvsknnsearchfilter(index, topn, vector, "", params);
    }

    public Response<VectorBuilderFactory.Knn<byte[]>> tvsknnsearch(byte[] index, Long topn, byte[] vector, final byte[]... params) {
        return tvsknnsearchfilter(index, topn, vector, SafeEncoder.encode(""), params);
    }

    /**
     * TVS.KNNSEARCH TVS.KNNSEARCH index_name topn vector pattern
     * <p>
     * query entity by vector and scalar pattern
     *
     * @param index   index name
     * @param topn    topn result
     * @param vector  query vector
     * @param pattern support +, -，>, <, !=， ,()，&&, ||, !, ==
     * @param params  for HNSW, params include:
     *                ef_search     range [0, 1000]
     * @return VectorBuilderFactory.Knn<>
     */
    public Response<VectorBuilderFactory.Knn<String>> tvsknnsearchfilter(final String index, Long topn, final String vector, final String pattern, final String... params) {
        getClient(index).sendCommand(ModuleCommand.TVSKNNSEARCH, JoinParameters.joinParameters(SafeEncoder.encode(index), toByteArray(topn), SafeEncoder.encode(vector), SafeEncoder.encode(pattern), SafeEncoder.encodeMany(params)));
        return getResponse(VectorBuilderFactory.STRING_KNN_RESULT);
    }

    public Response<VectorBuilderFactory.Knn<byte[]>> tvsknnsearchfilter(byte[] index, Long topn, byte[] vector, byte[] pattern, final byte[]... params) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSKNNSEARCH, JoinParameters.joinParameters(index, toByteArray(topn), vector, pattern, params));
        return getResponse(VectorBuilderFactory.BYTE_KNN_RESULT);
    }


    /**
     * TVS.MKNNSEARCH TVS.MKNNSEARCH index_name topn vector [vector...]
     *
     * @param index   index name
     * @param topn    topn for each vector
     * @param vectors vector list
     * @param params  for HNSW, params include:
     *                ef_search     range [0, 1000]
     * @return Collection<></>
     */
    public Response<Collection<VectorBuilderFactory.Knn<String>>> tvsmknnsearch(final String index, Long topn, Collection<String> vectors, final String... params) {
        return tvsmknnsearchfilter(index, topn, vectors, "", params);
    }

    public Response<Collection<VectorBuilderFactory.Knn<byte[]>>> tvsmknnsearch(byte[] index, Long topn, Collection<byte[]> vectors, final byte[]... params) {
        return tvsmknnsearchfilter(index, topn, vectors, SafeEncoder.encode(""), params);
    }

    /**
     * TVS.MKNNSEARCH TVS.MKNNSEARCH index_name topn vector [vector...] pattern
     *
     * @param index   index name
     * @param topn    topn for each vector
     * @param vectors vector list
     * @param pattern filter pattern
     * @param params  for HNSW, params include:
     *                ef_search     range [0, 1000]
     * @return Collection<></>
     */
    public Response<Collection<VectorBuilderFactory.Knn<String>>> tvsmknnsearchfilter(final String index, Long topn, Collection<String> vectors, final String pattern, final String... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(SafeEncoder.encode(index));
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors.stream().map(vector -> SafeEncoder.encode(vector)).collect(Collectors.toList()));
        args.add(SafeEncoder.encode(pattern));
        args.addAll(Arrays.stream(params).map(str -> SafeEncoder.encode(str)).collect(Collectors.toList()));
        getClient(index).sendCommand(ModuleCommand.TVSMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.STRING_KNN_BATCH_RESULT);
    }

    public Response<Collection<VectorBuilderFactory.Knn<byte[]>>> tvsmknnsearchfilter(byte[] index, Long topn, Collection<byte[]> vectors, byte[] pattern, final byte[]... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(index);
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors);
        args.add(pattern);
        args.addAll(Arrays.stream(params).collect(Collectors.toList()));
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.BYTE_KNN_BATCH_RESULT);
    }

    public Response<VectorBuilderFactory.Knn<String>> tvsmindexknnsearch(Collection<String> indexs, Long topn, String vector, String... params) {
        return tvsmindexknnsearchfilter(indexs, topn, vector, "", params);
    }

    public Response<VectorBuilderFactory.Knn<byte[]>> tvsmindexknnsearch(Collection<byte[]> indexs, Long topn, byte[] vector, byte[]... params) {
        return tvsmindexknnsearchfilter(indexs, topn, vector, SafeEncoder.encode(""), params);
    }

    public Response<VectorBuilderFactory.Knn<String>> tvsmindexknnsearchfilter(Collection<String> indexs, Long topn, String vector, String pattern, String... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(toByteArray(indexs.size()));
        args.addAll(indexs.stream().map(index -> SafeEncoder.encode(index)).collect(Collectors.toList()));
        args.add(toByteArray(topn));
        args.add(SafeEncoder.encode(vector));
        args.add(SafeEncoder.encode(pattern));
        args.addAll(Arrays.stream(params).map(str -> SafeEncoder.encode(str)).collect(Collectors.toList()));
        getClient("").sendCommand(ModuleCommand.TVSMINDEXKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.STRING_KNN_RESULT);
    }

    public Response<VectorBuilderFactory.Knn<byte[]>> tvsmindexknnsearchfilter(Collection<byte[]> indexs, Long topn, byte[] vector, byte[] pattern, final byte[]... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(toByteArray(indexs.size()));
        args.addAll(indexs);
        args.add(toByteArray(topn));
        args.add(vector);
        args.add(pattern);
        args.addAll(Arrays.stream(params).collect(Collectors.toList()));
        getClient("").sendCommand(ModuleCommand.TVSMINDEXKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.BYTE_KNN_RESULT);
    }

    public Response<Collection<VectorBuilderFactory.Knn<String>>> tvsmindexmknnsearch(Collection<String> indexs, Long topn, Collection<String> vectors, String... params) {
        return tvsmindexmknnsearchfilter(indexs, topn, vectors, "", params);
    }

    public Response<Collection<VectorBuilderFactory.Knn<byte[]>>> tvsmindexmknnsearch(Collection<byte[]> indexs, Long topn, Collection<byte[]> vectors, byte[]... params) {
        return tvsmindexmknnsearchfilter(indexs, topn, vectors, SafeEncoder.encode(""), params);
    }

    public Response<Collection<VectorBuilderFactory.Knn<String>>> tvsmindexmknnsearchfilter(Collection<String> indexs, Long topn, Collection<String> vectors, String pattern, String... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(toByteArray(indexs.size()));
        args.addAll(indexs.stream().map(index -> SafeEncoder.encode(index)).collect(Collectors.toList()));
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors.stream().map(vector -> SafeEncoder.encode(vector)).collect(Collectors.toList()));
        args.add(SafeEncoder.encode(pattern));
        args.addAll(Arrays.stream(params).map(str -> SafeEncoder.encode(str)).collect(Collectors.toList()));
        getClient("").sendCommand(ModuleCommand.TVSMINDEXMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.STRING_KNN_BATCH_RESULT);
    }

    public Response<Collection<VectorBuilderFactory.Knn<byte[]>>> tvsmindexmknnsearchfilter(Collection<byte[]> indexs, Long topn, Collection<byte[]> vectors, byte[] pattern, byte[]... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(toByteArray(indexs.size()));
        args.addAll(indexs);
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors);
        args.add(pattern);
        args.addAll(Arrays.stream(params).collect(Collectors.toList()));
        getClient("").sendCommand(ModuleCommand.TVSMINDEXMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.BYTE_KNN_BATCH_RESULT);
    }

    /**
     * TVS.HINCRBY index entityid field value
     * Increment the long value of a tairvector field by the given amount, not support VECTOR
     *
     * @param index  index name
     * @param entityid entity id
     * @param field  the field type: key
     * @param value  the increment type: long
     * @return Long integer-reply the value at {@code field} after the increment operation.
     */
    public Response<Long> tvshincrby(final String index, final String entityid, final String field, final long value) {
        getClient(index).sendCommand(ModuleCommand.TVSHINCRBY, SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encode(field), toByteArray(value));
        return getResponse(BuilderFactory.LONG);
    }

    public Response<Long> tvshincrby(byte[] index, byte[] entityid, byte[] field, long value) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHINCRBY, index, entityid, field, toByteArray(value));
        return getResponse(BuilderFactory.LONG);
    }


    /**
     * TVS.HINCRBYFOLAT index entityid field value
     * Increment the float value of a tairvector field by the given amount, not support VECTOR
     *
     * @param index index name
     * @param entityid entity id
     * @param field the field type: key
     * @param value the increment type: double
     * @return Double bulk-string-reply the value of {@code field} after the increment.
     */
    public Response<Double> tvshincrbyfloat(final String index, final String entityid, final String field, final double value) {
        getClient(index).sendCommand(ModuleCommand.TVSHINCRBYFLOAT, SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encode(field), toByteArray(value));
        return getResponse(BuilderFactory.DOUBLE);
    }

    public Response<Double> tvshincrbyfloat(byte[] index, byte[] entityid, byte[] field, double value) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHINCRBYFLOAT, index, entityid, field, toByteArray(value));
        return getResponse(BuilderFactory.DOUBLE);
    }


    public Response<VectorBuilderFactory.Knn<String>> tvsgetdistance(String index, String vector, Collection<String> keys, Long topn, Float max_dist, String filter) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(SafeEncoder.encode(index));
        args.add(SafeEncoder.encode(vector));
        args.add(toByteArray(keys.size()));
        args.addAll(keys.stream().map(key -> SafeEncoder.encode(key)).collect(Collectors.toList()));
        if (topn != null) {
            args.add(SafeEncoder.encode("TOPN"));
            args.add(toByteArray(topn));
        }
        if (max_dist != null) {
            args.add(SafeEncoder.encode("MAX_DIST"));
            args.add(toByteArray(max_dist));
        }
        if (filter != null) {
            args.add(SafeEncoder.encode("FILTER"));
            args.add(SafeEncoder.encode(filter));
        }

        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSGETDISTANCE, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.STRING_KNN_RESULT);
    }

    public Response<VectorBuilderFactory.Knn<byte[]>> tvsgetdistance(byte[] index, byte[] vector, Collection<byte[]> keys, Long topn, Float max_dist, byte[] filter) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(index);
        args.add(vector);
        args.add(toByteArray(keys.size()));
        args.addAll(keys);
        if (topn != null) {
            args.add(SafeEncoder.encode("TOPN"));
            args.add(toByteArray(topn));
        }
        if (max_dist != null) {
            args.add(SafeEncoder.encode("MAX_DIST"));
            args.add(toByteArray(max_dist));
        }
        if (filter != null) {
            args.add(SafeEncoder.encode("FILTER"));
            args.add(filter);
        }

        getClient(index).sendCommand(ModuleCommand.TVSGETDISTANCE, args.toArray(new byte[args.size()][]));
        return getResponse(VectorBuilderFactory.BYTE_KNN_RESULT);
    }

    /**
     * Set expire time (seconds).
     *
     * @param index   index name
     * @param key     the key
     * @param seconds time is seconds
     * @return Success: true, fail: false.
     */
    public Response<Boolean> tvshexpire(final String index, final String key, final int seconds) {
        return tvshexpire(SafeEncoder.encode(index), SafeEncoder.encode(key), seconds);
    }

    public Response<Boolean> tvshexpire(final byte[] index, final byte[] key, final int seconds) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHEXPIRE, index, key, toByteArray(seconds));
        return getResponse(BuilderFactory.BOOLEAN);
    }

    /**
     * Set expire time (seconds).
     *
     * @param index        index name
     * @param key          the key
     * @param milliseconds time is milliseconds
     * @return Success: true, fail: false.
     */
    public Response<Boolean> tvshpexpire(final String index, final String key, final int milliseconds) {
        return tvshpexpire(SafeEncoder.encode(index), SafeEncoder.encode(key), milliseconds);
    }

    public Response<Boolean> tvshpexpire(final byte[] index, final byte[] key, final int milliseconds) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHPEXPIRE, index, key, toByteArray(milliseconds));
        return getResponse(BuilderFactory.BOOLEAN);
    }

    /**
     * Set the expiration for a key as a UNIX timestamp (seconds).
     *
     * @param index    the index name
     * @param key      the key
     * @param unixTime timestamp the timestamp type: posix time, time is seconds
     * @return Success: true, fail: false.
     */
    public Response<Boolean> tvshexpireAt(final String index, final String key, final long unixTime) {
        return tvshexpireAt(SafeEncoder.encode(index), SafeEncoder.encode(key), unixTime);
    }

    public Response<Boolean> tvshexpireAt(final byte[] index, final byte[] key, final long unixTime) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHEXPIREAT, index, key, toByteArray(unixTime));
        return getResponse(BuilderFactory.BOOLEAN);
    }

    /**
     * Set the expiration for a key as a UNIX timestamp (milliseconds).
     *
     * @param index    the index name
     * @param key      the key
     * @param unixTime timestamp the timestamp type: posix time, time is milliseconds
     * @return Success: true, fail: false.
     */
    public Response<Boolean> tvshpexpireAt(final String index, final String key, final long unixTime) {
        return tvshpexpireAt(SafeEncoder.encode(index), SafeEncoder.encode(key), unixTime);
    }

    public Response<Boolean> tvshpexpireAt(final byte[] index, final byte[] key, final long unixTime) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHPEXPIREAT, index, key, toByteArray(unixTime));
        return getResponse(BuilderFactory.BOOLEAN);
    }

    /**
     * Get ttl (seconds).
     *
     * @param index index name
     * @param key   the key
     * @return ttl
     */
    public Response<Long> tvshttl(final String index, final String key) {
        return tvshttl(SafeEncoder.encode(index), SafeEncoder.encode(key));
    }

    public Response<Long> tvshttl(final byte[] index, final byte[] key) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHTTL, index, key);
        return getResponse(BuilderFactory.LONG);
    }

    /**
     * Get ttl (milliseconds).
     *
     * @param index index name
     * @param key   the key
     * @return ttl
     */
    public Response<Long> tvshpttl(final String index, final String key) {
        return tvshpttl(SafeEncoder.encode(index), SafeEncoder.encode(key));
    }

    public Response<Long> tvshpttl(final byte[] index, final byte[] key) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHPTTL, index, key);
        return getResponse(BuilderFactory.LONG);
    }

    /**
     * Get abs expire time  (seconds).
     *
     * @param index index name
     * @param key   the key
     * @return abs expire time
     */
    public Response<Long> tvshexpiretime(final String index, final String key) {
        return tvshexpiretime(SafeEncoder.encode(index), SafeEncoder.encode(key));
    }

    public Response<Long> tvshexpiretime(final byte[] index, final byte[] key) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHEXPIRETIME, index, key);
        return getResponse(BuilderFactory.LONG);
    }

    /**
     * Get abs expire time  (milliseconds).
     *
     * @param index index name
     * @param key   the key
     * @return abs expire time
     */
    public Response<Long> tvshpexpiretime(final String index, final String key) {
        return tvshpexpiretime(SafeEncoder.encode(index), SafeEncoder.encode(key));
    }

    public Response<Long> tvshpexpiretime(final byte[] index, final byte[] key) {
        getClient(SafeEncoder.encode(index)).sendCommand(ModuleCommand.TVSHPEXPIRETIME, index, key);
        return getResponse(BuilderFactory.LONG);
    }
}
