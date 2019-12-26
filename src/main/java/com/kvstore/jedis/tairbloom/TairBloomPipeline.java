package com.kvstore.jedis.tairbloom;

import com.kvstore.jedis.ModuleCommand;
import com.kvstore.jedis.tairbloom.factory.BloomBuilderFactory;
import com.kvstore.jedis.tairbloom.params.BfinsertParams;
import com.kvstore.jedis.tairbloom.params.BfmexistParams;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.util.SafeEncoder;

import java.util.List;

import static redis.clients.jedis.Protocol.toByteArray;

/**
 * @author dwan
 * @date 2019/12/26
 */
public class TairBloomPipeline extends Pipeline {
    public Response<String> bfreserve(String key, long initCapacity, double errorRate) {
        return bfreserve(SafeEncoder.encode(key), initCapacity, errorRate);
    }

    public Response<String> bfreserve(byte[] key, long initCapacity, double errorRate) {
        getClient("").sendCommand(ModuleCommand.BFRESERVE, key, toByteArray(errorRate), toByteArray(initCapacity));
        return getResponse(BuilderFactory.STRING);
    }

    public Response<Boolean> bfadd(String key, String item) {
        return bfadd(SafeEncoder.encode(key), SafeEncoder.encode(item));
    }

    public Response<Boolean> bfadd(byte[] key, byte[] item) {
        getClient("").sendCommand(ModuleCommand.BFADD, key, item);
        return getResponse(BuilderFactory.BOOLEAN);
    }

    public Response<Boolean[]> bfmadd(String... args) {
        getClient("").sendCommand(ModuleCommand.BFMADD, args);
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfmadd(byte[]... args) {
        getClient("").sendCommand(ModuleCommand.BFMADD, args);
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean> bfexists(String key, String value) {
        getClient("").sendCommand(ModuleCommand.BFEXISTS, key, value);
        return getResponse(BuilderFactory.BOOLEAN);
    }

    public Response<Boolean> bfexists(byte[] key, byte[] value) {
        getClient("").sendCommand(ModuleCommand.BFEXISTS, key, value);
        return getResponse(BuilderFactory.BOOLEAN);
    }

    public Response<Boolean[]> bfmexists(String key, String... value) {
        BfmexistParams params = new BfmexistParams();
        getClient("").sendCommand(ModuleCommand.BFMEXISTS, params.getByteParams(key, value));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfmexists(byte[] key, byte[]... value) {
        BfmexistParams params = new BfmexistParams();
        getClient("").sendCommand(ModuleCommand.BFMEXISTS, params.getByteParams(key, value));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(String key, String initCapacityTag, long initCapacity, String errorRateTag, Double errorRate, String itemTag, String... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, initCapacityTag, String.valueOf(initCapacity), errorRateTag, String.valueOf(errorRate), itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(String key, String nocreateTag, String itemTag, String... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, nocreateTag, itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(String key, String itemTag, String... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(byte[] key, byte[] initCapacityTag, long initCapacity, byte[] errorRateTag, Double errorRate, byte[] itemTag, byte[]... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, initCapacityTag, toByteArray(initCapacity), errorRateTag, toByteArray(errorRate), itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(byte[] key, byte[] nocreateTag, byte[] itemTag, byte[]... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, nocreateTag, itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<Boolean[]> bfinsert(byte[] key, byte[] itemTag, byte[]... items) {
        BfinsertParams params = new BfinsertParams();
        byte[][] metadata = params.getByteParamsMeta(key, itemTag);
        getClient("").sendCommand(ModuleCommand.BFINSERT, params.getByteParams(metadata, items));
        return getResponse(BloomBuilderFactory.BFMADD_RESULT_BOOLEAN_LIST);
    }

    public Response<List<String>> bfdebug(String key) {
        getClient("").sendCommand(ModuleCommand.BFDEBUG, key);
        return getResponse(BuilderFactory.STRING_LIST);
    }

    public Response<List<String>> bfdebug(byte[] key) {
        getClient("").sendCommand(ModuleCommand.BFDEBUG, key);
        return getResponse(BuilderFactory.STRING_LIST);
    }
}
