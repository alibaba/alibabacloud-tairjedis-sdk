# kvstore-jedis

kvstore-jedis�ǻ���[Jedis](https://github.com/xetorthio/jedis)��ɵģ�����[�����ݿ�Redis��](https://yuque.antfin-inc.com/tair-userdoc/cloud-redis) Moule���ݽṹ�Ŀͻ��ˣ���Ҫ�������й��ܣ�

- ֧����ͨ����
- ֧��Pipeline
- ֧��Cluster

��ʼ��
```
public class TestBase {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6379;
    private static final int CLUSTER_PORT = 30001;

    private static Jedis jedis;
    private static JedisCluster jedisCluster;

    public static TairDoc tairDoc;
    public static TairDocPipeline tairDocPipeline;
    public static TairDocCluster tairDocCluster;

    @BeforeClass
    public static void setUp() {
        if (jedis == null) {
            jedis = new Jedis(HOST, PORT);
            if (!"PONG".equals(jedis.ping())) {
                System.exit(-1);
            }

            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            jedisClusterNodes.add(new HostAndPort(HOST, CLUSTER_PORT));
            jedisCluster = new JedisCluster(jedisClusterNodes);

            tairDoc = new TairDoc(jedis);
            tairDocPipeline = new TairDocPipeline();
            tairDocPipeline.setClient(jedis.getClient());
            tairDocCluster = new TairDocCluster(jedisCluster);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
```

��ͨ���
```
    @Test
    public void jsonSetTest() {
        String ret = tairDoc.jsonset(jsonKey, ".", JSON_STRING_EXAMPLE);
        assertEquals("OK", ret);

        ret = tairDoc.jsonget(jsonKey, ".");
        assertEquals(JSON_STRING_EXAMPLE, ret);

        ret = tairDoc.jsonget(jsonKey, ".foo");
        assertEquals("\"bar\"", ret);

        ret = tairDoc.jsonget(jsonKey, ".baz");
        assertEquals("42", ret);
    }
```

Pipeline
```
    @Test
    public void jsonSetPipelineTest() {
        tairDocPipeline.jsonset(jsonKey, ".", JSON_STRING_EXAMPLE);
        tairDocPipeline.jsonget(jsonKey, ".");
        tairDocPipeline.jsonget(jsonKey, ".foo");
        tairDocPipeline.jsonget(jsonKey, ".baz");

        List<Object> objs = tairDocPipeline.syncAndReturnAll();

        assertEquals("OK", objs.get(0));
        assertEquals(JSON_STRING_EXAMPLE, objs.get(1));
        assertEquals("\"bar\"", objs.get(2));
        assertEquals("42", objs.get(3));
    }
```

Cluster
```
    @Test
    public void jsonSetClusterTest() {
        String ret = tairDocCluster.jsonset(jsonKey, jsonKey, ".", JSON_STRING_EXAMPLE);
        assertEquals("OK", ret);

        ret = tairDocCluster.jsonget(jsonKey, jsonKey, ".");
        assertEquals(JSON_STRING_EXAMPLE, ret);

        ret = tairDocCluster.jsonget(jsonKey, jsonKey, ".foo");
        assertEquals("\"bar\"", ret);

        ret = tairDocCluster.jsonget(jsonKey, jsonKey, ".baz");
        assertEquals("42", ret);
    }
```

# ԭ��

ʹ��Jedis sendCommand�ӿڣ�����ԭ����RedisЭ�鵽����ִ�С�

# ����

Jedis�汾 >= 3.1.0 (������Jedis sendCommand�ӿ��ṩ�İ汾)
����û��Լ�������Jedis����Ҫ���������ų���