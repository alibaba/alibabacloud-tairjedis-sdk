package com.aliyun.tair;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public enum ModuleCommand implements ProtocolCommand {
    // com.kvstore.jedis.TairDoc command
    JSONDEL("JSON.DEL"),
    JSONGET("JSON.GET"),
    JSONMGET("JSON.MGET"),
    JSONSET("JSON.SET"),
    JSONTYPE("JSON.TYPE"),
    JSONNUMINCRBY("JSON.NUMINCRBY"),
    JSONSTRAPPEND("JSON.STRAPPEND"),
    JSONSTRLEN("JSON.STRLEN"),
    JSONARRAPPEND("JSON.ARRAPPEND"),
    JSONARRPOP("JSON.ARRPOP"),
    JSONARRINSERT("JSON.ARRINSERT"),
    JSONARRLEN("JSON.ARRLEN"),
    JSONARRTRIM("JSON.ARRTRIM"),

    // TairHash command
    EXHSET("EXHSET"),
    EXHSETNX("EXHSETNX"),
    EXHMSET("EXHMSET"),
    EXHMSETWITHOPTS("EXHMSETWITHOPTS"),
    EXHPEXPIREAT("EXHPEXPIREAT"),
    EXHPEXPIRE("EXHPEXPIRE"),
    EXHEXPIREAT("EXHEXPIREAT"),
    EXHEXPIRE("EXHEXPIRE"),
    EXHPTTL("EXHPTTL"),
    EXHTTL("EXHTTL"),
    EXHVER("EXHVER"),
    EXHSETVER("EXHSETVER"),
    EXHINCRBY("EXHINCRBY"),
    EXHINCRBYFLOAT("EXHINCRBYFLOAT"),
    EXHGET("EXHGET"),
    EXHGETWITHVER("EXHGETWITHVER"),
    EXHMGET("EXHMGET"),
    EXHDEL("EXHDEL"),
    EXHLEN("EXHLEN"),
    EXHEXISTS("EXHEXISTS"),
    EXHSTRLEN("EXHSTRLEN"),
    EXHKEYS("EXHKEYS"),
    EXHVALS("EXHVALS"),
    EXHGETALL("EXHGETALL"),
    EXHMGETWITHVER("EXHMGETWITHVER"),
    EXHSCAN("EXHSCAN"),

    // CAS & CAD
    CAS("CAS"),
    CAD("CAD"),

    // com.kvstore.jedis.TairString command
    EXSET("EXSET"),
    EXGET("EXGET"),
    EXSETVER("EXSETVER"),
    EXINCRBY("EXINCRBY"),
    EXINCRBYFLOAT("EXINCRBYFLOAT"),
    EXCAS("EXCAS"),
    EXCAD("EXCAD"),

    // TairGis command
    GISADD("GIS.ADD"),
    GISGET("GIS.GET"),
    GISDEL("GIS.DEL"),
    GISSEARCH("GIS.SEARCH"),
    GISCONTAINS("GIS.CONTAINS"),
    GISINTERSECTS("GIS.INTERSECTS"),
    GISGETALL("GIS.GETALL"),

    // TairBloom command
    BFADD("BF.ADD"),
    BFMADD("BF.MADD"),
    BFEXISTS("BF.EXISTS"),
    BFMEXISTS("BF.MEXISTS"),
    BFINSERT("BF.INSERT"),
    BFRESERVE("BF.RESERVE"),
    BFDEBUG("BF.DEBUG"),

    // TairTs command
    // double value
    TSPCREATE("EXTS.P.CREATE"),
    TSSCREATE("EXTS.S.CREATE"),
    TSSALTER("EXTS.S.ALTER"),
    TSSADD("EXTS.S.ADD"),
    TSSMADD("EXTS.S.MADD"),
    TSSINCRBY("EXTS.S.INCRBY"),
    TSSMINCRBY("EXTS.S.MINCRBY"),
    TSSDEL("EXTS.S.DEL"),
    TSSGET("EXTS.S.GET"),
    TSSINFO("EXTS.S.INFO"),
    TSSQUERYINDEX("EXTS.S.QUERYINDEX"),
    TSSRANGE("EXTS.S.RANGE"),
    TSSMRANGE("EXTS.S.MRANGE"),
    TSPRANGE("EXTS.P.RANGE"),
    TSSRAWMODIFY("EXTS.S.RAW_MODIFY"),
    TSSRAWMULTIMODIFY("EXTS.S.RAW_MMODIFY"),
    TSSRAWINCRBY("EXTS.S.RAW_INCRBY"),
    TSSRAWMULTIINCRBY("EXTS.S.RAW_MINCRBY"),

    //string value
    TSPCREATESTR("TSSTRING.P.CREATE"),
    TSSCREATESTR("TSSTRING.S.CREATE"),
    TSSALTERSTR("TSSTRING.S.ALTER"),
    TSSADDSTR("TSSTRING.S.ADD"),
    TSSMADDSTR("TSSTRING.S.MADD"),
    TSSDELSTR("TSSTRING.S.DEL"),
    TSSGETSTR("TSSTRING.S.GET"),
    TSSINFOSTR("TSSTRING.S.INFO"),
    TSSQUERYINDEXSTR("TSSTRING.S.QUERYINDEX"),
    TSSRANGESTR("TSSTRING.S.RANGE"),
    TSSMRANGESTR("TSSTRING.S.MRANGE"),

    // TairCpc command
    CPCUPDATE("CPC.UPDATE"),
    CPCMERGE("CPC.MERGE"),
    CPCESTIMATE("CPC.ESTIMATE"),
    CPCUPDATE2JUD("CPC.UPDATE2JUD"),
    CPCUPDATE2EST("CPC.UPDATE2EST"),
    CPCMUPDATE("CPC.M.UPDATE"),
    CPCMUPDATE2EST("CPC.M.UPDATE2EST"),
    CPCMUPDATE2JUD("CPC.M.UPDATE2JUD"),
    CPCMUPDATE2ESTWITHKEY("CPC.M.UPDATE2EST.WITHKEY"),
//    CPCMUPDATE2JUDWITHKEY("CPC.M.UPDATE2JUD.WITHKEY"),

    CPCARRAYUPDATE("CPC.ARRAY.UPDATE"),
    CPCARRAYMERGE("CPC.ARRAY.MERGE"),
    CPCARRAYESTIMATE("CPC.ARRAY.ESTIMATE"),
    CPCARRAYESTIMATERANGE("CPC.ARRAY.ESTIMATE.RANGE"),
    CPCARRAYESTIMATERANGEMERGE("CPC.ARRAY.ESTIMATE.RANGE.MERGE"),
    CPCARRAYUPDATE2EST("CPC.ARRAY.UPDATE2EST"),
    CPCARRAYUPDATE2JUD("CPC.ARRAY.UPDATE2JUD"),
    CPCMARRAYUPDATE("CPC.M.ARRAY.UPDATE"),
    CPCMARRAYUPDATE2EST("CPC.M.ARRAY.UPDATE2EST"),
    CPCMARRAYUPDATE2JUD("CPC.M.ARRAY.UPDATE2JUD"),
    CPCMARRAYUPDATE2ESTWITHKEY("CPC.M.ARRAY.UPDATE2EST.WITHKEY"),
    CPCMARRAYUPDATE2JUDWITHKEY("CPC.M.ARRAY.UPDATE2JUD.WITHKEY");


    private final byte[] raw;

    ModuleCommand(String alt) {
        raw = SafeEncoder.encode(alt);
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
