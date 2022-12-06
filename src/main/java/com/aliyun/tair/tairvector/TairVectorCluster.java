package com.aliyun.tair.tairvector;

import com.aliyun.tair.ModuleCommand;
import com.aliyun.tair.tairvector.factory.VectorBuilderFactory;
import com.aliyun.tair.tairvector.params.DistanceMethod;
import com.aliyun.tair.tairvector.params.HscanParams;
import com.aliyun.tair.tairvector.params.IndexAlgorithm;
import com.aliyun.tair.util.JoinParameters;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static redis.clients.jedis.Protocol.toByteArray;

public class TairVectorCluster implements VectorShard {
    private JedisCluster jc;

    public TairVectorCluster(JedisCluster jc) {
        this.jc = jc;
    }

    @Override
    public void quit() {
        if (jc != null) {
            jc.close();
        }
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
    @Override
    public String tvscreateindex(final String index, int dims, IndexAlgorithm algorithm, DistanceMethod method, final String... attrs) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSCREATEINDEX, JoinParameters.joinParameters(SafeEncoder.encode(index), toByteArray(dims), SafeEncoder.encode(algorithm.name()), SafeEncoder.encode(method.name()), SafeEncoder.encodeMany(attrs)));
        return BuilderFactory.STRING.build(obj);
    }

    @Override
    public byte[] tvscreateindex(byte[] index, int dims, IndexAlgorithm algorithm, DistanceMethod method, final byte[]... params) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSCREATEINDEX, JoinParameters.joinParameters(index, toByteArray(dims), SafeEncoder.encode(algorithm.name()), SafeEncoder.encode(method.name()), params));
        return BuilderFactory.BYTE_ARRAY.build(obj);
    }

    /**
     * TVS.GETINDEX TVS.GETINDEX index_name
     * <p>
     * get index schema info, including: index_name, algorithm, distance_method, data_count, ...
     *
     * @param index index name
     * @return Success: string_map, Fail:  empty
     */
    @Override
    public Map<String, String> tvsgetindex(final String index) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSGETINDEX, SafeEncoder.encode(index));
        return BuilderFactory.STRING_MAP.build(obj);
    }

    @Override
    public Map<byte[], byte[]> tvsgetindex(byte[] index) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSGETINDEX, index);
        return BuilderFactory.BYTE_ARRAY_MAP.build(obj);
    }

    /**
     * TVS.DELINDEX TVS.DELINDEX index_name
     * <p>
     * delete index
     *
     * @param index index name
     * @return Success: 1; Fail: 0
     */
    @Override
    public Long tvsdelindex(final String index) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSDELINDEX, SafeEncoder.encode(index));
        return BuilderFactory.LONG.build(obj);
    }

    @Override
    public Long tvsdelindex(byte[] index) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSDELINDEX, index);
        return BuilderFactory.LONG.build(obj);
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
    @Override
    public Long tvshset(final String index, final String entityid, final String vector, final String... params) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSHSET, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG), SafeEncoder.encode(vector), SafeEncoder.encodeMany(params)));
        return BuilderFactory.LONG.build(obj);
    }

    @Override
    public Long tvshset(byte[] index, byte[] entityid, byte[] vector, final byte[]... params) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSHSET, JoinParameters.joinParameters(index, entityid, SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG), vector, params));
        return BuilderFactory.LONG.build(obj);
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
    @Override
    public Map<String, String> tvshgetall(final String index, final String entityid) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSHGETALL, SafeEncoder.encode(index), SafeEncoder.encode(entityid));
        return BuilderFactory.STRING_MAP.build(obj);
    }

    @Override
    public Map<byte[], byte[]> tvshgetall(byte[] index, byte[] entityid) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSHGETALL, index, entityid);
        return BuilderFactory.BYTE_ARRAY_MAP.build(obj);
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
    @Override
    public List<String> tvshmget(final String index, final String entityid, final String... attrs) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSHMGET, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encodeMany(attrs)));
        return BuilderFactory.STRING_LIST.build(obj);
    }

    @Override
    public List<byte[]> tvshmget(byte[] index, byte[] entityid, byte[]... attrs) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSHMGET, JoinParameters.joinParameters(index, entityid, attrs));
        return BuilderFactory.BYTE_ARRAY_LIST.build(obj);
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
    @Override
    public Long tvsdel(final String index, final String entityid) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSDEL, SafeEncoder.encode(index), SafeEncoder.encode(entityid));
        return BuilderFactory.LONG.build(obj);
    }

    @Override
    public Long tvsdel(byte[] index, byte[] entityid) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSDEL, index, entityid);
        return BuilderFactory.LONG.build(obj);
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
    @Override
    public Long tvshdel(final String index, final String entityid, final String... attrs) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSHDEL, JoinParameters.joinParameters(SafeEncoder.encode(index), SafeEncoder.encode(entityid), SafeEncoder.encodeMany(attrs)));
        return BuilderFactory.LONG.build(obj);
    }

    @Override
    public Long tvshdel(byte[] index, byte[] entityid, byte[]... attrs) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSHDEL, JoinParameters.joinParameters(index, entityid,attrs));
        return BuilderFactory.LONG.build(obj);
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
    @Override
    public ScanResult<String> tvsscan(final String index, Long cursor, HscanParams params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(SafeEncoder.encode(index));
        args.add(toByteArray(cursor));
        args.addAll(params.getParams());
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSSCAN, args.toArray(new byte[args.size()][]));
        return VectorBuilderFactory.SCAN_CURSOR_STRING.build(obj);
    }

    @Override
    public ScanResult<byte[]> tvsscan(byte[] index, Long cursor, HscanParams params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(index);
        args.add(toByteArray(cursor));
        args.addAll(params.getParams());
        Object obj = jc.sendCommand(index, ModuleCommand.TVSSCAN, args.toArray(new byte[args.size()][]));
        return VectorBuilderFactory.SCAN_CURSOR_BYTE.build(obj);
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
     * @return VectorBuilderFactory.Knn<>
     */
    @Override
    public VectorBuilderFactory.Knn<String> tvsknnsearch(final String index, Long topn, final String vector, final String... params) {
        return tvsknnsearchfilter(index, topn, vector, "", params);
    }

    @Override
    public VectorBuilderFactory.Knn<byte[]> tvsknnsearch(byte[] index, Long topn, byte[] vector, final byte[]... params) {
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
    @Override
    public VectorBuilderFactory.Knn<String> tvsknnsearchfilter(final String index, Long topn, final String vector, final String pattern, final String... params) {
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSKNNSEARCH, JoinParameters.joinParameters(SafeEncoder.encode(index), toByteArray(topn),
                SafeEncoder.encode(vector), SafeEncoder.encode(pattern), SafeEncoder.encodeMany(params)));
        return VectorBuilderFactory.STRING_KNN_RESULT.build(obj);
    }

    @Override
    public VectorBuilderFactory.Knn<byte[]> tvsknnsearchfilter(byte[] index, Long topn, byte[] vector, byte[] pattern, final byte[]... params) {
        Object obj = jc.sendCommand(index, ModuleCommand.TVSKNNSEARCH, JoinParameters.joinParameters(index, toByteArray(topn), vector, pattern, params));
        return VectorBuilderFactory.BYTE_KNN_RESULT.build(obj);
    }

    /**
     * TVS.MKNNSEARCH TVS.MKNNSEARCH index_name topn vector [vector...]
     *
     * @param index   index name
     * @param topn    topn for each vector
     * @param vectors vector list
     * @param params  for HNSW, params include:
     *                ef_search     range [0, 1000]
     * @return Collection<>
     */
    @Override
    public Collection<VectorBuilderFactory.Knn<String>> tvsmknnsearch(final String index, Long topn, Collection<String> vectors, final String... params) {
        return tvsmknnsearchfilter(index, topn, vectors, "", params);
    }

    @Override
    public Collection<VectorBuilderFactory.Knn<byte[]>> tvsmknnsearch(byte[] index, Long topn, Collection<byte[]> vectors, final byte[]... params) {
        return tvsmknnsearchfilter(index, topn, vectors, SafeEncoder.encode(""), params);
    }

    /**
     * TVS.MKNNSEARCH TVS.MKNNSEARCH index_name topn vector [vector...] pattern
     *
     * @param index   index name
     * @param topn    topn for each vector
     * @param vectors vector list
     * @param pattern support +, -，>, <, !=， ,()，&&, ||, !, ==
     * @param params  for HNSW, params include:
     *                ef_search     range [0, 1000]
     * @return Collection<>
     */
    @Override
    public Collection<VectorBuilderFactory.Knn<String>> tvsmknnsearchfilter(final String index, Long topn, Collection<String> vectors, final String pattern, final String... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(SafeEncoder.encode(index));
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors.stream().map(vector -> SafeEncoder.encode(vector)).collect(Collectors.toList()));
        args.add(SafeEncoder.encode(pattern));
        args.addAll(Arrays.stream(params).map(str -> SafeEncoder.encode(str)).collect(Collectors.toList()));
        Object obj = jc.sendCommand(SafeEncoder.encode(index), ModuleCommand.TVSMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return VectorBuilderFactory.STRING_KNN_BATCH_RESULT.build(obj);
    }

    @Override
    public Collection<VectorBuilderFactory.Knn<byte[]>> tvsmknnsearchfilter(byte[] index, Long topn, Collection<byte[]> vectors, byte[] pattern, final byte[]... params) {
        final List<byte[]> args = new ArrayList<byte[]>();
        args.add(index);
        args.add(toByteArray(topn));
        args.add(toByteArray(vectors.size()));
        args.addAll(vectors);
        args.add(pattern);
        args.addAll(Arrays.stream(params).collect(Collectors.toList()));
        Object obj = jc.sendCommand(index, ModuleCommand.TVSMKNNSEARCH, args.toArray(new byte[args.size()][]));
        return VectorBuilderFactory.BYTE_KNN_BATCH_RESULT.build(obj);
    }


}
