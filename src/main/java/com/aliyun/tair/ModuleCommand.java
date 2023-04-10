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
    EXHSCANUNORDER("EXHSCANUNORDER"),

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
    EXAPPEND("EXAPPEND"),
    EXPREPEND("EXPREPEND"),
    EXGAE("EXGAE"),

    // TairGis command
    GISADD("GIS.ADD"),
    GISGET("GIS.GET"),
    GISDEL("GIS.DEL"),
    GISSEARCH("GIS.SEARCH"),
    GISCONTAINS("GIS.CONTAINS"),
    GISWITHIN("GIS.WITHIN"),
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
    TSSRANGESPECIFIEDKEYS("EXTS.S.RANGE.KEYS"),
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
    CPCARRAYESTIMATERANGESUM("CPC.ARRAY.ESTIMATE.RANGE.SUM"),
    CPCARRAYESTIMATERANGEMERGE("CPC.ARRAY.ESTIMATE.RANGE.MERGE"),
    CPCARRAYESTIMATETIMEMERGE("CPC.ARRAY.ESTIMATE.TIME.MERGE"),
    CPCARRAYUPDATE2EST("CPC.ARRAY.UPDATE2EST"),
    CPCARRAYUPDATE2JUD("CPC.ARRAY.UPDATE2JUD"),
    CPCMARRAYUPDATE("CPC.M.ARRAY.UPDATE"),
    CPCMARRAYUPDATE2EST("CPC.M.ARRAY.UPDATE2EST"),
    CPCMARRAYUPDATE2JUD("CPC.M.ARRAY.UPDATE2JUD"),
    CPCMARRAYUPDATE2ESTWITHKEY("CPC.M.ARRAY.UPDATE2EST.WITHKEY"),
    CPCMARRAYUPDATE2JUDWITHKEY("CPC.M.ARRAY.UPDATE2JUD.WITHKEY"),

    SKETCHESGET("SKETCHES.GET"),
    SKETCHESRANGEMERGE("SKETCHES.RANGE.MERGE"),
    SKETCHESRANGE("SKETCHES.RANGE"),
    SKETCHESBATCHWRITE("SKETCHES.BATCH.WRITE"),

    SUMADD("SUM.ADD"),
    SUMSET("SUM.SET"),
    SUMGET("SUM.GET"),

    SUMARRAYADD("SUM.ARRAY.ADD"),
    SUMARRAYGET("SUM.ARRAY.GET"),
    SUMARRAYGETRANGE("SUM.ARRAY.GET.RANGE"),
    SUMARRAYGETTIMEMERGE("SUM.ARRAY.GET.TIME.MERGE"),
    SUMARRAYGETRANGEMERGE("SUM.ARRAY.GET.RANGE.MERGE"),

    MAXADD("MAX.ADD"),
    MAXSET("MAX.SET"),
    MAXGET("MAX.GET"),

    MAXARRAYADD("MAX.ARRAY.ADD"),
    MAXARRAYGET("MAX.ARRAY.GET"),
    MAXARRAYGETRANGE("MAX.ARRAY.GET.RANGE"),
    MAXARRAYGETTIMEMERGE("MAX.ARRAY.GET.TIME.MERGE"),
    MAXARRAYGETRANGEMERGE("MAX.ARRAY.GET.RANGE.MERGE"),

    MINADD("MIN.ADD"),
    MINSET("MIN.SET"),
    MINGET("MIN.GET"),

    MINARRAYADD("MIN.ARRAY.ADD"),
    MINARRAYGET("MIN.ARRAY.GET"),
    MINARRAYGETRANGE("MIN.ARRAY.GET.RANGE"),
    MINARRAYGETTIMEMERGE("MIN.ARRAY.GET.TIME.MERGE"),
    MINARRAYGETRANGEMERGE("MIN.ARRAY.GET.RANGE.MERGE"),

    FIRSTADD("FIRST.ADD"),
    FIRSTSET("FIRST.SET"),
    FIRSTGET("FIRST.GET"),

    FIRSTARRAYADD("FIRST.ARRAY.ADD"),
    FIRSTARRAYGET("FIRST.ARRAY.GET"),
    FIRSTARRAYGETRANGE("FIRST.ARRAY.GET.RANGE"),
    FIRSTARRAYGETTIMEMERGE("FIRST.ARRAY.GET.TIME.MERGE"),
    FIRSTARRAYGETRANGEMERGE("FIRST.ARRAY.GET.RANGE.MERGE"),

    LASTADD("LAST.ADD"),
    LASTSET("LAST.SET"),
    LASTGET("LAST.GET"),

    LASTARRAYADD("LAST.ARRAY.ADD"),
    LASTARRAYGET("LAST.ARRAY.GET"),
    LASTARRAYGETRANGE("LAST.ARRAY.GET.RANGE"),
    LASTARRAYGETTIMEEMERGE("LAST.ARRAY.GET.TIME.MERGE"),
    LASTARRAYGETRANGEMERGE("LAST.ARRAY.GET.RANGE.MERGE"),

    AVGADD("AVG.ADD"),
    AVGSET("AVG.SET"),
    AVGGET("AVG.GET"),

    AVGARRAYADD("AVG.ARRAY.ADD"),
    AVGARRAYGET("AVG.ARRAY.GET"),
    AVGARRAYGETRANGE("AVG.ARRAY.GET.RANGE"),
    AVGARRAYGETTIMEMERGE("AVG.ARRAY.GET.TIME.MERGE"),
    AVGARRAYGETRANGEMERGE("AVG.ARRAY.GET.RANGE.MERGE"),

    STDDEVADD("STDDEV.ADD"),
    STDDEVSET("STDDEV.SET"),
    STDDEVGET("STDDEV.GET"),

    STDDEVARRAYADD("STDDEV.ARRAY.ADD"),
    STDDEVARRAYGET("STDDEV.ARRAY.GET"),
    STDDEVARRAYGETRANGE("STDDEV.ARRAY.GET.RANGE"),
    STDDEVARRAYGETTIMEMERGE("STDDEV.ARRAY.GET.TIME.MERGE"),
    STDDEVARRAYGETRANGEMERGE("STDDEV.ARRAY.GET.RANGE.MERGE"),

    // TairZset
    EXZADD("exzadd"),
    EXZINCRBY("exzincrby"),
    EXZREM("exzrem"),
    EXZREMRANGEBYSCORE("exzremrangebyscore"),
    EXZREMRANGEBYRANK("exzremrangebyrank"),
    EXZREMRANGEBYLEX("exzremrangebylex"),
    EXZSCORE("exzscore"),
    EXZRANGE("exzrange"),
    EXZREVRANGE("exzrevrange"),
    EXZRANGEBYSCORE("exzrangebyscore"),
    EXZREVRANGEBYSCORE("exzrevrangebyscore"),
    EXZRANGEBYLEX("exzrangebylex"),
    EXZREVRANGEBYLEX("exzrevrangebylex"),
    EXZCARD("exzcard"),
    EXZRANK("exzrank"),
    EXZREVRANK("exzrevrank"),
    EXZRANKBYSCORE("exzrankbyscore"),
    EXZREVRANKBYSCORE("exzrevrankbyscore"),
    EXZCOUNT("exzcount"),
    EXZLEXCOUNT("exzlexcount"),

    // TairRoaring
    TRSETBIT("tr.setbit"),
    TRSETBITS("tr.setbits"),
    TRGETBIT("tr.getbit"),
    TRGETBITS("tr.getbits"),
    TRCLEARBITS("tr.clearbits"),
    TRRANGE("tr.range"),
    TRSETRANGE("tr.setrange"),
    TRRANGEBITARRAY("tr.rangebitarray"),
    TRAPPENDBITARRAY("tr.appendbitarray"),
    TRFLIPRANGE("tr.fliprange"),
    TRBITOP("tr.bitop"),
    TRBITOPCARD("tr.bitopcard"),
    TRBITCOUNT("tr.bitcount"),
    TRSCAN("tr.scan"),
    TRBITPOS("tr.bitpos"),
    TRMIN("tr.min"),
    TRMAX("tr.max"),
    TRLOAD("tr.load"),
    TRSTAT("tr.stat"),
    TROPTIMIZE("tr.optimize"),
    TRLOADSTRING("tr.loadstring"),
    TRAPPENDINTARRAY("tr.appendintarray"),
    TRSETINTARRAY("tr.setintarray"),
    TRSETBITARRAY("tr.setbitarray"),
    TRDIFF("tr.diff"),
    TRJACCARD("tr.jaccard"),
    TRCONTAINS("tr.contains"),
    TRRANK("tr.rank"),

    // TairSearch
    TFTMAPPINGINDEX("tft.mappingindex"),
    TFTCREATEINDEX("tft.createindex"),
    TFTUPDATEINDEX("tft.updateindex"),
    TFTADDDOC("tft.adddoc"),
    TFTMADDDOC("tft.madddoc"),
    TFTUPDATEDOC("tft.updatedoc"),
    TFTUPDATEDOCFIELD("tft.updatedocfield"),
    TFTDELDOC("tft.deldoc"),
    TFTDELALL("tft.delall"),
    TFTGETINDEX("tft.getindex"),
    TFTGETDOC("tft.getdoc"),
    TFTSEARCH("tft.search"),
    TFTMSEARCH("tft.msearch"),
    TFTEXISTS("tft.exists"),
    TFTSCANDOCID("tft.scandocid"),
    TFTDOCNUM("tft.docnum"),
    TFTINCRLONGDOCFIELD("tft.incrlongdocfield"),
    TFTINCRFLOATDOCFIELD("tft.incrfloatdocfield"),
    TFTDELDOCFIELD("tft.deldocfield"),
    TFTANALYZER("tft.analyzer"),
    TFTEXPLAINCOST("tft.explaincost"),
    TFTADDSUG("tft.addsug"),
    TFTDELSUG("tft.delsug"),
    TFTSUGNUM("tft.sugnum"),
    TFTGETSUG("tft.getsug"),
    TFTGETALLSUGS("tft.getallsugs"),

    // TairVector
    TVSCREATEINDEX("tvs.createindex"),
    TVSGETINDEX("tvs.getindex"),
    TVSDELINDEX("tvs.delindex"),
    TVSSCANINDEX("tvs.scanindex"),
    TVSHSET("tvs.hset"),
    TVSHGETALL("tvs.hgetall"),
    TVSHMGET("tvs.hmget"),
    TVSDEL("tvs.del"),
    TVSHDEL("tvs.hdel"),
    TVSSCAN("tvs.scan"),
    TVSKNNSEARCH("tvs.knnsearch"),
    TVSMKNNSEARCH("tvs.mknnsearch"),
    TVSMINDEXKNNSEARCH("tvs.mindexknnsearch"),
    TVSMINDEXMKNNSEARCH("tvs.mindexmknnsearch"),

    // Aliyun Commands
    IINFO("iinfo"),
    RIINFO("riinfo"),
    ISCAN("iscan"),
    IMONITOR("imonitor"),
    RIMONITOR("rimonitor");

    private final byte[] raw;

    ModuleCommand(String alt) {
        raw = SafeEncoder.encode(alt);
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
