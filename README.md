# ����

tairjedis-sdk�ǻ���[Jedis](https://github.com/xetorthio/jedis)��װ�ģ�����[�����ݿ�Redis��ҵ��](https://help.aliyun.com/document_detail/146579.html) �Ŀͻ��ˣ���Ҫ�������й��ܣ�

- ֧����ҵ�����[Module](https://help.aliyun.com/document_detail/146579.html)�Ĳ�������
- ֧�ּ�Ⱥֱ��ģʽ��mset/mget/del���δ��ɡ�

# ��װ����

```
<dependency>
  <groupId>com.aliyun.tair</groupId>
  <artifactId>tairjedis-sdk</artifactId>
  <version>1.0.0������ʹ�����°汾��</version>
</dependency>
```

���°汾���ģ�[����](http://repo.alibaba-inc.com/nexus/index.html#nexus-search;quick~tairjedis-sdk)

# ����ʹ��

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

# ����ʵ��

## �ֲ�ʽ��

������redis set with nx��and expiretime
������cad(compare and delete)
```
public class DistributeLock {
    private TairString tairString;
    private Jedis jedis;

    public DistributeLock(Jedis j) {
        jedis = j;
        tairString = new TairString(j);
    }

    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        try {
            String result = jedis.set(lockKey, requestId, SetParams.setParams().nx().ex(expireTime));
            if ("OK".equals(result)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        try {
            Long ret = tairString.cad(lockKey, requestId);
            if (1 == ret) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

## TairDocʾ��

��ѧ����Ϣ�洢ΪJSON��ʽ��ֱ��ͨ��path���²���JSON����
```
public class TairDocTest {
    private static TairDoc tairDoc;

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        tairDoc = new TairDoc(jedis);

        Student student = new Student("Tom", 18);

        // �洢
        tairDoc.jsonset("tominfo", ".", JSON.toJSONString(student));

        // ��������ΪDanny, ����+1
        tairDoc.jsonset("tominfo", ".name", "\"Danny\"");
        tairDoc.jsonnumincrBy("tominfo", ".age", 2.0);

        // ���»�ȡ
        String tomInfo = tairDoc.jsonget("tominfo");
        System.out.println(tomInfo);

        jedis.close();
    }

    static class Student {
        private String name;
        private int age;

        Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
```