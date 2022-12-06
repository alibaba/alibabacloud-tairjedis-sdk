package com.aliyun.tair.tests.tairvector;

import com.aliyun.tair.tairvector.factory.VectorBuilderFactory;
import com.aliyun.tair.tairvector.params.DistanceMethod;
import com.aliyun.tair.tairvector.params.HscanParams;
import com.aliyun.tair.tairvector.params.IndexAlgorithm;
import org.junit.Test;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TairVectorShardTest extends TairVectorTestBase {

    final String index = "default_index";
    final int dims = 8;
    final IndexAlgorithm algorithm = IndexAlgorithm.HNSW;
    final DistanceMethod method = DistanceMethod.IP;
    final long dbid = 2;
    final List<String> index_params = Arrays.asList("ef_construct", "100", "M", "16");
    final List<String> index_params_with_dataType = Arrays.asList("ef_construct", "100", "M", "16","data_type","BINARY");
    final List<String> ef_params = Arrays.asList("ef_search", "100");

    private String get_shard_index_name(String index_name){
        return index_name+"_0";
    }
    /**
     * 127.0.0.1:6379> tvs.createindex default_index 8 HNSW IP
     */
    private void tvs_create_index(int dims, IndexAlgorithm algorithm, DistanceMethod method, final String... attr) {
        tairVectorDistribute.tvsdelindex(index);
        assertEquals("OK", tairVectorDistribute.tvscreateindex(index, dims, algorithm, method, attr));
    }

    private void check_index(int dims, IndexAlgorithm algorithm, DistanceMethod method, final String... attr) {
        List<Map<String, String>> objs = tairVectorDistribute.tvsgetindex(index);
        if (!objs.isEmpty()) {
            long result = tairVectorDistribute.tvsdelindex(index);
            assertEquals(result, 2);
        }
        assertEquals("OK", tairVectorDistribute.tvscreateindex(index, dims, algorithm, method, attr));
    }

    private void tvs_hset(final String entityid, final String vector, final String param_k, final String param_v) {
        long result = tairVectorDistribute.tvshset(index, entityid, vector, param_k, param_v);
        assertEquals(result, 2);
    }

    private void tvs_hset(byte[] entityid, byte[] vector, byte[] param_k, byte[] param_v) {
        long result = tairVectorDistribute.tvshset(SafeEncoder.encode(index), entityid, vector, param_k, param_v);
        assertTrue(result <= 2);
    }

    private long tvs_del_entity(String entity) {
        return tairVectorDistribute.tvsdel(index, entity);
    }

    private long tvs_del_entity(byte[] entity) {
        return tairVectorDistribute.tvsdel(SafeEncoder.encode(index), entity);
    }

    @Test
    public void tvs_create_index() {
        tvs_del_index();
        assertEquals("OK", tairVectorDistribute.tvscreateindex(index, dims, algorithm, method, index_params.toArray(new String[0])));
        try {
            tairVectorDistribute.tvscreateindex(SafeEncoder.encode(index), dims, algorithm, method);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "ERR duplicated index key");
        }
    }


    @Test
    public void tvs_create_index_with_datatype() {
        tvs_del_index();
        try {
            tairVectorDistribute.tvscreateindex(index, dims, algorithm, method, index_params_with_dataType.toArray(new String[0]));
        }catch (Exception e){
            assertEquals(e.getMessage(), "ERR index parameters invalid");
        }
        assertEquals("OK", tairVectorDistribute.tvscreateindex(index, dims, algorithm, DistanceMethod.JACCARD, index_params_with_dataType.toArray(new String[0])));
        try {
            tairVectorDistribute.tvscreateindex(SafeEncoder.encode(index), dims, algorithm, method);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "ERR duplicated index key");
        }
    }

    @Test
    public void tvs_create_index_withoption_args() {
        tvs_del_index();
        assertEquals("OK", tairVectorDistribute.tvscreateindex(index, dims, algorithm, method,
                "ef_construct", "50", "M", "20"));
        List<Map<String, String>> schema = tairVectorDistribute.tvsgetindex(index);
        assertEquals(String.valueOf(50), schema.get(0).get("ef_construct"));
        assertEquals(String.valueOf(20), schema.get(0).get("M"));
    }

    /**
     * 127.0.0.1:6379> tvs.getindex default_index
     */
    @Test
    public void tvs_get_index() {
        tvs_create_index(dims, algorithm, method, index_params.toArray(new String[0]));

        List<Map<String, String>> schema = tairVectorDistribute.tvsgetindex(index);
        assertEquals(get_shard_index_name(index), schema.get(0).get("index_name"));
        assertEquals(algorithm.name(), schema.get(0).get("algorithm"));
        assertEquals(method.name(), schema.get(0).get("distance_method"));
        assertEquals(String.valueOf(0), schema.get(0).get("data_count"));


        List<Map<byte[], byte[]>> schema_bytecode = tairVectorDistribute.tvsgetindex(SafeEncoder.encode(index));
        Iterator<Map.Entry<byte[], byte[]>> entries = schema_bytecode.get(0).entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<byte[], byte[]> entry = entries.next();
            assertEquals(schema.get(0).get(SafeEncoder.encode(entry.getKey())), SafeEncoder.encode(entry.getValue()));
        }
    }

    @Test
    public void tvs_del_index() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));

        List<Map<String, String>> schema = tairVectorDistribute.tvsgetindex(index);
        assertEquals(get_shard_index_name(index), schema.get(0).get("index_name"));
        assertEquals(algorithm.name(), schema.get(0).get("algorithm"));
        assertEquals(method.name(), schema.get(0).get("distance_method"));
        assertEquals(String.valueOf(0), schema.get(0).get("data_count"));

        long result = tairVectorDistribute.tvsdelindex(index);
        assertEquals(result, 2);
        long result_byte = tairVectorDistribute.tvsdelindex(SafeEncoder.encode(index));
        assertEquals(result_byte, 0);
    }

    @Test
    public void tvs_hset_data_bin() {
        check_index(dims, algorithm, DistanceMethod.JACCARD, index_params_with_dataType.toArray(new String[0]));
        tvs_del_entity("fourth_entity_knn");
        tvs_hset("fourth_entity_knn", "[1,1,0,0,1,0,1,0]", "name", "sammy");
        tvs_del_entity("ten_entity_knn");
        tvs_hset(SafeEncoder.encode("ten_entity_knn"), SafeEncoder.encode("[1,1,0,0,1,0,1,0]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));
    }

    @Test
    public void tvs_hgetall_data_bin() {
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");
        tvs_hset("first_entity_knn", "[1,1,1,1,0,0,0,0]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[1,1,1,1,0,0,0,0]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        Map<String, String> entity_string = tairVectorDistribute.tvshgetall(index, "first_entity_knn");
        assertEquals("[1,1,1,1,0,0,0,0]", entity_string.get(VectorBuilderFactory.VECTOR_TAG));
        assertEquals("sammy", entity_string.get("name"));

        Map<byte[], byte[]> entity_byte = tairVectorDistribute.tvshgetall(SafeEncoder.encode(index), SafeEncoder.encode("first_entity_knn"));
        assertEquals("[1,1,1,1,0,0,0,0]", SafeEncoder.encode(entity_byte.get(SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG))));
        assertEquals("sammy", SafeEncoder.encode(entity_byte.get(SafeEncoder.encode("name"))));
    }

    @Test
    public void tvs_hset() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("fourth_entity_knn");
        tvs_hset("fourth_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_del_entity("ten_entity_knn");
        tvs_hset(SafeEncoder.encode("ten_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));
    }

    @Test
    public void tvs_hgetall() {
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");
        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        Map<String, String> entity_string = tairVectorDistribute.tvshgetall(index, "first_entity_knn");
        assertEquals("[0.12,0.23,0.56,0.67,0.78,0.89,0.01,0.89]", entity_string.get(VectorBuilderFactory.VECTOR_TAG));
        assertEquals("sammy", entity_string.get("name"));

        Map<byte[], byte[]> entity_byte = tairVectorDistribute.tvshgetall(SafeEncoder.encode(index), SafeEncoder.encode("first_entity_knn"));
        assertEquals("[0.12,0.23,0.56,0.67,0.78,0.89,0.01,0.89]", SafeEncoder.encode(entity_byte.get(SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG))));
        assertEquals("sammy", SafeEncoder.encode(entity_byte.get(SafeEncoder.encode("name"))));
    }

    @Test
    public void tvs_hmgetall() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");
        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        List<String> entity_string = tairVectorDistribute.tvshmget(index, "first_entity_knn", VectorBuilderFactory.VECTOR_TAG, "name");
        assertEquals("[0.12,0.23,0.56,0.67,0.78,0.89,0.01,0.89]", entity_string.get(0));
        assertEquals("sammy", entity_string.get(1));

        List<byte[]> entity_byte = tairVectorDistribute.tvshmget(SafeEncoder.encode(index), SafeEncoder.encode("first_entity_knn"),
                SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG), SafeEncoder.encode("name"));
        assertEquals("[0.12,0.23,0.56,0.67,0.78,0.89,0.01,0.89]", SafeEncoder.encode(entity_byte.get(0)));
        assertEquals("sammy", SafeEncoder.encode(entity_byte.get(1)));
    }

    @Test
    public void tvs_del() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long count_string = tvs_del_entity("first_entity_knn");
        assertEquals(1, count_string);

        long count_byte = tvs_del_entity(SafeEncoder.encode("second_entity_knn"));
        assertEquals(1, count_byte);
    }

    @Test
    public void tvs_hdel() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long count_string = tairVectorDistribute.tvshdel(index, "first_entity_knn", "name");
        assertEquals(1, count_string);
        Map<String, String> entity_string = tairVectorDistribute.tvshgetall(index, "first_entity_knn");
        assertTrue(entity_string.size() == 1 && (!entity_string.containsKey("name")));

        long count_byte = tairVectorDistribute.tvshdel(SafeEncoder.encode(index), SafeEncoder.encode("second_entity_knn"),
                SafeEncoder.encode(VectorBuilderFactory.VECTOR_TAG));
        //assertEquals(1, count_byte);
        Map<String, String> entity_byte = tairVectorDistribute.tvshgetall(index, "second_entity_knn");
        assertTrue(entity_byte.size() == 1 && (!entity_byte.containsKey(VectorBuilderFactory.VECTOR_TAG)));
    }

    @Test
    public void tvs_scan() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");

        tvs_del_entity("five_entity_knn");
        tvs_hset(SafeEncoder.encode("five_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long cursor = 0;
        HscanParams hscanParams = new HscanParams();
        hscanParams.count(1);
        hscanParams.match("*entit*");
        List<ScanResult<String>> result_string = tairVectorDistribute.tvsscan(index, cursor, hscanParams);
        assert (result_string.get(0).getResult().size() >= 1);

        List<ScanResult<byte[]>> entity_byte = tairVectorDistribute.tvsscan(SafeEncoder.encode(index), cursor, hscanParams);
        assert (entity_byte.get(0).getResult().size() >= 1);
    }

    @Test
    public void tvs_knnsearch() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity(SafeEncoder.encode("second_entity_knn"));

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 2L;
        VectorBuilderFactory.Knn<String> result_string = tairVectorDistribute.tvsknnsearch(index, topn, "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]");
        assertEquals(2, result_string.getKnnResults().size());

        VectorBuilderFactory.Knn<byte[]> entity_byte = tairVectorDistribute.tvsknnsearch(SafeEncoder.encode(index), topn,
                SafeEncoder.encode("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]"));
        assertEquals(2, entity_byte.getKnnResults().size());
    }

    @Test
    public void tvs_knnsearch_with_databin() {
        check_index(dims, algorithm, DistanceMethod.JACCARD, index_params_with_dataType.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity(SafeEncoder.encode("second_entity_knn"));

        tvs_hset("first_entity_knn", "[1,1,1,1,0,0,0,0]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[1,1,1,1,0,0,0,0]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 2L;
        VectorBuilderFactory.Knn<String> result_string = tairVectorDistribute.tvsknnsearch(index, topn, "[1,1,1,1,0,0,0,0]");
        assertEquals(2, result_string.getKnnResults().size());

        VectorBuilderFactory.Knn<byte[]> entity_byte = tairVectorDistribute.tvsknnsearch(SafeEncoder.encode(index), topn,
                SafeEncoder.encode("[1,1,1,1,0,0,0,0]"));
        assertEquals(2, entity_byte.getKnnResults().size());
    }

    @Test
    public void tvs_knnsearch_with_filter() {
        tairVectorDistribute.tvsdelindex(SafeEncoder.encode(index));

        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity(SafeEncoder.encode("second_entity_knn"));

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 10L;
        VectorBuilderFactory.Knn<String> result_string = tairVectorDistribute.tvsknnsearchfilter(index, topn,
                "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name == \"sammy\"");
        assertEquals(1, result_string.getKnnResults().size());

        VectorBuilderFactory.Knn<byte[]> entity_byte = tairVectorDistribute.tvsknnsearchfilter(SafeEncoder.encode(index), topn,
                SafeEncoder.encode("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]"), SafeEncoder.encode("name != \"sammy\""));
        assertEquals(1, entity_byte.getKnnResults().size());
    }

    @Test
    public void tvs_knnsearch_with_params() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity(SafeEncoder.encode("second_entity_knn"));

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 2L;
        VectorBuilderFactory.Knn<String> result_string = tairVectorDistribute.tvsknnsearch(index, topn,
                "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", ef_params.toArray(new String[0]));
        assertEquals(2, result_string.getKnnResults().size());

        VectorBuilderFactory.Knn<byte[]> entity_byte = tairVectorDistribute.tvsknnsearch(SafeEncoder.encode(index), topn,
                SafeEncoder.encode("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]"),
                SafeEncoder.encodeMany(ef_params.toArray(new String[0])));
        assertEquals(2, entity_byte.getKnnResults().size());
    }

    @Test
    public void tvs_mknnsearch() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 2L;
        List<String> vectors = Arrays.asList("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]");
        Collection<VectorBuilderFactory.Knn<String>> result_string = tairVectorDistribute.tvsmknnsearch(index, topn, vectors);
        result_string.forEach(result -> {
            assertEquals(2, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("string: %s\n", one.toString()));


        Collection<VectorBuilderFactory.Knn<byte[]>> result_byte = tairVectorDistribute.tvsmknnsearch(SafeEncoder.encode(index), topn,
                vectors.stream().map(item -> SafeEncoder.encode(item)).collect(Collectors.toList()));
        result_byte.forEach(result -> {
            assertEquals(2, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("byte: %s\n", one.toString()));
    }

    @Test
    public void tvs_mknnsearch_filter() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 1L;
        List<String> vectors = Arrays.asList("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]");
        String pattern = "name == \"no-sammy\"";
        Collection<VectorBuilderFactory.Knn<String>> result_string = tairVectorDistribute.tvsmknnsearchfilter(index, topn, vectors, pattern);
        result_string.forEach(result -> {
            assertEquals(0, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("string: %s\n", one.toString()));


        Collection<VectorBuilderFactory.Knn<byte[]>> result_byte = tairVectorDistribute.tvsmknnsearchfilter(SafeEncoder.encode(index),
                topn, vectors.stream().map(item -> SafeEncoder.encode(item)).collect(Collectors.toList()), SafeEncoder.encode(pattern));
        result_byte.forEach(result -> {
            assertEquals(0, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("byte: %s\n", one.toString()));
    }

    @Test
    public void tvs_mknnsearch_with_params() {
        check_index(dims, algorithm, method, index_params.toArray(new String[0]));
        tvs_del_entity("first_entity_knn");
        tvs_del_entity("second_entity_knn");

        tvs_hset("first_entity_knn", "[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "name", "sammy");
        tvs_hset(SafeEncoder.encode("second_entity_knn"), SafeEncoder.encode("[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]"),
                SafeEncoder.encode("name"), SafeEncoder.encode("tiddy"));

        long topn = 2L;
        List<String> vectors = Arrays.asList("[0.12, 0.23, 0.56, 0.67, 0.78, 0.89, 0.01, 0.89]", "[0.22, 0.33, 0.66, 0.77, 0.88, 0.89, 0.11, 0.89]");
        Collection<VectorBuilderFactory.Knn<String>> result_string = tairVectorDistribute.tvsmknnsearch(index, topn, vectors,
                ef_params.toArray(new String[0]));
        result_string.forEach(result -> {
            assertEquals(2, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("string: %s\n", one.toString()));


        Collection<VectorBuilderFactory.Knn<byte[]>> result_byte = tairVectorDistribute.tvsmknnsearch(SafeEncoder.encode(index), topn,
                vectors.stream().map(item -> SafeEncoder.encode(item)).collect(Collectors.toList()),
                SafeEncoder.encodeMany(ef_params.toArray(new String[0])));
        result_byte.forEach(result -> {
            assertEquals(2, result.getKnnResults().size());
        });
        result_string.forEach(one -> System.out.printf("byte: %s\n", one.toString()));
    }
}
